package repository;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import model.Profissional;

public class ArquivoProfissionalRepository implements ProfissionalRepository {

    private String arquivo;
    private List<Profissional> profissionais;

    public ArquivoProfissionalRepository(String arquivo) {
        this.arquivo = arquivo;
        this.profissionais = new ArrayList<>();
        carregar();
    }

    @Override
    public void salvar(Profissional p) {
        boolean atualizou = false;
        for (int i = 0; i < profissionais.size(); i++) {
            if (profissionais.get(i).getCpf().equals(p.getCpf())) {
                profissionais.set(i, p);
                atualizou = true;
                break;
            }
        }

        if (!atualizou) {
            profissionais.add(p);
        }

        gravar();
    }

    @Override
    public Profissional buscarPorCpf(String cpf) {
        for (Profissional p : profissionais) {
            if (p.getCpf().equals(cpf)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public List<Profissional> listar() {
        return List.copyOf(profissionais);
    }

    @Override
    public void remover(String cpf) {
        if (cpf != null && profissionais.removeIf(p -> p.getCpf().equals(cpf))) {
            gravar();
        }
    }

    private void gravar() {
        Path destino = Paths.get(arquivo);
        Path temporario = Paths.get(arquivo + ".tmp");

        try {
            // 1. Garante que a pasta "data/" exista
            if (destino.getParent() != null) {
                Files.createDirectories(destino.getParent());
            }

            // 2. Grava os bytes no arquivo temporário primeiro
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(temporario)))) {
                oos.writeObject(profissionais);
                oos.flush();
            }

            // 3. Substitui o arquivo oficial somente após a gravação terminar 100%
            try {
                Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                // Fallback seguro caso o sistema de arquivos não suporte ATOMIC_MOVE
                Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {
            System.err.println("Erro crítico ao gravar " + arquivo + ": " + e.getMessage());
            try {
                Files.deleteIfExists(temporario); // Limpa o arquivo temporário incompleto
            } catch (IOException ignored) {
            }
        }
    }

    private void carregar() {
        Path origem = Paths.get(arquivo);
        this.profissionais = new ArrayList<>();

        try {
            // 1. Se o arquivo não existe ou está vazio (0 bytes), retorna
            if (!Files.exists(origem) || Files.size(origem) == 0) {
                return;
            }

            // 2. Lê os objetos com buffer para maior performance e segurança
            try (ObjectInputStream ois = new ObjectInputStream(
                    new BufferedInputStream(Files.newInputStream(origem)))) {
                Object lido = ois.readObject();

                // Percorre a lista genérica validando cada elemento individualmente
                if (lido instanceof List<?> listaGenerica) {
                    for (Object item : listaGenerica) {
                        if (item instanceof Profissional p) {
                            this.profissionais.add(p);
                        }
                    }
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Aviso: arquivo " + arquivo + " corrompido ou incompatível (" + e.getMessage() + ").");
            this.profissionais = new ArrayList<>();

            // 3. Salva uma cópia .bak do arquivo problemático para não perder evidências e
            // destrava o sistema
            try {
                Path backup = Paths.get(arquivo + ".corrompido.bak");
                Files.move(origem, backup, StandardCopyOption.REPLACE_EXISTING);
                System.err.println("Backup do arquivo anterior salvo em: " + backup);
            } catch (IOException ignored) {
            }
        }
    }
}
package repository;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.Consulta;

public class ArquivoConsultaRepository implements ConsultaRepository {

    private String arquivo;
    private List<Consulta> consultas;

    public ArquivoConsultaRepository(String arquivo) {
        this.arquivo = arquivo;
        this.consultas = new ArrayList<>();
        carregar();
    }

    @Override
    public void salvar(Consulta c) {
        consultas.removeIf(existente -> existente.getId() == c.getId());
        consultas.add(c);
        consultas.sort(Comparator.comparing(Consulta::getDataHora));
        gravar();
    }

    @Override
    public Consulta buscarPorId(int id) {
        for (Consulta c : consultas) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    @Override
    public List<Consulta> listar() {
        return List.copyOf(consultas);
    }

    @Override
    public int proximoId() {
        int maiorId = 0;
        for (Consulta c : consultas) {
            if (c.getId() > maiorId) {
                maiorId = c.getId();
            }
        }
        return maiorId + 1;
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
                oos.writeObject(consultas);
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
        this.consultas = new ArrayList<>();

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
                        if (item instanceof Consulta c) {
                            this.consultas.add(c);
                        }
                    }
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Aviso: arquivo " + arquivo + " corrompido ou incompatível (" + e.getMessage() + ").");
            this.consultas = new ArrayList<>();

            // 3. Salva uma cópia .bak do arquivo problemático para não perder evidências e destrava o sistema
            try {
                Path backup = Paths.get(arquivo + ".corrompido.bak");
                Files.move(origem, backup, StandardCopyOption.REPLACE_EXISTING);
                System.err.println("Backup do arquivo anterior salvo em: " + backup);
            } catch (IOException ignored) {
            }
        }
    }
}
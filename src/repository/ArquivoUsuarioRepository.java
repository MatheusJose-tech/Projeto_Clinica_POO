package repository;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import model.Usuario;

public class ArquivoUsuarioRepository implements UsuarioRepository {

    private String arquivo;
    private List<Usuario> usuarios;

    public ArquivoUsuarioRepository(String arquivo) {
        this.arquivo = arquivo;
        this.usuarios = new ArrayList<>();
        carregar();
    }

    @Override
    public void salvar(Usuario u) {
        boolean atualizou = false;
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getLogin().equals(u.getLogin())) {
                usuarios.set(i, u);
                atualizou = true;
                break;
            }
        }

        if (!atualizou) {
            usuarios.add(u);
        }

        gravar();
    }

    @Override
    public Usuario buscarPorLogin(String login) {
        for (Usuario u : usuarios) {
            if (u.getLogin().equals(login)) {
                return u;
            }
        }
        return null;
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
                oos.writeObject(usuarios);
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
        this.usuarios = new ArrayList<>();

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
                        if (item instanceof Usuario c) {
                            this.usuarios.add(c);
                        }
                    }
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Aviso: arquivo " + arquivo + " corrompido ou incompatível (" + e.getMessage() + ").");
            this.usuarios = new ArrayList<>();

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
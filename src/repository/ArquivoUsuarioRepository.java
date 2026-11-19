package repository;

import model.Usuario;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
        usuarios.add(u);
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

        File file = new File(arquivo);

        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(usuarios);
        } catch (IOException e) {
            System.err.println("Erro ao gravar arquivo de usuarios: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void carregar() {

        File file = new File(arquivo);

        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            usuarios = (List<Usuario>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar arquivo de usuarios: " + e.getMessage());
        }
    }
}

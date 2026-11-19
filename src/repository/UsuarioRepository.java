package repository;

import model.Usuario;

public interface UsuarioRepository {

    public void salvar(Usuario u);

    public Usuario buscarPorLogin(String login);
}
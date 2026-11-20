package repository;

import java.util.List;

import model.Profissional;

public interface ProfissionalRepository {

    // Insere o profissional ou substitui o que já tiver o mesmo CPF.
    public void salvar(Profissional p);

    // Devolve null se não encontrar.
    public Profissional buscarPorCpf(String cpf);

    // Devolve uma cópia imutável da lista.
    public List<Profissional> listar();

    public void remover(String cpf);
}

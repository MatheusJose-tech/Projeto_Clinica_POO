package repository;

import java.util.List;

import model.Paciente;

public interface PacienteRepository {

    // Insere o paciente ou substitui o que já tiver o mesmo CPF.
    public void salvar(Paciente p);

    // Devolve null se não encontrar.
    public Paciente buscarPorCpf(String cpf);

    // Devolve uma cópia imutável da lista.
    public List<Paciente> listar();

    public void remover(String cpf);
}

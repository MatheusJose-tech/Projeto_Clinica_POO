package repository;

import java.util.List;

import model.Consulta;

public interface ConsultaRepository {

    public void salvar(Consulta c);

    public Consulta buscarPorId(int id);

    public List<Consulta> listar();

    public int proximoId();
}
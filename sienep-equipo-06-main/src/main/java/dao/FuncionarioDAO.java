package dao;

import dao.GenericDAO;
import model.Funcionario;
import model.Rol;

import java.util.List;

public interface FuncionarioDAO extends GenericDAO<Funcionario> {
    // Métodos espéíficos de Funcionario
    // Asignar un Rol a un funcionario por medio de su ID
    void assignRol(int idFuncionario, Rol rol) throws Exception;
    // Buscar funcionarios por nombre de rol
    List<Funcionario> findByRol(String nombreRol) throws Exception;
}

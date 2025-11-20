package service;

import exception.InvalidInputException;
import model.Funcionario;
import model.Rol;

import java.util.List;

public interface FuncionarioService {
    void guardarFuncionario(Funcionario funcionario) throws InvalidInputException;
    Funcionario buscarFuncionarioPorId(int id);
    List<Funcionario> listarFuncionarios();
    void actualizarFuncionario(Funcionario funcionario) throws InvalidInputException;
    void eliminarFuncionario(int id);

    void asignarRol(int idFuncionario, Rol rol);
    List<Funcionario> buscarFuncionariosPorRol(String nombreRol);
}
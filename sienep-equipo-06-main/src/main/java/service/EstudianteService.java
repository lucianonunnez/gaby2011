package service;

import exception.InvalidInputException;
import model.Estudiante;

import java.util.List;

public interface EstudianteService {
    List<Estudiante> listarEstudiantesPorCarrera(String carrera);
    List<Estudiante> listarEstudiantePorGrupo(String grupo);
    List<Estudiante> listarEstudiantePorSistemaSalud(String sistemaSalud);
    void guardarEstudiante(Estudiante estudiante) throws Exception;
    Estudiante buscarEstudiantePorId(int id);
    List<Estudiante> listarTodosEstudiantes();
    void actualizarEstudiante(Estudiante estudiante) throws InvalidInputException;
    void eliminarEstudiante(int id);
}
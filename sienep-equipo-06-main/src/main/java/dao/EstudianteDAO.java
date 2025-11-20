package dao;

import model.Estudiante;

import java.util.List;

public interface EstudianteDAO extends GenericDAO<Estudiante> {
    // métodos específicos de Estudiante
    List<Estudiante> findByCarrera(String carrera) throws Exception;
    List<Estudiante> findByGrupo(String grupo) throws Exception;
    List<Estudiante> findBySistemaSalud(String sistemaSalud) throws Exception;

    void updateTelefono(int id, String telefono) throws Exception;
}

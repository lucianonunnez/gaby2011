package dao;

import model.Instancia;

import java.util.List;

public interface InstanciaDAO extends GenericDAO<Instancia> {
    List<Instancia> findByEstudiante(int idEstudiante) throws Exception;
}

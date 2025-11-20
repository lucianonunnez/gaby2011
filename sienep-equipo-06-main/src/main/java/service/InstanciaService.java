package service;

import model.Incidencia;
import model.Instancia;
import model.InstanciaComun;

import java.time.LocalDateTime;
import java.util.List;

public interface InstanciaService {
    void createInstanciaComun(InstanciaComun instancia) throws Exception;
    void createIncidencia(Incidencia incidencia) throws Exception;
    List<Instancia> getInstanciasByEstudiante(int idEstudiante) throws Exception;
    String generateCodigo() throws Exception;
    List<Instancia> listarTodasLasInstancias() throws Exception;
    List<InstanciaComun> listarInstanciasComunes() throws Exception;
    List<Incidencia> listarIncidencias() throws Exception;
    void actualizarFechaInstanciaComun(int id, LocalDateTime nuevaFecha) throws Exception;
    void actualizarComentarioInstanciaComun(int id, String nuevoComentario) throws Exception;
    void actualizarFechaIncidencia(int id, LocalDateTime nuevaFecha) throws Exception;
    void actualizarComentarioIncidencia(int id, String nuevoComentario) throws Exception;
    void eliminarInstanciaPorId(int id) throws Exception;
    void clonarInstanciaComun(int idInstanciaOriginal) throws Exception;
    void actualizarIncidencia(Incidencia incidencia) throws Exception;
}
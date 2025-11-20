package service.impl;

import dao.GenericDAO;
import dao.InstanciaDAO;
import model.Incidencia;
import model.Instancia;
import model.InstanciaComun;
import org.apache.log4j.Logger;
import service.GoogleCalendarService;
import service.InstanciaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class InstanciaServiceImpl implements InstanciaService {
    private static final Logger logger = Logger.getLogger(InstanciaServiceImpl.class);
    private final InstanciaDAO instanciaDAO;
    private final GenericDAO<Incidencia> incidenciaDAO;
    private final GenericDAO<InstanciaComun> instanciaComunDAO;
    private GoogleCalendarService googleCalendarService;

    public InstanciaServiceImpl(InstanciaDAO instanciaDAO,
                                GenericDAO<Incidencia> incidenciaDAO,
                                GenericDAO<InstanciaComun> instanciaComunDAO) {
        this.instanciaDAO = instanciaDAO;
        this.incidenciaDAO = incidenciaDAO;
        this.instanciaComunDAO = instanciaComunDAO;

        try {
            this.googleCalendarService = new GoogleCalendarService();
        } catch (Exception e) {
            logger.warn("No se pudo inicializar Google Calendar Service: " + e.getMessage());
            this.googleCalendarService = null;
        }

    }

    @Override
    public void createInstanciaComun(InstanciaComun instancia) throws Exception {
        instancia.setTipo("COMUN");
        instancia.setCodigo(generateCodigo());

        // Crear evento en Google Calendar
        if (googleCalendarService != null) {
            try {
                String descripcion = "Instancia común - " + instancia.getMotivacion() +
                        "\nEstudiante: " + instancia.getEstudianteAsociado().getNombre() +
                        " " + instancia.getEstudianteAsociado().getApellido() +
                        "\nCategoría: " + instancia.getCategoria().getNombre();

                String eventId = googleCalendarService.crearEventoConRecordatorio(
                        instancia.getTitulo(),
                        descripcion,
                        instancia.getFechaHora(),
                        60, // 60 minutos de duración por defecto
                        30  // recordatorio 30 minutos antes
                );

                instancia.setGoogleCalendarEventId(eventId);
                logger.info("Evento creado en Google Calendar para instancia: " + instancia.getCodigo());
            } catch (Exception e) {
                logger.error("Error al crear evento en Google Calendar: " + e.getMessage(), e);
                // Continúa sin el evento de calendar si falla
            }
        }

        instanciaComunDAO.save(instancia);
        logger.info("InstanciaComun creada con código: " + instancia.getCodigo());
    }

    @Override
    public void createIncidencia(Incidencia incidencia) throws Exception {
        incidencia.setTipo("INCIDENCIA");
        incidencia.setCodigo(generateCodigo());

        // Crear evento en Google Calendar para incidencias
        if (googleCalendarService != null) {
            try {
                String descripcion = "INCIDENCIA - CONFIDENCIAL" +
                        "\nLugar: " + incidencia.getLugar() +
                        "\nEstudiante: " + incidencia.getEstudianteAsociado().getNombre() +
                        " " + incidencia.getEstudianteAsociado().getApellido() +
                        "\nReportado por: " + incidencia.getReportadoPor().getNombre();

                String eventId = googleCalendarService.crearEventoConRecordatorio(
                        "INCIDENCIA: " + incidencia.getTitulo(),
                        descripcion,
                        incidencia.getFechaHora(),
                        90, // 90 minutos para incidencias
                        60  // recordatorio 60 minutos antes
                );

                incidencia.setGoogleCalendarEventId(eventId);
                logger.info("Evento creado en Google Calendar para incidencia: " + incidencia.getCodigo());
            } catch (Exception e) {
                logger.error("Error al crear evento en Google Calendar: " + e.getMessage(), e);
            }
        }

        incidenciaDAO.save(incidencia);
        logger.info("Incidencia creada con código: " + incidencia.getCodigo());
    }

    @Override
    public List<Instancia> getInstanciasByEstudiante(int idEstudiante) throws Exception {
        return instanciaDAO.findByEstudiante(idEstudiante);
    }

    @Override
    public String generateCodigo() throws Exception {
        // segun el RF14, debia seguir un formato tipo: INST-2025-XXXX
        int contador = (int) (Math.random() * 10000);
        return "INST-" + LocalDate.now().getYear() + "-" + String.format("%04d", contador);

    }

    // método para listar todas las instancias
    @Override
    public List<Instancia> listarTodasLasInstancias() throws Exception {
        return instanciaDAO.findAll();
    }

    @Override
    public List<InstanciaComun> listarInstanciasComunes() throws Exception {
        try {
            return instanciaComunDAO.findAll();
        } catch (Exception e) {
            logger.error("Error al listar instancias comunes", e);
            throw e;
        }
    }

    @Override
    public List<Incidencia> listarIncidencias() throws Exception {
        try {
            return incidenciaDAO.findAll();
        } catch (Exception e) {
            logger.error("Error al listar incidencias", e);
            throw e;
        }
    }

    @Override
    public void actualizarFechaInstanciaComun(int id, LocalDateTime nuevaFecha) throws Exception {
        try {
            InstanciaComun instancia = instanciaComunDAO.findById(id);  // Asume que tiene findById()
            if (instancia == null) {
                throw new Exception("Instancia común no encontrada.");
            }
            instancia.setFechaHora(nuevaFecha);
            instanciaComunDAO.update(instancia);  // Asume que tiene update()
            logger.info("Fecha actualizada para instancia común ID: " + id);
        } catch (Exception e) {
            logger.error("Error al actualizar fecha de instancia común ID: " + id, e);
            throw e;
        }
    }

    @Override
    public void actualizarComentarioInstanciaComun(int id, String nuevoComentario) throws Exception {
        try {
            InstanciaComun instancia = instanciaComunDAO.findById(id);
            if (instancia == null) {
                throw new Exception("Instancia común no encontrada.");
            }
            instancia.setComentario(nuevoComentario);
            instanciaComunDAO.update(instancia);
            logger.info("Comentario actualizado para instancia común ID: " + id);
        } catch (Exception e) {
            logger.error("Error al actualizar comentario de instancia común ID: " + id, e);
            throw e;
        }
    }

    @Override
    public void actualizarFechaIncidencia(int id, LocalDateTime nuevaFecha) throws Exception {
        try {
            Incidencia incidencia = incidenciaDAO.findById(id);
            if (incidencia == null) {
                throw new Exception("Incidencia no encontrada.");
            }
            incidencia.setFechaHora(nuevaFecha);
            incidenciaDAO.update(incidencia);
            logger.info("Fecha actualizada para incidencia ID: " + id);
        } catch (Exception e) {
            logger.error("Error al actualizar fecha de incidencia ID: " + id, e);
            throw e;
        }
    }

    @Override
    public void actualizarComentarioIncidencia(int id, String nuevoComentario) throws Exception {
        try {
            Incidencia incidencia = incidenciaDAO.findById(id);
            if (incidencia == null) {
                throw new Exception("Incidencia no encontrada.");
            }
            incidencia.setComentario(nuevoComentario);
            incidenciaDAO.update(incidencia);
            logger.info("Comentario actualizado para incidencia ID: " + id);
        } catch (Exception e) {
            logger.error("Error al actualizar comentario de incidencia ID: " + id, e);
            throw e;
        }
    }

    @Override
    public void eliminarInstanciaPorId(int id) throws Exception {
        try {
            Instancia instancia = instanciaDAO.findById(id);
            if (instancia == null) {
                throw new Exception("Instancia no encontrada.");
            }

            // Eliminar evento de Google Calendar si existe
            if (googleCalendarService != null && instancia.getGoogleCalendarEventId() != null) {
                try {
                    googleCalendarService.eliminarEvento(instancia.getGoogleCalendarEventId());
                    logger.info("Evento de Google Calendar eliminado para instancia ID: " + id);
                } catch (Exception e) {
                    logger.error("Error al eliminar evento de Google Calendar para instancia ID: " + id, e);
                }
            }

            instanciaDAO.delete(id);
            logger.info("Instancia eliminada con ID: " + id);
        } catch (Exception e) {
            logger.error("Error al eliminar instancia con ID: " + id, e);
            throw e;
        }
    }

    @Override
    public void clonarInstanciaComun(int idInstanciaOriginal) throws Exception {
        try {
            // Buscar la instancia original
            InstanciaComun original = instanciaComunDAO.findById(idInstanciaOriginal);
            if (original == null) {
                throw new Exception("Instancia común original no encontrada.");
            }

            // Crear el clon copiando todos los campos (excepto ID y código)
            InstanciaComun clon = new InstanciaComun();
            clon.setTitulo(original.getTitulo());
            clon.setComentario(original.getComentario());
            clon.setEstudianteAsociado(original.getEstudianteAsociado());
            clon.setCreador(original.getCreador());
            clon.setFechaHora(original.getFechaHora());
            clon.setCanal(original.getCanal());
            clon.setConfidencial(original.isConfidencial());
            clon.setCategoria(original.getCategoria());
            clon.setMotivacion(original.getMotivacion());
            clon.setTipo(original.getTipo());

            createInstanciaComun(clon);

            logger.info("Instancia común clonada de ID: " + idInstanciaOriginal + " a nueva ID: " + clon.getId());
        } catch (Exception e) {
            logger.error("Error al clonar instancia común ID: " + idInstanciaOriginal, e);
            throw e;
        }
    }

    @Override
    public void actualizarIncidencia(Incidencia incidencia) throws Exception {
        try {
            incidenciaDAO.update(incidencia);
            logger.info("Incidencia actualizada ID: " + incidencia.getId());
        } catch (Exception e) {
            logger.error("Error al actualizar incidencia ID: " + incidencia.getId(), e);
            throw e;
        }
    }
}
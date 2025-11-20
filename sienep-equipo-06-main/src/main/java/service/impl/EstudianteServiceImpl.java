package service.impl;

import dao.EstudianteDAO;
import exception.InvalidInputException;
import model.Estudiante;
import org.apache.log4j.Logger;
import service.EstudianteService;
import utils.ValidationUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Pattern;

public class EstudianteServiceImpl implements EstudianteService {
    private static final Logger logger = Logger.getLogger(EstudianteServiceImpl.class);
    private final EstudianteDAO estudianteDAO;

    // patron regex para validar el formato de correo estudiantil
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@estudiantes\\.utec\\.edu\\.uy$");

    public EstudianteServiceImpl(EstudianteDAO estudianteDAO) {
        this.estudianteDAO = estudianteDAO;
    }

    @Override
    public List<Estudiante> listarEstudiantesPorCarrera(String carrera) {
        try {
            return estudianteDAO.findByCarrera(carrera);
        } catch (Exception e) {
            logger.error("Error al listar estudiantes por carrera: " + carrera, e);
            return null;
        }
    }

    @Override
    public List<Estudiante> listarEstudiantePorGrupo(String grupo) {
        try {
            return estudianteDAO.findByGrupo(grupo);
        } catch (Exception e) {
            logger.error("Error al listar estudiantes por grupo: " + grupo, e);
            return null;
        }
    }

    @Override
    public List<Estudiante> listarEstudiantePorSistemaSalud(String sistemaSalud) {
        try {
            return estudianteDAO.findBySistemaSalud(sistemaSalud);
        } catch (Exception e) {
            logger.error("Error al listar estudiantes por sistema de salud: " + sistemaSalud, e);
            return null;
        }
    }

    @Override
    public void guardarEstudiante(Estudiante estudiante) throws Exception {
        // validar todos los datos antes de guardar
        validateEstudianteData(estudiante);

        try {
            estudianteDAO.save(estudiante);
            logger.info("Estudiante guardado con ID: " + estudiante.getId());
        } catch (Exception e) {
            logger.error("Error al guardar estudiante: " + estudiante, e);
            throw e;
        }
    }

    // Calcula la edad a partir de la fecha de nacimiento
    private int calcularEdad(LocalDate fechaNacimiento) throws InvalidInputException {
        if(fechaNacimiento.isAfter(LocalDate.now())) {
            throw new InvalidInputException("La fecha de nacimiento no puede ser en el futuro.");
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    private void validarEdadEstudiante(LocalDate fechanacimiento) throws InvalidInputException {
        int edad = calcularEdad(fechanacimiento);
        if(edad < 18) {
            throw new InvalidInputException("El usuario debe ser mayor a 18 años.");
        }
    }

    // metodo centralizado para validar datos del estudiante
    private void validateEstudianteData(Estudiante estudiante) throws InvalidInputException {
        try {
            ValidationUtils.validateDocumento(estudiante.getDocumento());
            ValidationUtils.validatePassword(estudiante.getContrasenia());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(e.getMessage());  // Convierte a InvalidInputException si usas esa
        }

        // validacion del email
        if (estudiante.getEmail() == null || estudiante.getEmail().trim().isEmpty()) {
            throw new InvalidInputException("El email es obligatorio.");
        }
        String rawEmail = estudiante.getEmail();
        String trimmedEmail = rawEmail.trim();
        // validar si hay esacios internos
        if (trimmedEmail.contains(" ")) {
            throw new InvalidInputException("El email no debe contener espacios internos.");
        }
        // se remplaza el email por el trimmed solo si pasó la verificación
        estudiante.setEmail(trimmedEmail);
        // validar formato con regex
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            throw new InvalidInputException("El email debe tener el formato usuario@estudiantes.utec.edu.uy.");
        }
        // validar fecha de nacimiento y edad
        if (estudiante.getFechaNacimiento() == null) {
            throw new InvalidInputException("La fecha de nacimiento es obligatoria.");
        }
        validarEdadEstudiante(estudiante.getFechaNacimiento());
    }

    @Override
    public Estudiante buscarEstudiantePorId(int id) {
        try {
            return estudianteDAO.findById(id);
        } catch (Exception e) {
            logger.error("Error al buscar estudiante por ID: " + id, e);
            return null;
        }
    }

    @Override
    public List<Estudiante> listarTodosEstudiantes() {
        try {
            return estudianteDAO.findAll();
        } catch (Exception e) {
            logger.error("Error al listar todos los estudiantes", e);
            return null;
        }
    }

    @Override
    public void actualizarEstudiante(Estudiante estudiante) throws InvalidInputException {
        // Validar todos los datos antes de actualizar (incluyendo edad si cambia fecha)
        try {
            validateEstudianteData(estudiante);
            estudianteDAO.update(estudiante);
            logger.info("Estudiante actualizado con ID: " + estudiante.getId());
        } catch (InvalidInputException e) {
            logger.warn("Validación fallida al actualizar estudiante ID: " + estudiante.getId() + " - " + e.getMessage());
            throw e; // Re-lanzar para que el facade lo maneje
        } catch (Exception e) {
            logger.error("Error al actualizar estudiante con ID: " + estudiante.getId(), e);
        }
    }

    @Override
    public void eliminarEstudiante(int id) {
        try {
            estudianteDAO.delete(id);
            logger.info("Estudiante eliminado con ID: " + id);
        } catch (Exception e) {
            logger.error("Error al eliminar estudiante con ID: " + id, e);
        }
    }
}

package service.impl;

import dao.FuncionarioDAO;
import dao.impl.FuncionarioDAOImpl;
import exception.InvalidInputException;
import model.Funcionario;
import model.Rol;
import org.apache.log4j.Logger;
import service.FuncionarioService;
import service.RolService;
import utils.ValidationUtils;

import java.util.List;
import java.util.regex.Pattern;

public class FuncionarioServiceImpl implements FuncionarioService {
    private static final Logger logger = Logger.getLogger(FuncionarioServiceImpl.class);
    private final FuncionarioDAO funcionarioDAO;
    private final RolService rolService;

    // patron regex para validar el formato del email
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@utec\\.edu\\.uy$");

    public FuncionarioServiceImpl() throws Exception {
        this.funcionarioDAO = new FuncionarioDAOImpl();
        this.rolService = new RolServiceImpl();
    }

    @Override
    public void guardarFuncionario(Funcionario funcionario) throws InvalidInputException {
        // Validar todos los datos antes de guardar
        validateFuncionarioData(funcionario);
        try {
            funcionarioDAO.save(funcionario);
            logger.info("Funcionario guardado exitosamente: " + funcionario.getNombre());
        } catch (Exception e) {
            logger.error("Error al guardar el funcionario: " + funcionario.getNombre(), e);
            throw new RuntimeException("Error interno al guardar el funcionario", e);
        }
    }

    @Override
    public Funcionario buscarFuncionarioPorId(int id) {
        try {
            return funcionarioDAO.findById(id);
        } catch (Exception e) {
            logger.error("Error al buscar el funcionario con ID: " + id, e);
            return null;
        }
    }

    @Override
    public List<Funcionario> listarFuncionarios() {
        try {
            return funcionarioDAO.findAll();
        } catch (Exception e) {
            logger.error("Error al listar los funcionarios", e);
            return null;
        }
    }

    @Override
    public void actualizarFuncionario(Funcionario funcionario) throws InvalidInputException {
        // Validar todos los datos antes de actualizar
        validateFuncionarioData(funcionario);
        try {
            funcionarioDAO.update(funcionario);
            logger.info("Funcionario actualizado exitosamente: " + funcionario.getNombre());
        } catch (Exception e) {
            logger.error("Error al actualizar el funcionario: " + funcionario.getNombre(), e);
            throw new RuntimeException("Error interno al actualizar el funcionario", e);
        }
    }

    @Override
    public void eliminarFuncionario(int id) {
        try {
            funcionarioDAO.delete(id);
            logger.info("Funcionario eliminado exitosamente con ID: " + id);
        } catch (Exception e) {
            logger.error("Error al eliminar el funcionario con ID: " + id, e);
        }
    }

    @Override
    public void asignarRol(int idFuncionario, Rol rol) {
        try {
            funcionarioDAO.assignRol(idFuncionario, rol);
        } catch (Exception e) {
            logger.error("Error al asignar rol al funcionario ID: " + idFuncionario, e);
        }
    }

    @Override
    public List<Funcionario> buscarFuncionariosPorRol(String nombreRol) {
        try {
            return funcionarioDAO.findByRol(nombreRol);
        } catch (Exception e) {
            logger.error("Error al buscar funcionarios por rol: " + nombreRol, e);
            return null;
        }
    }

    // Método centralizado para validar datos del funcionario
    private void validateFuncionarioData(Funcionario funcionario) throws InvalidInputException {
        try {
            ValidationUtils.validateDocumento(funcionario.getDocumento());
            ValidationUtils.validatePassword(funcionario.getContrasenia());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(e.getMessage());  // Convierte a InvalidInputException si usas esa
        }

        //validacion del email
        if (funcionario.getEmail() == null || funcionario.getEmail().trim().isEmpty()) {
            throw new InvalidInputException("El email es obligatorio.");
        }
        String rawEmail = funcionario.getEmail();
        String trimmedEmail = rawEmail.trim();
        // verificacion de espacios laterales
        if (trimmedEmail.contains(" ")) {
            throw new InvalidInputException("El email no debe contener espacios internos.");
        }
        // validar formato con regex
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            throw new InvalidInputException("El email debe tener el formato usuario@utec.edu.uy.");
        }
        // persistir email ya limpio
        funcionario.setEmail(trimmedEmail);

        // validacion del rol
        if(funcionario.getRol() == null) {
            throw new InvalidInputException("El rol es obligatorio.");
        }

        // verificar que el rol exista en la base de datos
        Rol rolExistente = rolService.buscarRolPorId(funcionario.getRol().getId());
        if(rolExistente == null) {
            throw new InvalidInputException("El rol especificado no existe.");
        }
    }
}
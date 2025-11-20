package service.impl;

import dao.UsuarioDAO;
import dao.impl.UsuarioDAOImpl;
import exception.CuentaInactivaException;
import model.Usuario;
import org.apache.log4j.Logger;
import service.UsuarioService;

import java.util.List;
import java.util.regex.Pattern;

public class UsuarioServiceImpl implements UsuarioService {
    private static final Logger logger = Logger.getLogger(UsuarioServiceImpl.class);
    private final UsuarioDAO usuarioDAO;

    // patrones regex para validaciones comunes a estudiantes y funcionarios
    private static final Pattern NOMBRE_APELLIDO_PATTERN = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$"); // Solo letras, espacios y acentos
    private static final Pattern DOCUMENTO_PATTERN = Pattern.compile("^\\d+$"); // Solo números
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{7,}$");


    public UsuarioServiceImpl() throws Exception {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    // Método para validar datos comunes de Usuario
    private void validateUsuarioData(Usuario usuario) throws IllegalArgumentException {
        // Validar nombre
        if (usuario.getNombre() == null || usuario.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (!NOMBRE_APELLIDO_PATTERN.matcher(usuario.getNombre()).matches()) {
            throw new IllegalArgumentException("El nombre no debe contener números ni caracteres especiales inválidos.");
        }
        // Validar apellido
        if (usuario.getApellido() == null || usuario.getApellido().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }
        if (!NOMBRE_APELLIDO_PATTERN.matcher(usuario.getApellido()).matches()) {
            throw new IllegalArgumentException("El apellido no debe contener números ni caracteres especiales inválidos.");
        }
        // Validar documento
        if (usuario.getDocumento() == null || usuario.getDocumento().isEmpty()) {
            throw new IllegalArgumentException("El documento es obligatorio.");
        }
        if (!DOCUMENTO_PATTERN.matcher(usuario.getDocumento()).matches()) {
            throw new IllegalArgumentException("El documento debe contener solo números.");
        }
        if (usuario.getDocumento().length() != 7) {
            throw new IllegalArgumentException("El documento debe tener exactamente 7 dígitos.");
        }
        // Validar contraseña (si está presente, e.g., en guardar o actualizar)
        if (usuario.getContrasenia() != null && !usuario.getContrasenia().isEmpty()) {
            validatePassword(usuario.getContrasenia());
        }
    }

    // Método privado para validar contraseña
    private void validatePassword(String password) throws IllegalArgumentException {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 7 caracteres, incluir una mayúscula, un número y un símbolo (ej. @, $, !, %, *, ?, &).");
        }
        // validar longitud máxima de la contraseña = 16 caracteres
        if (password.length() > 16) {
            throw new IllegalArgumentException("La contraseña no puede exceder los 16 caracteres.");
        }
    }

    // CRUD y métodos específicos de Usuario

    @Override
    public void guardarUsuario(Usuario usuario) throws IllegalArgumentException {
        //validacion de los datos comunes antes de guardar
        validateUsuarioData(usuario);
        try {
            usuarioDAO.save(usuario);
            logger.info("Usuario guardado exitosamente: " + usuario.getEmail());
        } catch (Exception e) {
            logger.error("Error al guardar el usuario: " + usuario.getEmail(), e);
            throw new RuntimeException("Error interno al guardar el usuario", e);
        }
    }

    @Override
    public Usuario buscarUsuarioPorId(int id) {
        try {
            return usuarioDAO.findById(id);
        } catch (Exception e) {
            logger.error("Error al buscar el usuario con ID: " + id, e);
            return null;
        }
    }

    @Override
    public List<Usuario> listarUsuarios() {
        try {
            return usuarioDAO.findAll();
        } catch (Exception e) {
            logger.error("Error al listar los usuarios", e);
            return null;
        }
    }

    @Override
    public void actualizarUsuario(Usuario usuario) throws IllegalArgumentException {
        // Validar datos comunes antes de actualizar
        validateUsuarioData(usuario);
        try {
            usuarioDAO.update(usuario);
            logger.info("Usuario actualizado exitosamente: " + usuario.getEmail());
        } catch (Exception e) {
            logger.error("Error al actualizar el usuario: " + usuario.getEmail(), e);
            throw new RuntimeException("Error interno al actualizar el usuario", e);
        }
    }

    @Override
    public void eliminarUsuario(int id) {
        try {
            usuarioDAO.deleteLogical(id);
            logger.info("Usuario eliminado exitosamente con ID: " + id);
        } catch (Exception e) {
            logger.error("Error al eliminar el usuario con ID: " + id, e);
        }
    }

    @Override
    public Usuario buscarUsuarioPorEmail(String email) {
        try {
            return usuarioDAO.findByEmail(email);
        } catch (Exception e) {
            logger.error("Error al buscar el usuario con email: " + email, e);
            return null;
        }
    }

    @Override
    public Usuario validarCredenciales(String email, String contrasenia) throws Exception {
        try {
            return usuarioDAO.validateCredentials(email, contrasenia);
        } catch (CuentaInactivaException e) {
            logger.warn("Intento de login con cuenta inactiva: " + email);
            throw e;  // Re-lanza sin envolver
        } catch (Exception e) {
            logger.error("Error al validar las credenciales para el usuario: " + email, e);
            throw e;
        }
    }

    @Override
    public void cambiarContrasenia(int idUsuario, String nuevaContrasenia) throws IllegalArgumentException {
        // Validar nueva contraseña antes de cambiar
        validatePassword(nuevaContrasenia);
        try {
            usuarioDAO.changePassword(idUsuario, nuevaContrasenia);
            logger.info("Contraseña cambiada exitosamente para el usuario ID: " + idUsuario);
        } catch (Exception e) {
            logger.error("Error al cambiar la contraseña para el usuario ID: " + idUsuario, e);
        }
    }

    @Override
    public Usuario verDatosBasicos(int idUsuario) {
        try {
            Usuario usuario = usuarioDAO.viewNonSensitiveData(idUsuario);
            if (usuario == null) {
                logger.warn("No se encontró el usuario con ID: " + idUsuario);
            } else {
                logger.info("Datos básicos obtenidos para el usuario ID: " + idUsuario);
            }
            return usuario;
        } catch (Exception e) {
            logger.error("Error al obtener los datos básicos del usuario ID: " + idUsuario, e);
            throw new RuntimeException("Error al recuperar los datos del usuario", e);
        }
    }



}
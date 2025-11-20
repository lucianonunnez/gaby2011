package dao.impl;

import dao.UsuarioDAO;
import db.ConfiguracionSistema;
import exception.AutenticacionFallidaException;
import exception.CuentaInactivaException;
import model.Estudiante;
import model.Funcionario;
import model.Rol;
import model.Usuario;
import model.enums.EstadoUsuario;
import model.enums.TipoUsuario;
import org.apache.log4j.Logger;
import utils.PasswordHasher;

import java.sql.*;
import java.util.List;

public class UsuarioDAOImpl implements UsuarioDAO {
    private static final Logger logger = Logger.getLogger(UsuarioDAOImpl.class);
    private Connection connection;

    public UsuarioDAOImpl() throws SQLException {
        this.connection = ConfiguracionSistema.getInstance().getConnection();
    }

    // constructor para inyección de dependencias en tests
    public UsuarioDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Usuario usuario) throws Exception {
        String passwordHash = PasswordHasher.hash(usuario.getContrasenia());
        usuario.setContrasenia(passwordHash);

        String query = "INSERT INTO proyecto.usuarios (nombre, apellido, email, contrasenia, documento, estado, tipo) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getContrasenia());
            ps.setString(5, usuario.getDocumento());
            ps.setString(6, usuario.getEstado().toString().toUpperCase());
            ps.setString(7, usuario.getTipo().toString().toUpperCase());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                    logger.info("Usuario guardado con ID: " + usuario.getId());
                }
            }
        } catch (SQLException e) {
            logger.error("Error al guardar el usuario: " + usuario.getEmail(), e);
            throw e;
        }
    }

    @Override
    public Usuario findById(int id) throws Exception {
        String query = "SELECT * FROM proyecto.usuarios WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    logger.info("Usuario encontrado con ID: " + id);
                    return mapUsuarioFromRS(rs);
                } else {
                    logger.warn("No se encontró usuario con ID: " + id);
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar el usuario con ID: " + id, e);
            throw e;
        }
        return null;
    }

    @Override
    public List<Usuario> findAll() throws Exception {
        String query = "SELECT * FROM proyecto.usuarios";
        try(PreparedStatement ps = connection.prepareStatement(query)) {
            try(ResultSet rs = ps.executeQuery()) {
                List<Usuario> usuarios = new java.util.ArrayList<>();
                while(rs.next()) {
                    usuarios.add(mapUsuarioFromRS(rs));
                }
                logger.info("Usuarios recuperados: " + usuarios.size());
                return usuarios;
            }
        } catch (SQLException e) {
            logger.error("Error al recuperar todos los usuarios", e);
            throw e;
        }
    }

    @Override
    public void update(Usuario usuario) throws Exception {
        String query = "UPDATE proyecto.usuarios SET nombre = ?, apellido = ?, email = ?, contrasenia = ?, documento = ?, estado = ?, tipo = ? WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getContrasenia());
            ps.setString(5, usuario.getDocumento());
            ps.setString(6, usuario.getEstado().toString().toUpperCase());
            ps.setString(7, usuario.getTipo().toString().toUpperCase());
            ps.setInt(8, usuario.getId());

            int rowsAffected = ps.executeUpdate();
            if(rowsAffected > 0) {
                logger.info("Usuario actualizado con ID: " + usuario.getId());
            } else {
                logger.warn("No se encontró usuario para actualizar con ID: " + usuario.getId());
            }
        } catch (SQLException e) {
            logger.error("Error al actualizar el usuario con ID: " + usuario.getId(), e);
            throw e;
        }
    }

    @Override
    public void deleteLogical(int id) throws Exception {
        String query = "UPDATE proyecto.usuarios SET estado = 'INACTIVO' WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                logger.info("Usuario desactivado correctamente: ID " + id);
            } else {
                logger.warn("No se encontró el usuario para desactivar: ID " + id);
            }
        } catch (SQLException e) {
            logger.error("Error desactivando usuario: ID " + id, e);
            throw e;
        }
    }

    @Override
    public Usuario findByEmail(String email) throws Exception {
        String query = """
        SELECT u.*, f.id_rol, r.nombre AS nombre_rol
        FROM proyecto.usuarios u
        LEFT JOIN proyecto.funcionarios f ON f.id = u.id
        LEFT JOIN proyecto.roles r ON f.id_rol = r.id
        WHERE u.email = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = mapUsuarioFromRS(rs);

                    if (usuario instanceof Funcionario func) {
                        if (rs.getObject("id_rol") != null) {
                            Rol rol = new Rol();
                            rol.setId(rs.getInt("id_rol"));
                            rol.setNombre(rs.getString("nombre_rol"));
                            func.setRol(rol);
                        }
                    }

                    logger.info("Usuario encontrado por email: " + email);
                    return usuario;
                } else {
                    logger.warn("Usuario no encontrado por email: " + email);
                }
            }
        } catch (SQLException e) {
            logger.error("Error buscando usuario por email: " + email, e);
            throw e;
        }
        return null;
    }


    @Override
    public Usuario validateCredentials(String email, String contrasenia) throws Exception {
        Usuario usuario = findByEmail(email);
        if (usuario != null && usuario.getEstado() == EstadoUsuario.ACTIVO) {
            if (PasswordHasher.verify(contrasenia, usuario.getContrasenia())) {
                logger.info("Autenticación exitosa para: " + email);
                return usuario;
            } else {
                logger.warn("Contraseña incorrecta para el usuario: " + email);
            }
        }
        throw new AutenticacionFallidaException("Credenciales inválidas para el usuario: " + email);
    }


    @Override
    public void changePassword(int id, String nuevaContrasenia) throws Exception {
        String hashedPassword = PasswordHasher.hash(nuevaContrasenia);
        String query = "UPDATE proyecto.usuarios SET contrasenia = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, id);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                logger.info("Contraseña cambiada correctamente para el usuario ID " + id);
            } else {
                logger.warn("No se encontró el usuario para cambiar la contraseña: ID " + id);
            }
        } catch (SQLException e) {
            logger.error("Error cambiando contraseña para usuario ID " + id, e);
            throw e;
        }
    }

    @Override
    public Usuario viewNonSensitiveData(int id) throws Exception {
        String query = "SELECT id, nombre, apellido, email, documento, estado FROM proyecto.usuarios WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()) {
                    logger.info("Datos no sensibles recuperados para el usuario ID: " + id);
                    return mapUsuarioWithoutPasswordFromRS(rs);
                } else {
                    logger.warn("No se encontró usuario con ID: " + id);
                    return null;
                }
            }
        }
    }

    // Métodos auxiliares

    @Override
    public Usuario mapUsuarioFromRS(ResultSet rs) throws Exception {
        String tipo = rs.getString("tipo");
        Usuario usuario;
        switch (tipo) {
            case "FUNCIONARIO" -> {
                usuario = new Funcionario();
                usuario.setTipo(TipoUsuario.FUNCIONARIO);
            }
            case "ESTUDIANTE" -> {
                usuario = new Estudiante();
                usuario.setTipo(TipoUsuario.ESTUDIANTE);
            }
            default -> throw new SQLException("Tipo de usuario desconocido: " + tipo);
        }
        usuario.setId(rs.getInt("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellido(rs.getString("apellido"));
        usuario.setEmail(rs.getString("email"));
        usuario.setContrasenia(rs.getString("contrasenia"));
        usuario.setDocumento(rs.getString("documento"));
        usuario.setEstado(EstadoUsuario.valueOf(rs.getString("estado")));
        return usuario;
    }

    @Override
    public Usuario mapUsuarioWithoutPasswordFromRS(ResultSet rs) throws Exception {
        String tipo = rs.getString("tipo");
        Usuario usuario;
        switch (tipo) {
            case "FUNCIONARIO" -> {
                usuario = new Funcionario();
                usuario.setTipo(TipoUsuario.FUNCIONARIO);
            }
            case "ESTUDIANTE" -> {
                usuario = new Estudiante();
                usuario.setTipo(TipoUsuario.ESTUDIANTE);
            }
            default -> throw new SQLException("Tipo de usuario desconocido: " + tipo);
        }
        usuario.setId(rs.getInt("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellido(rs.getString("apellido"));
        usuario.setEmail(rs.getString("email"));
        usuario.setDocumento(rs.getString("documento"));
        usuario.setEstado(EstadoUsuario.valueOf(rs.getString("estado")));
        return usuario;
    }
}

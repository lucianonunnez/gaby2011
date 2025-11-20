package dao;

import model.Usuario;

import java.sql.ResultSet;
import java.util.List;

public interface UsuarioDAO {
    // CRUD básico
    void save(Usuario usuario) throws Exception;
    Usuario findById(int id) throws Exception;
    List<Usuario> findAll() throws Exception;
    void update(Usuario usuario) throws Exception;
    void deleteLogical(int id) throws Exception;

    // Búsqueda por email (login)
    Usuario findByEmail(String email) throws Exception;
    // Validación de credenciales
    Usuario validateCredentials(String email, String contrasenia) throws Exception;
    // Cambiar contraseña
    void changePassword(int id, String nuevaContrasenia) throws Exception;
    // Ver datos NO sensibles
    Usuario viewNonSensitiveData(int id) throws Exception;

    //Método auxiliar
    // Mapear Usuario desde ResultSet
    Usuario mapUsuarioFromRS(ResultSet rs) throws Exception;
    // Mapeo sin contraseña
    Usuario mapUsuarioWithoutPasswordFromRS(ResultSet rs) throws Exception;
}

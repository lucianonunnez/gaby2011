package service;

import model.Rol;
import model.Usuario;

import java.util.List;

public interface UsuarioService {
    void guardarUsuario(Usuario usuario);
    Usuario buscarUsuarioPorId(int id);
    List<Usuario> listarUsuarios();
    void actualizarUsuario(Usuario usuario);
    void eliminarUsuario(int id);
    Usuario buscarUsuarioPorEmail(String email);
    Usuario validarCredenciales(String email, String contrasenia)  throws Exception;
    void cambiarContrasenia(int idUsuario, String nuevaContrasenia);
    Usuario verDatosBasicos(int idUsuario);
}

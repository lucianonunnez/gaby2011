package auth;

import lombok.Getter;
import model.Funcionario;
import model.Usuario;
import org.apache.log4j.Logger;
import service.UsuarioService;
import service.impl.UsuarioServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
// Singleton, maneja el login/logout usando UsuarioDAO (validateCredentials())
public class AuthManager {
    private static final Logger logger = Logger.getLogger(AuthManager.class);
    private static final int TIEMPO_INACTIVIDAD_MINUTOS = 15;
    private static AuthManager instance;
    private Usuario usuarioActual;
    private LocalDateTime ultimaActividad;
    private final UsuarioService usuarioService;

    private AuthManager(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public static AuthManager getInstance() {
        if (instance == null) {
            try{
                instance = new AuthManager(new UsuarioServiceImpl());
            } catch (Exception e) {
                logger.fatal("No se pudo inicializar AuthManager", e);
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public boolean login(String email, String contrasenia) {
        try {
            usuarioActual = usuarioService.validarCredenciales(email, contrasenia);
            ultimaActividad = LocalDateTime.now();
            logger.info("Usuaario autenticado: " + email);
            return true;
        } catch (Exception e) {
            logger.error("Error en autenticación para el usuario: " + email, e);
            return false;
        }
    }

    public void logout() {
        if(usuarioActual != null) {
            logger.info("Usuario " + usuarioActual.getEmail() + " ha cerrado sesión.");
        }
        usuarioActual = null;
        ultimaActividad = null;
    }

    // Método para actualizar la última actividad - llamado en cada acción del usuario
    public void actualizarActividad() {
        if(usuarioActual != null) {
            ultimaActividad = LocalDateTime.now();
        }
    }

    private void verificarInactividad() {
        if (ultimaActividad == null) {
            System.out.println("[DEBUG] ultimaActividad es null, retornando.");
            return;
        }
        long minutosInactivo = ChronoUnit.MINUTES.between(ultimaActividad, LocalDateTime.now());
        System.out.println("[DEBUG] Minutos inactivo: " + minutosInactivo);
        if (minutosInactivo >= TIEMPO_INACTIVIDAD_MINUTOS) {
            logger.warn("Sesión cerrada por inactividad: " + usuarioActual.getEmail() + "(inactivo por " + minutosInactivo + " minutos)");
            logout();
        }
    }

    public boolean isAutenticado() {
        System.out.println("[DEBUG] Verificando autenticación...");
        verificarInactividad();
        boolean autenticado = usuarioActual != null;
        System.out.println("[DEBUG] Usuario autenticado: " + autenticado);
        return autenticado;
    }

    public String getRolActual() {
        return usuarioActual != null && usuarioActual instanceof Funcionario ? ((Funcionario) usuarioActual).getRol().getNombre() : null;
    }
}
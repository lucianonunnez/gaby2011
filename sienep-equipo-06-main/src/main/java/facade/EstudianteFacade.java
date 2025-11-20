package facade;

import auth.AuthManager;
import model.Estudiante;
import model.Instancia;
import service.InstanciaService;
import service.UsuarioService;

import java.util.List;
import java.util.Scanner;

public class EstudianteFacade implements MenuFacade {
    private final AuthManager authManager;
    private final UsuarioService usuarioService;
    private final Scanner scanner = new Scanner(System.in);
    private final InstanciaService instanciaService;

    public EstudianteFacade( AuthManager authManager,
                             UsuarioService usuarioService,
                             InstanciaService instanciaService) {
        this.authManager = authManager;
        this.usuarioService = usuarioService;
        this.instanciaService = instanciaService;
    }

    @Override
    public void mostrarMenu() {
        System.out.println("=== Menú Estudiante ===");
        System.out.println("1. Ver mis datos");
        System.out.println("2. Ver mis instancias");
        System.out.println("3. Cambiar mi contraseña");
        System.out.println("0. Cerrar sesión");
        System.out.println("Ingrese una opción: ");
    }

    @Override
    public void ejecutarOpcion(int opcion) {
        Estudiante estudiante = (Estudiante) authManager.getUsuarioActual();
        try {
            switch (opcion) {
                case 1 ->{
                    // Visualización de datos no sensibles
                    System.out.println("=== Mis datos ===");
                    System.out.println("Nombre: " + estudiante.getNombre());
                    System.out.println("Apellido: " + estudiante.getApellido());
                    System.out.println("Email: " + estudiante.getEmail());
                    System.out.println("Documento: " +estudiante.getDocumento());
                    System.out.println("Carrera: " + estudiante.getCarrera());
                    System.out.println("Grupo: " + estudiante.getGrupo());
                }
                case 2 -> {
                    try {
                        List<Instancia> instancias = instanciaService.getInstanciasByEstudiante(estudiante.getId());
                        System.out.println("=== Mis instancias ===");
                        if(instancias.isEmpty()) {
                            System.out.println("No tienes instancias asignadas.");
                        } else {
                            for(Instancia i : instancias) {
                                System.out.println("ID: " + i.getId() + ", Título: " + i.getTitulo() + ", Fecha: " + i.getFechaHora() + ", Canal: " + i.getCanal());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error al obtener las instancias: " + e.getMessage());
                    }
                }
                case 3 -> {
                    System.out.println("=== Cambiar mi contraseña ===");
                    System.out.println("Contraseña actual: ");
                    String contraseniaActual = scanner.nextLine();

                    // Verificación de la contraseña actual
                    try {
                        authManager.getUsuarioService().validarCredenciales(
                                estudiante.getEmail(), contraseniaActual
                        );
                    } catch (Exception e) {
                        System.out.println("Contraseña actual incorrecta.");
                        return;
                    }

                    System.out.println("Nueva contraseña: ");
                    String nuevaContrasenia = scanner.nextLine();
                    System.out.println("Confirmar nueva contraseña: ");
                    String confirmarContrasenia = scanner.nextLine();

                    if(!nuevaContrasenia.equals(confirmarContrasenia)) {
                        System.out.println("Las contraseñas no coinciden.");
                        return;
                    }

                    if(nuevaContrasenia.length() < 7) {
                        System.out.println("La contraseña debe tener al menos 7 caracteres.");
                        return;
                    }

                    // se cambia la contraseña usando el service
                    usuarioService.cambiarContrasenia(estudiante.getId(), nuevaContrasenia);
                    System.out.println("Contraseña cambiada exitosamente.");
                }
                case 0 -> {
                    authManager.logout();
                    System.out.println("Sesión cerrada. Regresando al login...");
                }
                default -> {
                    System.out.println("Opción inválida. Intente de nuevo.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al ejecutar la opción: " + e.getMessage());
        }
    }
}

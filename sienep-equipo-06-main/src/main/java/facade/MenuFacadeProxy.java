package facade;

import auth.AuthManager;
import dao.impl.*;
import service.*;
import service.impl.*;

import java.util.Scanner;

public class MenuFacadeProxy implements MenuFacade {
    private MenuFacade realFacade;
    private final AuthManager authManager;
    private final EstudianteService estudianteService;
    private final InstanciaService instanciaService;
    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final CategoriaService categoriaService;
    private final FuncionarioService funcionarioService;
    private final PermisoService permisoService;
    private final Scanner scanner = new Scanner(System.in);

    public MenuFacadeProxy() throws Exception {
        this.authManager = AuthManager.getInstance();
        // Instancia DAOs
        EstudianteDAOImpl estudianteDAO = new EstudianteDAOImpl();
        InstanciaDAOImpl instanciaDAO = new InstanciaDAOImpl();
        IncidenciaDAOImpl incidenciaDAO = new IncidenciaDAOImpl();
        InstanciaComunDAOImpl instanciaComunDAO = new InstanciaComunDAOImpl();
        UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();
        RolDAOImpl rolDAO = new RolDAOImpl();
        CategoriaDAOImpl categoriaDAO = new CategoriaDAOImpl();
        // Instancia Services con inyección
        this.estudianteService = new EstudianteServiceImpl(estudianteDAO);
        this.instanciaService = new InstanciaServiceImpl(instanciaDAO, incidenciaDAO, instanciaComunDAO);
        this.usuarioService = new UsuarioServiceImpl();
        this.rolService = new RolServiceImpl();
        this.categoriaService = new CategoriaServiceImpl();
        this.funcionarioService = new FuncionarioServiceImpl();
        this.permisoService = new PermisoServiceImpl();
    }

    @Override
    public void mostrarMenu() {
        if(!authManager.isAutenticado()) {
            mostrarLogin();
            return;
        }

        // Actualizar actividad al mostrar el menú
        authManager.actualizarActividad();

        String rol = authManager.getRolActual();
        if ("ADMINISTRADOR".equals(rol)) {
            realFacade = new AdminFacade(authManager, usuarioService, rolService, categoriaService,instanciaService, estudianteService, funcionarioService, permisoService);
        } else if ("PSICOPEDAGOGO".equals(rol)) {
            realFacade = new PsicopedagogoFacade(authManager, instanciaService, estudianteService,categoriaService);
        } else if ("TUTOR".equals(rol)) {
            realFacade = new TutorFacade(authManager, instanciaService, estudianteService);
        } else if ("DOCENTE".equals(rol)) {
            realFacade = new DocenteFacade(authManager, instanciaService, estudianteService);
        } else {
            realFacade = new EstudianteFacade(authManager, usuarioService, instanciaService);
        }
        realFacade.mostrarMenu();
    }

    @Override
    public void ejecutarOpcion(int opcion) {
        if(!authManager.isAutenticado()) {
            System.out.println("Su sesión ha expirado por inactividad. Por favor, inicie sesión nuevamente.");
            return;
        }
        // actualización de la última actividad en cada interacción
        authManager.actualizarActividad();
        realFacade.ejecutarOpcion(opcion);
    }

    private void mostrarLogin() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Bienvenido a SIENEP (equipo 06) ===");
        System.out.print("Email: ");
        String email = scanner.nextLine();

        if (email.startsWith(" ") || email.endsWith(" ")) {
            email = email.trim();
            System.out.println("Email ajustado por espacios en blanco.");  // Opcional: feedback al usuario
        }

        System.out.print("Contraseña: ");
        String contrasenia = scanner.nextLine();

        if (authManager.login(email, contrasenia)) {
            System.out.println("Login exitoso. Redirigiendo...");
            mostrarMenu();  // Recarga menú
        } else {
            System.out.println("Credenciales inválidas. Intente de nuevo.");
            mostrarLogin();  // Retry
        }
    }
}
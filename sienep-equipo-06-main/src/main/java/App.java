import facade.MenuFacade;
import facade.MenuFacadeProxy;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        try {
            MenuFacade proxy = new MenuFacadeProxy();
            Scanner scanner = new Scanner(System.in);

            while (true) {
                proxy.mostrarMenu();

                try {
                    int opcion = Integer.parseInt(scanner.nextLine());
                    proxy.ejecutarOpcion(opcion);

                    if(opcion == 0) {
                        System.out.println("\n¡Hasta pronto!");
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("\n Por favor ingrese un número válido.\n");
                }

                // Pequeña pausa para que el usuario vea los resultados
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
            }
        } catch (Exception e) {
            System.err.println("Error crítico en la aplicación:");
            e.printStackTrace();
        }
    }
}
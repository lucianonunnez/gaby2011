# SIENEP - Sistema Integral para Estudiantes con Necesidades Educativas Peronsalizadas

SIENEP es una plataforma diseñada para gestionar y apoyar a estudiantes con necesidades educativas personalizadas. 
Proporciona herramientas para la administración de planes educativos, seguimiento del progreso y 
comunicación entre educadores, estudiantes y familias.

## Tecnologías Utilizadas
- **Lenguaje**: Java
- **Framework**: Maven
- **Base de Datos**: PostgreSQL
- **Dependencias principales**: 
  - `Lombok` para reducir el código boilerplate.
  - `JUnit` para pruebas unitarias.
  - `Log4j` para registro de eventos.
  - `Driver JDBC PostgreSQL` para la conexión a la base de datos.
  - `Google API Client` para integraciones con servicios de Google.

## Estructura del Proyecto
- `src/main/java`: Contiene el código fuente de la aplicación.
- `src/main/resources`: Archivos de configuración y recursos estáticos.
- `src/test/java`: Pruebas unitarias
- `pom.xml`: Archivo de configuración de Maven.
- `README.md`: Documentación del proyecto.

## Instalación
1. Clonar este repositorio:
    ````bash
   git clone https://git.utec.edu.uy/angel.gutierrez/sienep-equipo-06.git
    ````
2. Asegúrate de tener Java 21 (o superior) y Maven instalados en tu sistema.
3. Configura la base de datos PostgreSQL y actualiza las credenciales en el archivo de configuración.
4. Construye el proyecto usando Maven:
    ````bash
   mvn clean install
    ````
## Uso
1. Inicia sesión con las credenciales correspondientes
2. Accede al menú principal y selecciona las opciones disponibles según el rol (estudiante, psicopedagogo, tutor, docente, administrador).
3. Sigue las instrucciones en pantalla para completar las acciones deseadas.

## Colaboradores
| Nombre | Rol/Contribución |
|--------|------------------|
| Gabriela Malacre | Documentación del proyecto, desarrollo de las clases base | 
| Valentin Fernández | Desarrollo de las interfaces DAOs y sus implementaciones |
| Kevin Magallanes | Desarrollo de las interfaces Services y sus implementaciones |
| Angel Gutiérrez | Integración de depedencias, desarrollo los facades, conexión a la base de datos y pruebas unitarias |



package dao.impl;

import model.Estudiante;
import model.enums.EstadoUsuario;
import model.enums.TipoUsuario;
import dao.UsuarioDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EstudianteDAOImplTest {

    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private UsuarioDAO mockUsuarioDAO;
    private EstudianteDAOImpl estudianteDAO;

    @BeforeEach
    void setUp() throws Exception {
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        mockUsuarioDAO = mock(UsuarioDAO.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockStatement);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        doNothing().when(mockConnection).setAutoCommit(anyBoolean());
        doNothing().when(mockConnection).commit();
        doNothing().when(mockConnection).rollback();

        estudianteDAO = new EstudianteDAOImpl(mockConnection, mockUsuarioDAO) {
            @Override
            public Estudiante findById(int id) {
                if (id == 1) {
                    Estudiante estudiante = new Estudiante();
                    estudiante.setId(1);
                    estudiante.setNombre("Juan");
                    estudiante.setApellido("Pérez");
                    estudiante.setEmail("juan@example.com");
                    estudiante.setContrasenia("pass123");
                    estudiante.setDocumento("12345678");
                    estudiante.setEstado(EstadoUsuario.ACTIVO);
                    estudiante.setTipo(TipoUsuario.ESTUDIANTE);
                    estudiante.setMotivoDerivacion("Derivación test");
                    estudiante.setCarrera("Ingeniería");
                    estudiante.setGrupo("Grupo A");
                    estudiante.setTelefono("123456789");
                    estudiante.setCalle("Calle Falsa");
                    estudiante.setNumeroPuerta("123");
                    estudiante.setFechaNacimiento(LocalDate.of(2000, 1, 1));  // No null
                    estudiante.setFoto("foto.jpg");
                    estudiante.setSistemaSalud("Público");
                    estudiante.setComentariosGenerales("Comentarios test");
                    estudiante.setEstadoSalud("Bueno");
                    estudiante.setObservacionesConfidenciales(List.of("Obs1", "Obs2"));
                    return estudiante;
                }
                return null;
            }
        };
    }

    @Test
    void testSave() throws Exception {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(1);
        estudiante.setNombre("Juan");
        estudiante.setApellido("Pérez");
        estudiante.setMotivoDerivacion("Derivación test");
        estudiante.setCarrera("Ingeniería");
        estudiante.setGrupo("Grupo A");
        estudiante.setTelefono("123456789");
        estudiante.setCalle("Calle Falsa");
        estudiante.setNumeroPuerta("123");
        estudiante.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        estudiante.setFoto("foto.jpg");
        estudiante.setSistemaSalud("Público");
        estudiante.setComentariosGenerales("Comentarios test");
        estudiante.setEstadoSalud("Bueno");
        estudiante.setObservacionesConfidenciales(List.of("Obs1", "Obs2"));

        doNothing().when(mockUsuarioDAO).save(estudiante);
        estudianteDAO.save(estudiante);

        verify(mockUsuarioDAO).save(estudiante);
        verify(mockStatement, times(1)).setInt(1, 1);
        verify(mockStatement, times(1)).executeUpdate();
        verify(mockConnection).commit();
    }

    @Test
    void testFindById() throws Exception {
        Estudiante estudiante = estudianteDAO.findById(1);

        assertNotNull(estudiante);
        assertEquals(1, estudiante.getId());
        assertEquals("Juan", estudiante.getNombre());
        assertEquals("Ingeniería", estudiante.getCarrera());
    }

    @Test
    void testFindAll() throws Exception {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(1);

        List<Estudiante> estudiantes = estudianteDAO.findAll();
        assertNotNull(estudiantes);
        assertEquals(1, estudiantes.size());
        assertEquals(1, estudiantes.get(0).getId());
    }

    @Test
    void testUpdate() throws Exception {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(1);
        estudiante.setNombre("Juan");
        estudiante.setApellido("Pérez");
        estudiante.setEmail("juan@example.com");
        estudiante.setContrasenia("pass123");
        estudiante.setDocumento("12345678");
        estudiante.setEstado(EstadoUsuario.ACTIVO);
        estudiante.setTipo(TipoUsuario.ESTUDIANTE);
        estudiante.setMotivoDerivacion("Nuevo motivo");
        estudiante.setCarrera("Ingeniería");
        estudiante.setGrupo("Grupo A");
        estudiante.setTelefono("123456789");
        estudiante.setCalle("Calle Falsa");
        estudiante.setNumeroPuerta("123");
        estudiante.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        estudiante.setFoto("foto.jpg");
        estudiante.setSistemaSalud("Público");
        estudiante.setComentariosGenerales("Comentarios test");
        estudiante.setEstadoSalud("Bueno");
        estudiante.setObservacionesConfidenciales(List.of("Obs1", "Obs2"));

        // mockear UsuarioDAO
        doNothing().when(mockUsuarioDAO).update(estudiante);
        estudianteDAO.update(estudiante);

        verify(mockUsuarioDAO).update(estudiante);
        verify(mockStatement).setString(1, "Nuevo motivo");
        verify(mockStatement).setInt(13, 1);
        verify(mockConnection).commit();
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(mockUsuarioDAO).deleteLogical(1);
        estudianteDAO.delete(1);

        verify(mockUsuarioDAO).deleteLogical(1);
    }

    @Test
    void testFindByCarrera() throws Exception {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(1);

        List<Estudiante> estudiantes = estudianteDAO.findByCarrera("Ingeniería");

        assertNotNull(estudiantes);
        assertEquals(1, estudiantes.size());
        assertEquals("Ingeniería", estudiantes.get(0).getCarrera());
    }
}

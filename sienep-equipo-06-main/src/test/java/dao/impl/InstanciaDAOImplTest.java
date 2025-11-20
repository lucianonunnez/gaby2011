package dao.impl;

import dao.EstudianteDAO;
import dao.GenericDAO;
import dao.UsuarioDAO;
import model.*;
import model.enums.Canal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InstanciaDAOImplTest {

    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private GenericDAO<Categoria> mockCategoriaDAO;
    private EstudianteDAO mockEstudianteDAO;
    private UsuarioDAO mockUsuarioDAO;
    private InstanciaDAOImpl instanciaDAO;

    @BeforeEach
    void setUp() throws Exception {
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        mockCategoriaDAO = mock(GenericDAO.class);
        mockEstudianteDAO = mock(EstudianteDAO.class);
        mockUsuarioDAO = mock(UsuarioDAO.class);

        // Mock para prepareStatement(String) - cubre la mayoría de métodos
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        // Mock para prepareStatement(String, int) - para save con generated keys
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockStatement);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        // Mock para executeQuery() - necesario para métodos como findAll, findById, etc.
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        // Sobrescribimos el DAO para usar mocks y evitar llamadas reales
        instanciaDAO = new InstanciaDAOImpl(mockConnection, mockCategoriaDAO, mockEstudianteDAO, mockUsuarioDAO) {
            @Override
            protected Instancia mapInstanciaFromRS(ResultSet rs) throws Exception {
                // Devuelve una Instancia mockeada para evitar consultas reales
                Instancia instancia = new InstanciaComun();
                instancia.setId(1);
                instancia.setTitulo("Instancia Test");
                instancia.setCodigo("CODE123");
                instancia.setFechaHora(LocalDateTime.of(2023, 10, 1, 10, 0));
                instancia.setCanal(Canal.EMAIL);
                instancia.setComentario("Comentario test");
                instancia.setConfidencial(false);
                instancia.setCategoria(new Categoria("Categoria Test", "Categoria Test"));
                instancia.setEstudianteAsociado(new Estudiante());
                instancia.setCreador(new Funcionario());
                instancia.setTipo("COMUN");
                instancia.setGoogleCalendarEventId("event123");
                return instancia;
            }
        };
    }

    @Test
    void testSave() throws Exception {
        Instancia instancia = new InstanciaComun();
        instancia.setTitulo("Instancia Test");
        instancia.setCodigo("CODE123");
        instancia.setFechaHora(LocalDateTime.of(2023, 10, 1, 10, 0));
        instancia.setCanal(Canal.EMAIL);  // Válido
        instancia.setComentario("Comentario test");
        instancia.setConfidencial(false);
        instancia.setCategoria(new Categoria("Categoria Test", "Categoria Test"));  // ID = 0 por defecto
        instancia.setEstudianteAsociado(new Estudiante());  // ID = 0
        instancia.setCreador(new Funcionario());  // ID = 0
        instancia.setTipo("COMUN");
        instancia.setGoogleCalendarEventId("event123");
        instanciaDAO.save(instancia);
        verify(mockStatement).setString(1, "Instancia Test");
        verify(mockStatement).setString(2, "CODE123");
        verify(mockStatement).setTimestamp(3, Timestamp.valueOf(LocalDateTime.of(2023, 10, 1, 10, 0)));
        verify(mockStatement).setString(4, "EMAIL");
        verify(mockStatement).setString(5, "Comentario test");
        verify(mockStatement).setBoolean(6, false);
        verify(mockStatement).setInt(7, 0);
        verify(mockStatement).setInt(8, 0);  // ID de Estudiante
        verify(mockStatement).setInt(9, 0);  // ID de Creador
        verify(mockStatement).setString(10, "COMUN");
        verify(mockStatement).setString(11, "event123");
        verify(mockStatement).executeUpdate();
        assertEquals(1, instancia.getId());
    }

    @Test
    void testFindById() throws Exception {
        Instancia instancia = instanciaDAO.findById(1);

        assertNotNull(instancia);
        assertEquals(1, instancia.getId());
        assertEquals("Instancia Test", instancia.getTitulo());
        assertEquals("COMUN", instancia.getTipo());
    }

    @Test
    void testFindAll() throws Exception {
        // Mockea el ResultSet para devolver múltiples filas
        when(mockResultSet.next()).thenReturn(true, false);

        List<Instancia> instancias = instanciaDAO.findAll();

        assertNotNull(instancias);
        assertEquals(1, instancias.size());
        assertEquals("Instancia Test", instancias.get(0).getTitulo());
        assertEquals("COMUN", instancias.get(0).getTipo());
    }

    @Test
    void testUpdate() throws Exception {
        Instancia instancia = new InstanciaComun();
        instancia.setId(1);
        instancia.setTitulo("Instancia Actualizada");
        instancia.setCodigo("CODE456");
        instancia.setFechaHora(LocalDateTime.of(2023, 10, 2, 11, 0));
        instancia.setCanal(Canal.EMAIL);
        instancia.setComentario("Comentario actualizado");
        instancia.setConfidencial(true);
        instancia.setCategoria(new Categoria("Categoria nueva", "Categoria Nueva"));  // ID = 0
        instancia.setEstudianteAsociado(new Estudiante());  // ID = 0
        instancia.setCreador(new Funcionario());  // ID = 0
        instancia.setTipo("COMUN");
        instanciaDAO.update(instancia);
        verify(mockStatement).setString(1, "Instancia Actualizada");
        verify(mockStatement).setString(2, "CODE456");
        verify(mockStatement).setTimestamp(3, Timestamp.valueOf(LocalDateTime.of(2023, 10, 2, 11, 0)));
        verify(mockStatement).setString(4, "EMAIL");
        verify(mockStatement).setString(5, "Comentario actualizado");
        verify(mockStatement).setBoolean(6, true);
        verify(mockStatement).setInt(7, 0);
        verify(mockStatement).setInt(8, 0);
        verify(mockStatement).setInt(9, 0);
        verify(mockStatement).setString(10, "COMUN");
        verify(mockStatement).setInt(11, 1);
        verify(mockStatement).executeUpdate();
    }

    @Test
    void testDelete() throws Exception {
        when(mockStatement.executeUpdate()).thenReturn(1);

        instanciaDAO.delete(1);

        verify(mockStatement).setInt(1, 1);
        verify(mockStatement).executeUpdate();
    }

    @Test
    void testFindByEstudiante() throws Exception {
        // Mockea el ResultSet para devolver filas
        when(mockResultSet.next()).thenReturn(true, false);

        List<Instancia> instancias = instanciaDAO.findByEstudiante(1);

        assertNotNull(instancias);
        assertEquals(1, instancias.size());
        assertEquals("Instancia Test", instancias.get(0).getTitulo());
        assertEquals("COMUN", instancias.get(0).getTipo());
    }

    @Test
    void testGetInstaciasByCategoria() throws Exception {
        // Mockea el ResultSet para devolver filas
        when(mockResultSet.next()).thenReturn(true, false);

        List<Instancia> instancias = instanciaDAO.getInstaciasByCategoria(1);

        assertNotNull(instancias);
        assertEquals(1, instancias.size());
        assertEquals("Instancia Test", instancias.get(0).getTitulo());
        assertEquals("COMUN", instancias.get(0).getTipo());
    }
}

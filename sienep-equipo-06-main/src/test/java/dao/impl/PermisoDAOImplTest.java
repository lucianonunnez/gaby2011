package dao.impl;

import model.Permiso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PermisoDAOImplTest {
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private PermisoDAOImpl permisoDAO;

    @BeforeEach
    public void setUp() throws Exception {
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        permisoDAO = new PermisoDAOImpl(mockConnection);
    }

    @Test
    void testFindById() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("nombre")).thenReturn("READ");

        Permiso permiso = permisoDAO.findById(1);

        assertNotNull(permiso);
        assertEquals(1, permiso.getId());
        assertEquals("READ", permiso.getNombre());

        verify(mockStatement).setInt(1, 1);
        verify(mockStatement).executeQuery();
    }

    @Test
    void testFindAll() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("nombre")).thenReturn("READ", "WRITE");

        List<Permiso> permisos = permisoDAO.findAll();

        assertNotNull(permisos);
        assertEquals(2, permisos.size());
        assertEquals("READ", permisos.get(0).getNombre());
        assertEquals("WRITE", permisos.get(1).getNombre());

        verify(mockStatement).executeQuery();
    }

    @Test
    void testUpdate() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        Permiso permisoToUpdate = new Permiso(1, "EXECUTE");

        permisoDAO.update(permisoToUpdate);

        verify(mockStatement).setString(1, "EXECUTE");
        verify(mockStatement).setInt(2, 1);
        verify(mockStatement).executeUpdate();
    }

    @Test
    void testDelete() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        permisoDAO.delete(1);

        verify(mockStatement).setInt(1, 1);
        verify(mockStatement).executeUpdate();
    }
}

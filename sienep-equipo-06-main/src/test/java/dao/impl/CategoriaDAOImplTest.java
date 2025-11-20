package dao.impl;

import model.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoriaDAOImplTest {

    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private CategoriaDAOImpl categoriaDAO;

    @BeforeEach
    void setUp() throws Exception {
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        categoriaDAO = new CategoriaDAOImpl(mockConnection);
    }

    @Test
    void testSave() throws Exception {
        Categoria categoria = new Categoria("Educación", "Categoría educativa");

        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockStatement);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        categoriaDAO.save(categoria);

        verify(mockStatement).setString(1, "Educación");
        verify(mockStatement).setString(2, "Categoría educativa");
        verify(mockStatement).executeUpdate();
        assertEquals(1, categoria.getId());
    }

    @Test
    void testFindById() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("nombre")).thenReturn("Deportes");
        when(mockResultSet.getString("descripcion")).thenReturn("Categoría de deportes");

        Categoria categoria = categoriaDAO.findById(1);

        assertNotNull(categoria);
        assertEquals(1, categoria.getId());
        assertEquals("Deportes", categoria.getNombre());
        assertEquals("Categoría de deportes", categoria.getDescripcion());

        verify(mockStatement).setInt(1, 1);
        verify(mockStatement).executeQuery();
    }

    @Test
    void testFindAll() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("nombre")).thenReturn("Deportes", "Ciencia");
        when(mockResultSet.getString("descripcion")).thenReturn("Categoría de deportes", "Categoría de ciencia");

        List<Categoria> categorias = categoriaDAO.findAll();

        assertNotNull(categorias);
        assertEquals(2, categorias.size());
        assertEquals("Deportes", categorias.get(0).getNombre());
        assertEquals("Ciencia", categorias.get(1).getNombre());

        verify(mockStatement).executeQuery();
    }

    @Test
    void testUpdate() throws Exception {
        Categoria categoria = new Categoria(1, "Arte", "Categoría de arte");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        categoriaDAO.update(categoria);

        verify(mockStatement).setString(1, "Arte");
        verify(mockStatement).setString(2, "Categoría de arte");
        verify(mockStatement).setInt(3, 1);
        verify(mockStatement).executeUpdate();
    }

    @Test
    void testDelete() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        categoriaDAO.delete(1);

        verify(mockStatement).setInt(1, 1);
        verify(mockStatement).executeUpdate();
    }
}

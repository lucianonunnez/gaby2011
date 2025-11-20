package dao.impl;

import model.Permiso;
import model.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RolDAOImplTest {

    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;
    @InjectMocks
    private RolDAOImpl rolDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        rolDAO = new RolDAOImpl(connection);
    }

    @Test
    void testSave() throws Exception {
        Rol rol = new Rol("ADMIN", List.of(new Permiso(1, "GESTIONAR")));

        // Mock prepareStatement
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);

        // Mock executeUpdate
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Mock generated keys
        ResultSet generatedKeys = mock(ResultSet.class);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(55);

        when(connection.prepareStatement(contains("rol_permisos"))).thenReturn(preparedStatement);

        rolDAO.save(rol);

        assertEquals(55, rol.getId());
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testFindById() throws Exception {
        String sql = "SELECT id, nombre FROM proyecto.roles WHERE id = ?";
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("nombre")).thenReturn("ADMIN");

        // Stub para loadPermisos
        String sqlPermisos =
                "SELECT p.id, p.nombre FROM proyecto.permisos p " +
                        "JOIN proyecto.rol_permisos rp ON p.id = rp.id_permiso " +
                        "WHERE rp.id_rol = ?";

        PreparedStatement ps2 = mock(PreparedStatement.class);
        ResultSet rs2 = mock(ResultSet.class);

        when(connection.prepareStatement(sqlPermisos)).thenReturn(ps2);
        when(ps2.executeQuery()).thenReturn(rs2);

        when(rs2.next()).thenReturn(false);

        Rol rol = rolDAO.findById(1);

        assertNotNull(rol);
        assertEquals(1, rol.getId());
        assertEquals("ADMIN", rol.getNombre());
    }

    @Test
    void testFindByNombre() throws Exception {
        String sql = "SELECT id, nombre FROM proyecto.roles WHERE nombre = ?";
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);

        // devuelve el ResultSet
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("nombre")).thenReturn("ADMIN");

        // stubear loadPermisos → usa otro prepareStatement
        String sqlPermisos =
                "SELECT p.id, p.nombre FROM proyecto.permisos p " +
                        "JOIN proyecto.rol_permisos rp ON p.id = rp.id_permiso " +
                        "WHERE rp.id_rol = ?";
        PreparedStatement ps2 = mock(PreparedStatement.class);
        ResultSet rs2 = mock(ResultSet.class);

        when(connection.prepareStatement(sqlPermisos)).thenReturn(ps2);
        when(ps2.executeQuery()).thenReturn(rs2);

        when(rs2.next()).thenReturn(false);

        Rol rol = rolDAO.findByNombre("ADMIN");

        assertNotNull(rol);
        assertEquals(1, rol.getId());
        assertEquals("ADMIN", rol.getNombre());
    }

    @Test
    void testUpdate() throws Exception {
        Rol rol = new Rol(5, "NuevoNombre", List.of(new Permiso(2, "CREAR")));

        when(connection.prepareStatement(startsWith("UPDATE"))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // deletePermisos()
        PreparedStatement psDelete = mock(PreparedStatement.class);
        when(connection.prepareStatement(startsWith("DELETE FROM proyecto.rol_permisos")))
                .thenReturn(psDelete);

        // savePermisos()
        PreparedStatement psInsert = mock(PreparedStatement.class);
        when(connection.prepareStatement(startsWith("INSERT INTO proyecto.rol_permisos")))
                .thenReturn(psInsert);

        rolDAO.update(rol);

        verify(preparedStatement).executeUpdate();
        verify(psDelete).executeUpdate();
        verify(psInsert, atLeastOnce()).addBatch();
    }


    @Test
    void testDelete() throws Exception {

        when(connection.prepareStatement(startsWith("DELETE FROM proyecto.roles"))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        rolDAO.delete(8);

        verify(preparedStatement, times(1)).executeUpdate();
    }


    @Test
    void testLoadPermisos() throws Exception {

        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(connection.prepareStatement(startsWith("SELECT p.id"))).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("nombre")).thenReturn("GESTIONAR");

        List<Permiso> permisos = rolDAO.loadPermisos(10);

        assertEquals(1, permisos.size());
        assertEquals("GESTIONAR", permisos.get(0).getNombre());
    }
}

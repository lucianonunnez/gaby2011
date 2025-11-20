package dao.impl;

import dao.GenericDAO;
import dao.UsuarioDAO;
import model.Funcionario;
import model.Rol;
import model.enums.EstadoUsuario;
import model.enums.TipoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FuncionarioDAOImplTest {

    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private UsuarioDAO mockUsuarioDAO;
    private GenericDAO<Rol> mockRolDAO;
    private FuncionarioDAOImpl funcionarioDAO;

    @BeforeEach
    void setUp() throws Exception {
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        mockUsuarioDAO = mock(UsuarioDAO.class);
        mockRolDAO = mock(GenericDAO.class);
        // Mock para prepareStatement(String) - cubre la mayoría de métodos
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        // Mock para prepareStatement(String, int) - si se usa en algún save/update con generated keys
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockStatement);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        // Quita esta línea: when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);
        // Mock para executeQuery() - necesario para métodos como findAll, findByRol, etc.
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        // Mock para transacciones
        doNothing().when(mockConnection).setAutoCommit(anyBoolean());
        doNothing().when(mockConnection).commit();
        doNothing().when(mockConnection).rollback();
        // Sobrescribimos el DAO para usar mocks y evitar llamadas reales
        funcionarioDAO = new FuncionarioDAOImpl(mockConnection, mockUsuarioDAO, mockRolDAO) {
            @Override
            public Funcionario findById(int id) {
                // Devuelve un Funcionario mockeado para evitar consultas reales
                if (id == 1) {
                    Funcionario funcionario = new Funcionario();
                    funcionario.setId(1);
                    funcionario.setNombre("Ana");
                    funcionario.setApellido("Gómez");
                    funcionario.setEmail("ana@example.com");
                    funcionario.setContrasenia("pass123");
                    funcionario.setDocumento("87654321");
                    funcionario.setEstado(EstadoUsuario.ACTIVO);
                    funcionario.setTipo(TipoUsuario.FUNCIONARIO);
                    // Rol mockeado
                    Rol rol = new Rol(1, "ADMINISTRADOR", List.of());
                    funcionario.setRol(rol);
                    return funcionario;
                }
                return null;
            }
        };
    }

    @Test
    void testFindById() throws Exception {
        Funcionario funcionario = funcionarioDAO.findById(1);

        assertNotNull(funcionario);
        assertEquals(1, funcionario.getId());
        assertEquals("Ana", funcionario.getNombre());
        assertNotNull(funcionario.getRol());
        assertEquals("ADMINISTRADOR", funcionario.getRol().getNombre());
    }

    @Test
    void testFindAll() throws Exception {
        // Mockea el ResultSet para devolver IDs
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        List<Funcionario> funcionarios = funcionarioDAO.findAll();
        assertNotNull(funcionarios);
        assertEquals(1, funcionarios.size());
        assertEquals("Ana", funcionarios.get(0).getNombre());
    }

    @Test
    void testUpdate() throws Exception {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(1);
        funcionario.setNombre("Ana");
        funcionario.setApellido("Gómez");
        funcionario.setEmail("ana@example.com");
        funcionario.setContrasenia("pass123");
        funcionario.setDocumento("87654321");
        funcionario.setEstado(EstadoUsuario.ACTIVO);
        funcionario.setTipo(TipoUsuario.FUNCIONARIO);
        funcionario.setRol(new Rol(2, "DOCENTE", List.of()));

        // Mockea UsuarioDAO
        doNothing().when(mockUsuarioDAO).update(funcionario);
        funcionarioDAO.update(funcionario);

        verify(mockUsuarioDAO).update(funcionario);
        verify(mockStatement).setInt(1, 2);  // ID del rol
        verify(mockStatement).setInt(2, 1);  // ID del funcionario
        verify(mockConnection).commit();
    }

    @Test
    void testDelete() throws Exception {
        // Mockea UsuarioDAO
        doNothing().when(mockUsuarioDAO).deleteLogical(1);
        funcionarioDAO.delete(1);

        verify(mockUsuarioDAO).deleteLogical(1);
    }

    @Test
    void testAssignRol() throws Exception {
        Rol rol = new Rol(1, "ADMINISTRADOR", List.of());

        funcionarioDAO.assignRol(1, rol);

        verify(mockStatement).setInt(1, 1);  // ID del rol
        verify(mockStatement).setInt(2, 1);  // ID del funcionario
        verify(mockStatement).executeUpdate();
    }

}

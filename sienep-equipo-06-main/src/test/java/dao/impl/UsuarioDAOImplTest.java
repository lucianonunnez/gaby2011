package dao.impl;

import exception.AutenticacionFallidaException;
import model.Estudiante;
import model.Funcionario;
import model.Usuario;
import model.enums.EstadoUsuario;
import model.enums.TipoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsuarioDAOImplTest {

    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private UsuarioDAOImpl usuarioDAO;

    @BeforeEach
    public void setUp() throws Exception {
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        usuarioDAO = new UsuarioDAOImpl(mockConnection); // Constructor que recibe Connection
    }

    @Test
    void testSave() throws Exception {
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(10);

        Usuario usuario = new Estudiante();
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");
        usuario.setEmail("juan@mail.com");
        usuario.setContrasenia("Pass123!");
        usuario.setDocumento("1234567");
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario.setTipo(TipoUsuario.ESTUDIANTE);

        usuarioDAO.save(usuario);

        assertEquals(10, usuario.getId());
        verify(mockStatement).executeUpdate();
    }

    @Test
    void testFindById() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("tipo")).thenReturn("ESTUDIANTE");
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("nombre")).thenReturn("Juan");
        when(mockResultSet.getString("apellido")).thenReturn("Perez");
        when(mockResultSet.getString("email")).thenReturn("juan@mail.com");
        when(mockResultSet.getString("contrasenia")).thenReturn("hashed");
        when(mockResultSet.getString("documento")).thenReturn("1234567");
        when(mockResultSet.getString("estado")).thenReturn("ACTIVO");

        Usuario usuario = usuarioDAO.findById(1);

        assertNotNull(usuario);
        assertEquals(1, usuario.getId());
        assertTrue(usuario instanceof Estudiante);
        assertEquals("Juan", usuario.getNombre());
    }

    @Test
    void testFindAll() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("tipo")).thenReturn("ESTUDIANTE", "FUNCIONARIO");
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("nombre")).thenReturn("Juan", "Ana");
        when(mockResultSet.getString("apellido")).thenReturn("Perez", "Lopez");
        when(mockResultSet.getString("email")).thenReturn("juan@mail.com", "ana@mail.com");
        when(mockResultSet.getString("contrasenia")).thenReturn("hashed1", "hashed2");
        when(mockResultSet.getString("documento")).thenReturn("1234567", "7654321");
        when(mockResultSet.getString("estado")).thenReturn("ACTIVO", "ACTIVO");

        List<Usuario> usuarios = usuarioDAO.findAll();

        assertEquals(2, usuarios.size());
        assertTrue(usuarios.get(0) instanceof Estudiante);
        assertTrue(usuarios.get(1) instanceof Funcionario);
    }

    @Test
    void testUpdate() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        Usuario usuario = new Estudiante();
        usuario.setId(1);
        usuario.setNombre("Juan");

        usuarioDAO.update(usuario);

        verify(mockStatement).setInt(8, 1);
        verify(mockStatement).executeUpdate();
    }

    @Test
    void testDeleteLogical() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

        usuarioDAO.deleteLogical(1);

        verify(mockStatement).setInt(1, 1);
        verify(mockStatement).executeUpdate();
    }

    @Test
    void testFindByEmail() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("tipo")).thenReturn("FUNCIONARIO");
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("nombre")).thenReturn("Ana");
        when(mockResultSet.getString("apellido")).thenReturn("Lopez");
        when(mockResultSet.getString("email")).thenReturn("ana@mail.com");
        when(mockResultSet.getString("contrasenia")).thenReturn("hashed");
        when(mockResultSet.getString("documento")).thenReturn("7654321");
        when(mockResultSet.getString("estado")).thenReturn("ACTIVO");
        when(mockResultSet.getObject("id_rol")).thenReturn(null);

        Usuario usuario = usuarioDAO.findByEmail("ana@mail.com");

        assertNotNull(usuario);
        assertEquals("Ana", usuario.getNombre());
        assertTrue(usuario instanceof Funcionario);
    }

    @Test
    void testValidateCredentials_success() throws Exception {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getEstado()).thenReturn(EstadoUsuario.ACTIVO);
        when(usuario.getContrasenia()).thenReturn("hashed");

        UsuarioDAOImpl spyDAO = spy(usuarioDAO);
        doReturn(usuario).when(spyDAO).findByEmail("email@mail.com");

        try (var mocked = mockStatic(utils.PasswordHasher.class)) {
            mocked.when(() -> utils.PasswordHasher.verify("password", "hashed")).thenReturn(true);

            Usuario result = spyDAO.validateCredentials("email@mail.com", "password");
            assertEquals(usuario, result);
        }
    }

    @Test
    void testValidateCredentials_fail() throws Exception {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getEstado()).thenReturn(EstadoUsuario.ACTIVO);
        when(usuario.getContrasenia()).thenReturn("hashed");

        UsuarioDAOImpl spyDAO = spy(usuarioDAO);
        doReturn(usuario).when(spyDAO).findByEmail("email@mail.com");
        // Mock PasswordHasher
        mockStaticPasswordHasher(false);

        assertThrows(AutenticacionFallidaException.class,
                () -> spyDAO.validateCredentials("email@mail.com", "wrongpass"));
    }

    @Test
    void testViewNonSensitiveData() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("tipo")).thenReturn("ESTUDIANTE");
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("nombre")).thenReturn("Juan");
        when(mockResultSet.getString("apellido")).thenReturn("Perez");
        when(mockResultSet.getString("email")).thenReturn("juan@mail.com");
        when(mockResultSet.getString("documento")).thenReturn("1234567");
        when(mockResultSet.getString("estado")).thenReturn("ACTIVO");

        Usuario usuario = usuarioDAO.viewNonSensitiveData(1);

        assertNotNull(usuario);
        assertEquals("Juan", usuario.getNombre());
        assertNull(usuario.getContrasenia());
    }

    // Mock PasswordHasher.hash y verify para tests
    private void mockStaticPasswordHasher(boolean verifyResult) {
        // Como PasswordHasher tiene métodos estáticos, se puede usar Mockito.mockStatic
        try (var mocked = mockStatic(utils.PasswordHasher.class)) {
            mocked.when(() -> utils.PasswordHasher.verify(anyString(), anyString())).thenReturn(verifyResult);
            mocked.when(() -> utils.PasswordHasher.hash(anyString())).thenReturn("hashed");
        }
    }
}

package clases.proyectopruebas.controllers;

import clases.proyectopruebas.dao.UsuarioDAO;
import clases.proyectopruebas.models.Usuario;
import clases.proyectopruebas.models.enums.EstadoUsuario;
import clases.proyectopruebas.models.enums.TipoUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.security.MessageDigest;
import java.util.List;
import java.util.Optional;

public class UsuarioController {

    private UsuarioDAO usuarioDAO;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public Usuario saveUser(Usuario usuario) {
        try {
            return usuarioDAO.save(usuario);
        } catch (Exception e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
            return null;
        }
    }

    public Usuario updateUser(Usuario usuario) {
        try {
            return usuarioDAO.update(usuario);
        } catch (Exception e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteUser(int idUsuario) {
        try {
            return usuarioDAO.delete(idUsuario);
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    public Optional<Usuario> findUserById(int id) {
        try {
            return usuarioDAO.findById(id);
        } catch (Exception e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<Usuario> findAllUsers() {
        try {
            return usuarioDAO.findAll();
        } catch (Exception e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
            return List.of();
        }
    }

    public Optional<Usuario> findUserByEmail(String email) {
        try {
            return usuarioDAO.findByEmail(email);
        } catch (Exception e) {
            System.err.println("Error al buscar por email: " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Usuario> findUserByDni(String dni) {
        try {
            return usuarioDAO.findByDni(dni);
        } catch (Exception e) {
            System.err.println("Error al buscar por DNI: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<Usuario> findUsersByType(TipoUsuario tipo) {
        try {
            return usuarioDAO.findByTipo(tipo);
        } catch (Exception e) {
            System.err.println("Error al buscar por tipo: " + e.getMessage());
            return List.of();
        }
    }

    public List<Usuario> findUsersByState(EstadoUsuario estado) {
        try {
            return usuarioDAO.findByEstado(estado);
        } catch (Exception e) {
            System.err.println("Error al buscar por estado: " + e.getMessage());
            return List.of();
        }
    }

    public boolean authenticate(String credential, String password) {
        try {
            String hashedPassword = encryptSHA1(password);
            return usuarioDAO.autenticar(credential, hashedPassword).isPresent();
        } catch (Exception e) {
            System.err.println("Error en autenticación: " + e.getMessage());
            return false;
        }
    }

    public boolean registerUser(String dni, String nombre, String email, String telefono,
                                String direccion, String password, TipoUsuario tipo) {
        try {
            // Verificar duplicados
            if (usuarioDAO.existeDni(dni)) {
                return false;
            }
            if (usuarioDAO.existeEmail(email)) {
                return false;
            }

            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setDni(dni);
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setTelefono(telefono);
            nuevoUsuario.setDireccion(direccion);
            nuevoUsuario.setTipoUsuario(tipo);
            nuevoUsuario.setEstado(EstadoUsuario.ACTIVO);
            nuevoUsuario.setPasswordHash(encryptSHA1(password));
            nuevoUsuario.setLimitePrestamos(tipo == TipoUsuario.LECTOR ? 3 : 10);

            usuarioDAO.save(nuevoUsuario);
            return true;

        } catch (Exception e) {
            System.err.println("Error en registro: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUserState(int idUsuario, EstadoUsuario nuevoEstado) {
        try {
            return usuarioDAO.actualizarEstado(idUsuario, nuevoEstado);
        } catch (Exception e) {
            System.err.println("Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }

    public boolean validateUserData(Usuario usuario) {
        if (usuario.getDni() == null || usuario.getDni().trim().isEmpty()) {
            return false;
        }
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            return false;
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            return false;
        }
        if (usuario.getTipoUsuario() == null) {
            return false;
        }
        return true;
    }

    public boolean isDniAvailable(String dni) {
        try {
            return !usuarioDAO.existeDni(dni);
        } catch (Exception e) {
            System.err.println("Error al verificar DNI: " + e.getMessage());
            return false;
        }
    }

    public boolean isEmailAvailable(String email) {
        try {
            return !usuarioDAO.existeEmail(email);
        } catch (Exception e) {
            System.err.println("Error al verificar email: " + e.getMessage());
            return false;
        }
    }

    public ObservableList<TipoUsuario> getAllUserTypes() {
        return FXCollections.observableArrayList(TipoUsuario.values());
    }

    public ObservableList<EstadoUsuario> getAllUserStates() {
        return FXCollections.observableArrayList(EstadoUsuario.values());
    }

    private String encryptSHA1(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long getTotalUsersCount() {
        try {
            return usuarioDAO.count();
        } catch (Exception e) {
            System.err.println("Error al contar usuarios: " + e.getMessage());
            return 0;
        }
    }

    public long getUsersCountByType(TipoUsuario tipo) {
        try {
            return usuarioDAO.findByTipo(tipo).size();
        } catch (Exception e) {
            System.err.println("Error al contar usuarios por tipo: " + e.getMessage());
            return 0;
        }
    }
}

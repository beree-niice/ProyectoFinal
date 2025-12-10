package clases.proyectopruebas.dao;

import clases.proyectopruebas.models.Usuario;
import clases.proyectopruebas.models.enums.EstadoUsuario;
import clases.proyectopruebas.models.enums.TipoUsuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO extends GenericDAO<Usuario, Integer> {

    @Override
    protected String getTableName() {
        return "usuario";
    }

    @Override
    protected String getIdColumnName() {
        return "id_usuario";
    }

    @Override
    protected String buildInsertQuery() {
        return "INSERT INTO usuario (dni, nombre, email, telefono, direccion, " +
                "tipo_usuario, password_hash, fecha_registro, estado, limite_prestamos) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String buildUpdateQuery() {
        return "UPDATE usuario SET dni=?, nombre=?, email=?, telefono=?, direccion=?, " +
                "tipo_usuario=?, password_hash=?, estado=?, limite_prestamos=?, " +
                "fecha_ultima_actividad=? WHERE id_usuario=?";
    }

    @Override
    protected int getUpdateParameterCount() {
        return 11; // 10 parámetros + 1 ID
    }

    @Override
    protected Usuario mapResultSetToEntity(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setDni(rs.getString("dni"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setEmail(rs.getString("email"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setDireccion(rs.getString("direccion"));

        // Convertir Enums
        String tipoStr = rs.getString("tipo_usuario");
        if (tipoStr != null && !tipoStr.trim().isEmpty()) {
            usuario.setTipoUsuario(TipoUsuario.fromString(tipoStr));
        } else {
            usuario.setTipoUsuario(TipoUsuario.LECTOR); // Valor por defecto
        }

        String estadoStr = rs.getString("estado");
        if (estadoStr != null && !estadoStr.trim().isEmpty()) {
            usuario.setEstado(EstadoUsuario.fromString(estadoStr));
        } else {
            usuario.setEstado(EstadoUsuario.ACTIVO); // Valor por defecto
        }

        usuario.setPasswordHash(rs.getString("password_hash"));
        usuario.setFechaRegistro(rs.getDate("fecha_registro").toLocalDate());
        usuario.setLimitePrestamos(rs.getInt("limite_prestamos"));

        Timestamp ultimaActividad = rs.getTimestamp("fecha_ultima_actividad");
        if (ultimaActividad != null) {
            usuario.setFechaUltimaActividad(ultimaActividad.toLocalDateTime());
        }

        return usuario;
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Usuario usuario) throws SQLException {
        int index = 1;
        stmt.setString(index++, usuario.getDni());
        stmt.setString(index++, usuario.getNombre());
        stmt.setString(index++, usuario.getEmail());
        stmt.setString(index++, usuario.getTelefono());
        stmt.setString(index++, usuario.getDireccion());
        stmt.setString(index++, usuario.getTipoUsuario().name());
        stmt.setString(index++, usuario.getPasswordHash());
        stmt.setDate(index++, Date.valueOf(usuario.getFechaRegistro()));
        stmt.setString(index++, usuario.getEstado().name());
        stmt.setInt(index++, usuario.getLimitePrestamos());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Usuario usuario) throws SQLException {
        int index = 1;
        stmt.setString(index++, usuario.getDni());
        stmt.setString(index++, usuario.getNombre());
        stmt.setString(index++, usuario.getEmail());
        stmt.setString(index++, usuario.getTelefono());
        stmt.setString(index++, usuario.getDireccion());
        stmt.setString(index++, usuario.getTipoUsuario().name());
        stmt.setString(index++, usuario.getPasswordHash());
        stmt.setString(index++, usuario.getEstado().name());
        stmt.setInt(index++, usuario.getLimitePrestamos());

        // Manejar fecha_ultima_actividad (puede ser null)
        if (usuario.getFechaUltimaActividad() != null) {
            stmt.setTimestamp(index++, Timestamp.valueOf(usuario.getFechaUltimaActividad()));
        } else {
            stmt.setNull(index++, Types.TIMESTAMP);
        }

        // FALTA EL ID EN TU VERSIÓN - ¡ESTO ES IMPORTANTE!
        stmt.setInt(index++, usuario.getIdUsuario());
    }

    @Override
    protected Integer getIdFromEntity(Usuario usuario) {
        return usuario.getIdUsuario();
    }

    @Override
    protected void setGeneratedId(Usuario usuario, ResultSet rs) throws SQLException {
        usuario.setIdUsuario(rs.getInt(1));
    }

    // ========== MÉTODOS ESPECÍFICOS DE USUARIO ==========

    public Optional<Usuario> findByEmail(String email) {
        String sql = "SELECT * FROM usuario WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToEntity(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            handleSQLException("Error al buscar por email", e);
            return Optional.empty();
        }
    }

    public Optional<Usuario> findByDni(String dni) {
        String sql = "SELECT * FROM usuario WHERE dni = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, dni);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToEntity(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            handleSQLException("Error al buscar por DNI", e);
            return Optional.empty();
        }
    }

    public Optional<Usuario> autenticar(String emailODni, String passwordHash) {
        String sql = "SELECT * FROM usuario WHERE (email = ? OR dni = ?) AND password_hash = ? AND estado = 'ACTIVO'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, emailODni);
            stmt.setString(2, emailODni);
            stmt.setString(3, passwordHash);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = mapResultSetToEntity(rs);
                actualizarUltimaActividad(usuario.getIdUsuario());
                return Optional.of(usuario);
            }
            return Optional.empty();

        } catch (SQLException e) {
            handleSQLException("Error en autenticación", e);
            return Optional.empty();
        }
    }

    public List<Usuario> findByTipo(TipoUsuario tipo) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE tipo_usuario = ? ORDER BY nombre";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tipo.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                usuarios.add(mapResultSetToEntity(rs));
            }

        } catch (SQLException e) {
            handleSQLException("Error al buscar por tipo", e);
        }
        return usuarios;
    }

    public List<Usuario> findByEstado(EstadoUsuario estado) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE estado = ? ORDER BY nombre";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, estado.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                usuarios.add(mapResultSetToEntity(rs));
            }

        } catch (SQLException e) {
            handleSQLException("Error al buscar por estado", e);
        }
        return usuarios;
    }

    public boolean actualizarEstado(int idUsuario, EstadoUsuario nuevoEstado) {
        String sql = "UPDATE usuario SET estado = ? WHERE id_usuario = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nuevoEstado.name());
            stmt.setInt(2, idUsuario);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            handleSQLException("Error al actualizar estado", e);
            return false;
        }
    }

    public boolean actualizarUltimaActividad(int idUsuario) {
        String sql = "UPDATE usuario SET fecha_ultima_actividad = NOW() WHERE id_usuario = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            handleSQLException("Error al actualizar última actividad", e);
            return false;
        }
    }

    public boolean existeEmail(String email) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            handleSQLException("Error al verificar email", e);
            return false;
        }
    }

    public boolean existeDni(String dni) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE dni = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, dni);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            handleSQLException("Error al verificar DNI", e);
            return false;
        }
    }

    // ========== MÉTODOS NUEVOS PARA COMPATIBILIDAD ==========

    /**
     * Método para compatibilidad con AuthController
     */
    public Optional<Usuario> authenticate(String email, String passwordHash) {
        return autenticar(email, passwordHash);
    }

    /**
     * Método para compatibilidad con AuthController
     */
    public boolean usernameExists(String email) {
        return existeEmail(email);
    }

    /**
     * Método para compatibilidad con AuthController
     */
    public boolean emailExists(String email) {
        return existeEmail(email);
    }

    /**
     * Método para compatibilidad con AuthController
     */
    public boolean dniExists(String dni) {
        return existeDni(dni);
    }

    /**
     * Obtiene todos los usuarios activos
     */
    public List<Usuario> findActivos() {
        return findByEstado(EstadoUsuario.ACTIVO);
    }

    /**
     * Obtiene estadísticas de usuarios
     */
    public int countByTipo(TipoUsuario tipo) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE tipo_usuario = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tipo.name());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            handleSQLException("Error al contar por tipo", e);
            return 0;
        }
    }

    /**
     * Busca usuarios por nombre o email (para búsquedas)
     */
    public List<Usuario> buscar(String criterio) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE nombre LIKE ? OR email LIKE ? OR dni LIKE ? ORDER BY nombre";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String likeCriterio = "%" + criterio + "%";
            stmt.setString(1, likeCriterio);
            stmt.setString(2, likeCriterio);
            stmt.setString(3, likeCriterio);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                usuarios.add(mapResultSetToEntity(rs));
            }

        } catch (SQLException e) {
            handleSQLException("Error en búsqueda de usuarios", e);
        }
        return usuarios;
    }

    /**
     * Actualiza el límite de préstamos de un usuario
     */
    public boolean actualizarLimitePrestamos(int idUsuario, int nuevoLimite) {
        String sql = "UPDATE usuario SET limite_prestamos = ? WHERE id_usuario = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, nuevoLimite);
            stmt.setInt(2, idUsuario);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            handleSQLException("Error al actualizar límite de préstamos", e);
            return false;
        }
    }

    /**
     * Verifica si un usuario puede tomar más préstamos
     */
    public boolean puedeTomarPrestamo(int idUsuario) {
        String sql = "SELECT COUNT(*) as prestamos_activos, limite_prestamos " +
                "FROM usuario u " +
                "LEFT JOIN prestamo p ON u.id_usuario = p.id_usuario AND p.estado = 'ACTIVO' " +
                "WHERE u.id_usuario = ? " +
                "GROUP BY u.id_usuario";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int prestamosActivos = rs.getInt("prestamos_activos");
                int limitePrestamos = rs.getInt("limite_prestamos");
                return prestamosActivos < limitePrestamos;
            }
            return false;

        } catch (SQLException e) {
            handleSQLException("Error al verificar disponibilidad de préstamos", e);
            return false;
        }
    }
}
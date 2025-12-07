package org.biblioteca.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class GenericDAO<T, ID> implements DAO<T, ID> {

    protected Connection connection;

    public GenericDAO() {
        try {
            this.connection = ConexionBD.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener conexión", e);
        }
    }

    // Métodos abstractos que cada DAO debe implementar
    protected abstract String getTableName();
    protected abstract String getIdColumnName();
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
    protected abstract void setInsertParameters(PreparedStatement stmt, T entity) throws SQLException;
    protected abstract void setUpdateParameters(PreparedStatement stmt, T entity) throws SQLException;
    protected abstract ID getIdFromEntity(T entity);

    // Métodos CRUD genéricos
    @Override
    public Optional<T> findById(ID id) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?",
                getTableName(), getIdColumnName());

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setIdParameter(stmt, 1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToEntity(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            handleSQLException("Error al buscar por ID", e);
            return Optional.empty();
        }
    }

    @Override
    public List<T> findAll() {
        List<T> entities = new ArrayList<>();
        String sql = String.format("SELECT * FROM %s", getTableName());

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                entities.add(mapResultSetToEntity(rs));
            }

        } catch (SQLException e) {
            handleSQLException("Error al obtener todos", e);
        }
        return entities;
    }

    @Override
    public T save(T entity) {
        String sql = buildInsertQuery();

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setInsertParameters(stmt, entity);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        setGeneratedId(entity, rs);
                    }
                }
                return entity;
            }
            return null;

        } catch (SQLException e) {
            handleSQLException("Error al guardar", e);
            return null;
        }
    }

    @Override
    public T update(T entity) {
        String sql = buildUpdateQuery();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setUpdateParameters(stmt, entity);
            setIdParameter(stmt, getUpdateParameterCount(), getIdFromEntity(entity));

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0 ? entity : null;

        } catch (SQLException e) {
            handleSQLException("Error al actualizar", e);
            return null;
        }
    }

    @Override
    public boolean delete(ID id) {
        String sql = String.format("DELETE FROM %s WHERE %s = ?",
                getTableName(), getIdColumnName());

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setIdParameter(stmt, 1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            handleSQLException("Error al eliminar", e);
            return false;
        }
    }

    @Override
    public boolean existsById(ID id) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?",
                getTableName(), getIdColumnName());

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setIdParameter(stmt, 1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            handleSQLException("Error al verificar existencia", e);
            return false;
        }
    }

    @Override
    public long count() {
        String sql = String.format("SELECT COUNT(*) FROM %s", getTableName());

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;

        } catch (SQLException e) {
            handleSQLException("Error al contar registros", e);
            return 0;
        }
    }

    // Métodos protegidos para construir queries
    protected String buildInsertQuery() {
        // Sobreescribir en clases hijas si necesitan columnas específicas
        return String.format("INSERT INTO %s DEFAULT VALUES", getTableName());
    }

    protected String buildUpdateQuery() {
        // Sobreescribir en clases hijas
        return String.format("UPDATE %s SET ? WHERE %s = ?",
                getTableName(), getIdColumnName());
    }

    protected int getUpdateParameterCount() {
        return 2; // Sobreescribir si hay más parámetros
    }

    // Métodos helper
    protected void setIdParameter(PreparedStatement stmt, int index, ID id) throws SQLException {
        if (id instanceof Integer) {
            stmt.setInt(index, (Integer) id);
        } else if (id instanceof Long) {
            stmt.setLong(index, (Long) id);
        } else if (id instanceof String) {
            stmt.setString(index, (String) id);
        } else {
            throw new SQLException("Tipo de ID no soportado: " + id.getClass().getName());
        }
    }

    protected void setGeneratedId(T entity, ResultSet rs) throws SQLException {
        // Sobreescribir si la entidad tiene ID auto-generado
    }

    protected void handleSQLException(String message, SQLException e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }

    // Método para cerrar recursos
    protected void closeResources(ResultSet rs, Statement stmt) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            System.err.println("Error al cerrar recursos: " + e.getMessage());
        }
    }
}

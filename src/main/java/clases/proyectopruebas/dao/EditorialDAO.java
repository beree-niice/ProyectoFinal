package clases.proyectopruebas.dao;

import clases.proyectopruebas.models.Editorial;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditorialDAO extends GenericDAO<Editorial, Integer> {

    @Override
    protected String getTableName() {
        return "editorial";
    }

    @Override
    protected String getIdColumnName() {
        return "id_editorial";
    }

    @Override
    protected Integer getIdFromEntity(Editorial entity) {
        return entity.getIdEditorial();
    }

    @Override
    protected void setGeneratedId(Editorial entity, ResultSet rs) throws SQLException {
        entity.setIdEditorial(rs.getInt(1));
    }

    @Override
    protected Editorial mapResultSetToEntity(ResultSet rs) throws SQLException {
        Editorial e = new Editorial();
        e.setIdEditorial(rs.getInt("id_editorial"));
        e.setNombre(rs.getString("nombre"));
        e.setPais(rs.getString("pais"));
        e.setCiudad(rs.getString("ciudad"));
        e.setTelefono(rs.getString("telefono"));
        e.setEmail(rs.getString("email"));
        e.setSitioWeb(rs.getString("sitio_web"));
        e.setActiva(rs.getBoolean("activa"));
        return e;
    }

    @Override
    protected String buildInsertQuery() {
        return "INSERT INTO editorial (nombre, pais, ciudad, telefono, email, sitio_web, activa) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Editorial entity) throws SQLException {
        stmt.setString(1, entity.getNombre());
        stmt.setString(2, entity.getPais());
        stmt.setString(3, entity.getCiudad());
        stmt.setString(4, entity.getTelefono());
        stmt.setString(5, entity.getEmail());
        stmt.setString(6, entity.getSitioWeb());
        stmt.setBoolean(7, entity.isActiva());
    }

    @Override
    protected String buildUpdateQuery() {
        return "UPDATE editorial SET nombre=?, pais=?, ciudad=?, telefono=?, email=?, sitio_web=?, activa=? WHERE id_editorial=?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Editorial entity) throws SQLException {
        setInsertParameters(stmt, entity);
    }

    @Override
    protected int getUpdateParameterCount() {
        return 8; // 7 campos + 1 ID
    }
}
package clases.proyectopruebas.dao;


import clases.proyectopruebas.models.Autor;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AutorDAO extends GenericDAO<Autor, Integer> {

    @Override
    protected String getTableName() {
        return "autor";
    }

    @Override
    protected String getIdColumnName() {
        return "id_autor";
    }

    @Override
    protected Integer getIdFromEntity(Autor entity) {
        return entity.getIdAutor();
    }

    @Override
    protected void setGeneratedId(Autor entity, ResultSet rs) throws SQLException {
        entity.setIdAutor(rs.getInt(1));
    }

    @Override
    protected Autor mapResultSetToEntity(ResultSet rs) throws SQLException {
        Autor autor = new Autor();
        autor.setIdAutor(rs.getInt("id_autor"));
        autor.setNombreCompleto(rs.getString("nombre_completo"));
        autor.setNacionalidad(rs.getString("nacionalidad"));

        Date fecNac = rs.getDate("fecha_nacimiento");
        if (fecNac != null) autor.setFechaNacimiento(fecNac.toLocalDate());

        Date fecFall = rs.getDate("fecha_fallecimiento");
        if (fecFall != null) autor.setFechaFallecimiento(fecFall.toLocalDate());

        autor.setBiografia(rs.getString("biografia"));
        autor.setPremios(rs.getString("premios"));
        return autor;
    }

    @Override
    protected String buildInsertQuery() {
        return "INSERT INTO autor (nombre_completo, nacionalidad, fecha_nacimiento, fecha_fallecimiento, biografia, premios) VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Autor entity) throws SQLException {
        stmt.setString(1, entity.getNombreCompleto());
        stmt.setString(2, entity.getNacionalidad());
        stmt.setDate(3, entity.getFechaNacimiento() != null ? Date.valueOf(entity.getFechaNacimiento()) : null);
        stmt.setDate(4, entity.getFechaFallecimiento() != null ? Date.valueOf(entity.getFechaFallecimiento()) : null);
        stmt.setString(5, entity.getBiografia());
        stmt.setString(6, entity.getPremios());
    }

    @Override
    protected String buildUpdateQuery() {
        return "UPDATE autor SET nombre_completo=?, nacionalidad=?, fecha_nacimiento=?, fecha_fallecimiento=?, biografia=?, premios=? WHERE id_autor=?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Autor entity) throws SQLException {
        setInsertParameters(stmt, entity); // Reutilizamos lógica ya que los campos son los mismos
    }

    @Override
    protected int getUpdateParameterCount() {
        return 7; // 6 campos + 1 ID
    }
}
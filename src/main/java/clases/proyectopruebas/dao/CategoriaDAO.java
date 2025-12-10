package clases.proyectopruebas.dao;



import clases.proyectopruebas.models.Categoria;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class CategoriaDAO extends GenericDAO<Categoria, Integer> {

    @Override
    protected String getTableName() {
        return "categoria";
    }

    @Override
    protected String getIdColumnName() {
        return "id_categoria";
    }

    @Override
    protected Integer getIdFromEntity(Categoria entity) {
        return entity.getIdCategoria();
    }

    @Override
    protected void setGeneratedId(Categoria entity, ResultSet rs) throws SQLException {
        entity.setIdCategoria(rs.getInt(1));
    }

    @Override
    protected Categoria mapResultSetToEntity(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setIdCategoria(rs.getInt("id_categoria"));
        c.setNombre(rs.getString("nombre"));
        c.setDescripcion(rs.getString("descripcion"));
        c.setCodigo(rs.getString("codigo"));
        c.setActiva(rs.getBoolean("activa")); // MySQL TINYINT(1) se mapea a boolean

        // Manejo de categoría padre (Solo ID para evitar recursividad infinita al mapear)
        int idPadre = rs.getInt("categoria_padre");
        if (!rs.wasNull()) {
            // Podrías setear solo el ID o crear un objeto Categoria dummy
            // c.setIdCategoriaPadre(idPadre);
        }

        return c;
    }

    @Override
    protected String buildInsertQuery() {
        return "INSERT INTO categoria (nombre, descripcion, codigo, activa, categoria_padre) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Categoria entity) throws SQLException {
        stmt.setString(1, entity.getNombre());
        stmt.setString(2, entity.getDescripcion());
        stmt.setString(3, entity.getCodigo());
        stmt.setBoolean(4, entity.isActiva());

        // Asumiendo que tu modelo tiene una lógica para obtener el ID del padre
        // Si no usas categorías anidadas en tu modelo Java, envía NULL siempre.
        stmt.setNull(5, Types.INTEGER);
        /* Si tuvieras el campo en el modelo, sería así:
           if (entity.getCategoriaPadre() != null) {
               stmt.setInt(5, entity.getCategoriaPadre().getIdCategoria());
           } else {
               stmt.setNull(5, Types.INTEGER);
           }
        */
    }

    @Override
    protected String buildUpdateQuery() {
        return "UPDATE categoria SET nombre=?, descripcion=?, codigo=?, activa=?, categoria_padre=? WHERE id_categoria=?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Categoria entity) throws SQLException {
        setInsertParameters(stmt, entity);
    }

    @Override
    protected int getUpdateParameterCount() {
        return 6; // 5 campos + 1 ID
    }
}
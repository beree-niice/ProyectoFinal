package org.biblioteca.dao;

import org.biblioteca.models.Libro;
import org.biblioteca.models.Reserva;
import org.biblioteca.models.Usuario;
import org.biblioteca.models.enums.EstadoReserva;
import java.sql.*;

public class ReservaDAO extends GenericDAO<Reserva, Integer> {

    @Override
    protected String getTableName() { return "reserva"; }

    @Override
    protected String getIdColumnName() { return "id_reserva"; }

    @Override
    protected Integer getIdFromEntity(Reserva entity) { return entity.getIdReserva(); }

    @Override
    protected void setGeneratedId(Reserva entity, ResultSet rs) throws SQLException {
        entity.setIdReserva(rs.getInt(1));
    }

    @Override
    protected Reserva mapResultSetToEntity(ResultSet rs) throws SQLException {
        Reserva r = new Reserva();
        r.setIdReserva(rs.getInt("id_reserva"));

        // Mapeo simple de relaciones (Solo IDs para evitar recursividad infinita o complejidad aquí)
        // En una app real, podrías usar los DAOs de Libro/Usuario para llenar el objeto completo
        Libro libro = new Libro();
        libro.setIdLibro(rs.getInt("id_libro"));
        r.setLibro(libro);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        r.setUsuario(usuario);

        Timestamp tsReserva = rs.getTimestamp("fecha_reserva");
        if(tsReserva != null) r.setFechaReserva(tsReserva.toLocalDateTime());

        Date dateVenc = rs.getDate("fecha_vencimiento");
        if(dateVenc != null) r.setFechaVencimiento(dateVenc.toLocalDate());

        r.setEstado(EstadoReserva.valueOf(rs.getString("estado")));

        Timestamp tsNotif = rs.getTimestamp("fecha_notificacion");
        if(tsNotif != null) r.setFechaNotificacion(tsNotif.toLocalDateTime());

        r.setPrioridad(rs.getInt("prioridad"));
        r.setObservaciones(rs.getString("observaciones"));

        return r;
    }

    @Override
    protected String buildInsertQuery() {
        return "INSERT INTO reserva (id_libro, id_usuario, fecha_reserva, fecha_vencimiento, estado, fecha_notificacion, prioridad, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Reserva entity) throws SQLException {
        stmt.setInt(1, entity.getLibro().getIdLibro());
        stmt.setInt(2, entity.getUsuario().getIdUsuario());
        stmt.setTimestamp(3, Timestamp.valueOf(entity.getFechaReserva()));
        stmt.setDate(4, Date.valueOf(entity.getFechaVencimiento()));
        stmt.setString(5, entity.getEstado().name());
        stmt.setTimestamp(6, entity.getFechaNotificacion() != null ? Timestamp.valueOf(entity.getFechaNotificacion()) : null);
        stmt.setInt(7, entity.getPrioridad());
        stmt.setString(8, entity.getObservaciones());
    }

    @Override
    protected String buildUpdateQuery() {
        return "UPDATE reserva SET id_libro=?, id_usuario=?, fecha_reserva=?, fecha_vencimiento=?, estado=?, fecha_notificacion=?, prioridad=?, observaciones=? WHERE id_reserva=?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Reserva entity) throws SQLException {
        setInsertParameters(stmt, entity);
    }

    @Override
    protected int getUpdateParameterCount() {
        return 9;
    }
}

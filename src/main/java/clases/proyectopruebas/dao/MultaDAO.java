package clases.proyectopruebas.dao;

import clases.proyectopruebas.models.Multa;
import clases.proyectopruebas.models.Prestamo;
import clases.proyectopruebas.models.Usuario;
import clases.proyectopruebas.models.enums.EstadoMulta;
import clases.proyectopruebas.models.enums.MetodoPago;

import java.sql.*;

public class MultaDAO extends GenericDAO<Multa, Integer> {

    @Override
    protected String getTableName() { return "multa"; }

    @Override
    protected String getIdColumnName() { return "id_multa"; }

    @Override
    protected Integer getIdFromEntity(Multa entity) { return entity.getIdMulta(); }

    @Override
    protected void setGeneratedId(Multa entity, ResultSet rs) throws SQLException {
        entity.setIdMulta(rs.getInt(1));
    }

    @Override
    protected Multa mapResultSetToEntity(ResultSet rs) throws SQLException {
        Multa m = new Multa();
        m.setIdMulta(rs.getInt("id_multa"));

        // Relación Préstamo (Solo ID)
        Prestamo p = new Prestamo();
        p.setIdPrestamo(rs.getInt("id_prestamo"));
        m.setPrestamo(p);

        m.setMonto(rs.getBigDecimal("monto"));
        m.setMontoPorDia(rs.getBigDecimal("monto_por_dia"));
        m.setDiasRetraso(rs.getInt("dias_retraso"));

        Date fecGen = rs.getDate("fecha_generacion");
        if(fecGen != null) m.setFechaGeneracion(fecGen.toLocalDate());

        Date fecPago = rs.getDate("fecha_pago");
        if(fecPago != null) m.setFechaPago(fecPago.toLocalDate());

        String mp = rs.getString("metodo_pago");
        if(mp != null) m.setMetodoPago(MetodoPago.valueOf(mp));

        m.setEstado(EstadoMulta.valueOf(rs.getString("estado")));

        int idCobrador = rs.getInt("id_usuario_recibe_pago");
        if (!rs.wasNull()) {
            Usuario u = new Usuario();
            u.setIdUsuario(idCobrador);
            m.setUsuarioRecibePago(u);
        }

        m.setObservaciones(rs.getString("observaciones"));
        return m;
    }

    @Override
    protected String buildInsertQuery() {
        return "INSERT INTO multa (id_prestamo, monto, monto_por_dia, dias_retraso, fecha_generacion, fecha_pago, metodo_pago, estado, id_usuario_recibe_pago, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Multa entity) throws SQLException {
        stmt.setInt(1, entity.getPrestamo().getIdPrestamo());
        stmt.setBigDecimal(2, entity.getMonto());
        stmt.setBigDecimal(3, entity.getMontoPorDia());
        stmt.setInt(4, entity.getDiasRetraso());
        stmt.setDate(5, Date.valueOf(entity.getFechaGeneracion()));

        stmt.setDate(6, entity.getFechaPago() != null ? Date.valueOf(entity.getFechaPago()) : null);

        stmt.setString(7, entity.getMetodoPago() != null ? entity.getMetodoPago().name() : null);
        stmt.setString(8, entity.getEstado().name());

        if (entity.getUsuarioRecibePago() != null) {
            stmt.setInt(9, entity.getUsuarioRecibePago().getIdUsuario());
        } else {
            stmt.setNull(9, Types.INTEGER);
        }

        stmt.setString(10, entity.getObservaciones());
    }

    @Override
    protected String buildUpdateQuery() {
        return "UPDATE multa SET id_prestamo=?, monto=?, monto_por_dia=?, dias_retraso=?, fecha_generacion=?, fecha_pago=?, metodo_pago=?, estado=?, id_usuario_recibe_pago=?, observaciones=? WHERE id_multa=?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Multa entity) throws SQLException {
        setInsertParameters(stmt, entity);
    }

    @Override
    protected int getUpdateParameterCount() {
        return 11;
    }
}
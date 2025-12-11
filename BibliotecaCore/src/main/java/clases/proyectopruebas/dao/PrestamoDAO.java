package clases.proyectopruebas.dao;


import clases.proyectopruebas.dao.GenericDAO;
import clases.proyectopruebas.models.Libro;
import clases.proyectopruebas.models.Prestamo;
import clases.proyectopruebas.models.Usuario;
import clases.proyectopruebas.models.enums.EstadoPrestamo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrestamoDAO extends GenericDAO<Prestamo, Integer> {

    public PrestamoDAO() {
        super();
    }

    @Override
    protected String getTableName() {
        return "prestamo";
    }

    @Override
    protected String getIdColumnName() {
        return "id_prestamo";
    }

    @Override
    protected String buildInsertQuery() {
        return "INSERT INTO prestamo (id_libro, id_usuario, id_bibliotecario, " +
                "fecha_prestamo, fecha_devolucion_esperada, estado, " +
                "renovaciones_realizadas, max_renovaciones, observaciones) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String buildUpdateQuery() {
        return "UPDATE prestamo SET id_libro=?, id_usuario=?, id_bibliotecario=?, " +
                "fecha_prestamo=?, fecha_devolucion_esperada=?, fecha_devolucion_real=?, " +
                "estado=?, renovaciones_realizadas=?, max_renovaciones=?, observaciones=? " +
                "WHERE id_prestamo=?";
    }

    @Override
    protected int getUpdateParameterCount() {
        return 11;
    }

    @Override
    protected Prestamo mapResultSetToEntity(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo();

        prestamo.setIdPrestamo(rs.getInt("id_prestamo"));

        // Fechas
        Date fechaPrestamo = rs.getDate("fecha_prestamo");
        if (fechaPrestamo != null) {
            prestamo.setFechaPrestamo(fechaPrestamo.toLocalDate());
        }

        Date fechaDevolucionEsperada = rs.getDate("fecha_devolucion_esperada");
        if (fechaDevolucionEsperada != null) {
            prestamo.setFechaDevolucionEsperada(fechaDevolucionEsperada.toLocalDate());
        }

        Date fechaDevolucionReal = rs.getDate("fecha_devolucion_real");
        if (fechaDevolucionReal != null) {
            prestamo.setFechaDevolucionReal(fechaDevolucionReal.toLocalDate());
        }

        // Estado
        String estadoStr = rs.getString("estado");
        if (estadoStr != null) {
            try {
                prestamo.setEstado(EstadoPrestamo.valueOf(estadoStr));
            } catch (IllegalArgumentException e) {
                prestamo.setEstado(EstadoPrestamo.ACTIVO);
            }
        } else {
            prestamo.setEstado(EstadoPrestamo.ACTIVO);
        }

        prestamo.setRenovacionesRealizadas(rs.getInt("renovaciones_realizadas"));
        prestamo.setMaxRenovaciones(rs.getInt("max_renovaciones"));
        prestamo.setObservaciones(rs.getString("observaciones"));

        // Solo IDs para relaciones
        Libro libro = new Libro();
        libro.setIdLibro(rs.getInt("id_libro"));
        prestamo.setLibro(libro);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        prestamo.setUsuario(usuario);

        Usuario bibliotecario = new Usuario();
        bibliotecario.setIdUsuario(rs.getInt("id_bibliotecario"));
        prestamo.setBibliotecario(bibliotecario);

        return prestamo;
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Prestamo prestamo) throws SQLException {
        int index = 1;

        // Validar IDs
        if (prestamo.getLibro() == null || prestamo.getLibro().getIdLibro() <= 0) {
            throw new SQLException("ID de libro inválido");
        }
        if (prestamo.getUsuario() == null || prestamo.getUsuario().getIdUsuario() <= 0) {
            throw new SQLException("ID de usuario inválido");
        }
        if (prestamo.getBibliotecario() == null || prestamo.getBibliotecario().getIdUsuario() <= 0) {
            throw new SQLException("ID de bibliotecario inválido");
        }

        stmt.setInt(index++, prestamo.getLibro().getIdLibro());
        stmt.setInt(index++, prestamo.getUsuario().getIdUsuario());
        stmt.setInt(index++, prestamo.getBibliotecario().getIdUsuario());

        // Fecha préstamo (hoy si es null)
        LocalDate fechaPrestamo = prestamo.getFechaPrestamo() != null ?
                prestamo.getFechaPrestamo() : LocalDate.now();
        stmt.setDate(index++, Date.valueOf(fechaPrestamo));

        // Fecha devolución esperada (obligatoria)
        if (prestamo.getFechaDevolucionEsperada() == null) {
            // Por defecto: 14 días desde fecha préstamo
            prestamo.setFechaDevolucionEsperada(fechaPrestamo.plusDays(14));
        }
        stmt.setDate(index++, Date.valueOf(prestamo.getFechaDevolucionEsperada()));

        // Estado (ACTIVO por defecto)
        EstadoPrestamo estado = prestamo.getEstado() != null ?
                prestamo.getEstado() : EstadoPrestamo.ACTIVO;
        stmt.setString(index++, estado.name());

        stmt.setInt(index++, prestamo.getRenovacionesRealizadas());
        stmt.setInt(index++, prestamo.getMaxRenovaciones());
        stmt.setString(index++, prestamo.getObservaciones());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Prestamo prestamo) throws SQLException {
        int index = 1;
        stmt.setInt(index++, prestamo.getLibro().getIdLibro());
        stmt.setInt(index++, prestamo.getUsuario().getIdUsuario());
        stmt.setInt(index++, prestamo.getBibliotecario().getIdUsuario());
        stmt.setDate(index++, Date.valueOf(prestamo.getFechaPrestamo()));
        stmt.setDate(index++, Date.valueOf(prestamo.getFechaDevolucionEsperada()));

        if (prestamo.getFechaDevolucionReal() != null) {
            stmt.setDate(index++, Date.valueOf(prestamo.getFechaDevolucionReal()));
        } else {
            stmt.setNull(index++, Types.DATE);
        }

        stmt.setString(index++, prestamo.getEstado().name());
        stmt.setInt(index++, prestamo.getRenovacionesRealizadas());
        stmt.setInt(index++, prestamo.getMaxRenovaciones());
        stmt.setString(index++, prestamo.getObservaciones());
    }

    @Override
    protected Integer getIdFromEntity(Prestamo prestamo) {
        return prestamo.getIdPrestamo();
    }

    @Override
    protected void setGeneratedId(Prestamo prestamo, ResultSet rs) throws SQLException {
        prestamo.setIdPrestamo(rs.getInt(1));
    }

    // ========== MÉTODOS PRINCIPALES ==========

    /**
     * Método principal: Registrar un nuevo préstamo
     * Usa el stored procedure sp_registrar_prestamo de tu BD
     */
    public Map<String, Object> registrarPrestamo(int idLibro, int idUsuario, int idBibliotecario, int diasPrestamo) {
        Map<String, Object> resultado = new HashMap<>();

        // Usar el stored procedure de tu BD
        String sql = "CALL sp_registrar_prestamo(?, ?, ?, ?, ?)";

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, idLibro);
            stmt.setInt(2, idUsuario);
            stmt.setInt(3, idBibliotecario);
            stmt.setInt(4, diasPrestamo);
            stmt.registerOutParameter(5, Types.VARCHAR);

            stmt.execute();

            String mensaje = stmt.getString(5);
            resultado.put("exito", mensaje.startsWith("OK:"));
            resultado.put("mensaje", mensaje);

            // Extraer ID si fue exitoso
            if (mensaje.startsWith("OK:")) {
                String[] partes = mensaje.split("ID: ");
                if (partes.length > 1) {
                    resultado.put("id_prestamo", Integer.parseInt(partes[1].trim()));
                }
            }

        } catch (SQLException e) {
            resultado.put("exito", false);
            resultado.put("mensaje", "Error al ejecutar procedimiento: " + e.getMessage());
            handleSQLException("Error al registrar préstamo", e);
        }

        return resultado;
    }

    /**
     * Registrar devolución usando el stored procedure
     */
    public Map<String, Object> registrarDevolucion(int idPrestamo, String observaciones) {
        Map<String, Object> resultado = new HashMap<>();

        String sql = "CALL sp_registrar_devolucion(?, ?, ?, ?, ?)";

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setInt(1, idPrestamo);
            stmt.setString(2, observaciones);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.registerOutParameter(4, Types.INTEGER);
            stmt.registerOutParameter(5, Types.DECIMAL);

            stmt.execute();

            resultado.put("exito", stmt.getString(3).startsWith("OK:"));
            resultado.put("mensaje", stmt.getString(3));
            resultado.put("dias_retraso", stmt.getInt(4));
            resultado.put("multa_generada", stmt.getBigDecimal(5));

        } catch (SQLException e) {
            resultado.put("exito", false);
            resultado.put("mensaje", "Error: " + e.getMessage());
            handleSQLException("Error al registrar devolución", e);
        }

        return resultado;
    }

    /**
     * Renovar préstamo directamente con SQL
     */
    public boolean renovarPrestamo(int idPrestamo, int diasExtension) {
        String sql = "UPDATE prestamo SET " +
                "fecha_devolucion_esperada = DATE_ADD(fecha_devolucion_esperada, INTERVAL ? DAY), " +
                "renovaciones_realizadas = renovaciones_realizadas + 1, " +
                "estado = 'RENOVADO' " +
                "WHERE id_prestamo = ? AND estado = 'ACTIVO'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, diasExtension);
            stmt.setInt(2, idPrestamo);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                // Actualizar fecha última actividad del usuario
                actualizarUltimaActividadUsuario(idPrestamo);
                return true;
            }
            return false;

        } catch (SQLException e) {
            handleSQLException("Error al renovar préstamo", e);
            return false;
        }
    }

    // ========== MÉTODOS DE CONSULTA ==========

    /**
     * Obtener préstamos activos (usa tu vista v_prestamos_activos)
     */
    public List<Map<String, Object>> getPrestamosActivos() {
        List<Map<String, Object>> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM v_prestamos_activos";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> prestamo = new HashMap<>();
                prestamo.put("id_prestamo", rs.getInt("id_prestamo"));
                prestamo.put("libro", rs.getString("libro"));
                prestamo.put("isbn", rs.getString("isbn"));
                prestamo.put("usuario", rs.getString("usuario"));
                prestamo.put("bibliotecario", rs.getString("bibliotecario"));
                prestamo.put("fecha_prestamo", rs.getDate("fecha_prestamo"));
                prestamo.put("fecha_devolucion_esperada", rs.getDate("fecha_devolucion_esperada"));
                prestamo.put("dias_vencido", rs.getInt("dias_vencido"));
                prestamo.put("renovaciones_realizadas", rs.getInt("renovaciones_realizadas"));
                prestamo.put("max_renovaciones", rs.getInt("max_renovaciones"));

                prestamos.add(prestamo);
            }

        } catch (SQLException e) {
            handleSQLException("Error al obtener préstamos activos", e);
        }
        return prestamos;
    }

    /**
     * Obtener multas pendientes (usa tu vista v_multas_pendientes)
     */
    public List<Map<String, Object>> getMultasPendientes() {
        List<Map<String, Object>> multas = new ArrayList<>();
        String sql = "SELECT * FROM v_multas_pendientes";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> multa = new HashMap<>();
                multa.put("id_multa", rs.getInt("id_multa"));
                multa.put("dni", rs.getString("dni"));
                multa.put("usuario", rs.getString("usuario"));
                multa.put("email", rs.getString("email"));
                multa.put("telefono", rs.getString("telefono"));
                multa.put("libro", rs.getString("libro"));
                multa.put("fecha_devolucion_esperada", rs.getDate("fecha_devolucion_esperada"));
                multa.put("monto", rs.getBigDecimal("monto"));
                multa.put("dias_retraso", rs.getInt("dias_retraso"));
                multa.put("fecha_generacion", rs.getDate("fecha_generacion"));
                multa.put("dias_generada", rs.getInt("dias_generada"));

                multas.add(multa);
            }

        } catch (SQLException e) {
            handleSQLException("Error al obtener multas pendientes", e);
        }
        return multas;
    }

    /**
     * Obtener estadísticas de usuario (usa tu vista v_estadisticas_usuarios)
     */
    public Map<String, Object> getEstadisticasUsuario(int idUsuario) {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT * FROM v_estadisticas_usuarios WHERE id_usuario = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                stats.put("total_prestamos", rs.getInt("total_prestamos"));
                stats.put("prestamos_activos", rs.getInt("prestamos_activos"));
                stats.put("total_reservas", rs.getInt("total_reservas"));
                stats.put("total_multas", rs.getInt("total_multas"));
                stats.put("multas_pendientes", rs.getBigDecimal("multas_pendientes"));
            }

        } catch (SQLException e) {
            handleSQLException("Error al obtener estadísticas de usuario", e);
        }
        return stats;
    }

    /**
     * Obtener préstamos por usuario con información completa
     */
    public List<Prestamo> findPrestamosByUsuarioCompletos(int idUsuario) {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT p.*, " +
                "l.titulo, l.isbn, " +
                "u.nombre as nombre_usuario, u.dni, " +
                "b.nombre as nombre_bibliotecario " +
                "FROM prestamo p " +
                "JOIN libro l ON p.id_libro = l.id_libro " +
                "JOIN usuario u ON p.id_usuario = u.id_usuario " +
                "JOIN usuario b ON p.id_bibliotecario = b.id_usuario " +
                "WHERE p.id_usuario = ? " +
                "ORDER BY p.fecha_prestamo DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Prestamo prestamo = mapResultSetToEntity(rs);
                // Agregar información adicional si tu modelo la soporta
                prestamos.add(prestamo);
            }

        } catch (SQLException e) {
            handleSQLException("Error al buscar préstamos por usuario", e);
        }
        return prestamos;
    }

    /**
     * Buscar préstamos vencidos (ACTIVOS con fecha pasada)
     */
    public List<Prestamo> findPrestamosVencidos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamo " +
                "WHERE estado = 'ACTIVO' " +
                "AND fecha_devolucion_esperada < CURDATE() " +
                "ORDER BY fecha_devolucion_esperada ASC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                prestamos.add(mapResultSetToEntity(rs));
            }

        } catch (SQLException e) {
            handleSQLException("Error al buscar préstamos vencidos", e);
        }
        return prestamos;
    }

    /**
     * Contar préstamos activos de un usuario
     */
    public int contarPrestamosActivosUsuario(int idUsuario) {
        String sql = "SELECT COUNT(*) FROM prestamo " +
                "WHERE id_usuario = ? AND estado = 'ACTIVO'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            handleSQLException("Error al contar préstamos activos", e);
            return 0;
        }
    }

    /**
     * Verificar si un libro está prestado actualmente
     */
    public boolean estaLibroPrestado(int idLibro) {
        String sql = "SELECT COUNT(*) FROM prestamo " +
                "WHERE id_libro = ? AND estado = 'ACTIVO'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idLibro);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            handleSQLException("Error al verificar libro prestado", e);
            return false;
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Actualizar última actividad del usuario (usado por trigger)
     */
    private void actualizarUltimaActividadUsuario(int idPrestamo) {
        String sql = "UPDATE usuario u " +
                "JOIN prestamo p ON u.id_usuario = p.id_usuario " +
                "SET u.fecha_ultima_actividad = NOW() " +
                "WHERE p.id_prestamo = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idPrestamo);
            stmt.executeUpdate();

        } catch (SQLException e) {
            // No lanzar excepción, solo log
            System.err.println("Error al actualizar última actividad: " + e.getMessage());
        }
    }

    /**
     * Generar reporte de préstamos entre fechas
     */
    public List<Map<String, Object>> generarReportePrestamos(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Map<String, Object>> reporte = new ArrayList<>();
        String sql = "SELECT p.*, l.titulo, u.nombre as nombre_usuario, " +
                "b.nombre as nombre_bibliotecario, " +
                "DATEDIFF(p.fecha_devolucion_real, p.fecha_prestamo) as dias_reales " +
                "FROM prestamo p " +
                "JOIN libro l ON p.id_libro = l.id_libro " +
                "JOIN usuario u ON p.id_usuario = u.id_usuario " +
                "JOIN usuario b ON p.id_bibliotecario = b.id_usuario " +
                "WHERE p.fecha_prestamo BETWEEN ? AND ? " +
                "ORDER BY p.fecha_prestamo DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> registro = new HashMap<>();
                registro.put("id_prestamo", rs.getInt("id_prestamo"));
                registro.put("titulo", rs.getString("titulo"));
                registro.put("nombre_usuario", rs.getString("nombre_usuario"));
                registro.put("nombre_bibliotecario", rs.getString("nombre_bibliotecario"));
                registro.put("fecha_prestamo", rs.getDate("fecha_prestamo"));
                registro.put("fecha_devolucion_esperada", rs.getDate("fecha_devolucion_esperada"));
                registro.put("fecha_devolucion_real", rs.getDate("fecha_devolucion_real"));
                registro.put("estado", rs.getString("estado"));
                registro.put("dias_reales", rs.getInt("dias_reales"));

                reporte.add(registro);
            }

        } catch (SQLException e) {
            handleSQLException("Error al generar reporte", e);
        }
        return reporte;
    }

    /**
     * Obtener configuración del sistema
     */
    public int getDiasPrestamoConfig() {
        String sql = "SELECT valor FROM configuracion WHERE clave = 'dias_prestamo'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return Integer.parseInt(rs.getString("valor"));
            }
            return 14; // Valor por defecto

        } catch (SQLException e) {
            handleSQLException("Error al obtener configuración", e);
            return 14;
        }
    }

    /**
     * Verificar si un préstamo puede renovarse
     */
    public boolean puedeRenovar(int idPrestamo) {
        String sql = "SELECT p.renovaciones_realizadas, p.max_renovaciones, " +
                "p.estado, p.fecha_devolucion_esperada, " +
                "u.estado as estado_usuario " +
                "FROM prestamo p " +
                "JOIN usuario u ON p.id_usuario = u.id_usuario " +
                "WHERE p.id_prestamo = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idPrestamo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Verificar condiciones
                int renovaciones = rs.getInt("renovaciones_realizadas");
                int maxRenovaciones = rs.getInt("max_renovaciones");
                String estado = rs.getString("estado");
                Date fechaEsperada = rs.getDate("fecha_devolucion_esperada");
                String estadoUsuario = rs.getString("estado_usuario");

                // 1. Debe estar ACTIVO
                if (!"ACTIVO".equals(estado)) return false;

                // 2. Usuario debe estar ACTIVO
                if (!"ACTIVO".equals(estadoUsuario)) return false;

                // 3. No haber superado el máximo de renovaciones
                if (renovaciones >= maxRenovaciones) return false;

                // 4. No estar vencido
                if (fechaEsperada.before(new Date(System.currentTimeMillis()))) {
                    return false;
                }

                return true;
            }
            return false;

        } catch (SQLException e) {
            handleSQLException("Error al verificar renovación", e);
            return false;
        }
    }
}
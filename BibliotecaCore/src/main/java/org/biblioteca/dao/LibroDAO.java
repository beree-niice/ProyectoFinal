package org.biblioteca.dao;


import org.biblioteca.models.Libro;
import org.biblioteca.models.Categoria;
import org.biblioteca.models.Editorial;
import org.biblioteca.models.enums.EstadoFisico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibroDAO extends GenericDAO<Libro, Integer> {

    @Override
    protected String getTableName() {
        return "libro";
    }

    @Override
    protected String getIdColumnName() {
        return "id_libro";
    }

    @Override
    protected String buildInsertQuery() {
        return "INSERT INTO libro (isbn, titulo, id_editorial, año_publicacion, " +
                "id_categoria, num_paginas, idioma, copias_totales, copias_disponibles, " +
                "ubicacion, descripcion, estado_fisico, fecha_adquisicion, precio) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String buildUpdateQuery() {
        return "UPDATE libro SET isbn=?, titulo=?, id_editorial=?, año_publicacion=?, " +
                "id_categoria=?, num_paginas=?, idioma=?, copias_totales=?, " +
                "copias_disponibles=?, ubicacion=?, descripcion=?, estado_fisico=?, " +
                "precio=?, fecha_adquisicion=? WHERE id_libro=?";
    }

    @Override
    protected int getUpdateParameterCount() {
        return 15; // 14 parámetros + 1 ID
    }

    @Override
    protected Libro mapResultSetToEntity(ResultSet rs) throws SQLException {
        Libro libro = new Libro();
        libro.setIdLibro(rs.getInt("id_libro"));
        libro.setIsbn(rs.getString("isbn"));
        libro.setTitulo(rs.getString("titulo"));
        libro.setAñoPublicacion(rs.getInt("año_publicacion"));
        libro.setNumPaginas(rs.getInt("num_paginas"));
        libro.setIdioma(rs.getString("idioma"));
        libro.setCopiasTotales(rs.getInt("copias_totales"));
        libro.setCopiasDisponibles(rs.getInt("copias_disponibles"));
        libro.setUbicacion(rs.getString("ubicacion"));
        libro.setDescripcion(rs.getString("descripcion"));
        libro.setPortadaUrl(rs.getString("portada_url"));
        libro.setFechaAdquisicion(rs.getDate("fecha_adquisicion").toLocalDate());
        libro.setPrecio(rs.getBigDecimal("precio"));

        // Convertir String a Enum
        String estadoFisicoStr = rs.getString("estado_fisico");
        if (estadoFisicoStr != null) {
            libro.setEstadoFisico(EstadoFisico.valueOf(estadoFisicoStr.toUpperCase()));
        }

        // Nota: Para relaciones completas, necesitarías JOINs o DAOs separados
        return libro;
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Libro libro) throws SQLException {
        int index = 1;
        stmt.setString(index++, libro.getIsbn());
        stmt.setString(index++, libro.getTitulo());
        stmt.setInt(index++, libro.getEditorial().getIdEditorial());
        stmt.setInt(index++, libro.getAñoPublicacion());
        stmt.setInt(index++, libro.getCategoria().getIdCategoria());
        stmt.setInt(index++, libro.getNumPaginas());
        stmt.setString(index++, libro.getIdioma());
        stmt.setInt(index++, libro.getCopiasTotales());
        stmt.setInt(index++, libro.getCopiasDisponibles());
        stmt.setString(index++, libro.getUbicacion());
        stmt.setString(index++, libro.getDescripcion());
        stmt.setString(index++, libro.getEstadoFisico().name());
        stmt.setDate(index++, Date.valueOf(libro.getFechaAdquisicion()));
        stmt.setBigDecimal(index++, libro.getPrecio());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Libro libro) throws SQLException {
        setInsertParameters(stmt, libro);
        // Agregar fecha_adquisicion que falta en insert
        stmt.setDate(14, Date.valueOf(libro.getFechaAdquisicion()));
    }

    @Override
    protected Integer getIdFromEntity(Libro libro) {
        return libro.getIdLibro();
    }

    @Override
    protected void setGeneratedId(Libro libro, ResultSet rs) throws SQLException {
        libro.setIdLibro(rs.getInt(1));
    }

    // ========== MÉTODOS ESPECÍFICOS DE LIBRO ==========

    public Optional<Libro> findByIsbn(String isbn) {
        String sql = "SELECT * FROM libro WHERE isbn = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToEntity(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            handleSQLException("Error al buscar por ISBN", e);
            return Optional.empty();
        }
    }

    public List<Libro> findByTitulo(String titulo) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libro WHERE titulo LIKE ? ORDER BY titulo";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + titulo + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                libros.add(mapResultSetToEntity(rs));
            }

        } catch (SQLException e) {
            handleSQLException("Error en búsqueda por título", e);
        }
        return libros;
    }

    public List<Libro> findDisponibles() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libro WHERE copias_disponibles > 0 ORDER BY titulo";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                libros.add(mapResultSetToEntity(rs));
            }

        } catch (SQLException e) {
            handleSQLException("Error al obtener libros disponibles", e);
        }
        return libros;
    }

    public List<Libro> findByCategoria(int idCategoria) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libro WHERE id_categoria = ? ORDER BY titulo";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCategoria);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                libros.add(mapResultSetToEntity(rs));
            }

        } catch (SQLException e) {
            handleSQLException("Error al buscar por categoría", e);
        }
        return libros;
    }

    public List<Libro> findByEditorial(int idEditorial) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libro WHERE id_editorial = ? ORDER BY titulo";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idEditorial);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                libros.add(mapResultSetToEntity(rs));
            }

        } catch (SQLException e) {
            handleSQLException("Error al buscar por editorial", e);
        }
        return libros;
    }

    public boolean actualizarCopiasDisponibles(int idLibro, int cantidad) {
        String sql = "UPDATE libro SET copias_disponibles = copias_disponibles + ? WHERE id_libro = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cantidad);
            stmt.setInt(2, idLibro);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            handleSQLException("Error al actualizar copias disponibles", e);
            return false;
        }
    }

    public List<Libro> buscarAvanzada(String titulo, Integer idCategoria, Integer idEditorial,
                                      Integer añoDesde, Integer añoHasta) {
        List<Libro> libros = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM libro WHERE 1=1");
        List<Object> parametros = new ArrayList<>();

        if (titulo != null && !titulo.trim().isEmpty()) {
            sql.append(" AND titulo LIKE ?");
            parametros.add("%" + titulo + "%");
        }

        if (idCategoria != null && idCategoria > 0) {
            sql.append(" AND id_categoria = ?");
            parametros.add(idCategoria);
        }

        if (idEditorial != null && idEditorial > 0) {
            sql.append(" AND id_editorial = ?");
            parametros.add(idEditorial);
        }

        if (añoDesde != null) {
            sql.append(" AND año_publicacion >= ?");
            parametros.add(añoDesde);
        }

        if (añoHasta != null) {
            sql.append(" AND año_publicacion <= ?");
            parametros.add(añoHasta);
        }

        sql.append(" ORDER BY titulo");

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                libros.add(mapResultSetToEntity(rs));
            }

        } catch (SQLException e) {
            handleSQLException("Error en búsqueda avanzada", e);
        }
        return libros;
    }
}
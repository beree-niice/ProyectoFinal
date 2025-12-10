package clases.proyectopruebas.dao;


import clases.proyectopruebas.models.Libro;
import clases.proyectopruebas.models.enums.EstadoFisico;

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
        libro.setAnioPublicacion(rs.getInt("año_publicacion"));
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
        stmt.setInt(index++, libro.getAnioPublicacion());
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
    // 1. Sobrescribir findAll para traer DATOS REALES (Nombres) y no solo IDs
    @Override
    public List<Libro> findAll() {
        List<Libro> libros = new ArrayList<>();
        // Hacemos JOIN para traer el nombre de la editorial y categoría en una sola consulta
        String sql = "SELECT l.*, e.nombre as nom_editorial, c.nombre as nom_categoria " +
                "FROM libro l " +
                "LEFT JOIN editorial e ON l.id_editorial = e.id_editorial " +
                "LEFT JOIN categoria c ON l.id_categoria = c.id_categoria " +
                "ORDER BY l.titulo";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Libro libro = mapResultSetToEntity(rs);

                // Si usamos el query con JOIN, llenamos los nombres extra
                try {
                    libro.getEditorial().setNombre(rs.getString("nom_editorial"));
                    libro.getCategoria().setNombre(rs.getString("nom_categoria"));
                } catch (SQLException e) { /* Ignorar si columna no existe */ }

                // IMPORTANTE: Cargar autores (Relación N:M)
                libro.setAutores(obtenerAutoresPorLibro(libro.getIdLibro()));

                libros.add(libro);
            }
        } catch (SQLException e) {
            handleSQLException("Error al listar libros con detalles", e);
        }
        return libros;
    }

    // 2. Método auxiliar para cargar autores
    private List<clases.proyectopruebas.models.Autor> obtenerAutoresPorLibro(int idLibro) {
        List<clases.proyectopruebas.models.Autor> autores = new ArrayList<>();
        String sql = "SELECT a.* FROM autor a " +
                "INNER JOIN libro_autor la ON a.id_autor = la.id_autor " +
                "WHERE la.id_libro = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idLibro);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                clases.proyectopruebas.models.Autor a = new clases.proyectopruebas.models.Autor();
                a.setIdAutor(rs.getInt("id_autor"));
                a.setNombreCompleto(rs.getString("nombre_completo"));
                autores.add(a);
            }
        } catch (SQLException e) {
            System.err.println("Error cargando autores: " + e.getMessage());
        }
        return autores;
    }

    // 3. Sobrescribir save para guardar la relación en libro_autor
    @Override
    public Libro save(Libro entity) {
        // Primero guardamos el libro
        Libro libroGuardado = super.save(entity);

        // Si se guardó bien y tiene autores, guardamos la relación
        if (libroGuardado != null && entity.getAutores() != null && !entity.getAutores().isEmpty()) {
            guardarRelacionAutores(libroGuardado.getIdLibro(), entity.getAutores());
        }
        return libroGuardado;
    }

    private void guardarRelacionAutores(int idLibro, List<clases.proyectopruebas.models.Autor> autores) {
        String sql = "INSERT INTO libro_autor (id_libro, id_autor) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (clases.proyectopruebas.models.Autor autor : autores) {
                stmt.setInt(1, idLibro);
                stmt.setInt(2, autor.getIdAutor());
                stmt.addBatch(); // Optimización por lotes
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error guardando relación autores: " + e.getMessage());
        }
    }

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
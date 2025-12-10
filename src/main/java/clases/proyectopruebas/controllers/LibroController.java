package clases.proyectopruebas.controllers;


import clases.proyectopruebas.dao.AutorDAO;
import clases.proyectopruebas.dao.CategoriaDAO;
import clases.proyectopruebas.dao.EditorialDAO;
import clases.proyectopruebas.dao.LibroDAO;
import clases.proyectopruebas.models.Autor;
import clases.proyectopruebas.models.Categoria;
import clases.proyectopruebas.models.Editorial;
import clases.proyectopruebas.models.Libro;

import java.util.List;
import java.util.Optional;

public class LibroController {

    private final LibroDAO libroDAO;
    private final CategoriaDAO categoriaDAO;
    private final EditorialDAO editorialDAO;
    private final AutorDAO autorDAO;

    public LibroController() {
        this.libroDAO = new LibroDAO();
        this.categoriaDAO = new CategoriaDAO(); // Necesitarás crear este DAO simple
        this.editorialDAO = new EditorialDAO(); // Necesitarás crear este DAO simple
        this.autorDAO = new AutorDAO();
    }

    // --- Gestión de Libros ---
    public List<Libro> obtenerTodosLibros() {
        return libroDAO.findAll();
    }

    public boolean crearLibro(Libro libro) {
        // Validaciones de negocio antes de llamar al DAO
        if (libro.getCopiasTotales() < 0) return false;
        return libroDAO.save(libro) != null;
    }

    public boolean actualizarLibro(Libro libro) {
        return libroDAO.update(libro) != null;
    }

    public boolean eliminarLibro(int id) {
        return libroDAO.delete(id);
    }

    public Optional<Libro> obtenerLibro(int id) {
        // Aquí podríamos cargar detalles extra si el DAO no lo hiciera
        return libroDAO.findById(id);
    }

    public List<Libro> buscarPorTitulo(String titulo) {
        return libroDAO.findByTitulo(titulo);
    }

    public Optional<Libro> buscarPorISBN(String isbn) {
        return libroDAO.findByIsbn(isbn);
    }

    public String obtenerEstadisticas() {
        long total = libroDAO.count();
        long disponibles = libroDAO.findDisponibles().size();
        return "Total: " + total + " | Disponibles: " + disponibles;
    }

    // --- Datos Auxiliares para la Vista (ComboBoxes) ---

    public List<Categoria> obtenerCategorias() {
        return categoriaDAO.findAll();
    }

    public List<Editorial> obtenerEditoriales() {
        return editorialDAO.findAll();
    }

    public List<Autor> obtenerTodosAutores() {
        return autorDAO.findAll();
    }
}
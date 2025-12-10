package clases.proyectopruebas.models;

import clases.proyectopruebas.models.enums.EstadoFisico;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Libro {
    private int idLibro;
    private String isbn;
    private String titulo;

    // RELACIONES (Importante para la Vista)
    private Editorial editorial;
    private Categoria categoria;
    private List<Autor> autores;

    private int anioPublicacion;
    private int numPaginas;
    private String idioma;
    private int copiasTotales;
    private int copiasDisponibles;
    private String ubicacion;
    private String descripcion;
    private String portadaUrl;
    private LocalDate fechaAdquisicion;
    private BigDecimal precio;
    private EstadoFisico estadoFisico;


    public Libro() {
        this.autores = new ArrayList<>();
        this.editorial = new Editorial(); // Inicializar para evitar NullPointerException
        this.categoria = new Categoria();
        this.estadoFisico = EstadoFisico.BUENO;
        this.fechaAdquisicion = LocalDate.now();
    }

    // Getters y Setters
    public int getIdLibro() { return idLibro; }
    public void setIdLibro(int idLibro) { this.idLibro = idLibro; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Editorial getEditorial() { return editorial; }
    public void setEditorial(Editorial editorial) { this.editorial = editorial; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public List<Autor> getAutores() { return autores; }
    public void setAutores(List<Autor> autores) { this.autores = autores; }
    public void addAutor(Autor autor) { this.autores.add(autor); }

    public int getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(int añoPublicacion) { this.anioPublicacion = anioPublicacion; }

    public int getNumPaginas() { return numPaginas; }
    public void setNumPaginas(int numPaginas) { this.numPaginas = numPaginas; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public int getCopiasTotales() { return copiasTotales; }
    public void setCopiasTotales(int copiasTotales) { this.copiasTotales = copiasTotales; }

    public int getCopiasDisponibles() { return copiasDisponibles; }
    public void setCopiasDisponibles(int copiasDisponibles) { this.copiasDisponibles = copiasDisponibles; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getPortadaUrl() { return portadaUrl; }
    public void setPortadaUrl(String portadaUrl) { this.portadaUrl = portadaUrl; }

    public LocalDate getFechaAdquisicion() { return fechaAdquisicion; }
    public void setFechaAdquisicion(LocalDate fechaAdquisicion) { this.fechaAdquisicion = fechaAdquisicion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public EstadoFisico getEstadoFisico() { return estadoFisico; }
    public void setEstadoFisico(EstadoFisico estadoFisico) { this.estadoFisico = estadoFisico; }

    // Helpers para la Vista
    public String getDisponibilidadTexto() {
        return copiasDisponibles + "/" + copiasTotales;
    }

    public boolean estaDisponible() {
        return copiasDisponibles > 0;
    }

    public String getNombresAutores() {
        if (autores == null || autores.isEmpty()) return "Anónimo";
        return autores.stream().map(Autor::getNombreCompleto).collect(Collectors.joining(", "));
    }

    @Override
    public String toString() { return titulo; }
}
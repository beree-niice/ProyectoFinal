package org.biblioteca.models;

import org.biblioteca.models.enums.EstadoFisico;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Libro {
    private int idLibro;
    private String isbn;
    private String titulo;
    private Editorial editorial;
    private int añoPublicacion;
    private Categoria categoria;
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
        this.idioma = "Español";
        this.copiasTotales = 1;
        this.copiasDisponibles = 1;
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

    public int getAñoPublicacion() { return añoPublicacion; }
    public void setAñoPublicacion(int añoPublicacion) { this.añoPublicacion = añoPublicacion; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public int getNumPaginas() { return numPaginas; }
    public void setNumPaginas(int numPaginas) { this.numPaginas = numPaginas; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public int getCopiasTotales() { return copiasTotales; }
    public void setCopiasTotales(int copiasTotales) { this.copiasTotales = copiasTotales; }

    public int getCopiasDisponibles() { return copiasDisponibles; }
    public void setCopiasDisponibles(int copiasDisponibles) {
        this.copiasDisponibles = copiasDisponibles;
    }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getPortadaUrl() { return portadaUrl; }
    public void setPortadaUrl(String portadaUrl) { this.portadaUrl = portadaUrl; }

    public LocalDate getFechaAdquisicion() { return fechaAdquisicion; }
    public void setFechaAdquisicion(LocalDate fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public EstadoFisico getEstadoFisico() { return estadoFisico; }
    public void setEstadoFisico(EstadoFisico estadoFisico) { this.estadoFisico = estadoFisico; }

    // Métodos de negocio
    public boolean estaDisponible() {
        return this.copiasDisponibles > 0;
    }

    public void decrementarDisponibilidad() {
        if (this.copiasDisponibles > 0) {
            this.copiasDisponibles--;
        }
    }

    public void incrementarDisponibilidad() {
        if (this.copiasDisponibles < this.copiasTotales) {
            this.copiasDisponibles++;
        }
    }

    public String getDisponibilidadTexto() {
        return copiasDisponibles + "/" + copiasTotales;
    }

    @Override
    public String toString() {
        return titulo + " (" + isbn + ")";
    }
}

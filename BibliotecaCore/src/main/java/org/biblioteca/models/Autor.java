package org.biblioteca.models;

import java.time.LocalDate;

public class Autor {
    private int idAutor;
    private String nombreCompleto;
    private String nacionalidad;
    private LocalDate fechaNacimiento;
    private LocalDate fechaFallecimiento;
    private String biografia;
    private String premios;

    public Autor() {}

    public Autor(int idAutor, String nombreCompleto) {
        this.idAutor = idAutor;
        this.nombreCompleto = nombreCompleto;
    }

    // Getters y Setters
    public int getIdAutor() { return idAutor; }
    public void setIdAutor(int idAutor) { this.idAutor = idAutor; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public LocalDate getFechaFallecimiento() { return fechaFallecimiento; }
    public void setFechaFallecimiento(LocalDate fechaFallecimiento) { this.fechaFallecimiento = fechaFallecimiento; }

    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }

    public String getPremios() { return premios; }
    public void setPremios(String premios) { this.premios = premios; }

    @Override
    public String toString() {
        return nombreCompleto;
    }
}
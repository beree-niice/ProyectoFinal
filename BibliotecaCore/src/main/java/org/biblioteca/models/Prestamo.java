package org.biblioteca.models;

import org.biblioteca.models.enums.EstadoPrestamo;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Prestamo {
    private int idPrestamo;
    private Libro libro;
    private Usuario usuario;
    private Usuario bibliotecario;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEsperada;
    private LocalDate fechaDevolucionReal;
    private EstadoPrestamo estado;
    private int renovacionesRealizadas;
    private int maxRenovaciones;
    private String observaciones;

    public Prestamo() {
        this.fechaPrestamo = LocalDate.now();
        this.estado = EstadoPrestamo.ACTIVO;
        this.renovacionesRealizadas = 0;
        this.maxRenovaciones = 2;
    }

    // Getters y Setters
    public int getIdPrestamo() { return idPrestamo; }
    public void setIdPrestamo(int idPrestamo) { this.idPrestamo = idPrestamo; }

    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Usuario getBibliotecario() { return bibliotecario; }
    public void setBibliotecario(Usuario bibliotecario) { this.bibliotecario = bibliotecario; }

    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucionEsperada() { return fechaDevolucionEsperada; }
    public void setFechaDevolucionEsperada(LocalDate fechaDevolucionEsperada) {
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
    }

    public LocalDate getFechaDevolucionReal() { return fechaDevolucionReal; }
    public void setFechaDevolucionReal(LocalDate fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public EstadoPrestamo getEstado() { return estado; }
    public void setEstado(EstadoPrestamo estado) { this.estado = estado; }

    public int getRenovacionesRealizadas() { return renovacionesRealizadas; }
    public void setRenovacionesRealizadas(int renovacionesRealizadas) {
        this.renovacionesRealizadas = renovacionesRealizadas;
    }

    public int getMaxRenovaciones() { return maxRenovaciones; }
    public void setMaxRenovaciones(int maxRenovaciones) {
        this.maxRenovaciones = maxRenovaciones;
    }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    // Métodos de negocio
    public int calcularDiasRetraso() {
        if (fechaDevolucionReal != null) {
            long dias = ChronoUnit.DAYS.between(fechaDevolucionEsperada, fechaDevolucionReal);
            return (int) Math.max(0, dias);
        } else if (LocalDate.now().isAfter(fechaDevolucionEsperada)) {
            long dias = ChronoUnit.DAYS.between(fechaDevolucionEsperada, LocalDate.now());
            return (int) dias;
        }
        return 0;
    }

    public boolean puedeRenovarse() {
        return estado == EstadoPrestamo.ACTIVO &&
                renovacionesRealizadas < maxRenovaciones &&
                calcularDiasRetraso() == 0;
    }

    public boolean estaVencido() {
        return estado == EstadoPrestamo.ACTIVO &&
                LocalDate.now().isAfter(fechaDevolucionEsperada);
    }

    public void renovar() {
        if (puedeRenovarse()) {
            this.renovacionesRealizadas++;
            this.fechaDevolucionEsperada = this.fechaDevolucionEsperada.plusDays(14);
            this.estado = EstadoPrestamo.RENOVADO;
        }
    }
}
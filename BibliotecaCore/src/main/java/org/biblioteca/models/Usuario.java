package org.biblioteca.models;

import org.biblioteca.models.enums.EstadoUsuario;
import org.biblioteca.models.enums.TipoUsuario;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Usuario {
    private int idUsuario;
    private String dni;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private TipoUsuario tipoUsuario;
    private String passwordHash;
    private LocalDate fechaRegistro;
    private EstadoUsuario estado;
    private int limitePrestamos;
    private LocalDateTime fechaUltimaActividad;

    // Constructores
    public Usuario() {
        this.fechaRegistro = LocalDate.now();
        this.estado = EstadoUsuario.ACTIVO;
        this.limitePrestamos = 3;
    }

    public Usuario(String dni, String nombre, String email, TipoUsuario tipo) {
        this();
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
        this.tipoUsuario = tipo;
    }

    // Getters y Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public EstadoUsuario getEstado() { return estado; }
    public void setEstado(EstadoUsuario estado) { this.estado = estado; }

    public int getLimitePrestamos() { return limitePrestamos; }
    public void setLimitePrestamos(int limitePrestamos) { this.limitePrestamos = limitePrestamos; }

    public LocalDateTime getFechaUltimaActividad() { return fechaUltimaActividad; }
    public void setFechaUltimaActividad(LocalDateTime fechaUltimaActividad) {
        this.fechaUltimaActividad = fechaUltimaActividad;
    }

    // Métodos de negocio
    public boolean esAdmin() {
        return this.tipoUsuario == TipoUsuario.ADMIN;
    }

    public boolean esBibliotecario() {
        return this.tipoUsuario == TipoUsuario.BIBLIOTECARIO;
    }

    public boolean puedeRealizarPrestamo() {
        return this.estado == EstadoUsuario.ACTIVO;
    }

    @Override
    public String toString() {
        return nombre + " (" + dni + ")";
    }
}
package org.biblioteca.models.enums;

public enum EstadoMulta {
    PENDIENTE("Pendiente"),
    PAGADA("Pagada"),
    CONDONADA("Condonada");

    private final String nombre;

    EstadoMulta(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
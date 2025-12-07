package org.biblioteca.models.enums;

public enum EstadoUsuario {
    ACTIVO("Activo"),
    SUSPENDIDO("Suspendido"),
    MOROSO("Moroso"),
    INACTIVO("Inactivo");

    private final String nombre;

    EstadoUsuario(String nombre) {
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
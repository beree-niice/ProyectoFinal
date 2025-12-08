package org.biblioteca.models.enums;

public enum TipoUsuario {
    ADMIN("Administrador"),
    BIBLIOTECARIO("Bibliotecario"),
    LECTOR("Lector");

    private final String nombre;

    TipoUsuario(String nombre) {
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
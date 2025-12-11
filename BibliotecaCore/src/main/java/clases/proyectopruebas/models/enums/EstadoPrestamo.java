package clases.proyectopruebas.models.enums;

public enum EstadoPrestamo {
    ACTIVO("Activo"),
    DEVUELTO("Devuelto"),
    VENCIDO("Vencido"),
    RENOVADO("Renovado");

    private final String nombre;

    EstadoPrestamo(String nombre) {
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
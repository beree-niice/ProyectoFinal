package clases.proyectopruebas.models.enums;

public enum EstadoReserva {
    PENDIENTE("Pendiente"),
    DISPONIBLE("Disponible"),
    RECOGIDA("Recogida"),
    CANCELADA("Cancelada"),
    VENCIDA("Vencida");

    private final String nombre;

    EstadoReserva(String nombre) {
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

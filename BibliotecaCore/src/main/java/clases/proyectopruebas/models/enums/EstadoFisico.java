package clases.proyectopruebas.models.enums;

public enum EstadoFisico {
    EXCELENTE("Excelente"),
    BUENO("Bueno"),
    REGULAR("Regular"),
    MALO("Malo");

    private final String nombre;

    EstadoFisico(String nombre) {
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

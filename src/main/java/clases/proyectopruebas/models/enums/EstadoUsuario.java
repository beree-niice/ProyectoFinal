package clases.proyectopruebas.models.enums;

public enum EstadoUsuario {
    ACTIVO("Activo", "Usuario activo y operativo"),
    SUSPENDIDO("Suspendido", "Usuario suspendido temporalmente"),
    MOROSO("Moroso", "Usuario con deudas pendientes"),
    INACTIVO("Inactivo", "Usuario inactivo");

    private final String nombre;
    private final String descripcion;

    EstadoUsuario(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static EstadoUsuario fromString(String text) {
        if (text == null) return INACTIVO;

        for (EstadoUsuario estado : EstadoUsuario.values()) {
            if (estado.name().equalsIgnoreCase(text) ||
                    estado.nombre.equalsIgnoreCase(text)) {
                return estado;
            }
        }
        return INACTIVO;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
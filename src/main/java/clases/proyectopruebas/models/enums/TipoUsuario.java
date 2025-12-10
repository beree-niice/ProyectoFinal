package clases.proyectopruebas.models.enums;

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

    public static TipoUsuario fromString(String text) {
        if (text == null) return LECTOR;

        for (TipoUsuario tipo : TipoUsuario.values()) {
            if (tipo.name().equalsIgnoreCase(text) ||
                    tipo.nombre.equalsIgnoreCase(text)) {
                return tipo;
            }
        }
        return LECTOR;
    }

    @Override
    public String toString() {
        return nombre;
    }
}







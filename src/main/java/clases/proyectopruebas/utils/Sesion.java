package clases.proyectopruebas.utils;


import clases.proyectopruebas.models.Usuario;

public class Sesion {
    private static Sesion instance;
    private Usuario usuarioActual;

    private Sesion() {}

    public static Sesion getInstance() {
        if (instance == null) instance = new Sesion();
        return instance;
    }

    public void login(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void logout() {
        this.usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean estaLogueado() {
        return usuarioActual != null;
    }
}
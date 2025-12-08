package org.biblioteca.models;

public class Editorial {
    private int idEditorial;
    private String nombre;
    private String pais;
    private String ciudad;
    private String telefono;
    private String email;
    private String sitioWeb;
    private boolean activa;

    public Editorial() {
        this.activa = true;
    }

    // Getters y Setters
    public int getIdEditorial() { return idEditorial; }
    public void setIdEditorial(int idEditorial) { this.idEditorial = idEditorial; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSitioWeb() { return sitioWeb; }
    public void setSitioWeb(String sitioWeb) { this.sitioWeb = sitioWeb; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    @Override
    public String toString() {
        return nombre;
    }
}

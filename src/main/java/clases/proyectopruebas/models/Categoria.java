package clases.proyectopruebas.models;

public class Categoria {
    private int idCategoria;
    private String nombre;
    private String descripcion;
    private String codigo;
    private boolean activa;

    public Categoria() {
        this.activa = true;
    }

    public Categoria(String nombre, String codigo) {
        this();
        this.nombre = nombre;
        this.codigo = codigo;
    }

    // Getters y Setters
    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    @Override
    public String toString() {
        return nombre;
    }
}

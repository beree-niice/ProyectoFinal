package org.biblioteca.models;

import org.biblioteca.models.enums.EstadoMulta;
import org.biblioteca.models.enums.MetodoPago;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Multa {
    private int idMulta;
    private Prestamo prestamo; // Relación
    private BigDecimal monto;
    private BigDecimal montoPorDia;
    private int diasRetraso;
    private LocalDate fechaGeneracion;
    private LocalDate fechaPago;
    private MetodoPago metodoPago;
    private EstadoMulta estado;
    private Usuario usuarioRecibePago; // El bibliotecario/admin que cobra
    private String observaciones;

    public Multa() {}

    // Getters y Setters
    public int getIdMulta() { return idMulta; }
    public void setIdMulta(int idMulta) { this.idMulta = idMulta; }

    public Prestamo getPrestamo() { return prestamo; }
    public void setPrestamo(Prestamo prestamo) { this.prestamo = prestamo; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public BigDecimal getMontoPorDia() { return montoPorDia; }
    public void setMontoPorDia(BigDecimal montoPorDia) { this.montoPorDia = montoPorDia; }

    public int getDiasRetraso() { return diasRetraso; }
    public void setDiasRetraso(int diasRetraso) { this.diasRetraso = diasRetraso; }

    public LocalDate getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDate fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }

    public EstadoMulta getEstado() { return estado; }
    public void setEstado(EstadoMulta estado) { this.estado = estado; }

    public Usuario getUsuarioRecibePago() { return usuarioRecibePago; }
    public void setUsuarioRecibePago(Usuario usuarioRecibePago) { this.usuarioRecibePago = usuarioRecibePago; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}

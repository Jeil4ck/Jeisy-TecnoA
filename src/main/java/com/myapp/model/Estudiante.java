package com.myapp.model;

public class Estudiante extends Persona {
    private Double codigo;
    private Programa programa;
    private Boolean activo;
    private Double promedio;

    public Estudiante(double id, String nombres, String apellidos, String email,
                      double codigo, Programa programa, boolean activo, double promedio) {
        super(id, nombres, apellidos, email);
        this.codigo = codigo;
        this.programa = programa;
        this.activo = activo;
        this.promedio = promedio;
    }

    public Double getCodigo() { return codigo; }
    public void setCodigo(Double codigo) { this.codigo = codigo; }

    public Programa getPrograma() { return programa; }
    public void setPrograma(Programa programa) { this.programa = programa; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Double getPromedio() { return promedio; }
    public void setPromedio(Double promedio) { this.promedio = promedio; }

    @Override
    public String toString() {
        return "Estudiante{" + super.toString() + ", codigo=" + codigo +
               ", programa=" + (programa!=null?programa.getNombre():"null") +
               ", activo=" + activo + ", promedio=" + promedio + "}";
    }
}

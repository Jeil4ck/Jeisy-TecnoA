package com.myapp.model;

public class Curso {
    private Double id;
    private String nombre;
    private Boolean activo;

    public Curso(Double id, String nombre, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.activo = activo;
    }

    public Double getId() { return id; }
    public void setId(Double id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "Curso{id=" + id + ", nombre='" + nombre + "', activo=" + activo + "}";
    }
}

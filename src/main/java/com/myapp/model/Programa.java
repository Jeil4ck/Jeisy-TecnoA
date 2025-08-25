package com.myapp.model;

import java.time.LocalDate;

public class Programa {
    private Integer id;
    private String nombre;
    private Double duracion;
    private LocalDate registro;
    private Facultad facultad;

    public Programa(Integer id, String nombre, Double duracion, LocalDate registro, Facultad facultad) {
        this.id = id;
        this.nombre = nombre;
        this.duracion = duracion;
        this.registro = registro;
        this.facultad = facultad;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getDuracion() { return duracion; }
    public void setDuracion(Double duracion) { this.duracion = duracion; }

    public LocalDate getRegistro() { return registro; }
    public void setRegistro(LocalDate registro) { this.registro = registro; }

    public Facultad getFacultad() { return facultad; }
    public void setFacultad(Facultad facultad) { this.facultad = facultad; }

    @Override
    public String toString() {
        return "Programa{id=" + id + ", nombre='" + nombre + "', duracion=" + duracion + ", registro=" + registro + ", facultad=" + (facultad!=null?facultad.getNombre():"null") + "}";
    }
}

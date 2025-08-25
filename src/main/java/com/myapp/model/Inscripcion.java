package com.myapp.model;

public class Inscripcion {
    private Curso curso;
    private Integer anio;
    private Integer semestre;
    private Estudiante estudiante;

    public Inscripcion(Curso curso, Integer anio, Integer semestre, Estudiante estudiante) {
        this.curso = curso;
        this.anio = anio;
        this.semestre = semestre;
        this.estudiante = estudiante;
    }

    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public Integer getSemestre() { return semestre; }
    public void setSemestre(Integer semestre) { this.semestre = semestre; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    @Override
    public String toString() {
        return "Inscripcion{curso=" + (curso!=null?curso.getNombre():"null") +
               ", anio=" + anio + ", semestre=" + semestre +
               ", estudiante=" + (estudiante!=null?estudiante.getNombres():"null") + "}";
    }
}

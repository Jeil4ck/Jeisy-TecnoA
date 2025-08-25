package com.myapp;

import com.myapp.model.*;
import com.myapp.repository.InscripcionesPersonas;

public class Main {
    public static void main(String[] args) {
        var repo = new InscripcionesPersonas();

        // Si usas BD en archivo y quieres vaciar al salir:
        // Runtime.getRuntime().addShutdownHook(new Thread(repo::truncateTabla));

        Programa progIng = new Programa(1, "Ing. Sistemas", 10.0, java.time.LocalDate.now(), null);
        Curso avanzadas = new Curso(101.0, "Tecnologias Avanzadas", true);

        Profesor prof1 = new Profesor(2001, "Roger", "Calderon", "RogerC@unillanos.edu.co", "Planta");
        Estudiante est1 = new Estudiante(1001, "Jeisy", "Bermudez", "jlbermudez@unillanos.edu.co", 4802, progIng, true, 4.7);
        Estudiante est2 = new Estudiante(1002, "Andrea", "Rojas", "ARojas@unillanos.edu.co", 5002, progIng, true, 4.8);

        // Lista en memoria (diagrama)
        repo.inscribir(prof1);
        repo.inscribir(est1);
        repo.inscribir(est2);

        // Persistir SOLO Personas (requisito del profe)
        repo.guardarInformacion(prof1);
        repo.guardarInformacion(est1);
        repo.guardarInformacion(est2);

        // Leer desde H2 y mostrar
        repo.cargarDatos();
        System.out.println("Cantidad de personas: " + repo.cantidadActual());
        for (String s : repo.imprimirListado()) {
            System.out.println(s);
        }

        System.out.println("\n[APP] Fin de ejecución.");
    }
}

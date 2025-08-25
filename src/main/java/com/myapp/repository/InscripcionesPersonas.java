package com.myapp.repository;

import com.myapp.dao.PersonaDAO;
import com.myapp.model.Persona;
import com.myapp.servicios.Servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InscripcionesPersonas implements Servicios {
    private final List<Persona> listaPersonas = new ArrayList<>();
    private final PersonaDAO dao = new PersonaDAO();

    public InscripcionesPersonas() {
        dao.createTableIfNotExists();
    }

    // CRUD en memoria
    public boolean inscribir(Persona persona) {
        Objects.requireNonNull(persona, "persona");
        int idx = indexOfById(persona.getId());
        if (idx != -1) return false;
        return listaPersonas.add(persona);
    }

    public boolean eliminar(Persona persona) {
        int idx = indexOfById(persona.getId());
        if (idx == -1) return false;
        listaPersonas.remove(idx);
        return true;
    }

    public boolean actualizar(Persona persona) {
        int idx = indexOfById(persona.getId());
        if (idx == -1) return false;
        listaPersonas.set(idx, persona);
        return true;
    }

    private int indexOfById(double id) {
        for (int i = 0; i < listaPersonas.size(); i++) {
            if (Double.compare(listaPersonas.get(i).getId(), id) == 0) return i;
        }
        return -1;
    }

    // Persistencia H2
    public void guardarInformacion(Persona persona) {
        if (!dao.update(persona)) {
            dao.insert(persona);
        }
    }

    public void cargarDatos() {
        listaPersonas.clear();
        listaPersonas.addAll(dao.findAll());
    }

    public void truncateTabla() { dao.truncate(); }

    // Servicios
    @Override
    public String imprimirPosicion(int posicion) {
        if (posicion < 0 || posicion >= listaPersonas.size()) return "Posición inválida";
        return listaPersonas.get(posicion).toString();
    }

    @Override
    public Integer cantidadActual() { return listaPersonas.size(); }

    @Override
    public List<String> imprimirListado() {
        List<String> out = new ArrayList<>();
        for (Persona p : listaPersonas) out.add(p.toString());
        return out;
    }
}

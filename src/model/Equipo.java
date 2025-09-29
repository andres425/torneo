package model;

import java.util.ArrayList;
import java.util.List;

public class Equipo {
    private String nombre;
    private List<Jugador> jugadores;
    private int puntos;
    private int golesFavor;
    private int golesContra;

    public Equipo(String nombre) {
        setNombre(nombre);
        this.jugadores = new ArrayList<>();
        this.puntos = 0;
        this.golesFavor = 0;
        this.golesContra = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty() || !nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            throw new IllegalArgumentException("el nombre debe contener algo y ser solo letras");
        } else {
            this.nombre = nombre;
        }
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<Jugador> jugadores) {
        if (jugadores == null || jugadores.isEmpty()) {
            throw new IllegalArgumentException("El equipo debe tener al menos un jugador");
        }
        this.jugadores = jugadores;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        if (puntos >= 0) {
            this.puntos = puntos;
        } else {
            throw new IllegalArgumentException("Los puntos deben ser mayor o igual a 0");
        }
    }

    public int getGolesFavor() {
        return golesFavor;
    }

    public void setGolesFavor(int golesFavor) {
        if (golesFavor >= 0) {
            this.golesFavor = golesFavor;
        } else {
             throw new IllegalArgumentException("Los goles a favor deben ser mayor o igual a 0");
        }
    }

    public int getGolesContra() {
        return golesContra;
    }

    public void setGolesContra(int golesContra) {
        if (golesContra >= 0) {
            this.golesContra = golesContra;
        } else {
            throw new IllegalArgumentException("Los goles en contra deben ser mayor o igual a 0");
        }
    }

    public void agregarJugador(Jugador jugador) {
        if (jugador == null) {
            throw new IllegalArgumentException("El jugador no puede ser null");
        }
        jugadores.add(jugador);
    }

 public void mostrarJugadores() {
    if (jugadores == null || jugadores.isEmpty()) {
        System.out.println("El equipo no tiene jugadores registrados.");
    } else {
        for (Jugador j : jugadores) {
            System.out.println(j); 
        }
    }
}




}

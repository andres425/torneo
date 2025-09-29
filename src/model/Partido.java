package model;

import java.time.LocalTime;

public class Partido {
    private Equipo equipoLocal;
    private Equipo equipoVisitante;
    private int golesLocal;
    private int golesVisitante;
    private boolean jugado;
    private LocalTime hora;

    public Partido(Equipo equipoLocal, Equipo equipoVisitante, int golesLocal, int golesVisitante, boolean jugado,
            LocalTime hora) {
        setEquipoLocal(equipoLocal);
        setEquipoVisitante(equipoVisitante);
        setGolesLocal(golesLocal);
        setGolesVisitante(golesVisitante);
        setJugado(jugado);
        setHora(hora);
    }

    public Equipo getEquipoLocal() {
        return equipoLocal;
    }

    public void setEquipoLocal(Equipo equipoLocal) {
        if (equipoLocal == null || equipoLocal.getNombre() == null || equipoLocal.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El equipo local no puede estar vacío.");
        }
        this.equipoLocal = equipoLocal;

    }

    public Equipo getEquipoVisitante() {
        return equipoVisitante;
    }

    public void setEquipoVisitante(Equipo equipoVisitante) {
        if (equipoVisitante == null || equipoVisitante.getNombre() == null
                || equipoVisitante.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El equipo visitante no puede estar vacío.");
        }
        this.equipoVisitante = equipoVisitante;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(int golesLocal) {
        if (golesLocal >= 0) {
            this.golesLocal = golesLocal;
        } else {
            throw new IllegalArgumentException("los goles del local deben ser mayor o igual a 0");
        }
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(int golesVisitante) {
        if (golesVisitante >= 0) {
            this.golesVisitante = golesVisitante;
        } else {
            throw new IllegalArgumentException("los goles de visitante deben ser mayores o iguales a 0");
        }
    }

    public boolean getJugado() {
        return jugado;
    }

    public void setJugado(boolean jugado) {
        this.jugado = jugado;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        if (hora == null) {
            throw new IllegalArgumentException("la hora no puede ser nula");
        }
        this.hora = hora;
    }

    public void registrarResultados(int golesLocal, int golesVisitante) {
        if (golesLocal < 0 || golesVisitante < 0) {
            throw new IllegalArgumentException("Los goles no pueden ser negativos");
        }
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
        this.jugado = true; // cuando ya hay resultados, el partido se marca como jugado
    }

    public Equipo obtenerGanador() {
        if (!jugado) {
            throw new IllegalStateException("El partido aún no se ha jugado.");
        }

        if (golesLocal > golesVisitante) {
            return equipoLocal;
        } else if (golesVisitante > golesLocal) {
            return equipoVisitante;
        } else {
            return null;
        }
    }

    public String resumen() {
        if (!jugado) {
            return equipoLocal.getNombre() + " vs " + equipoVisitante.getNombre() +
                    " (Pendiente a las " + hora + ")";
        }
        return equipoLocal.getNombre() + " " + golesLocal +
                " - " + golesVisitante + " " + equipoVisitante.getNombre();
    }

    public boolean esEmpate() {
        return jugado && golesLocal == golesVisitante;
    }

    public boolean ganoLocal() {
        return jugado && golesLocal > golesVisitante;
    }

    public boolean ganoVisitante() {
        return jugado && golesVisitante > golesLocal;
    }

}

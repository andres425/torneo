package model;

import java.time.LocalTime;
import java.util.Objects;

public class Partido {
    private Equipo equipoLocal;
    private Equipo equipoVisitante;
    private int golesLocal;
    private int golesVisitante;
    private boolean jugado;
    private LocalTime hora;

    public Partido(Equipo equipoLocal, Equipo equipoVisitante, LocalTime hora) {
        this.equipoLocal = Objects.requireNonNull(equipoLocal, "El equipo local no puede ser nulo");
        this.equipoVisitante = Objects.requireNonNull(equipoVisitante, "El equipo visitante no puede ser nulo");
        if (equipoLocal.equals(equipoVisitante)) {
            throw new IllegalArgumentException("Un equipo no puede jugar contra sí mismo");
        }
        this.hora = Objects.requireNonNull(hora, "La hora no puede ser nula");
        this.golesLocal = 0;
        this.golesVisitante = 0;
        this.jugado = false;
    }

    public Equipo getEquipoLocal() {
        return equipoLocal;
    }

    public Equipo getEquipoVisitante() {
        return equipoVisitante;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }
    public boolean getJugado() {
    return jugado;
}


    public boolean isJugado() {
        return jugado;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void registrarResultados(int golesLocal, int golesVisitante) {
        if (golesLocal < 0 || golesVisitante < 0) {
            throw new IllegalArgumentException("Los goles no pueden ser negativos");
        }
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
        this.jugado = true;
    }

    public Equipo obtenerGanador() {
        if (!jugado) {
            throw new IllegalStateException("El partido aún no se ha jugado.");
        }
        if (golesLocal > golesVisitante) return equipoLocal;
        if (golesVisitante > golesLocal) return equipoVisitante;
        return null; // empate
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

    public String resumen() {
        if (!jugado) {
            return equipoLocal.getNombre() + " vs " + equipoVisitante.getNombre() +
                    " (Pendiente a las " + hora + ")";
        }
        return equipoLocal.getNombre() + " " + golesLocal +
                " - " + golesVisitante + " " + equipoVisitante.getNombre();
    }

    @Override
    public String toString() {
        return resumen();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Partido)) return false;
        Partido partido = (Partido) o;
        return Objects.equals(equipoLocal, partido.equipoLocal) &&
               Objects.equals(equipoVisitante, partido.equipoVisitante) &&
               Objects.equals(hora, partido.hora);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipoLocal, equipoVisitante, hora);
    }
}

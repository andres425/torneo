package model;

import java.time.LocalDateTime;
import java.util.*;

public class Partido {
    private Equipo equipoLocal;
    private Equipo equipoVisitante;
    private int golesLocal;
    private int golesVisitante;
    private boolean jugado;
    private LocalDateTime fechaHora;
    private final Map<String, Integer> golesPorJugador = new HashMap<>();

    private List<String> eventos = new ArrayList<>();
    private Integer penalesLocal;
private Integer penalesVisitante;
private Equipo ganadorPorPenales;

    public Partido(Equipo equipoLocal, Equipo equipoVisitante, LocalDateTime fechaHora) {
        this.equipoLocal = Objects.requireNonNull(equipoLocal, "El equipo local no puede ser nulo");
        this.equipoVisitante = Objects.requireNonNull(equipoVisitante, "El equipo visitante no puede ser nulo");

        if (equipoLocal.equals(equipoVisitante)) {
            throw new IllegalArgumentException("Un equipo no puede jugar contra sí mismo");
        }

        this.fechaHora = fechaHora; // puede ser null si aún no se programa
        this.golesLocal = 0;
        this.golesVisitante = 0;
        this.jugado = false;
    }
    public Partido(Equipo local, Equipo visitante) {
    this.equipoLocal = local;
    this.equipoVisitante = visitante;
    this.fechaHora = null; // Se programará después
    this.jugado = false;
}

    // Getters y setters

public Integer getPenalesLocal() {
    return penalesLocal;
}

public Integer getPenalesVisitante() {
    return penalesVisitante;
}

public Equipo getGanadorPorPenales() {
    return ganadorPorPenales;
}

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Equipo getEquipoLocal() {
        return equipoLocal;
    }

    public void setEquipoLocal(Equipo equipoLocal) {
        this.equipoLocal = equipoLocal;
    }

    public Equipo getEquipoVisitante() {
        return equipoVisitante;
    }

    public void setEquipoVisitante(Equipo equipoVisitante) {
        this.equipoVisitante = equipoVisitante;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(int golesLocal) {
        this.golesLocal = golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(int golesVisitante) {
        this.golesVisitante = golesVisitante;
    }

    public boolean getJugado() {
        return jugado;
    }

    public void setJugado(boolean jugado) {
        this.jugado = jugado;
    }

  public Map<String, Integer> getGolesPorJugador() {
    return Collections.unmodifiableMap(golesPorJugador);
}

    public List<String> getEventos() {
        return eventos;
    }


    // Reglas de partido
    public boolean ganoLocal() {
        return jugado && golesLocal > golesVisitante;
    }

    public boolean ganoVisitante() {
        return jugado && golesVisitante > golesLocal;
    }

    public boolean esEmpate() {
        return jugado && golesLocal == golesVisitante;
    }
    public void setResultadoPenales(int penalesLocal, int penalesVisitante, Equipo ganador) {
    this.penalesLocal = penalesLocal;
    this.penalesVisitante = penalesVisitante;
    this.ganadorPorPenales = ganador;
}

    public void registrarResultados(int golesLocal, int golesVisitante) {
        if (golesLocal < 0 || golesVisitante < 0) {
            throw new IllegalArgumentException("Los goles no pueden ser negativos");
        }
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
        this.jugado = true;
    }

    // Registro de eventos
public void agregarGol(Equipo equipo, Jugador jugador) {
    if (jugador == null) {
        throw new IllegalArgumentException("⚠ El jugador recibido es null, no se puede registrar el gol.");
    }

    // incrementar contador en el objeto jugador (si usas las mismas instancias esto mantiene j.getGoles())
    jugador.setGoles(jugador.getGoles() + 1);

    if (equipo == equipoLocal) {
        golesLocal++;
    } else if (equipo == equipoVisitante) {
        golesVisitante++;
    }

    // Registrar también por partido (clave -> Jugador|Equipo)
    String clave = jugador.getNombre() + "||" + (equipo != null ? equipo.getNombre() : "Desconocido");
    golesPorJugador.put(clave, golesPorJugador.getOrDefault(clave, 0) + 1);
}



    public void agregarTarjeta(Equipo equipo, Jugador jugador, String tipo) {
        if (!equipo.equals(equipoLocal) && !equipo.equals(equipoVisitante)) {
            throw new IllegalArgumentException("El jugador no pertenece a ninguno de los equipos del partido");
        }

        if (tipo.equalsIgnoreCase("Amarilla")) {
            jugador.agregarTarjetaAmarilla();
            eventos.add("🟨 Amarilla para " + jugador.getNombre() + " (" + equipo.getNombre() + ")");
        } else if (tipo.equalsIgnoreCase("Roja")) {
            jugador.agregarTarjetaRoja();
            eventos.add("🟥 Roja para " + jugador.getNombre() + " (" + equipo.getNombre() + ")");
        } else {
            throw new IllegalArgumentException("Tipo de tarjeta no válido: " + tipo);
        }
    }
    public Equipo getGanador() {
    if (getGolesLocal() > getGolesVisitante()) {
        return equipoLocal;
    } else if (getGolesVisitante() > getGolesLocal()) {
        return equipoVisitante;
    } else if (ganadorPorPenales != null) {
        return ganadorPorPenales;
    }
    return null; // Empate sin definir → no debería pasar en eliminatorias
}

public Equipo getPerdedor() {
    Equipo ganador = getGanador();
    if (ganador == null) return null;
    return (ganador == equipoLocal) ? equipoVisitante : equipoLocal;
}


    public String resumen() {
        if (!jugado) {
            return equipoLocal.getNombre() + " vs " + equipoVisitante.getNombre() +
                    " (Pendiente " + fechaHora + ")";
        }
        return equipoLocal.getNombre() + " " + golesLocal +
                " - " + golesVisitante + " " + equipoVisitante.getNombre() +
                " [" + fechaHora + "]";
    }

    @Override
    public String toString() {
        return resumen();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Partido))
            return false;
        Partido partido = (Partido) o;
        return Objects.equals(equipoLocal, partido.equipoLocal) &&
                Objects.equals(equipoVisitante, partido.equipoVisitante) &&
                Objects.equals(fechaHora, partido.fechaHora);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipoLocal, equipoVisitante, fechaHora);
    }

}

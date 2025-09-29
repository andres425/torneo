package model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Torneo {
    private String nombre;
    private List<Equipo> equipos;
    private List<Partido> partidos;

    public Torneo(String nombre) {
        setNombre(nombre);
        this.equipos = new ArrayList<>();
        this.partidos = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty() || !nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            throw new IllegalArgumentException(
                    "El nombre del equipo debe contener solo letras y no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public List<Equipo> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<Equipo> equipos) {
        if (equipos == null || equipos.isEmpty() || equipos.contains(null)) {
            throw new IllegalArgumentException(
                    "La lista de equipos no puede ser nula, vacía ni contener valores nulos");
        }
        this.equipos = equipos;
    }

    public List<Partido> getPartidos() {
        return partidos;
    }

    public void setPartidos(List<Partido> partidos) {
        if (partidos == null || partidos.isEmpty() || partidos.contains(null)) {
            throw new IllegalArgumentException(
                    "la lista de partidos no puede ser nula,vacia ni contener valores nulos");
        }
        this.partidos = partidos;
    }

    public void agregarEquipo(Equipo equipo) {
        if (equipo == null || equipos.contains(equipo)) {
            throw new IllegalArgumentException("El equipo no puede ser nulo ni estar duplicado en el torneo");
        }
        equipos.add(equipo);
    }

    public void programarPartido(Equipo equipoLocal, Equipo equipoVisitante, LocalTime hora) {

        if (equipoLocal == null || equipoVisitante == null || hora == null ||
                equipoLocal.equals(equipoVisitante) ||
                !equipos.contains(equipoLocal) || !equipos.contains(equipoVisitante)) {

            throw new IllegalArgumentException(
                    "Partido inválido: equipos nulos, iguales o no registrados, o hora nula");
        }

        Partido partido = new Partido(equipoLocal, equipoVisitante, 0, 0, false, hora);

        if (partidos.contains(partido)) {
            throw new IllegalArgumentException("El partido ya está programado");
        }

        partidos.add(partido);
    }

    public void mostrarTablaPosiciones() {
        System.out.println("TABLA DE POSICIONES");
        System.out.println("Equipo\tPJ\tGF\tGC\tDG\tPuntos");

        // Clase interna para estadísticas
        class Stats {
            Equipo equipo;
            int PJ = 0;
            int GF = 0;
            int GC = 0;
            int puntos = 0;

            public int getDG() {
                return GF - GC;
            } // Diferencia de goles
        }

        // Crear tabla de estadísticas
        List<Stats> tabla = new ArrayList<>();
        for (Equipo e : equipos) {
            Stats st = new Stats();
            st.equipo = e;
            tabla.add(st);
        }

        // Recorremos partidos y actualizamos stats
        for (Partido p : partidos) {
            if (p.getJugado()) {
                Stats local = null;
                Stats visitante = null;

                // Buscar en la tabla el equipo local y visitante
                for (Stats s : tabla) {
                    if (s.equipo.equals(p.getEquipoLocal())) {
                        local = s;
                    }
                    if (s.equipo.equals(p.getEquipoVisitante())) {
                        visitante = s;
                    }
                }

                if (local == null || visitante == null)
                    continue;

                local.PJ++;
                visitante.PJ++;

                local.GF += p.getGolesLocal();
                local.GC += p.getGolesVisitante();

                visitante.GF += p.getGolesVisitante();
                visitante.GC += p.getGolesLocal();

                if (p.getGolesLocal() > p.getGolesVisitante()) {
                    local.puntos += 3;
                } else if (p.getGolesLocal() < p.getGolesVisitante()) {
                    visitante.puntos += 3;
                } else {
                    local.puntos += 1;
                    visitante.puntos += 1;
                }
            }
        }

        // Ordenar por puntos y diferencia de goles
        Collections.sort(tabla, new Comparator<Stats>() {
            @Override
            public int compare(Stats a, Stats b) {
                if (b.puntos != a.puntos) {
                    return Integer.compare(b.puntos, a.puntos);
                }
                return Integer.compare(b.getDG(), a.getDG());
            }
        });

        // Mostrar tabla final
        for (Stats s : tabla) {
            System.out.printf("%s\t%d\t%d\t%d\t%d\t%d%n",
                    s.equipo.getNombre(),
                    s.PJ,
                    s.GF,
                    s.GC,
                    s.getDG(),
                    s.puntos);
        }
    }

    public void mostrarPartidos() {
        if (partidos == null || partidos.isEmpty()) {
            System.out.println("⚠ No hay partidos registrados en el torneo.");
            return;
        }

        System.out.println("\n📅 LISTA DE PARTIDOS");
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-20s %-3s %-20s %-15s%n",
                "Equipo Local", " ", "Equipo Visitante", "Estado");
        System.out.println("------------------------------------------------------------");

        for (Partido p : partidos) {
            String estado = p.getJugado() ? "✅ JUGADO" : "⌛ PENDIENTE";
            String marcador = p.getJugado()
                    ? p.getGolesLocal() + " - " + p.getGolesVisitante()
                    : "vs";

            System.out.printf("%-20s %-3s %-20s %-15s%n",
                    p.getEquipoLocal().getNombre(),
                    marcador,
                    p.getEquipoVisitante().getNombre(),
                    estado);
        }

        System.out.println("------------------------------------------------------------\n");
    }

    public Equipo buscarEquipo(String nombre) {
        return equipos.stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    public void mostrarPartidosPendientes() {
        partidos.stream()
                .filter(p -> !p.getJugado())
                .forEach(p -> System.out.println(
                        p.getEquipoLocal().getNombre() + " vs " +
                                p.getEquipoVisitante().getNombre() + " a las " +
                                p.getHora()));
    }

    public void mostrarPartidosJugados() {
        partidos.stream()
                .filter(Partido::getJugado)
                .forEach(p -> System.out.println(
                        p.getEquipoLocal().getNombre() + " " + p.getGolesLocal() +
                                " - " + p.getGolesVisitante() + " " +
                                p.getEquipoVisitante().getNombre()));
    }

    public void mostrarGoleadores() {
        if (equipos == null || equipos.isEmpty()) {
            System.out.println("⚠ No hay equipos en el torneo.");
            return;
        }

        // Lista para guardar a todos los jugadores
        List<Jugador> goleadores = new ArrayList<>();

        // Recorremos todos los equipos y agregamos sus jugadores
        for (Equipo e : equipos) {
            goleadores.addAll(e.getJugadores());
        }

        if (goleadores.isEmpty()) {
            System.out.println("⚠ No hay jugadores registrados en el torneo.");
            return;
        }

        // Ordenar jugadores por cantidad de goles (descendente)
        goleadores.sort((j1, j2) -> Integer.compare(j2.getGoles(), j1.getGoles()));

        // Mostrar tabla de goleadores
        System.out.println("\n🏆 TABLA DE GOLEADORES");
        System.out.println("-------------------------------------------------");
        System.out.printf("%-20s %-15s %-10s%n", "Jugador", "Equipo", "Goles");
        System.out.println("-------------------------------------------------");

        for (Jugador j : goleadores) {
            System.out.printf("%-20s %-15s %-10d%n",
                    j.getNombre(),
                    equipos.stream().filter(e -> e.getJugadores().contains(j))
                            .findFirst().map(Equipo::getNombre).orElse("Desconocido"),
                    j.getGoles());
        }

        System.out.println("-------------------------------------------------\n");
    }

}

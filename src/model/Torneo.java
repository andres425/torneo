package model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JOptionPane;

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
                    "El nombre del torneo debe contener solo letras y no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public List<Equipo> getEquipos() {
        return equipos;
    }

    public List<Partido> getPartidos() {
        return partidos;
    }

    public boolean puedeIniciar() {
    return equipos != null && equipos.size() >= 12;
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

        Partido partido = new Partido(equipoLocal, equipoVisitante, hora);

        if (partidos.contains(partido)) {
            throw new IllegalArgumentException("El partido ya está programado");
        }

        partidos.add(partido);
    }

    public void mostrarTablaPosiciones() {
        if (equipos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠ No hay equipos en el torneo.");
            return;
        }

        StringBuilder sb = new StringBuilder("🏆 TABLA DE POSICIONES\n\n");
        sb.append(String.format("%-15s %-5s %-5s %-5s %-5s %-5s\n",
                "Equipo", "PJ", "GF", "GC", "DG", "Pts"));

        // aquí mantienes tu lógica para calcular la tabla
        class Stats {
            Equipo equipo;
            int PJ = 0;
            int GF = 0;
            int GC = 0;
            int puntos = 0;

            int getDG() {
                return GF - GC;
            }
        }

        List<Stats> tabla = new ArrayList<>();
        for (Equipo e : equipos) {
            Stats st = new Stats();
            st.equipo = e;
            tabla.add(st);
        }

        for (Partido p : partidos) {
            if (p.isJugado()) {
                Stats local = null, visitante = null;

                for (Stats s : tabla) {
                    if (s.equipo.equals(p.getEquipoLocal()))
                        local = s;
                    if (s.equipo.equals(p.getEquipoVisitante()))
                        visitante = s;
                }

                if (local == null || visitante == null)
                    continue;

                local.PJ++;
                visitante.PJ++;

                local.GF += p.getGolesLocal();
                local.GC += p.getGolesVisitante();

                visitante.GF += p.getGolesVisitante();
                visitante.GC += p.getGolesLocal();

                if (p.ganoLocal()) {
                    local.puntos += 3;
                } else if (p.ganoVisitante()) {
                    visitante.puntos += 3;
                } else {
                    local.puntos++;
                    visitante.puntos++;
                }
            }
        }

        // ordenar
        tabla.sort((a, b) -> {
            if (b.puntos != a.puntos)
                return Integer.compare(b.puntos, a.puntos);
            return Integer.compare(b.getDG(), a.getDG());
        });

        // construir texto
        for (Stats s : tabla) {
            sb.append(String.format("%-15s %-5d %-5d %-5d %-5d %-5d\n",
                    s.equipo.getNombre(), s.PJ, s.GF, s.GC, s.getDG(), s.puntos));
        }

        JOptionPane.showMessageDialog(null, sb.toString(), "Tabla de posiciones", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarPartidos() {
        if (partidos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠ No hay partidos registrados en el torneo.");
            return;
        }

        StringBuilder sb = new StringBuilder("📅 LISTA DE PARTIDOS\n\n");
        for (Partido p : partidos) {
            String estado = p.isJugado() ? "✅ JUGADO" : "⌛ PENDIENTE";
            String marcador = p.isJugado()
                    ? p.getGolesLocal() + " - " + p.getGolesVisitante()
                    : "vs";

            sb.append(p.getEquipoLocal().getNombre())
                    .append(" ").append(marcador).append(" ")
                    .append(p.getEquipoVisitante().getNombre())
                    .append("   [").append(estado).append("]\n");
        }

        JOptionPane.showMessageDialog(null, sb.toString(), "Partidos", JOptionPane.INFORMATION_MESSAGE);
    }

    public Equipo buscarEquipo(String nombre) {
        return equipos.stream()
                .filter(e -> e.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    public void mostrarPartidosPendientes() {
        partidos.stream()
                .filter(p -> !p.isJugado())
                .forEach(p -> System.out.println(
                        p.getEquipoLocal().getNombre() + " vs " +
                                p.getEquipoVisitante().getNombre() + " a las " +
                                p.getHora()));
    }

    public void mostrarPartidosJugados() {
        partidos.stream()
                .filter(Partido::isJugado)
                .forEach(p -> System.out.println(p.resumen()));
    }

    public void mostrarGoleadores() {
        if (equipos.isEmpty()) {
            System.out.println("⚠ No hay equipos en el torneo.");
            return;
        }

        List<Jugador> goleadores = new ArrayList<>();
        for (Equipo e : equipos) {
            goleadores.addAll(e.getJugadores());
        }

        if (goleadores.isEmpty()) {
            System.out.println("⚠ No hay jugadores registrados en el torneo.");
            return;
        }

        goleadores.sort((j1, j2) -> Integer.compare(j2.getGoles(), j1.getGoles()));

        System.out.println("\n🏆 TABLA DE GOLEADORES");
        System.out.println("-------------------------------------------------");
        System.out.printf("%-20s %-15s %-10s%n", "Jugador", "Equipo", "Goles");
        System.out.println("-------------------------------------------------");

        for (Jugador j : goleadores) {
            String equipo = equipos.stream()
                    .filter(e -> e.getJugadores().contains(j))
                    .findFirst()
                    .map(Equipo::getNombre)
                    .orElse("Desconocido");

            System.out.printf("%-20s %-15s %-10d%n",
                    j.getNombre(),
                    equipo,
                    j.getGoles());
        }

        System.out.println("-------------------------------------------------\n");
    }

    public List<List<Equipo>> crearGrupos() {
    if (equipos == null || equipos.size() < 12) {
        throw new IllegalStateException("Se necesitan mínimo 12 equipos para crear grupos.");
    }

    List<Equipo> copiaEquipos = new ArrayList<>(equipos);
    Collections.shuffle(copiaEquipos); // Mezclar aleatoriamente

    int totalEquipos = copiaEquipos.size();

    // Determinar cantidad de grupos según los equipos
    int grupos = totalEquipos / 4; // 12->3 grupos, 16->4 grupos, etc.

    List<List<Equipo>> listaGrupos = new ArrayList<>();

    for (int i = 0; i < grupos; i++) {
        int inicio = i * 4;
        int fin = inicio + 4;
        List<Equipo> grupo = copiaEquipos.subList(inicio, fin);
        listaGrupos.add(new ArrayList<>(grupo));
    }

    return listaGrupos;
}

public List<Equipo> clasificarPrimeros(List<List<Equipo>> grupos) {
    List<Equipo> clasificados = new ArrayList<>();

    for (List<Equipo> grupo : grupos) {
        grupo.sort((a, b) -> b.getPuntos() - a.getPuntos()); // Ordenar de mayor a menor
        clasificados.add(grupo.get(0));
        clasificados.add(grupo.get(1));
    }

    return clasificados;
}


}

package model;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Eliminatoria {
    private List<Partido> partidosCuartos = new ArrayList<>();
    private List<Partido> partidosSemifinal = new ArrayList<>();
    private Partido partidoFinal;
    private Partido partidoTercerPuesto;

    public List<Partido> getPartidosCuartos() {
        return partidosCuartos;
    }

    // ✅ Getter de Semifinal
    public List<Partido> getPartidosSemifinal() {
        return partidosSemifinal;
    }

    // ✅ Getter de la Final
    public Partido getPartidoFinal() {
        return partidoFinal;
    }

    // ✅ Getter del Tercer Puesto
    public Partido getPartidoTercerPuesto() {
        return partidoTercerPuesto;
    }

    // ✅ Verifica si ya se jugaron todos los partidos de fase de grupos
    // Dentro de Eliminatoria
    public void verificarYSortearCuartos(Torneo torneo) {
        if (torneo.todosPartidosDeGruposJugados() && partidosCuartos.isEmpty()) {
            List<Equipo> clasificados = torneo.obtenerClasificados();
            sortearCuartos(clasificados);
        }
    }

    // -----------------------------
    // 🔁 AVANCE AUTOMÁTICO DE FASES
    // -----------------------------
    public void verificarAvanceAutomatico(Torneo torneo) {
        // 1️⃣ Si todos los partidos de grupos se jugaron y no hay cuartos → sortear
        // cuartos
        if (torneo.todosPartidosDeGruposJugados() && partidosCuartos.isEmpty()) {
            List<Equipo> clasificados = torneo.clasificarOchoMejores();
            sortearCuartos(clasificados);
            JOptionPane.showMessageDialog(null,
                    "🏆 Se ha terminado la fase de grupos.\nSe sortearon los cuartos de final automáticamente.");
            return;
        }

        // 2️⃣ Si todos los cuartos se jugaron → sortear semifinales
        if (!partidosCuartos.isEmpty() &&
                partidosCuartos.stream().allMatch(Partido::getJugado) &&
                partidosSemifinal.isEmpty()) {

            List<Equipo> ganadores;
            try {
                ganadores = obtenerGanadoresCuartos();
            } catch (Exception e) {
                System.out.println("⚠ No se puede avanzar a semifinales: " + e.getMessage());
                return; // ← EVITA PETAR EL PROGRAMA
            }

            if (ganadores.size() == 4) {
                sortearSemifinales(ganadores);
                JOptionPane.showMessageDialog(null,
                        "✅ Cuartos finalizados.\nSe generaron las semifinales automáticamente.");
            }
            return;
        }

        // 3️⃣ Si todas las semis se jugaron → generar final y tercer puesto
        if (!partidosSemifinal.isEmpty() &&
                partidosSemifinal.stream().allMatch(Partido::getJugado) &&
                partidoFinal == null) {

            generarFinalYtercerPuesto();
            JOptionPane.showMessageDialog(null,
                    "🔥 Semifinales finalizadas.\nSe generaron la Final y el 3er Puesto automáticamente.");
        }
    }

    // -----------------------------
    // 📌 SORTEO DE CUARTOS
    // -----------------------------
    public List<String> sortearCuartos(List<Equipo> clasificados) {
        if (clasificados == null || clasificados.size() < 8) {
            JOptionPane.showMessageDialog(null, "⚠ Debe haber 8 equipos para el sorteo de cuartos.");
            return null;
        }

        // Si ya hay cuartos generados, no sobrescribir
        if (!partidosCuartos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠ Los cuartos ya han sido sorteados.");
            return null;
        }

        List<Equipo> copia = new ArrayList<>(clasificados);
        Collections.shuffle(copia);

        List<String> cruces = new ArrayList<>();

        for (int i = 0; i < 8; i += 2) {
            Equipo e1 = copia.get(i);
            Equipo e2 = copia.get(i + 1);

            Partido partido = new Partido(e1, e2, null);
            partidosCuartos.add(partido);

            cruces.add(e1.getNombre() + " vs " + e2.getNombre());
        }

        JOptionPane.showMessageDialog(null, "✅ Sorteo de cuartos realizado.");
        return cruces;
    }

    // -----------------------------
    // 📌 GANADORES DE CUARTOS
    // -----------------------------
   public List<Equipo> obtenerGanadoresCuartos() {
    List<Equipo> ganadores = new ArrayList<>();

    for (Partido partido : partidosCuartos) {
        if (!partido.getJugado()) {
            throw new IllegalStateException("⚠ Aún hay partidos de cuartos sin resultados.");
        }

        if (partido.getGolesLocal() > partido.getGolesVisitante()) {
            ganadores.add(partido.getEquipoLocal());
        } else if (partido.getGolesVisitante() > partido.getGolesLocal()) {
            ganadores.add(partido.getEquipoVisitante());
        } else {
            // 🟡 EMPATE → REVISAR PENALES
            if (partido.getPenalesLocal() > partido.getPenalesVisitante()) {
                ganadores.add(partido.getEquipoLocal());
            } else if (partido.getPenalesVisitante() > partido.getPenalesLocal()) {
                ganadores.add(partido.getEquipoVisitante());
            } else {
                throw new IllegalStateException("⚠ Partido empatado sin definición por penales: " + partido);
            }
        }
    }

    if (ganadores.size() != 4) {
        throw new IllegalStateException("⚠ No hay exactamente 4 ganadores de cuartos.");
    }

    return ganadores;
}


    /**
     * Actualiza el partido en las listas internas (si existe) buscando por nombres de equipo.
     * Esto evita el problema cuando la UI crea/copía una instancia distinta de Partido.
     */
    private boolean actualizarPartidoEnListas(Partido partidoExterno, int golesLocal, int golesVisitante) {
        if (partidoExterno == null) return false;

        // Helper lambda-like manual search: comparo nombres de equipos
        for (Partido p : partidosCuartos) {
            if (p.getEquipoLocal().getNombre().equals(partidoExterno.getEquipoLocal().getNombre())
                    && p.getEquipoVisitante().getNombre().equals(partidoExterno.getEquipoVisitante().getNombre())) {
                p.setGolesLocal(golesLocal);
                p.setGolesVisitante(golesVisitante);
                p.setJugado(true);
                return true;
            }
        }

        for (Partido p : partidosSemifinal) {
            if (p.getEquipoLocal().getNombre().equals(partidoExterno.getEquipoLocal().getNombre())
                    && p.getEquipoVisitante().getNombre().equals(partidoExterno.getEquipoVisitante().getNombre())) {
                p.setGolesLocal(golesLocal);
                p.setGolesVisitante(golesVisitante);
                p.setJugado(true);
                return true;
            }
        }

        if (partidoFinal != null) {
            if (partidoFinal.getEquipoLocal().getNombre().equals(partidoExterno.getEquipoLocal().getNombre())
                    && partidoFinal.getEquipoVisitante().getNombre().equals(partidoExterno.getEquipoVisitante().getNombre())) {
                partidoFinal.setGolesLocal(golesLocal);
                partidoFinal.setGolesVisitante(golesVisitante);
                partidoFinal.setJugado(true);
                return true;
            }
        }

        if (partidoTercerPuesto != null) {
            if (partidoTercerPuesto.getEquipoLocal().getNombre().equals(partidoExterno.getEquipoLocal().getNombre())
                    && partidoTercerPuesto.getEquipoVisitante().getNombre().equals(partidoExterno.getEquipoVisitante().getNombre())) {
                partidoTercerPuesto.setGolesLocal(golesLocal);
                partidoTercerPuesto.setGolesVisitante(golesVisitante);
                partidoTercerPuesto.setJugado(true);
                return true;
            }
        }

        // No encontrado en ninguna lista
        return false;
    }

    // En Eliminatoria.java
    public void registrarResultadoYAvanzar(Partido partido, int golesLocal, int golesVisitante) {
        // Intento actualizar la instancia guardada en mi eliminatoria
        boolean actualizado = actualizarPartidoEnListas(partido, golesLocal, golesVisitante);

        // Si no encontré la instancia, actualizo la que me pasaron igual (para compatibilidad)
        if (!actualizado) {
            partido.setGolesLocal(golesLocal);
            partido.setGolesVisitante(golesVisitante);
            partido.setJugado(true);
            System.out.println("WARN: El partido actualizado no estaba en las listas internas (se actualizó la instancia externa).");
        }

        // 1. Avanzar a semifinal si corresponde
        if (partidosCuartos != null && partidosCuartos.size() == 4 &&
                partidosCuartos.stream().allMatch(Partido::getJugado) &&
                (partidosSemifinal == null || partidosSemifinal.isEmpty())) {

            List<Equipo> ganadores = obtenerGanadoresCuartos();
            if (ganadores.size() == 4) { // Seguridad extra
                sortearSemifinales(ganadores);
            }
        }

        // 2. Avanzar a final y tercer puesto si corresponde
        if (partidosSemifinal != null && partidosSemifinal.size() == 2 &&
                partidosSemifinal.stream().allMatch(Partido::getJugado) &&
                partidoFinal == null) {

            generarFinalYtercerPuesto();
        }
    }


    // -----------------------------
    // 📌 SORTEO DE SEMIFINALES
    // -----------------------------
    public void sortearSemifinales(List<Equipo> ganadoresCuartos) {
        if (ganadoresCuartos == null || ganadoresCuartos.size() != 4) {
            JOptionPane.showMessageDialog(null, "⚠ Debe haber exactamente 4 equipos para las semifinales.");
            return;
        }

        // Evito volver a sortear si ya hay semifinales creadas
        if (!partidosSemifinal.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠ Las semifinales ya han sido generadas.");
            return;
        }

        Collections.shuffle(ganadoresCuartos);
        partidosSemifinal.clear();

        Partido semi1 = new Partido(ganadoresCuartos.get(0), ganadoresCuartos.get(1), null);
        Partido semi2 = new Partido(ganadoresCuartos.get(2), ganadoresCuartos.get(3), null);

        partidosSemifinal.add(semi1);
        partidosSemifinal.add(semi2);

        String mensaje = "🎲 SORTEO DE SEMIFINALES:\n" +
                "Semifinal 1: " + semi1.getEquipoLocal().getNombre() + " vs " + semi1.getEquipoVisitante().getNombre()
                + "\n" +
                "Semifinal 2: " + semi2.getEquipoLocal().getNombre() + " vs " + semi2.getEquipoVisitante().getNombre();

        JOptionPane.showMessageDialog(null, mensaje);
    }

    // -----------------------------
    // 📌 GANADORES Y PERDEDORES AUX
    // -----------------------------
    private List<Equipo> obtenerGanadores(List<Partido> partidos) {
        List<Equipo> ganadores = new ArrayList<>();
        for (Partido p : partidos) {
            if (!p.getJugado()) continue; // seguridad
            if (p.getGolesLocal() > p.getGolesVisitante()) {
                ganadores.add(p.getEquipoLocal());
            } else if (p.getGolesVisitante() > p.getGolesLocal()) {
                ganadores.add(p.getEquipoVisitante());
            }
        }
        return ganadores;
    }

    private List<Equipo> obtenerPerdedores(List<Partido> partidos) {
        List<Equipo> perdedores = new ArrayList<>();
        for (Partido p : partidos) {
            if (!p.getJugado()) continue; // seguridad
            if (p.getGolesLocal() < p.getGolesVisitante()) {
                perdedores.add(p.getEquipoLocal());
            } else if (p.getGolesVisitante() < p.getGolesLocal()) {
                perdedores.add(p.getEquipoVisitante());
            }
        }
        return perdedores;
    }

    // -----------------------------
    // 📌 FINAL Y TERCER PUESTO
    // -----------------------------
public void generarFinalYtercerPuesto() {
    if (partidosSemifinal == null || partidosSemifinal.size() < 2) return;

    Partido semi1 = partidosSemifinal.get(0);
    Partido semi2 = partidosSemifinal.get(1);

    Equipo finalista1 = semi1.getGanador();
    Equipo finalista2 = semi2.getGanador();

    Equipo perdedor1 = semi1.getPerdedor();
    Equipo perdedor2 = semi2.getPerdedor();

    // ✅ Crear partidos SIN FECHA para que puedan ser programados después
    partidoFinal = new Partido(finalista1, finalista2);
    partidoTercerPuesto = new Partido(perdedor1, perdedor2);

    // ✅ Asegurar explícitamente que no tienen fecha programada
    partidoFinal.setFechaHora(null);
    partidoTercerPuesto.setFechaHora(null);
}


    // -----------------------------
    // 📌 Mostrar toda la eliminatoria
    // -----------------------------
    public void mostrarEliminatoria() {
        JTextArea area = new JTextArea();
        area.setEditable(false);

        // CUARTOS
        area.append("==== CUARTOS ====\n");
        if (partidosCuartos != null && !partidosCuartos.isEmpty()) {
            for (Partido p : partidosCuartos) {
                area.append(p.toString() + "\n");
            }
        } else {
            area.append("(Aún no se han generado los partidos de cuartos)\n");
        }

        // SEMIFINALES
        area.append("\n==== SEMIFINALES ====\n");
        if (partidosSemifinal != null && !partidosSemifinal.isEmpty()) {
            for (Partido p : partidosSemifinal) {
                area.append(p.toString() + "\n");
            }
        } else {
            area.append("(Aún no se han generado los partidos de semifinal)\n");
        }

        // TERCER PUESTO
        area.append("\n==== TERCER PUESTO ====\n");
        if (partidoTercerPuesto != null) {
            area.append(partidoTercerPuesto.toString() + "\n");
        } else {
            area.append("(Aún no definido)\n");
        }

        // FINAL
        area.append("\n==== FINAL ====\n");
        if (partidoFinal != null) {
            area.append(partidoFinal.toString() + "\n");
        } else {
            area.append("(Aún no definido)\n");
        }

        // Mensaje para debug rápido en consola
        System.out.println("DEBUG: partidosCuartos = " + (partidosCuartos == null ? "null" : partidosCuartos.size()));
        System.out.println(
                "DEBUG: partidosSemifinal = " + (partidosSemifinal == null ? "null" : partidosSemifinal.size()));
        System.out.println("DEBUG: partidoTercerPuesto = " + (partidoTercerPuesto == null ? "null" : "listo"));
        System.out.println("DEBUG: partidoFinal = " + (partidoFinal == null ? "null" : "listo"));

        // Mostrar ventana
        JOptionPane.showMessageDialog(null, new JScrollPane(area),
                "Eliminatoria", JOptionPane.INFORMATION_MESSAGE);
    }

}

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

    // -----------------------------
    // 📌 SORTEO DE CUARTOS
    // -----------------------------
    public List<String> sortearCuartos(List<Equipo> clasificados) {
        if (clasificados == null || clasificados.size() < 8) {
            JOptionPane.showMessageDialog(null, "⚠ Debe haber 8 equipos para el sorteo de cuartos.");
            return null;
        }

        partidosCuartos.clear(); // limpiar previos

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
                throw new IllegalStateException("⚠ Partido empatado: falta resolver ganador.");
            }
        }

        if (ganadores.size() != 4) {
            throw new IllegalStateException("⚠ No hay exactamente 4 ganadores de cuartos.");
        }

        return ganadores;
    }
    // En Eliminatoria.java
public void registrarResultadoYAvanzar(Partido partido, int golesLocal, int golesVisitante) {
    partido.setGolesLocal(golesLocal);
    partido.setGolesVisitante(golesVisitante);
    partido.setJugado(true);

    // ✅ Si ya están todos los partidos de cuartos jugados → avanzar a semifinal
    if (partidosCuartos.stream().allMatch(Partido::getJugado) && partidosSemifinal.isEmpty()) {
        List<Equipo> ganadores = obtenerGanadoresCuartos();
        sortearSemifinales(ganadores);
    }

    // ✅ Si ya están todos los partidos de semifinal jugados → generar final
    if (partidosSemifinal.stream().allMatch(Partido::getJugado) && partidoFinal == null) {
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

        Collections.shuffle(ganadoresCuartos);
        partidosSemifinal.clear();

         Partido semi1 = new Partido(ganadoresCuartos.get(0), ganadoresCuartos.get(1), null);
    Partido semi2 = new Partido(ganadoresCuartos.get(2), ganadoresCuartos.get(3), null);

        partidosSemifinal.add(semi1);
        partidosSemifinal.add(semi2);

        String mensaje = "🎲 SORTEO DE SEMIFINALES:\n" +
                "Semifinal 1: " + semi1.getEquipoLocal().getNombre() + " vs " + semi1.getEquipoVisitante().getNombre() + "\n" +
                "Semifinal 2: " + semi2.getEquipoLocal().getNombre() + " vs " + semi2.getEquipoVisitante().getNombre();

        JOptionPane.showMessageDialog(null, mensaje);
    }

    // -----------------------------
    // 📌 GANADORES Y PERDEDORES AUX
    // -----------------------------
    private List<Equipo> obtenerGanadores(List<Partido> partidos) {
        List<Equipo> ganadores = new ArrayList<>();
        for (Partido p : partidos) {
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
        if (partidosSemifinal == null || partidosSemifinal.size() != 2) {
            throw new IllegalStateException("❌ No se puede generar final sin haber jugado las semifinales.");
        }

        List<Equipo> ganadores = obtenerGanadores(partidosSemifinal);
        List<Equipo> perdedores = obtenerPerdedores(partidosSemifinal);

        if (ganadores.size() != 2 || perdedores.size() != 2) {
            throw new IllegalStateException("❌ Deben haber exactamente 2 ganadores y 2 perdedores.");
        }

        partidoFinal = new Partido(ganadores.get(0), ganadores.get(1), null);
        partidoTercerPuesto = new Partido(perdedores.get(0), perdedores.get(1), null);

        JOptionPane.showMessageDialog(null,
                "✅ Final y partido por el 3er puesto generados:\n" +
                        "🏆 Final: " + ganadores.get(0).getNombre() + " vs " + ganadores.get(1).getNombre() + "\n" +
                        "🥉 Tercer Puesto: " + perdedores.get(0).getNombre() + " vs " + perdedores.get(1).getNombre());
    }

    // -----------------------------
    // 📌 Mostrar toda la eliminatoria
    // -----------------------------
    public void mostrarEliminatoria() {
        JTextArea area = new JTextArea();
        area.setEditable(false);

        area.append("==== CUARTOS ====\n");
        for (Partido p : partidosCuartos) {
            area.append(p.toString() + "\n");
        }

        area.append("\n==== SEMIFINALES ====\n");
        for (Partido p : partidosSemifinal) {
            area.append(p.toString() + "\n");
        }

        area.append("\n==== TERCER PUESTO ====\n");
        if (partidoTercerPuesto != null) {
            area.append(partidoTercerPuesto.toString() + "\n");
        } else {
            area.append("(Aún no definido)\n");
        }

        area.append("\n==== FINAL ====\n");
        if (partidoFinal != null) {
            area.append(partidoFinal.toString() + "\n");
        } else {
            area.append("(Aún no definido)\n");
        }

        JOptionPane.showMessageDialog(null, new JScrollPane(area),
                "Eliminatoria", JOptionPane.INFORMATION_MESSAGE);
    }
}

package model;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Torneo {
    private String nombre;
    private List<Equipo> equipos;
    private List<Partido> partidos;
    private List<List<Equipo>> grupos;

    private boolean partidosGenerados = false;
    private boolean gruposGenerados = false;

    public Torneo(String nombre) {
        setNombre(nombre);
        this.equipos = new ArrayList<>();
        this.partidos = new ArrayList<>();
   this.grupos = new ArrayList<>();
        this.gruposGenerados = false;
        this.partidosGenerados = false;
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
        equipos.add(equipo);
    }

    // metodo para agregar equipos
    public void agregarEquipo() {
        while (true) {
            String nombre = JOptionPane.showInputDialog(null, "Ingrese el nombre del equipo:");
            if (nombre == null) {
                int resp = JOptionPane.showConfirmDialog(null, "¿Desea cancelar la operación?", "Confirmar",
                        JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.YES_OPTION)
                    return; // cancelar
                else
                    continue; // volver a pedir
            }

            nombre = nombre.trim();

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(null, "⚠ El campo no puede estar vacío. Intente de nuevo.");
                continue;
            }

            if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
                JOptionPane.showMessageDialog(null, "⚠ Solo se permiten letras y espacios. Intente de nuevo.");
                continue;
            }

            for (Equipo e : equipos) {
                if (e.getNombre().equalsIgnoreCase(nombre)) {
                    JOptionPane.showMessageDialog(null, "⚠ Ya existe un equipo con este nombre.");
                    return;
                }
            }

            equipos.add(new Equipo(nombre));
            JOptionPane.showMessageDialog(null, "✅ Equipo agregado: " + nombre);
            return;
        }
    }

    // metodo para agregar jugadores a los equipos
    public void agregarJugadorEquipo() {
        if (equipos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠ No hay equipos registrados.");
            return;
        }

        // Mostrar lista de equipos
        Equipo equipoSeleccionado = (Equipo) JOptionPane.showInputDialog(
                null,
                "Seleccione el equipo:",
                "Agregar Jugador",
                JOptionPane.QUESTION_MESSAGE,
                null,
                equipos.toArray(),
                equipos.get(0));

        if (equipoSeleccionado == null)
            return;

        // Preguntar cuántos jugadores
        int cantidad = Integer.parseInt(JOptionPane.showInputDialog("¿Cuántos jugadores desea agregar?"));

        for (int i = 0; i < cantidad; i++) {
            if (equipoSeleccionado.getJugadores().size() >= 12) {
                JOptionPane.showMessageDialog(null,
                        "⚠ El equipo " + equipoSeleccionado.getNombre() +
                                " ya tiene el máximo de 12 jugadores.");
                break;
            }
            equipoSeleccionado.agregarJugador();
        }

        if (equipoSeleccionado.getJugadores().size() < 8) {
            JOptionPane.showMessageDialog(null,
                    "⚠ El equipo " + equipoSeleccionado.getNombre() +
                            " debe tener al menos 8 jugadores para participar.");
        }
    }

    // metodo que muestra la tabla de posiciones
    public void mostrarTablaPosiciones() {
        if (grupos == null || grupos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠ No hay grupos generados.");
            return;
        }

        StringBuilder sb = new StringBuilder("📊 TABLA DE POSICIONES\n\n");

        int grupoIndex = 1;
        for (List<Equipo> grupo : grupos) {
            sb.append("=== Grupo ").append(grupoIndex).append(" ===\n");
            sb.append(String.format("%-15s %-3s %-3s %-3s %-3s %-3s %-3s %-3s %-3s\n",
                    "Equipo", "PJ", "G", "E", "P", "GF", "GC", "DG", "Pts"));
            sb.append("------------------------------------------------------------\n");

            // Calcular estadísticas por equipo
            for (Equipo equipo : grupo) {
                int pj = 0, g = 0, e = 0, p = 0, gf = 0, gc = 0, pts = 0;

                for (Partido partido : partidos) {
                    if (!partido.getJugado())
                        continue; // solo partidos jugados

                    boolean esLocal = partido.getEquipoLocal().equals(equipo);
                    boolean esVisitante = partido.getEquipoVisitante().equals(equipo);

                    if (esLocal || esVisitante) {
                        pj++;
                        int golesFavor = esLocal ? partido.getGolesLocal() : partido.getGolesVisitante();
                        int golesContra = esLocal ? partido.getGolesVisitante() : partido.getGolesLocal();

                        gf += golesFavor;
                        gc += golesContra;

                        if (golesFavor > golesContra) {
                            g++;
                            pts += 3;
                        } else if (golesFavor == golesContra) {
                            e++;
                            pts += 1;
                        } else {
                            p++;
                        }
                    }
                }

                // actualizar estadísticas del equipo
                equipo.setPartidosJugados(pj);
                equipo.setGanados(g);
                equipo.setEmpatados(e);
                equipo.setPerdidos(p);
                equipo.setGolesFavor(gf);
                equipo.setGolesContra(gc);
                equipo.setPuntos(pts);

                int dg = gf - gc;

                sb.append(String.format("%-15s %-3d %-3d %-3d %-3d %-3d %-3d %-3d %-3d\n",
                        equipo.getNombre(), pj, g, e, p, gf, gc, dg, pts));
            }

            // ordenar grupo por puntos → diferencia de goles → goles a favor
            grupo.sort((a, b) -> {
                if (b.getPuntos() != a.getPuntos())
                    return b.getPuntos() - a.getPuntos();
                int dgA = a.getGolesFavor() - a.getGolesContra();
                int dgB = b.getGolesFavor() - b.getGolesContra();
                if (dgB != dgA)
                    return dgB - dgA;
                return b.getGolesFavor() - a.getGolesFavor();
            });

            sb.append("\n");
            grupoIndex++;
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new java.awt.Font("monospaced", java.awt.Font.PLAIN, 12));
        textArea.setEditable(false);

        JOptionPane.showMessageDialog(null, new JScrollPane(textArea),
                "Tabla de Posiciones", JOptionPane.INFORMATION_MESSAGE);
    }

    // metodo para mostrar los partidos
public void mostrarPartidos(Eliminatoria eliminatoria) {
    StringBuilder sb = new StringBuilder("📅 LISTA DE PARTIDOS\n\n");

    // === FASE DE GRUPOS ===
    if (!partidos.isEmpty()) {
        sb.append("=== Fase de Grupos ===\n");
        int grupoIndex = 1;
        for (List<Equipo> grupo : grupos) {
            sb.append(" Grupo ").append(grupoIndex).append(":\n");
            for (Partido p : partidos) {
                if (grupo.contains(p.getEquipoLocal()) && grupo.contains(p.getEquipoVisitante())) {
                    String estado = p.getJugado() ? "✅ JUGADO" : "⌛ PENDIENTE";
                    String marcador = p.getJugado()
                            ? p.getGolesLocal() + " - " + p.getGolesVisitante()
                            : "vs";
                    sb.append("   ")
                      .append(p.getEquipoLocal().getNombre())
                      .append(" ").append(marcador).append(" ")
                      .append(p.getEquipoVisitante().getNombre())
                      .append("   [").append(estado).append("]\n");
                }
            }
            sb.append("\n");
            grupoIndex++;
        }
    }

    // === CUARTOS ===
    if (eliminatoria != null && eliminatoria.getPartidosCuartos() != null && !eliminatoria.getPartidosCuartos().isEmpty()) {
        sb.append("=== Cuartos de Final ===\n");
        for (Partido p : eliminatoria.getPartidosCuartos()) {
            String estado = p.getJugado() ? "✅ JUGADO" : "⌛ PENDIENTE";
            String marcador = p.getJugado()
                    ? p.getGolesLocal() + " - " + p.getGolesVisitante()
                    : "vs";
            sb.append(p.getEquipoLocal().getNombre())
              .append(" ").append(marcador).append(" ")
              .append(p.getEquipoVisitante().getNombre())
              .append("   [").append(estado).append("]\n");
        }
        sb.append("\n");
    }

    // === SEMIS ===
    if (eliminatoria != null && eliminatoria.getPartidosSemifinal() != null && !eliminatoria.getPartidosSemifinal().isEmpty()) {
        sb.append("=== Semifinales ===\n");
        for (Partido p : eliminatoria.getPartidosSemifinal()) {
            String estado = p.getJugado() ? "✅ JUGADO" : "⌛ PENDIENTE";
            String marcador = p.getJugado()
                    ? p.getGolesLocal() + " - " + p.getGolesVisitante()
                    : "vs";
            sb.append(p.getEquipoLocal().getNombre())
              .append(" ").append(marcador).append(" ")
              .append(p.getEquipoVisitante().getNombre())
              .append("   [").append(estado).append("]\n");
        }
        sb.append("\n");
    }

    // === FINAL ===
    if (eliminatoria != null && eliminatoria.getPartidoFinal() != null) {
        sb.append("=== FINAL ===\n");
        Partido p = eliminatoria.getPartidoFinal();
        String estado = p.getJugado() ? "✅ JUGADO" : "⌛ PENDIENTE";
        String marcador = p.getJugado()
                ? p.getGolesLocal() + " - " + p.getGolesVisitante()
                : "vs";
        sb.append(p.getEquipoLocal().getNombre())
          .append(" ").append(marcador).append(" ")
          .append(p.getEquipoVisitante().getNombre())
          .append("   [").append(estado).append("]\n\n");
    }

    // === TERCER PUESTO ===
    if (eliminatoria != null && eliminatoria.getPartidoTercerPuesto() != null) {
        sb.append("=== Tercer Puesto ===\n");
        Partido p = eliminatoria.getPartidoTercerPuesto();
        String estado = p.getJugado() ? "✅ JUGADO" : "⌛ PENDIENTE";
        String marcador = p.getJugado()
                ? p.getGolesLocal() + " - " + p.getGolesVisitante()
                : "vs";
        sb.append(p.getEquipoLocal().getNombre())
          .append(" ").append(marcador).append(" ")
          .append(p.getEquipoVisitante().getNombre())
          .append("   [").append(estado).append("]\n\n");
    }

    // === Si no hay nada ===
    if (sb.toString().equals("📅 LISTA DE PARTIDOS\n\n")) {
        sb.append("⚠ No hay partidos programados en este momento.\n");
    }

    JOptionPane.showMessageDialog(null, sb.toString(), "Partidos", JOptionPane.INFORMATION_MESSAGE);
}



    // metodo para mostrar tabla de goleadores
public void mostrarGoleadores(Eliminatoria eliminatoria) {
    // 1) recolectar goles de TODOS los partidos jugados (fase + eliminatorias)
    Map<String, Integer> mapaGoles = new HashMap<>(); // clave -> total goles
    Map<String, String> claveAEquipo = new HashMap<>(); // para mostrar equipo
    Map<String, String> claveAJugador = new HashMap<>(); // para mostrar nombre jugador

    // --- Fase de grupos (lista 'partidos') ---
    if (partidos != null) {
        for (Partido p : partidos) {
            if (p.getJugado()) {
                for (Map.Entry<String, Integer> e : p.getGolesPorJugador().entrySet()) {
                    String clave = e.getKey();
                    int cantidad = e.getValue();
                    mapaGoles.put(clave, mapaGoles.getOrDefault(clave, 0) + cantidad);

                    // extraer datos de la clave: "Jugador||Equipo"
                    String[] parts = clave.split("\\|\\|", 2);
                    String nombreJugador = parts.length > 0 ? parts[0] : "Desconocido";
                    String nombreEquipo = parts.length > 1 ? parts[1] : "Desconocido";
                    claveAJugador.put(clave, nombreJugador);
                    claveAEquipo.put(clave, nombreEquipo);
                }
            }
        }
    }

    // --- Eliminatorias ---
    if (eliminatoria != null) {
        List<Partido> partidosElim = new ArrayList<>();
        if (eliminatoria.getPartidosCuartos() != null) partidosElim.addAll(eliminatoria.getPartidosCuartos());
        if (eliminatoria.getPartidosSemifinal() != null) partidosElim.addAll(eliminatoria.getPartidosSemifinal());
        if (eliminatoria.getPartidoFinal() != null) partidosElim.add(eliminatoria.getPartidoFinal());
        if (eliminatoria.getPartidoTercerPuesto() != null) partidosElim.add(eliminatoria.getPartidoTercerPuesto());

        for (Partido p : partidosElim) {
            if (p != null && p.getJugado()) {
                for (Map.Entry<String, Integer> e : p.getGolesPorJugador().entrySet()) {
                    String clave = e.getKey();
                    int cantidad = e.getValue();
                    mapaGoles.put(clave, mapaGoles.getOrDefault(clave, 0) + cantidad);

                    String[] parts = clave.split("\\|\\|", 2);
                    String nombreJugador = parts.length > 0 ? parts[0] : "Desconocido";
                    String nombreEquipo = parts.length > 1 ? parts[1] : "Desconocido";
                    claveAJugador.put(clave, nombreJugador);
                    claveAEquipo.put(clave, nombreEquipo);
                }
            }
        }
    }

    if (mapaGoles.isEmpty()) {
        JOptionPane.showMessageDialog(null, "⚠ No hay goles registrados aún.");
        return;
    }

    // 2) ordenar goleadores
    List<Map.Entry<String, Integer>> lista = new ArrayList<>(mapaGoles.entrySet());
    lista.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

    // 3) construir salida
    StringBuilder sb = new StringBuilder();
    sb.append("🏆 TABLA DE GOLEADORES\n");
    sb.append("-------------------------------------------------\n");
    sb.append(String.format("%-25s %-18s %-6s\n", "Jugador", "Equipo", "Goles"));
    sb.append("-------------------------------------------------\n");

    for (Map.Entry<String, Integer> e : lista) {
        String clave = e.getKey();
        String nombreJugador = claveAJugador.getOrDefault(clave, "Desconocido");
        String nombreEquipo = claveAEquipo.getOrDefault(clave, "Desconocido");
        int goles = e.getValue();

        sb.append(String.format("%-25s %-18s %-6d\n", nombreJugador, nombreEquipo, goles));
    }

    sb.append("-------------------------------------------------");

    JTextArea textArea = new JTextArea(sb.toString());
    textArea.setFont(new java.awt.Font("monospaced", java.awt.Font.PLAIN, 12));
    textArea.setEditable(false);

    JOptionPane.showMessageDialog(null, new JScrollPane(textArea),
            "Tabla de Goleadores", JOptionPane.INFORMATION_MESSAGE);
}



    // metodo para crear los grupos de forma aleatoria
    public List<List<Equipo>> crearGrupos() {
        if (gruposGenerados) {
            JOptionPane.showMessageDialog(null, "⚠ Los grupos ya fueron creados. No se pueden volver a generar.");
            return this.grupos;
        }

        if (equipos == null || equipos.size() < 8 || equipos.size() > 16) {
            JOptionPane.showMessageDialog(null, "Debe haber entre 8 y 16 equipos para crear los grupos.");
            return null;
        }

        List<Equipo> copiaEquipos = new ArrayList<>(equipos);
        Collections.shuffle(copiaEquipos);

        int totalEquipos = copiaEquipos.size();
        int cantidadGrupos;

        if (totalEquipos <= 10) {
            cantidadGrupos = 2; // 2 grupos
        } else if (totalEquipos <= 14) {
            cantidadGrupos = 3; // 3 grupos
        } else {
            cantidadGrupos = 4; // 4 grupos
        }

        this.grupos = new ArrayList<>();
        int indice = 0;

        for (int i = 0; i < cantidadGrupos; i++) {
            List<Equipo> grupo = new ArrayList<>();
            for (int j = 0; j < totalEquipos / cantidadGrupos; j++) {
                if (indice < totalEquipos) {
                    grupo.add(copiaEquipos.get(indice));
                    indice++;
                }
            }
            this.grupos.add(grupo);
        }

        // Si hay equipos sobrantes, se distribuyen
        while (indice < totalEquipos) {
            this.grupos.get(indice % cantidadGrupos).add(copiaEquipos.get(indice));
            indice++;
        }

        gruposGenerados = true;
        JOptionPane.showMessageDialog(null,
                "✅ Se crearon " + cantidadGrupos + " grupos con " + totalEquipos + " equipos.");
        return this.grupos;
    }

    // metodo para clasificar a los mejores 8
    public List<Equipo> clasificarOchoMejores() {
        if (grupos == null || grupos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠ Primero debe generar los grupos.");
            return null;
        }

        List<Equipo> clasificados = new ArrayList<>();

        // 1. Agregar los primeros de cada grupo
        for (List<Equipo> grupo : grupos) {
            grupo.sort((e1, e2) -> {
                int cmp = Integer.compare(e2.getPuntos(), e1.getPuntos());
                if (cmp == 0)
                    cmp = Integer.compare((e2.getGolesFavor() - e2.getGolesContra()),
                            (e1.getGolesFavor() - e1.getGolesContra()));
                if (cmp == 0)
                    cmp = Integer.compare(e2.getGolesFavor(), e1.getGolesFavor());
                return cmp;
            });

            clasificados.add(grupo.get(0)); // El primero del grupo
        }

        // 2. Si faltan para los 8, agregar segundos mejores
        List<Equipo> segundos = new ArrayList<>();
        for (List<Equipo> grupo : grupos) {
            if (grupo.size() > 1) {
                segundos.add(grupo.get(1));
            }
        }

        // Ordenar segundos
        segundos.sort((e1, e2) -> {
            int cmp = Integer.compare(e2.getPuntos(), e1.getPuntos());
            if (cmp == 0)
                cmp = Integer.compare((e2.getGolesFavor() - e2.getGolesContra()),
                        (e1.getGolesFavor() - e1.getGolesContra()));
            if (cmp == 0)
                cmp = Integer.compare(e2.getGolesFavor(), e1.getGolesFavor());
            return cmp;
        });

        for (int i = 0; i < segundos.size() && clasificados.size() < 8; i++) {
            clasificados.add(segundos.get(i));
        }

        JOptionPane.showMessageDialog(null,
                "✅ Se clasificaron " + clasificados.size() + " equipos a la siguiente fase.");
        return clasificados;
    }

    // metodo para sortear los partidos con los grupos ya creados
    public void generarPartidosDeGrupos() {
        if (grupos == null || grupos.isEmpty()) {
            throw new IllegalStateException("⚠ Primero debes crear los grupos (usa Iniciar Torneo).");
        }

        if (partidosGenerados) {
            JOptionPane.showMessageDialog(null, "⚠ Los partidos ya fueron sorteados. No puedes volver a sortear.");
            return;
        }

        partidos.clear(); // limpiar partidos anteriores
        int totalPartidos = 0;
        for (List<Equipo> grupo : grupos) {
            for (int i = 0; i < grupo.size(); i++) {
                for (int j = i + 1; j < grupo.size(); j++) {
                    Equipo local = grupo.get(i);
                    Equipo visitante = grupo.get(j);

                    // ⚠ Verificar que no exista ya ese partido (local vs visitante o visitante vs
                    // local)
                    boolean existe = partidos.stream().anyMatch(
                            p -> (p.getEquipoLocal().equals(local) && p.getEquipoVisitante().equals(visitante)) ||
                                    (p.getEquipoLocal().equals(visitante) && p.getEquipoVisitante().equals(local)));

                    if (!existe) {
                        partidos.add(new Partido(local, visitante, null));
                        totalPartidos++;
                    }
                }
            }
        }

        partidosGenerados = true;
        JOptionPane.showMessageDialog(null, "✅ Se generaron " + totalPartidos + " partidos de fase de grupos.");
    }

    // metodo para comprobar datos de jugador
    private Integer pedirEnteroValido(String mensaje, int min, int max) {
        while (true) {
            String input = JOptionPane.showInputDialog(null, mensaje);
            if (input == null)
                return null; // Cancelar
            try {
                int valor = Integer.parseInt(input);
                if (valor < min || valor > max) {
                    JOptionPane.showMessageDialog(null, "⚠ El valor debe estar entre " + min + " y " + max);
                } else {
                    return valor;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "⚠ Ingrese un número válido.");
            }
        }
    }

    // metodo para programar fecha del partido
    public void programarPartido(Eliminatoria eliminatoria) {
        // 0. Menú principal (programar o actualizar)
        String[] opcionesMenu = { "Programar nuevo partido", "Actualizar partido programado" };
        int eleccion = JOptionPane.showOptionDialog(null,
                "Seleccione una opción:",
                "Gestión de Partidos",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcionesMenu,
                opcionesMenu[0]);

        if (eleccion == -1)
            return; // Canceló

        if (eleccion == 0) {
            // ============================
            // OPCIÓN 1: PROGRAMAR PARTIDO
            // ============================
            List<Partido> partidosPendientes = new ArrayList<>();

            // Fase de grupos
            for (Partido p : partidos) {
                if (p.getFechaHora() == null) {
                    partidosPendientes.add(p);
                }
            }

            // Eliminatoria
            if (eliminatoria != null) {
                if (eliminatoria.getPartidosCuartos() != null) {
                    for (Partido p : eliminatoria.getPartidosCuartos()) {
                        if (p.getFechaHora() == null)
                            partidosPendientes.add(p);
                    }
                }
                if (eliminatoria.getPartidosSemifinal() != null) {
                    for (Partido p : eliminatoria.getPartidosSemifinal()) {
                        if (p.getFechaHora() == null)
                            partidosPendientes.add(p);
                    }
                }
                if (eliminatoria.getPartidoFinal() != null && eliminatoria.getPartidoFinal().getFechaHora() == null) {
                    partidosPendientes.add(eliminatoria.getPartidoFinal());
                }
                if (eliminatoria.getPartidoTercerPuesto() != null
                        && eliminatoria.getPartidoTercerPuesto().getFechaHora() == null) {
                    partidosPendientes.add(eliminatoria.getPartidoTercerPuesto());
                }
            }

            if (partidosPendientes.isEmpty()) {
                JOptionPane.showMessageDialog(null, "⚠ No hay partidos pendientes de programar.");
                return;
            }

            String[] opciones = new String[partidosPendientes.size()];
            for (int i = 0; i < partidosPendientes.size(); i++) {
                String local = (partidosPendientes.get(i).getEquipoLocal() != null)
                        ? partidosPendientes.get(i).getEquipoLocal().getNombre()
                        : "Pendiente";
                String visitante = (partidosPendientes.get(i).getEquipoVisitante() != null)
                        ? partidosPendientes.get(i).getEquipoVisitante().getNombre()
                        : "Pendiente";
                opciones[i] = local + " vs " + visitante;
            }

            String seleccion = (String) JOptionPane.showInputDialog(null,
                    "Seleccione el partido a programar:",
                    "Programar Partido",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]);

            if (seleccion == null)
                return;

            Partido partidoSeleccionado = null;
            for (Partido p : partidosPendientes) {
                String local = (p.getEquipoLocal() != null) ? p.getEquipoLocal().getNombre() : "Pendiente";
                String visitante = (p.getEquipoVisitante() != null) ? p.getEquipoVisitante().getNombre() : "Pendiente";
                String texto = local + " vs " + visitante;

                if (texto.equals(seleccion)) {
                    partidoSeleccionado = p;
                    break;
                }
            }
            if (partidoSeleccionado == null)
                return;

            LocalDateTime fechaPartido = pedirFechaHoraValida();
            if (fechaPartido == null)
                return;

            partidoSeleccionado.setFechaHora(fechaPartido);

            JOptionPane.showMessageDialog(null,
                    "✅ Partido programado con éxito:\n" +
                            partidoSeleccionado.getEquipoLocal().getNombre() + " vs " +
                            partidoSeleccionado.getEquipoVisitante().getNombre() + "\n" +
                            "📅 Fecha: " + fechaPartido);

        } else {
            // ================================
            // OPCIÓN 2: ACTUALIZAR PARTIDO
            // ================================
            List<Partido> partidosProgramados = new ArrayList<>();
            for (Partido p : partidos) {
                if (p.getFechaHora() != null)
                    partidosProgramados.add(p);
            }

            if (eliminatoria != null) {
                if (eliminatoria.getPartidosCuartos() != null) {
                    for (Partido p : eliminatoria.getPartidosCuartos()) {
                        if (p.getFechaHora() != null)
                            partidosProgramados.add(p);
                    }
                }
                if (eliminatoria.getPartidosSemifinal() != null) {
                    for (Partido p : eliminatoria.getPartidosSemifinal()) {
                        if (p.getFechaHora() != null)
                            partidosProgramados.add(p);
                    }
                }
                if (eliminatoria.getPartidoFinal() != null && eliminatoria.getPartidoFinal().getFechaHora() != null) {
                    partidosProgramados.add(eliminatoria.getPartidoFinal());
                }
                if (eliminatoria.getPartidoTercerPuesto() != null
                        && eliminatoria.getPartidoTercerPuesto().getFechaHora() != null) {
                    partidosProgramados.add(eliminatoria.getPartidoTercerPuesto());
                }
            }

            if (partidosProgramados.isEmpty()) {
                JOptionPane.showMessageDialog(null, "⚠ No hay partidos programados para actualizar.");
                return;
            }

            String[] opciones = new String[partidosProgramados.size()];
            for (int i = 0; i < partidosProgramados.size(); i++) {
                String local = (partidosProgramados.get(i).getEquipoLocal() != null)
                        ? partidosProgramados.get(i).getEquipoLocal().getNombre()
                        : "Pendiente";
                String visitante = (partidosProgramados.get(i).getEquipoVisitante() != null)
                        ? partidosProgramados.get(i).getEquipoVisitante().getNombre()
                        : "Pendiente";
                opciones[i] = local + " vs " + visitante + " | 📅 " + partidosProgramados.get(i).getFechaHora();
            }

            String seleccion = (String) JOptionPane.showInputDialog(null,
                    "Seleccione el partido a actualizar:",
                    "Actualizar Partido",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]);

            if (seleccion == null)
                return;

            Partido partidoSeleccionado = null;
            for (Partido p : partidosProgramados) {
                String local = (p.getEquipoLocal() != null) ? p.getEquipoLocal().getNombre() : "Pendiente";
                String visitante = (p.getEquipoVisitante() != null) ? p.getEquipoVisitante().getNombre() : "Pendiente";
                String texto = local + " vs " + visitante + " | 📅 " + p.getFechaHora();

                if (texto.equals(seleccion)) {
                    partidoSeleccionado = p;
                    break;
                }
            }
            if (partidoSeleccionado == null)
                return;

            LocalDateTime nuevaFecha = pedirFechaHoraValida();
            if (nuevaFecha == null)
                return;

            partidoSeleccionado.setFechaHora(nuevaFecha);

            JOptionPane.showMessageDialog(null,
                    "✅ Partido actualizado con éxito:\n" +
                            partidoSeleccionado.getEquipoLocal().getNombre() + " vs " +
                            partidoSeleccionado.getEquipoVisitante().getNombre() + "\n" +
                            "📅 Nueva fecha: " + nuevaFecha);
        }
    }

    // 🔹 Método auxiliar para pedir fecha y hora con validaciones
    private LocalDateTime pedirFechaHoraValida() {
        LocalDate hoy = LocalDate.now();
        int anio, mes, dia, hora, minuto;

        try {
            // Año
            while (true) {
                Integer anioTmp = pedirEnteroValido("Ingrese el año del partido:", hoy.getYear(), 2100);
                if (anioTmp == null)
                    return null;
                anio = anioTmp;
                if (anio < hoy.getYear()) {
                    JOptionPane.showMessageDialog(null, "⚠ El año no puede ser menor al actual.");
                } else
                    break;
            }

            // Mes
            while (true) {
                Integer mesTmp = pedirEnteroValido("Ingrese el mes (1-12):", 1, 12);
                if (mesTmp == null)
                    return null;
                mes = mesTmp;
                if (anio == hoy.getYear() && mes < hoy.getMonthValue()) {
                    JOptionPane.showMessageDialog(null,
                            "⚠ El mes no puede ser menor al actual (" + hoy.getMonthValue() + ").");
                } else
                    break;
            }

            // Día
            while (true) {
                int maxDia = YearMonth.of(anio, mes).lengthOfMonth();
                Integer diaTmp = pedirEnteroValido("Ingrese el día (1-" + maxDia + "):", 1, maxDia);
                if (diaTmp == null)
                    return null;
                dia = diaTmp;
                if (anio == hoy.getYear() && mes == hoy.getMonthValue() && dia < hoy.getDayOfMonth()) {
                    JOptionPane.showMessageDialog(null,
                            "⚠ El día no puede ser menor al actual (" + hoy.getDayOfMonth() + ").");
                } else
                    break;
            }

            // Hora
            while (true) {
                Integer horaTmp = pedirEnteroValido("Ingrese la hora (0-23):", 0, 23);
                if (horaTmp == null)
                    return null;
                hora = horaTmp;
                if (anio == hoy.getYear() && mes == hoy.getMonthValue() && dia == hoy.getDayOfMonth() &&
                        hora < LocalTime.now().getHour()) {
                    JOptionPane.showMessageDialog(null,
                            "⚠ La hora no puede ser menor a la actual (" + LocalTime.now().getHour() + ").");
                } else
                    break;
            }

            // Minuto
            while (true) {
                Integer minutoTmp = pedirEnteroValido("Ingrese los minutos (0-59):", 0, 59);
                if (minutoTmp == null)
                    return null;
                minuto = minutoTmp;
                if (anio == hoy.getYear() && mes == hoy.getMonthValue() && dia == hoy.getDayOfMonth() &&
                        hora == LocalTime.now().getHour() && minuto <= LocalTime.now().getMinute()) {
                    JOptionPane.showMessageDialog(null,
                            "⚠ Los minutos deben ser mayores a los actuales (" + LocalTime.now().getMinute() + ").");
                } else
                    break;
            }

            return LocalDateTime.of(anio, mes, dia, hora, minuto);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "⚠ Fecha inválida. Inténtelo nuevamente.");
            return null;
        }
    }

    // metodo para registrar el resultado del partido y ingresar las tarjetas
    public void registrarResultado(Eliminatoria eliminatoria) {
        List<Partido> pendientes = new ArrayList<>();

        // 1. Partidos de fase de grupos
        if (partidos != null) {
            for (Partido p : partidos) {
                if (!p.getJugado() && p.getFechaHora() != null) {
                    pendientes.add(p);
                }
            }
        }

        // 2. Partidos de eliminatoria
        if (eliminatoria != null) {
            if (eliminatoria.getPartidosCuartos() != null) {
                for (Partido p : eliminatoria.getPartidosCuartos()) {
                    if (!p.getJugado() && p.getFechaHora() != null && p.getFechaHora().isBefore(LocalDateTime.now())) {
                        pendientes.add(p);
                    }
                }
            }
            if (eliminatoria.getPartidosSemifinal() != null) {
                for (Partido p : eliminatoria.getPartidosSemifinal()) {
                    if (!p.getJugado() && p.getFechaHora() != null && p.getFechaHora().isBefore(LocalDateTime.now())) {
                        pendientes.add(p);
                    }
                }
            }
            if (eliminatoria.getPartidoFinal() != null) {
                Partido p = eliminatoria.getPartidoFinal();
                if (!p.getJugado() && p.getFechaHora() != null && p.getFechaHora().isBefore(LocalDateTime.now())) {
                    pendientes.add(p);
                }
            }
            if (eliminatoria.getPartidoTercerPuesto() != null) {
                Partido p = eliminatoria.getPartidoTercerPuesto();
                if (!p.getJugado() && p.getFechaHora() != null && p.getFechaHora().isBefore(LocalDateTime.now())) {
                    pendientes.add(p);
                }
            }
        }

        // 3. Validación
        if (pendientes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠ No hay partidos pendientes o aún no se ha llegado a la fecha.");
            return;
        }

        // 4. Selección de partido
        Partido partido = (Partido) JOptionPane.showInputDialog(
                null,
                "Seleccione el partido",
                "Registrar Resultado",
                JOptionPane.QUESTION_MESSAGE,
                null,
                pendientes.toArray(),
                pendientes.get(0));

        if (partido == null)
            return;

        // === Goles equipo local ===
        int golesLocal = Integer.parseInt(
                JOptionPane.showInputDialog(
                        "⚽ ¿Cuántos goles hizo el equipo " + partido.getEquipoLocal().getNombre() + "?"));

        for (int i = 0; i < golesLocal; i++) {
            Jugador jugadorObj = (Jugador) JOptionPane.showInputDialog(
                    null,
                    "Seleccione el jugador que anotó (local):",
                    "Gol equipo " + partido.getEquipoLocal().getNombre(),
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    partido.getEquipoLocal().getJugadores().toArray(),
                    partido.getEquipoLocal().getJugadores().get(0));

            if (jugadorObj != null) {
                partido.agregarGol(partido.getEquipoLocal(), jugadorObj);
            }
        }

        // === Goles equipo visitante ===
        int golesVisitante = Integer.parseInt(
                JOptionPane.showInputDialog(
                        "⚽ ¿Cuántos goles hizo el equipo " + partido.getEquipoVisitante().getNombre() + "?"));

        for (int i = 0; i < golesVisitante; i++) {
            Jugador jugadorObj = (Jugador) JOptionPane.showInputDialog(
                    null,
                    "Seleccione el jugador que anotó (visitante):",
                    "Gol equipo " + partido.getEquipoVisitante().getNombre(),
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    partido.getEquipoVisitante().getJugadores().toArray(),
                    partido.getEquipoVisitante().getJugadores().get(0));

            if (jugadorObj != null) {
                partido.agregarGol(partido.getEquipoVisitante(), jugadorObj);
            }
        }

        // --- Tarjetas Equipo Local ---
        int tarjetasLocal = JOptionPane.showConfirmDialog(null,
                "¿Hubo tarjetas para el equipo " + partido.getEquipoLocal().getNombre() + "?",
                "Tarjetas", JOptionPane.YES_NO_OPTION);

        if (tarjetasLocal == JOptionPane.YES_OPTION) {
            int cantTarjetas = Integer.parseInt(JOptionPane.showInputDialog(
                    "¿Cuántas tarjetas recibió el equipo " + partido.getEquipoLocal().getNombre() + "?"));
            for (int i = 0; i < cantTarjetas; i++) {
                Jugador jugador = (Jugador) JOptionPane.showInputDialog(
                        null,
                        "¿Qué jugador recibió la tarjeta " + (i + 1) + "?",
                        "Tarjetas " + partido.getEquipoLocal().getNombre(),
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        partido.getEquipoLocal().getJugadores().toArray(),
                        partido.getEquipoLocal().getJugadores().get(0));

                String tipo = (String) JOptionPane.showInputDialog(
                        null,
                        "¿Qué tipo de tarjeta?",
                        "Tipo de Tarjeta",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[] { "Amarilla", "Roja" },
                        "Amarilla");

                if (jugador != null && tipo != null) {
                    partido.agregarTarjeta(partido.getEquipoLocal(), jugador, tipo);
                }
            }
        }

        // --- Tarjetas Equipo Visitante ---
        int tarjetasVisitante = JOptionPane.showConfirmDialog(null,
                "¿Hubo tarjetas para el equipo " + partido.getEquipoVisitante().getNombre() + "?",
                "Tarjetas", JOptionPane.YES_NO_OPTION);

        if (tarjetasVisitante == JOptionPane.YES_OPTION) {
            int cantTarjetas = Integer.parseInt(JOptionPane.showInputDialog(
                    "¿Cuántas tarjetas recibió el equipo " + partido.getEquipoVisitante().getNombre() + "?"));
            for (int i = 0; i < cantTarjetas; i++) {
                Jugador jugador = (Jugador) JOptionPane.showInputDialog(
                        null,
                        "¿Qué jugador recibió la tarjeta " + (i + 1) + "?",
                        "Tarjetas " + partido.getEquipoVisitante().getNombre(),
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        partido.getEquipoVisitante().getJugadores().toArray(),
                        partido.getEquipoVisitante().getJugadores().get(0));

                String tipo = (String) JOptionPane.showInputDialog(
                        null,
                        "¿Qué tipo de tarjeta?",
                        "Tipo de Tarjeta",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[] { "Amarilla", "Roja" },
                        "Amarilla");

                if (jugador != null && tipo != null) {
                    partido.agregarTarjeta(partido.getEquipoVisitante(), jugador, tipo);
                }
            }
        }
        boolean esEliminatoria = (eliminatoria != null) &&
                (eliminatoria.getPartidosCuartos() != null && eliminatoria.getPartidosCuartos().contains(partido) ||
                        eliminatoria.getPartidosSemifinal() != null
                                && eliminatoria.getPartidosSemifinal().contains(partido)
                        ||
                        (eliminatoria.getPartidoFinal() != null && eliminatoria.getPartidoFinal().equals(partido)) ||
                        (eliminatoria.getPartidoTercerPuesto() != null
                                && eliminatoria.getPartidoTercerPuesto().equals(partido)));

        if (esEliminatoria && partido.getGolesLocal() == partido.getGolesVisitante()) {
            // Empate en eliminatoria -> preguntar penales
            String[] opciones = {
                    partido.getEquipoLocal().getNombre(),
                    partido.getEquipoVisitante().getNombre()
            };

            String ganador = (String) JOptionPane.showInputDialog(
                    null,
                    "⚽ El partido terminó empatado. ¿Quién ganó en penales?",
                    "Definición por penales",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]);

            if (ganador != null) {
                boolean valido = false;
                int penalesLocal = 0, penalesVisitante = 0;

                while (!valido) {
                    penalesLocal = Integer.parseInt(
                            JOptionPane.showInputDialog(
                                    "¿Cuántos penales convirtió " + partido.getEquipoLocal().getNombre() + "?"));
                    penalesVisitante = Integer.parseInt(
                            JOptionPane.showInputDialog(
                                    "¿Cuántos penales convirtió " + partido.getEquipoVisitante().getNombre() + "?"));

                    if (ganador.equals(partido.getEquipoLocal().getNombre()) && penalesLocal > penalesVisitante) {
                        valido = true;
                    } else if (ganador.equals(partido.getEquipoVisitante().getNombre())
                            && penalesVisitante > penalesLocal) {
                        valido = true;
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "❌ Error: el equipo ganador debe tener más penales que el rival.\n" +
                                        "Intente ingresar nuevamente los penales.");
                    }
                }

                // Guardamos el resultado de penales en el partido
                partido.setResultadoPenales(
                        penalesLocal,
                        penalesVisitante,
                        ganador.equals(partido.getEquipoLocal().getNombre())
                                ? partido.getEquipoLocal()
                                : partido.getEquipoVisitante());
            }
        }
        // Finalizar partido
        partido.setJugado(true);
        JOptionPane.showMessageDialog(null, "✅ Resultado y tarjetas registradas con éxito.");

        // 🚀 Verificar avance automático
        if (eliminatoria != null) {
            // Si todos los cuartos están jugados y aún no se sortearon semifinales
            if (eliminatoria.getPartidosCuartos() != null &&
                    eliminatoria.getPartidosCuartos().stream().allMatch(Partido::getJugado) &&
                    eliminatoria.getPartidosSemifinal().isEmpty()) {

                List<Equipo> ganadores = eliminatoria.obtenerGanadoresCuartos();
                eliminatoria.sortearSemifinales(ganadores);
            }

            // Si todos los semifinales están jugados y aún no existe la final
            if (eliminatoria.getPartidosSemifinal() != null &&
                    !eliminatoria.getPartidosSemifinal().isEmpty() &&
                    eliminatoria.getPartidosSemifinal().stream().allMatch(Partido::getJugado) &&
                    eliminatoria.getPartidoFinal() == null) {

                eliminatoria.generarFinalYtercerPuesto();
            }
        }

    }

}

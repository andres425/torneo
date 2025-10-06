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

    public void setGrupos(List<List<Equipo>> grupos) {
        this.grupos = grupos;
    }

    public void setPartidos(List<Partido> partidos) {
        this.partidos = partidos;
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
        String inputCantidad = JOptionPane.showInputDialog("¿Cuántos jugadores desea agregar?");
        if (inputCantidad == null)
            return; // usuario canceló
        int cantidad = Integer.parseInt(inputCantidad);

        for (int i = 0; i < cantidad; i++) {
            if (equipoSeleccionado.getJugadores().size() >= 12) {
                JOptionPane.showMessageDialog(null,
                        "⚠ El equipo " + equipoSeleccionado.getNombre() +
                                " ya tiene el máximo de 12 jugadores.");
                break;
            }

            Jugador jugador = new Jugador();

            // === Nombre con validación ===
            while (true) {
                try {
                    String nombre = JOptionPane.showInputDialog("Ingrese el nombre del jugador " + (i + 1) + ":");
                    if (nombre == null)
                        return; // usuario canceló
                    jugador.setNombre(nombre);
                    break;
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }

            // === Edad con validación ===

            while (true) {
                try {
                    String edadStr = JOptionPane.showInputDialog("Ingrese la edad de " + jugador.getNombre() + ":");
                    if (edadStr == null)
                        return; // usuario canceló
                    int edad = Integer.parseInt(edadStr); // aquí puede saltar NumberFormatException
                    jugador.setEdad(edad); // aquí puede saltar IllegalArgumentException
                    break;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "⚠ La edad debe ser un número válido.");
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }

            // === Número de camiseta con validación ===
            while (true) {
                String numStr = JOptionPane.showInputDialog(
                        "Ingrese el número de camiseta de " + jugador.getNombre() + " (1-99):");
                if (numStr == null)
                    return; // usuario canceló

                int numero;
                try {
                    numero = Integer.parseInt(numStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "⚠ El número debe ser un valor numérico válido.");
                    continue;
                }

                // validación explícita de rango (mensaje más claro antes de llamar al setter)
                if (numero < 1 || numero > 99) {
                    JOptionPane.showMessageDialog(null, "⚠ El número debe estar entre 1 y 99.");
                    continue;
                }

                // verificar si el número ya existe en el equipo
                if (equipoSeleccionado.existeNumeroCamiseta(numero)) {
                    JOptionPane.showMessageDialog(null, "⚠ Ese número ya está en uso en el equipo. Elija otro.");
                    continue;
                }

                // finalmente intentar setear (el setter también valida)
                try {
                    jugador.setNumero(numero);
                    break;
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }

            // Agregar jugador al equipo
            equipoSeleccionado.getJugadores().add(jugador);
        }

        // Validar cantidad mínima
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

            // Calcular estadísticas antes de ordenar
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
            }

            // 👉 Ordenar grupo ANTES de mostrarlo
            grupo.sort((a, b) -> {
                if (b.getPuntos() != a.getPuntos())
                    return b.getPuntos() - a.getPuntos();
                int dgA = a.getGolesFavor() - a.getGolesContra();
                int dgB = b.getGolesFavor() - b.getGolesContra();
                if (dgB != dgA)
                    return dgB - dgA;
                return b.getGolesFavor() - a.getGolesFavor();
            });

            // Ahora sí imprimir en orden
            for (Equipo equipo : grupo) {
                int dg = equipo.getGolesFavor() - equipo.getGolesContra();
                sb.append(String.format("%-15s %-3d %-3d %-3d %-3d %-3d %-3d %-3d %-3d\n",
                        equipo.getNombre(),
                        equipo.getPartidosJugados(),
                        equipo.getGanados(),
                        equipo.getEmpatados(),
                        equipo.getPerdidos(),
                        equipo.getGolesFavor(),
                        equipo.getGolesContra(),
                        dg,
                        equipo.getPuntos()));
            }

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

        // ==================== FASE DE GRUPOS ====================
        if (partidos != null && !partidos.isEmpty()) {
            sb.append("=== Fase de Grupos ===\n");
            int grupoIndex = 1;
            for (List<Equipo> grupo : grupos) {
                sb.append(" Grupo ").append(grupoIndex).append(":\n");
                for (Partido p : partidos) {
                    if (grupo.contains(p.getEquipoLocal()) && grupo.contains(p.getEquipoVisitante())) {
                        sb.append("   ")
                                .append(p.getEquipoLocal().getNombre()).append(" ")
                                .append(obtenerMarcador(p)).append(" ")
                                .append(p.getEquipoVisitante().getNombre())
                                .append("   [").append(p.getJugado() ? "✅ JUGADO" : "⌛ PENDIENTE").append("]\n");
                    }
                }
                sb.append("\n");
                grupoIndex++;
            }
        }

        // ==================== CUARTOS DE FINAL ====================
        if (eliminatoria != null && eliminatoria.getPartidosCuartos() != null
                && !eliminatoria.getPartidosCuartos().isEmpty()) {
            sb.append("=== Cuartos de Final ===\n");
            for (Partido p : eliminatoria.getPartidosCuartos()) {
                sb.append(p.getEquipoLocal().getNombre()).append(" ")
                        .append(obtenerMarcador(p)).append(" ")
                        .append(p.getEquipoVisitante().getNombre())
                        .append("   [").append(p.getJugado() ? "✅ JUGADO" : "⌛ PENDIENTE").append("]\n");
            }
            sb.append("\n");
        }

        // ==================== SEMIFINALES ====================
        if (eliminatoria != null && eliminatoria.getPartidosSemifinal() != null
                && !eliminatoria.getPartidosSemifinal().isEmpty()) {
            sb.append("=== Semifinales ===\n");
            for (Partido p : eliminatoria.getPartidosSemifinal()) {
                sb.append(p.getEquipoLocal().getNombre()).append(" ")
                        .append(obtenerMarcador(p)).append(" ")
                        .append(p.getEquipoVisitante().getNombre())
                        .append("   [").append(p.getJugado() ? "✅ JUGADO" : "⌛ PENDIENTE").append("]\n");
            }
            sb.append("\n");
        }

        // ==================== FINAL ====================
        if (eliminatoria != null && eliminatoria.getPartidoFinal() != null) {
            sb.append("=== FINAL ===\n");
            Partido p = eliminatoria.getPartidoFinal();
            sb.append(p.getEquipoLocal().getNombre()).append(" ")
                    .append(obtenerMarcador(p)).append(" ")
                    .append(p.getEquipoVisitante().getNombre())
                    .append("   [").append(p.getJugado() ? "✅ JUGADO" : "⌛ PENDIENTE").append("]\n\n");
        }

        // ==================== TERCER PUESTO ====================
        if (eliminatoria != null && eliminatoria.getPartidoTercerPuesto() != null) {
            sb.append("=== Tercer Puesto ===\n");
            Partido p = eliminatoria.getPartidoTercerPuesto();
            sb.append(p.getEquipoLocal().getNombre()).append(" ")
                    .append(obtenerMarcador(p)).append(" ")
                    .append(p.getEquipoVisitante().getNombre())
                    .append("   [").append(p.getJugado() ? "✅ JUGADO" : "⌛ PENDIENTE").append("]\n\n");
        }

        // ==================== Si no hay partidos ====================
        if (sb.toString().equals("📅 LISTA DE PARTIDOS\n\n")) {
            sb.append("⚠ No hay partidos programados en este momento.\n");
        }

        // Mostrar ventana
        JOptionPane.showMessageDialog(null, sb.toString(), "Partidos", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Obtiene el marcador de un partido, incluyendo penales si aplica.
     */
    private String obtenerMarcador(Partido p) {
        if (!p.getJugado())
            return "vs";

        String marcador = p.getGolesLocal() + " - " + p.getGolesVisitante();

        // Si se definió por penales
        if (p.getGanadorPorPenales() != null && p.getPenalesLocal() != null && p.getPenalesVisitante() != null) {
            marcador += " (" + p.getPenalesLocal() + " - " + p.getPenalesVisitante() + " penales)";
        }

        return marcador;
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
            if (eliminatoria.getPartidosCuartos() != null)
                partidosElim.addAll(eliminatoria.getPartidosCuartos());
            if (eliminatoria.getPartidosSemifinal() != null)
                partidosElim.addAll(eliminatoria.getPartidosSemifinal());
            if (eliminatoria.getPartidoFinal() != null)
                partidosElim.add(eliminatoria.getPartidoFinal());
            if (eliminatoria.getPartidoTercerPuesto() != null)
                partidosElim.add(eliminatoria.getPartidoTercerPuesto());

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

        List<Equipo> todos = new ArrayList<>();
        List<Equipo> terceros = new ArrayList<>();

        for (List<Equipo> grupo : grupos) {
            // Ordenar grupo por puntos, diferencia de goles y goles a favor
            grupo.sort((e1, e2) -> {
                int cmp = Integer.compare(e2.getPuntos(), e1.getPuntos());
                if (cmp == 0)
                    cmp = Integer.compare((e2.getGolesFavor() - e2.getGolesContra()),
                            (e1.getGolesFavor() - e1.getGolesContra()));
                if (cmp == 0)
                    cmp = Integer.compare(e2.getGolesFavor(), e1.getGolesFavor());
                return cmp;
            });

            // Guardar primeros dos y tercero
            if (grupo.size() > 0)
                todos.add(grupo.get(0));
            if (grupo.size() > 1)
                todos.add(grupo.get(1));
            if (grupo.size() > 2)
                terceros.add(grupo.get(2));
        }

        // Ordenar todos los candidatos (los primeros y segundos de cada grupo)
        todos.sort((e1, e2) -> {
            int cmp = Integer.compare(e2.getPuntos(), e1.getPuntos());
            if (cmp == 0)
                cmp = Integer.compare((e2.getGolesFavor() - e2.getGolesContra()),
                        (e1.getGolesFavor() - e1.getGolesContra()));
            if (cmp == 0)
                cmp = Integer.compare(e2.getGolesFavor(), e1.getGolesFavor());
            return cmp;
        });

        List<Equipo> clasificados = new ArrayList<>();
        for (Equipo e : todos) {
            if (clasificados.size() < 8) {
                clasificados.add(e);
            }
        }

        // Si aún no hay 8, agregamos los mejores terceros
        if (clasificados.size() < 8 && !terceros.isEmpty()) {
            terceros.sort((e1, e2) -> {
                int cmp = Integer.compare(e2.getPuntos(), e1.getPuntos());
                if (cmp == 0)
                    cmp = Integer.compare((e2.getGolesFavor() - e2.getGolesContra()),
                            (e1.getGolesFavor() - e1.getGolesContra()));
                if (cmp == 0)
                    cmp = Integer.compare(e2.getGolesFavor(), e1.getGolesFavor());
                return cmp;
            });

            for (Equipo t : terceros) {
                if (clasificados.size() < 8) {
                    clasificados.add(t);
                }
            }
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

    public void programarPartido(Eliminatoria eliminatoria) {
        String[] opcionesMenu = { "Programar nuevo partido", "Actualizar partido programado" };
        int eleccion = JOptionPane.showOptionDialog(
                null,
                "Seleccione una opción:",
                "Gestión de Partidos",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcionesMenu,
                opcionesMenu[0]);

        if (eleccion == -1)
            return; // Canceló

        boolean programarNuevo = (eleccion == 0);
        List<Partido> partidosCandidatos = obtenerPartidosFiltrados(eliminatoria, programarNuevo);

        if (partidosCandidatos.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    programarNuevo ? "⚠ No hay partidos pendientes de programar."
                            : "⚠ No hay partidos programados para actualizar.");
            return;
        }

        Partido partidoSeleccionado = seleccionarPartido(partidosCandidatos, programarNuevo);
        if (partidoSeleccionado == null)
            return;

        LocalDateTime fecha = pedirFechaHoraValida();
        if (fecha == null)
            return;

        partidoSeleccionado.setFechaHora(fecha);

        JOptionPane.showMessageDialog(null,
                "✅ Partido " + (programarNuevo ? "programado" : "actualizado") + " con éxito:\n" +
                        partidoSeleccionado.getEquipoLocal().getNombre() + " vs " +
                        partidoSeleccionado.getEquipoVisitante().getNombre() + "\n" +
                        "📅 Fecha: " + fecha);
    }

    /**
     * Devuelve la lista de partidos filtrados según si están pendientes o
     * programados.
     */
    private List<Partido> obtenerPartidosFiltrados(Eliminatoria eliminatoria, boolean programarNuevo) {
        List<Partido> resultado = new ArrayList<>();

        // 🔹 Fase de grupos
        for (Partido p : partidos) {
            if (!p.getJugado()) { // ✅ No mostrar partidos ya jugados
                if (programarNuevo && p.getFechaHora() == null) { // Programar nuevo
                    resultado.add(p);
                } else if (!programarNuevo && p.getFechaHora() != null) { // Actualizar existente
                    resultado.add(p);
                }
            }
        }

        // 🔹 Eliminatoria
        if (eliminatoria != null) {
            List<Partido> todos = new ArrayList<>();
            if (eliminatoria.getPartidosCuartos() != null)
                todos.addAll(eliminatoria.getPartidosCuartos());
            if (eliminatoria.getPartidosSemifinal() != null)
                todos.addAll(eliminatoria.getPartidosSemifinal());
            if (eliminatoria.getPartidoFinal() != null)
                todos.add(eliminatoria.getPartidoFinal());
            if (eliminatoria.getPartidoTercerPuesto() != null)
                todos.add(eliminatoria.getPartidoTercerPuesto());

            for (Partido p : todos) {
                if (!p.getJugado()) { // ✅ No mostrar ya jugados
                    if (programarNuevo && p.getFechaHora() == null) {
                        resultado.add(p);
                    } else if (!programarNuevo && p.getFechaHora() != null) {
                        resultado.add(p);
                    }
                }
            }
        }

        return resultado;
    }

    private LocalDateTime pedirFechaHoraValida() {
        try {
            String fechaStr = JOptionPane.showInputDialog(
                    null,
                    "Ingrese la fecha del partido (formato: yyyy-MM-dd HH:mm):",
                    "Programar Partido",
                    JOptionPane.QUESTION_MESSAGE);

            if (fechaStr == null || fechaStr.trim().isEmpty()) {
                return null; // Canceló o dejó vacío
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return LocalDateTime.parse(fechaStr.trim(), formatter);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "⚠ Fecha inválida. Use el formato yyyy-MM-dd HH:mm\nEjemplo: 2025-10-05 15:30",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return pedirFechaHoraValida(); // vuelve a pedir
        }
    }

    /**
     * Muestra un cuadro de diálogo para seleccionar un partido.
     */
    private Partido seleccionarPartido(List<Partido> partidos, boolean pendientes) {
        String[] opciones = new String[partidos.size()];
        for (int i = 0; i < partidos.size(); i++) {
            String local = (partidos.get(i).getEquipoLocal() != null) ? partidos.get(i).getEquipoLocal().getNombre()
                    : "Pendiente";
            String visitante = (partidos.get(i).getEquipoVisitante() != null)
                    ? partidos.get(i).getEquipoVisitante().getNombre()
                    : "Pendiente";
            opciones[i] = local + " vs " + visitante;
            if (!pendientes) {
                opciones[i] += " | 📅 " + partidos.get(i).getFechaHora();
            }
        }

        String seleccion = (String) JOptionPane.showInputDialog(null,
                "Seleccione el partido:",
                pendientes ? "Programar Partido" : "Actualizar Partido",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (seleccion == null)
            return null;

        for (int i = 0; i < partidos.size(); i++) {
            if (opciones[i].equals(seleccion)) {
                return partidos.get(i);
            }
        }
        return null;
    }

    // metodo para programar fecha del partido
    public void registrarResultado(Eliminatoria eliminatoria) {
        List<Partido> pendientes = new ArrayList<>();

        // 1️⃣ Fase de grupos
        if (partidos != null) {
            for (Partido p : partidos) {
                if (!p.getJugado() && p.getFechaHora() != null && p.getFechaHora().isBefore(LocalDateTime.now())) {
                    pendientes.add(p);
                }
            }
        }

        // 2️⃣ Eliminatorias
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

        // 3️⃣ Validar si hay algo pendiente
        if (pendientes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠ No hay partidos pendientes o aún no se ha llegado a la fecha.");
            return;
        }

        // 4️⃣ Seleccionar partido
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

        // 5️⃣ Registrar goles y tarjetas
        registrarGoles(partido, partido.getEquipoLocal());
        registrarGoles(partido, partido.getEquipoVisitante());
        registrarTarjetas(partido, partido.getEquipoLocal());
        registrarTarjetas(partido, partido.getEquipoVisitante());

        // 6️⃣ Penales si es necesario
        boolean esEliminatoria = (eliminatoria != null) &&
                ((eliminatoria.getPartidosCuartos() != null && eliminatoria.getPartidosCuartos().contains(partido)) ||
                        (eliminatoria.getPartidosSemifinal() != null
                                && eliminatoria.getPartidosSemifinal().contains(partido))
                        ||
                        (eliminatoria.getPartidoFinal() != null && eliminatoria.getPartidoFinal().equals(partido)) ||
                        (eliminatoria.getPartidoTercerPuesto() != null
                                && eliminatoria.getPartidoTercerPuesto().equals(partido)));

        if (esEliminatoria && partido.getGolesLocal() == partido.getGolesVisitante()) {
            definirPorPenales(partido);
        }

        // 7️⃣ Marcar como jugado
        partido.setJugado(true);
        /// 8️⃣ Avance automático de fases

        if (eliminatoria != null) {

            if (esEliminatoria) {
                // ✅ Registrar resultado en eliminatoria
                eliminatoria.registrarResultadoYAvanzar(partido, partido.getGolesLocal(), partido.getGolesVisitante());
            }

            // 🔁 Verificar si hay que avanzar de fase (grupos → cuartos → semis → final)
            if (!esEliminatoria) {
                // ✅ Si se terminó la fase de grupos → sortear cuartos
                if (partidos.stream().allMatch(Partido::getJugado)) {
                    if (eliminatoria.getPartidosCuartos().isEmpty()) {
                        List<Equipo> clasificados = clasificarOchoMejores();
                        eliminatoria.sortearCuartos(clasificados);
                        JOptionPane.showMessageDialog(null,
                                "🏁 Fase de grupos finalizada. Se sortearon los cuartos automáticamente.");
                    }
                }
            } else {
                // ✅ Si terminaron todos los cuartos → sortear semifinales
                if (!eliminatoria.getPartidosCuartos().isEmpty()
                        && eliminatoria.getPartidosCuartos().stream().allMatch(Partido::getJugado)
                        && eliminatoria.getPartidosSemifinal().isEmpty()) {

                    List<Equipo> ganadores = eliminatoria.obtenerGanadoresCuartos();
                    eliminatoria.sortearSemifinales(ganadores);
                    JOptionPane.showMessageDialog(null, "✅ Cuartos finalizados. Se sortearon las semifinales.");
                }

                // ✅ Si terminaron todas las semifinales → generar final y tercer puesto
                if (!eliminatoria.getPartidosSemifinal().isEmpty()
                        && eliminatoria.getPartidosSemifinal().stream().allMatch(Partido::getJugado)
                        && eliminatoria.getPartidoFinal() == null) {

                    eliminatoria.generarFinalYtercerPuesto();
                    JOptionPane.showMessageDialog(null, "🔥 Semifinales finalizadas. Se generaron Final y 3er Puesto.");
                }
            }
        }

        JOptionPane.showMessageDialog(null, "✅ Resultado registrado con éxito.");
    }

    private void registrarGoles(Partido partido, Equipo equipo) {
        int goles = Integer.parseInt(JOptionPane.showInputDialog(
                "⚽ ¿Cuántos goles hizo el equipo " + equipo.getNombre() + "?"));

        for (int i = 0; i < goles; i++) {
            Jugador jugadorObj = (Jugador) JOptionPane.showInputDialog(
                    null,
                    "Seleccione el jugador que anotó:",
                    "Gol equipo " + equipo.getNombre(),
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    equipo.getJugadores().toArray(),
                    equipo.getJugadores().get(0));

            if (jugadorObj != null) {
                partido.agregarGol(equipo, jugadorObj);
            }
        }
    }

    private void registrarTarjetas(Partido partido, Equipo equipo) {
        int tarjetas = JOptionPane.showConfirmDialog(null,
                "¿Hubo tarjetas para el equipo " + equipo.getNombre() + "?",
                "Tarjetas", JOptionPane.YES_NO_OPTION);

        if (tarjetas == JOptionPane.YES_OPTION) {
            int cantTarjetas = Integer.parseInt(JOptionPane.showInputDialog(
                    "¿Cuántas tarjetas recibió el equipo " + equipo.getNombre() + "?"));
            for (int i = 0; i < cantTarjetas; i++) {
                Jugador jugador = (Jugador) JOptionPane.showInputDialog(
                        null,
                        "¿Qué jugador recibió la tarjeta " + (i + 1) + "?",
                        "Tarjetas " + equipo.getNombre(),
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        equipo.getJugadores().toArray(),
                        equipo.getJugadores().get(0));

                String tipo = (String) JOptionPane.showInputDialog(
                        null,
                        "¿Qué tipo de tarjeta?",
                        "Tipo de Tarjeta",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[] { "Amarilla", "Roja" },
                        "Amarilla");

                if (jugador != null && tipo != null) {
                    partido.agregarTarjeta(equipo, jugador, tipo);
                }
            }
        }
    }

    private void definirPorPenales(Partido partido) {
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
                            "❌ Error: el equipo ganador debe tener más penales que el rival.\nIntente nuevamente.");
                }
            }

            partido.setResultadoPenales(
                    penalesLocal,
                    penalesVisitante,
                    ganador.equals(partido.getEquipoLocal().getNombre())
                            ? partido.getEquipoLocal()
                            : partido.getEquipoVisitante());
        }
    }

    public boolean todosPartidosDeGruposJugados() {
        if (partidos == null || partidos.isEmpty()) {
            return false;
        }

        for (Partido p : partidos) {
            if (!p.getJugado()) {
                return false;
            }
        }
        return true;
    }

    public List<Equipo> obtenerClasificados() {
        List<Equipo> clasificados = new ArrayList<>();

        if (grupos == null || grupos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠ No hay grupos generados.");
            return clasificados;
        }

        List<Equipo> primeros = new ArrayList<>();
        List<Equipo> segundos = new ArrayList<>();
        List<Equipo> terceros = new ArrayList<>();

        // 🔹 Ordenar cada grupo y separar posiciones
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

            if (grupo.size() > 0)
                primeros.add(grupo.get(0));
            if (grupo.size() > 1)
                segundos.add(grupo.get(1));
            if (grupo.size() > 2)
                terceros.add(grupo.get(2));
        }

        // 🔹 Agregar automáticos
        clasificados.addAll(primeros);
        clasificados.addAll(segundos);

        // 🔹 Si todavía faltan para 8, seleccionar mejores terceros
        if (clasificados.size() < 8 && !terceros.isEmpty()) {
            terceros.sort((e1, e2) -> {
                int cmp = Integer.compare(e2.getPuntos(), e1.getPuntos());
                if (cmp == 0)
                    cmp = Integer.compare((e2.getGolesFavor() - e2.getGolesContra()),
                            (e1.getGolesFavor() - e1.getGolesContra()));
                if (cmp == 0)
                    cmp = Integer.compare(e2.getGolesFavor(), e1.getGolesFavor());
                return cmp;
            });

            for (int i = 0; i < terceros.size() && clasificados.size() < 8; i++) {
                clasificados.add(terceros.get(i));
            }
        }

        return clasificados;
    }

    public void registrarResultadoYAvanzar(Partido partido, int golesLocal, int golesVisitante) {
        partido.setGolesLocal(golesLocal);
        partido.setGolesVisitante(golesVisitante);
        partido.setJugado(true);

    }

}

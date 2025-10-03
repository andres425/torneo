package model;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
            JOptionPane.showMessageDialog(null, "⚠ Primero debes crear los grupos y generar partidos.");
            return;
        }

        StringBuilder sb = new StringBuilder("🏆 TABLA DE POSICIONES POR GRUPOS\n\n");

        // Clase interna para estadísticas
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

        int grupoIndex = 1;
        for (List<Equipo> grupo : grupos) {
            sb.append("=== Grupo ").append(grupoIndex).append(" ===\n");
            sb.append(String.format("%-15s %-5s %-5s %-5s %-5s %-5s\n",
                    "Equipo", "PJ", "GF", "GC", "DG", "Pts"));

            // Inicializar estadísticas para equipos del grupo
            List<Stats> tabla = new ArrayList<>();
            for (Equipo e : grupo) {
                Stats st = new Stats();
                st.equipo = e;
                tabla.add(st);
            }

            // Procesar partidos del grupo
            for (Partido p : partidos) {
                if (grupo.contains(p.getEquipoLocal()) && grupo.contains(p.getEquipoVisitante()) && p.getJugado()) {
                    Stats local = null, visitante = null;

                    for (Stats s : tabla) {
                        if (s.equipo.equals(p.getEquipoLocal()))
                            local = s;
                        if (s.equipo.equals(p.getEquipoVisitante()))
                            visitante = s;
                    }

                    if (local == null || visitante == null)
                        continue;

                    // Partidos jugados
                    local.PJ++;
                    visitante.PJ++;

                    // Goles
                    local.GF += p.getGolesLocal();
                    local.GC += p.getGolesVisitante();

                    visitante.GF += p.getGolesVisitante();
                    visitante.GC += p.getGolesLocal();

                    // Puntos
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
            tabla.sort((a, b) -> {
                if (b.puntos != a.puntos)
                    return Integer.compare(b.puntos, a.puntos);
                return Integer.compare(b.getDG(), a.getDG());
            });

            // Construir texto del grupo
            for (Stats s : tabla) {
                sb.append(String.format("%-15s %-5d %-5d %-5d %-5d %-5d\n",
                        s.equipo.getNombre(), s.PJ, s.GF, s.GC, s.getDG(), s.puntos));
            }

            sb.append("\n");
            grupoIndex++;
        }

        JOptionPane.showMessageDialog(null, sb.toString(), "Tabla de posiciones", JOptionPane.INFORMATION_MESSAGE);
    }

    // metodo para mostrar los partidos
    public void mostrarPartidos() {
        if (partidos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠ No hay partidos registrados en el torneo.");
            return;
        }

        StringBuilder sb = new StringBuilder("📅 LISTA DE PARTIDOS\n\n");
        int grupoIndex = 1;
        for (List<Equipo> grupo : grupos) {
            sb.append("=== Grupo ").append(grupoIndex).append(" ===\n");
            for (Partido p : partidos) {
                if (grupo.contains(p.getEquipoLocal()) && grupo.contains(p.getEquipoVisitante())) {
                    String estado = p.getJugado() ? "✅ JUGADO" : "⌛ PENDIENTE";
                    String marcador = p.getJugado()
                            ? p.getGolesLocal() + " - " + p.getGolesVisitante()
                            : "vs";

                    sb.append(p.getEquipoLocal().getNombre())
                            .append(" ").append(marcador).append(" ")
                            .append(p.getEquipoVisitante().getNombre())
                            .append("   [").append(estado).append("]\n");
                }
            }
            sb.append("\n");
            grupoIndex++;
        }

        JOptionPane.showMessageDialog(null, sb.toString(), "Partidos", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarPartidosPendientes() {
        partidos.stream()
                .filter(p -> !p.getJugado())
                .forEach(p -> System.out.println(
                        p.getEquipoLocal().getNombre() + " vs " +
                                p.getEquipoVisitante().getNombre() + " a las " +
                                p.getFechaHora()));
    }

    public void mostrarPartidosJugados() {
        partidos.stream()
                .filter(Partido::getJugado)
                .forEach(p -> System.out.println(p.resumen()));
    }

    // metodo para mostrar tabla de goleadores
    public void mostrarGoleadores() {
        if (equipos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠ No hay equipos en el torneo.");
            return;
        }

        List<Jugador> goleadores = new ArrayList<>();
        for (Equipo e : equipos) {
            goleadores.addAll(e.getJugadores());
        }

        if (goleadores.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠ No hay jugadores registrados en el torneo.");
            return;
        }

        // 🔹 Filtrar solo jugadores con al menos 1 gol
        goleadores = goleadores.stream()
                .filter(j -> j.getGoles() > 0)
                .toList();

        if (goleadores.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠ No hay jugadores que hayan marcado goles todavía.");
            return;
        }

        // Ordenar por goles (de mayor a menor)
        goleadores.sort((j1, j2) -> Integer.compare(j2.getGoles(), j1.getGoles()));

        // Construir la tabla como texto
        StringBuilder sb = new StringBuilder();
        sb.append("🏆 TABLA DE GOLEADORES\n");
        sb.append("-------------------------------------------------\n");
        sb.append(String.format("%-20s %-15s %-10s%n", "Jugador", "Equipo", "Goles"));
        sb.append("-------------------------------------------------\n");

        for (Jugador j : goleadores) {
            String equipo = equipos.stream()
                    .filter(e -> e.getJugadores().contains(j))
                    .findFirst()
                    .map(Equipo::getNombre)
                    .orElse("Desconocido");

            sb.append(String.format("%-20s %-15s %-10d%n",
                    j.getNombre(),
                    equipo,
                    j.getGoles()));
        }

        sb.append("-------------------------------------------------");

        // Mostrar en JOptionPane con monoespaciado para que quede tabulado
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

        if (equipos == null || equipos.size() < 12) {
            JOptionPane.showMessageDialog(null, "debe de haber minimo 12 equipos");
        }

        List<Equipo> copiaEquipos = new ArrayList<>(equipos);
        Collections.shuffle(copiaEquipos); // barajar aleatoriamente

        int totalEquipos = copiaEquipos.size();
        int tamanioGrupo;

        // Si es múltiplo de 4 → grupos de 4, sino de 3
        if (totalEquipos % 4 == 0) {
            tamanioGrupo = 4;
        } else if (totalEquipos % 3 == 0) {
            tamanioGrupo = 3;
        } else {
            throw new IllegalStateException("⚠ El número de equipos debe permitir grupos de 3 o de 4.");
        }

        int cantidadGrupos = totalEquipos / tamanioGrupo;
        this.grupos = new ArrayList<>();

        for (int i = 0; i < cantidadGrupos; i++) {
            int inicio = i * tamanioGrupo;
            int fin = inicio + tamanioGrupo;
            List<Equipo> grupo = new ArrayList<>(copiaEquipos.subList(inicio, fin));
            this.grupos.add(grupo);
        }
        gruposGenerados = true;
        JOptionPane.showMessageDialog(null,
                "✅ Se crearon " + cantidadGrupos + " grupos de " + tamanioGrupo + " equipos.");

        return this.grupos;
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

                    Partido partido = new Partido(local, visitante, null);

                    partidos.add(partido);
                    totalPartidos++;
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
    public void programarPartido() {
        // Filtrar partidos pendientes (sin fecha)
        List<Partido> partidosPendientes = new ArrayList<>();
        for (Partido p : partidos) {
            if (p.getFechaHora() == null)
                partidosPendientes.add(p);
        }

        if (partidosPendientes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠ No hay partidos pendientes de programar.");
            return;
        }

        // Opciones para el menú desplegable
        String[] opciones = new String[partidosPendientes.size()];
        for (int i = 0; i < partidosPendientes.size(); i++) {
            opciones[i] = partidosPendientes.get(i).getEquipoLocal().getNombre()
                    + " vs " + partidosPendientes.get(i).getEquipoVisitante().getNombre();
        }

        String seleccion = (String) JOptionPane.showInputDialog(null,
                "Seleccione el partido a programar:",
                "Programar Partido",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (seleccion == null)
            return; // Canceló

        // Encontrar el partido seleccionado
        Partido partidoSeleccionado = null;
        for (Partido p : partidosPendientes) {
            String texto = p.getEquipoLocal().getNombre() + " vs " + p.getEquipoVisitante().getNombre();
            if (texto.equals(seleccion)) {
                partidoSeleccionado = p;
                break;
            }
        }
        if (partidoSeleccionado == null)
            return; // por seguridad

        // Bucle para pedir fecha/hora. Validaciones por campo:
        LocalDateTime fechaPartido = null;

        while (true) {
            try {
                LocalDate hoy = LocalDate.now();
                int anio, mes, dia, hora, minuto;

                // ✅ Año
                while (true) {
                    Integer anioTmp = pedirEnteroValido("Ingrese el año del partido:", hoy.getYear(), 2100);
                    if (anioTmp == null)
                        return; // Canceló
                    anio = anioTmp;

                    if (anio < hoy.getYear()) {
                        JOptionPane.showMessageDialog(null, "⚠ El año no puede ser menor al actual.");
                    } else {
                        break;
                    }
                }

                // ✅ Mes
                while (true) {
                    Integer mesTmp = pedirEnteroValido("Ingrese el mes (1-12):", 1, 12);
                    if (mesTmp == null)
                        return; // Canceló
                    mes = mesTmp;

                    if (anio == hoy.getYear() && mes < hoy.getMonthValue()) {
                        JOptionPane.showMessageDialog(null,
                                "⚠ El mes no puede ser menor al mes actual (" + hoy.getMonthValue() + ").");
                    } else {
                        break;
                    }
                }

                // ✅ Día
                while (true) {
                    int maxDia = YearMonth.of(anio, mes).lengthOfMonth();
                    Integer diaTmp = pedirEnteroValido("Ingrese el día (1-" + maxDia + "):", 1, maxDia);
                    if (diaTmp == null)
                        return; // Canceló
                    dia = diaTmp;

                    if (anio == hoy.getYear() && mes == hoy.getMonthValue() && dia < hoy.getDayOfMonth()) {
                        JOptionPane.showMessageDialog(null,
                                "⚠ El día no puede ser menor al día actual (" + hoy.getDayOfMonth() + ").");
                    } else {
                        break;
                    }
                }

                // ✅ Hora
                while (true) {
                    Integer horaTmp = pedirEnteroValido("Ingrese la hora (0-23):", 0, 23);
                    if (horaTmp == null)
                        return; // Canceló
                    hora = horaTmp;

                    if (anio == hoy.getYear() && mes == hoy.getMonthValue() && dia == hoy.getDayOfMonth() &&
                            hora < LocalTime.now().getHour()) {
                        JOptionPane.showMessageDialog(null,
                                "⚠ La hora no puede ser menor a la actual (" + LocalTime.now().getHour() + ").");
                    } else {
                        break;
                    }
                }

                // ✅ Minuto
                while (true) {
                    Integer minutoTmp = pedirEnteroValido("Ingrese los minutos (0-59):", 0, 59);
                    if (minutoTmp == null)
                        return; // Canceló
                    minuto = minutoTmp;

                    if (anio == hoy.getYear() && mes == hoy.getMonthValue() && dia == hoy.getDayOfMonth() &&
                            hora == LocalTime.now().getHour() && minuto <= LocalTime.now().getMinute()) {
                        JOptionPane.showMessageDialog(null,
                                "⚠ Los minutos deben ser mayores a los actuales (" + LocalTime.now().getMinute()
                                        + ").");
                    } else {
                        break;
                    }
                }

                // ✅ Construir fecha
                fechaPartido = LocalDateTime.of(anio, mes, dia, hora, minuto);

                // Asignar al partido seleccionado
                partidoSeleccionado.setFechaHora(fechaPartido);

                // Mostrar confirmación
                JOptionPane.showMessageDialog(null,
                        "✅ Partido programado con éxito:\n" +
                                partidoSeleccionado.getEquipoLocal().getNombre() + " vs " +
                                partidoSeleccionado.getEquipoVisitante().getNombre() + "\n" +
                                "📅 Fecha: " + fechaPartido);

                break; // salir del bucle

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "⚠ Fecha inválida. Inténtelo nuevamente.");
            }
        }
    }

    // metodo para registrar el resultado del partido y ingresar las tarjetas
    public void registrarResultado() {
        if (partidos == null || partidos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠ No hay partidos programados");
            return;
        }

        // Filtrar solo partidos no jugados y con fecha ya cumplida
        List<Partido> pendientes = new ArrayList<>();
        for (Partido p : partidos) {
            if (!p.getJugado() && p.getFechaHora() != null && p.getFechaHora().isBefore(LocalDateTime.now())) {
                pendientes.add(p);
            }
        }

        if (pendientes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠ No hay partidos pendientes o aún no se ha llegado a la fecha.");
            return;
        }

        // Seleccionar partido
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

        // Goles equipo local
        int golesLocal = Integer
                .parseInt(JOptionPane.showInputDialog("⚽ Goles del equipo " + partido.getEquipoLocal().getNombre()));
        partido.setGolesLocal(golesLocal);

        for (int i = 0; i < golesLocal; i++) {
            Jugador jugador = (Jugador) JOptionPane.showInputDialog(
                    null,
                    "¿Quién hizo el gol " + (i + 1) + "?",
                    "Goles " + partido.getEquipoLocal().getNombre(),
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    partido.getEquipoLocal().getJugadores().toArray(),
                    partido.getEquipoLocal().getJugadores().get(0));
            if (jugador != null) {
                partido.agregarGol(partido.getEquipoLocal(), jugador);
            }
        }

        // Goles equipo visitante
        int golesVisitante = Integer.parseInt(
                JOptionPane.showInputDialog("⚽ Goles del equipo " + partido.getEquipoVisitante().getNombre()));
        partido.setGolesVisitante(golesVisitante);

        for (int i = 0; i < golesVisitante; i++) {
            Jugador jugador = (Jugador) JOptionPane.showInputDialog(
                    null,
                    "¿Quién hizo el gol " + (i + 1) + "?",
                    "Goles " + partido.getEquipoVisitante().getNombre(),
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    partido.getEquipoVisitante().getJugadores().toArray(),
                    partido.getEquipoVisitante().getJugadores().get(0));
            if (jugador != null) {
                partido.agregarGol(partido.getEquipoVisitante(), jugador);
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

        partido.setJugado(true);
        JOptionPane.showMessageDialog(null, "✅ Resultado y tarjetas registradas con éxito.");
    }

}

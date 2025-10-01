import model.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends JFrame {
    private Torneo torneo;

    public MainApp() {
        torneo = new Torneo("Copa Intercolegial");

        setTitle("⚽ Sistema de Torneo de Fútbol");
        setSize(650, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ⬇️ Cambiado de 8 a 9 filas para agregar el nuevo botón
        JPanel panel = new JPanel(new GridLayout(9, 1, 10, 10));

        JButton btnAgregarEquipo = new JButton("➕ Agregar Equipo");
        JButton btnAgregarJugador = new JButton("👤 Agregar Jugador a Equipo");
        JButton btnProgramarPartido = new JButton("📅 Programar Partido");
        JButton btnRegistrarResultado = new JButton("🏆 Registrar Resultado");
        JButton btnAgregarTarjeta = new JButton("🟨🟥 Agregar Tarjeta");
        JButton btnMostrarTabla = new JButton("📊 Mostrar Tabla de Posiciones");
        JButton btnMostrarPartidos = new JButton("📋 Mostrar Partidos");
        JButton btnIniciarTorneo = new JButton("🏁 Iniciar Torneo"); // ✅ Nuevo
        JButton btnSalir = new JButton("❌ Salir");

        panel.add(btnAgregarEquipo);
        panel.add(btnAgregarJugador);
        panel.add(btnProgramarPartido);
        panel.add(btnRegistrarResultado);
        panel.add(btnAgregarTarjeta);
        panel.add(btnMostrarTabla);
        panel.add(btnMostrarPartidos);
        panel.add(btnIniciarTorneo); // ✅ Agregado al panel
        panel.add(btnSalir);

        add(panel);

        btnAgregarEquipo.addActionListener(e -> agregarEquipo());
        btnAgregarJugador.addActionListener(e -> agregarJugador());
        btnProgramarPartido.addActionListener(e -> programarPartido());
        btnRegistrarResultado.addActionListener(e -> registrarResultado());
        btnAgregarTarjeta.addActionListener(e -> registrarTarjetas());
        btnMostrarTabla.addActionListener(e -> torneo.mostrarTablaPosiciones());
        btnMostrarPartidos.addActionListener(e -> torneo.mostrarPartidos());
        btnIniciarTorneo.addActionListener(e -> iniciarTorneo()); // ✅ Listener agregado
        btnSalir.addActionListener(e -> System.exit(0));
    }

    // ✅ MÉTODO NUEVO
    private void iniciarTorneo() {
        if (!torneo.puedeIniciar()) {
            JOptionPane.showMessageDialog(this,
                    "⚠ Para iniciar el torneo debe haber al menos 12 equipos.");
            return;
        }

        List<List<Equipo>> grupos = torneo.crearGrupos();

        StringBuilder sb = new StringBuilder("✅ Grupos generados:\n\n");
        for (int i = 0; i < grupos.size(); i++) {
            sb.append("Grupo ").append(i + 1).append(":\n");
            for (Equipo eq : grupos.get(i)) {
                sb.append(" - ").append(eq.getNombre()).append("\n");
            }
            sb.append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString());
    }

    // ---------------- Helpers ----------------
    private String pedirTextoValido(String mensaje) {
        while (true) {
            String input = JOptionPane.showInputDialog(this, mensaje);
            if (input == null) {
                int resp = JOptionPane.showConfirmDialog(this, "¿Desea cancelar la operación?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.YES_OPTION) return null;
                else continue;
            }
            input = input.trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo no puede estar vacío. Intente de nuevo.");
                continue;
            }
            if (!input.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
                JOptionPane.showMessageDialog(this, "Solo se permiten letras y espacios. Intente de nuevo.");
                continue;
            }
            return input;
        }
    }

    private Integer pedirEnteroValido(String mensaje, int min, int max) {
        while (true) {
            String input = JOptionPane.showInputDialog(this, mensaje);
            if (input == null) {
                int resp = JOptionPane.showConfirmDialog(this, "¿Desea cancelar la operación?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.YES_OPTION) return null;
                else continue;
            }
            input = input.trim();
            try {
                int val = Integer.parseInt(input);
                if (val < min || val > max) {
                    JOptionPane.showMessageDialog(this, "Valor fuera de rango. Debe estar entre " + min + " y " + max + ".");
                    continue;
                }
                return val;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor ingrese un número válido.");
            }
        }
    }

    // ---------------- Opciones ----------------
    private void agregarEquipo() {
        String nombre = pedirTextoValido("Ingrese el nombre del equipo:");
        if (nombre == null) return;

        try {
            Equipo equipo = new Equipo(nombre.trim());
            torneo.agregarEquipo(equipo);
            JOptionPane.showMessageDialog(this, "✅ Equipo agregado: " + nombre);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "⚠ " + ex.getMessage());
        }
    }

    private void agregarJugador() {
        if (torneo.getEquipos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ Primero debe registrar un equipo");
            return;
        }

        List<Equipo> listaEquipos = torneo.getEquipos();
        String[] nombresEquipos = new String[listaEquipos.size()];
        for (int i = 0; i < listaEquipos.size(); i++) {
            nombresEquipos[i] = listaEquipos.get(i).getNombre();
        }

        String seleccionado = (String) JOptionPane.showInputDialog(this, "Seleccione equipo:",
                "Agregar Jugador", JOptionPane.QUESTION_MESSAGE, null, nombresEquipos, nombresEquipos[0]);
        if (seleccionado == null) return;

        Equipo equipo = null;
        for (Equipo e : listaEquipos) {
            if (e.getNombre().equals(seleccionado)) {
                equipo = e;
                break;
            }
        }
        if (equipo == null) return;

        Integer cantidad = pedirEnteroValido("¿Cuántos jugadores desea agregar? (1-30)", 1, 30);
        if (cantidad == null) return;

        for (int i = 1; i <= cantidad; i++) {
            String nombre = pedirTextoValido("Nombre del jugador " + i + ":");
            if (nombre == null) return;

            Integer edad = pedirEnteroValido("Edad del jugador " + nombre + " (15-50):", 15, 50);
            if (edad == null) return;

            // 🔹 Selección de posición con JComboBox usando el enum
            Posicion[] opciones = Posicion.values();
            Posicion posicion = (Posicion) JOptionPane.showInputDialog(
                    this,
                    "Seleccione la posición del jugador " + nombre + ":",
                    "Posición",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );
            if (posicion == null) return;

            Integer numero;
            while (true) {
                numero = pedirEnteroValido("Número del jugador " + nombre + " (1-99):", 1, 99);
                if (numero == null) return;

                boolean repetido = false;
                for (Jugador j : equipo.getJugadores()) {
                    if (j.getNumero() == numero) {
                        repetido = true;
                        break;
                    }
                }
                if (repetido) {
                    JOptionPane.showMessageDialog(this, "Ese número ya está en uso en el equipo. Elija otro.");
                    continue;
                }
                break;
            }

            try {
                Jugador jugador = new Jugador(nombre, edad, posicion, numero);
                equipo.agregarJugador(jugador);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al crear el jugador: " + ex.getMessage());
                i--;
            }
        }

        JOptionPane.showMessageDialog(this, "✅ Se agregaron " + cantidad + " jugadores al equipo " + equipo.getNombre());
    }

    private void programarPartido() {
        if (torneo.getEquipos().size() < 2) {
            JOptionPane.showMessageDialog(this, "⚠ Se necesitan al menos 2 equipos");
            return;
        }

        List<Equipo> equiposValidos = new ArrayList<>();
        for (Equipo e : torneo.getEquipos()) {
            if (e.getJugadores().size() >= 10) {
                equiposValidos.add(e);
            }
        }

        if (equiposValidos.size() < 2) {
            JOptionPane.showMessageDialog(this, "⚠ Al menos dos equipos deben tener 10 jugadores para programar partidos.");
            return;
        }

        String[] nombresEquipos = new String[equiposValidos.size()];
        for (int i = 0; i < equiposValidos.size(); i++) {
            nombresEquipos[i] = equiposValidos.get(i).getNombre();
        }

        String local = (String) JOptionPane.showInputDialog(this, "Seleccione equipo local:",
                "Equipo Local", JOptionPane.QUESTION_MESSAGE, null, nombresEquipos, nombresEquipos[0]);
        if (local == null) return;

        String visitante;
        do {
            visitante = (String) JOptionPane.showInputDialog(this, "Seleccione equipo visitante:",
                    "Equipo Visitante", JOptionPane.QUESTION_MESSAGE, null, nombresEquipos, nombresEquipos[0]);
            if (visitante == null) return;
            if (visitante.equals(local)) {
                JOptionPane.showMessageDialog(this, "Un equipo no puede jugar contra sí mismo.");
            } else break;
        } while (true);

        Equipo eqLocal = null, eqVisitante = null;
        for (Equipo e : equiposValidos) {
            if (e.getNombre().equals(local)) eqLocal = e;
            if (e.getNombre().equals(visitante)) eqVisitante = e;
        }

        Integer hora = pedirEnteroValido("Hora del partido (0-23):", 0, 23);
        if (hora == null) return;
        Integer minuto = pedirEnteroValido("Minutos del partido (0-59):", 0, 59);
        if (minuto == null) return;

        try {
            torneo.programarPartido(eqLocal, eqVisitante, LocalTime.of(hora, minuto));
            JOptionPane.showMessageDialog(this, "✅ Partido programado con éxito.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "⚠ " + ex.getMessage());
        }
    }

    private void registrarResultado() {
        if (torneo.getPartidos() == null || torneo.getPartidos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ No hay partidos programados");
            return;
        }

        List<Partido> pendientes = new ArrayList<>();
        for (Partido p : torneo.getPartidos()) {
            if (!p.getJugado()) pendientes.add(p);
        }

        if (pendientes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay partidos pendientes.");
            return;
        }

        String[] listaPartidos = new String[pendientes.size()];
        for (int i = 0; i < pendientes.size(); i++) {
            Partido p = pendientes.get(i);
            listaPartidos[i] = p.getEquipoLocal().getNombre() + " vs " + p.getEquipoVisitante().getNombre() + " (" + p.getHora() + ")";
        }

        String seleccionado = (String) JOptionPane.showInputDialog(this, "Seleccione un partido:",
                "Registrar Resultado", JOptionPane.QUESTION_MESSAGE, null, listaPartidos, listaPartidos[0]);
        if (seleccionado == null) return;

        Partido partido = null;
        for (Partido p : pendientes) {
            String desc = p.getEquipoLocal().getNombre() + " vs " + p.getEquipoVisitante().getNombre() + " (" + p.getHora() + ")";
            if (desc.equals(seleccionado)) {
                partido = p;
                break;
            }
        }

        if (partido == null) return;

        Integer golesLocal = pedirEnteroValido("Goles de " + partido.getEquipoLocal().getNombre() + " (>=0):", 0, 1000);
        if (golesLocal == null) return;
        Integer golesVisitante = pedirEnteroValido("Goles de " + partido.getEquipoVisitante().getNombre() + " (>=0):", 0, 1000);
        if (golesVisitante == null) return;

        try {
            partido.registrarResultados(golesLocal, golesVisitante);

            // Cumplir suspensión después del partido
            for (Jugador j : partido.getEquipoLocal().getJugadores()) {
                if (j.estaSuspendido()) j.cumplirSuspension();
            }
            for (Jugador j : partido.getEquipoVisitante().getJugadores()) {
                if (j.estaSuspendido()) j.cumplirSuspension();
            }

            JOptionPane.showMessageDialog(this, "✅ Resultado registrado.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "⚠ " + ex.getMessage());
        }
    }

    private void registrarTarjetas() {
        if (torneo.getPartidos() == null || torneo.getPartidos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ No hay partidos programados");
            return;
        }

        List<Partido> jugados = new ArrayList<>();
        for (Partido p : torneo.getPartidos()) {
            if (p.getJugado()) jugados.add(p);
        }

        if (jugados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ Primero debe registrar resultados de partidos.");
            return;
        }

        String[] listaPartidos = new String[jugados.size()];
        for (int i = 0; i < jugados.size(); i++) {
            Partido p = jugados.get(i);
            listaPartidos[i] = p.getEquipoLocal().getNombre() + " vs " + p.getEquipoVisitante().getNombre() + " (" + p.getHora() + ")";
        }

        String seleccionado = (String) JOptionPane.showInputDialog(this, "Seleccione un partido:",
                "Registrar Tarjetas", JOptionPane.QUESTION_MESSAGE, null, listaPartidos, listaPartidos[0]);
        if (seleccionado == null) return;

        Partido partido = null;
        for (Partido p : jugados) {
            String desc = p.getEquipoLocal().getNombre() + " vs " + p.getEquipoVisitante().getNombre() + " (" + p.getHora() + ")";
            if (desc.equals(seleccionado)) {
                partido = p;
                break;
            }
        }
        if (partido == null) return;

        String[] equipos = { partido.getEquipoLocal().getNombre(), partido.getEquipoVisitante().getNombre() };
        String eqSeleccionado = (String) JOptionPane.showInputDialog(this, "Seleccione equipo:",
                "Equipo", JOptionPane.QUESTION_MESSAGE, null, equipos, equipos[0]);
        if (eqSeleccionado == null) return;

        Equipo equipo = eqSeleccionado.equals(partido.getEquipoLocal().getNombre()) ? partido.getEquipoLocal() : partido.getEquipoVisitante();

        List<Jugador> jugadores = equipo.getJugadores();
        String[] nombresJugadores = new String[jugadores.size()];
        for (int i = 0; i < jugadores.size(); i++) {
            nombresJugadores[i] = jugadores.get(i).getNombre() + " (#" + jugadores.get(i).getNumero() + ")";
        }

        String jugadorSel = (String) JOptionPane.showInputDialog(this, "Seleccione jugador:",
                "Jugador", JOptionPane.QUESTION_MESSAGE, null, nombresJugadores, nombresJugadores[0]);
        if (jugadorSel == null) return;

        Jugador jugador = null;
        for (Jugador j : jugadores) {
            if (jugadorSel.contains(j.getNombre())) {
                jugador = j;
                break;
            }
        }
        if (jugador == null) return;

        String[] tipos = { "Amarilla", "Roja" };
        String tipo = (String) JOptionPane.showInputDialog(this, "Seleccione tipo de tarjeta:",
                "Tarjeta", JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);
        if (tipo == null) return;

        if (tipo.equals("Amarilla")) {
            jugador.agregarTarjetaAmarilla();
            JOptionPane.showMessageDialog(this, "🟨 " + jugador.getNombre() + " recibió una tarjeta amarilla.");
        } else {
            jugador.agregarTarjetaRoja();
            JOptionPane.showMessageDialog(this, "🟥 " + jugador.getNombre() + " fue expulsado y queda suspendido.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}

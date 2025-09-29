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
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(7, 1, 10, 10));

        JButton btnAgregarEquipo = new JButton("➕ Agregar Equipo");
        JButton btnAgregarJugador = new JButton("👤 Agregar Jugador a Equipo");
        JButton btnProgramarPartido = new JButton("📅 Programar Partido");
        JButton btnRegistrarResultado = new JButton("🏆 Registrar Resultado");
        JButton btnMostrarTabla = new JButton("📊 Mostrar Tabla de Posiciones");
        JButton btnMostrarPartidos = new JButton("📋 Mostrar Partidos");
        JButton btnSalir = new JButton("❌ Salir");

        panel.add(btnAgregarEquipo);
        panel.add(btnAgregarJugador);
        panel.add(btnProgramarPartido);
        panel.add(btnRegistrarResultado);
        panel.add(btnMostrarTabla);
        panel.add(btnMostrarPartidos);
        panel.add(btnSalir);

        add(panel);

        btnAgregarEquipo.addActionListener(e -> agregarEquipo());
        btnAgregarJugador.addActionListener(e -> agregarJugador());
        btnProgramarPartido.addActionListener(e -> programarPartido());
        btnRegistrarResultado.addActionListener(e -> registrarResultado());
        btnMostrarTabla.addActionListener(e -> torneo.mostrarTablaPosiciones());
        btnMostrarPartidos.addActionListener(e -> torneo.mostrarPartidos());
        btnSalir.addActionListener(e -> System.exit(0));
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

        // nombres de equipos
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

            String posicion = pedirTextoValido("Posición del jugador " + nombre + ":");
            if (posicion == null) return;

            Integer numero;
            while (true) {
                numero = pedirEnteroValido("Número del jugador " + nombre + " (1-99):", 1, 99);
                if (numero == null) return;

                // verificación sin stream
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
                i--; // repetir
            }
        }

        JOptionPane.showMessageDialog(this, "✅ Se agregaron " + cantidad + " jugadores al equipo " + equipo.getNombre());
    }

    private void programarPartido() {
        if (torneo.getEquipos().size() < 2) {
            JOptionPane.showMessageDialog(this, "⚠ Se necesitan al menos 2 equipos");
            return;
        }

        // filtrar equipos con >=10 jugadores
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

        // filtrar partidos pendientes
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

        if (partido == null) {
            JOptionPane.showMessageDialog(this, "Partido no encontrado.");
            return;
        }

        Integer golesLocal = pedirEnteroValido("Goles de " + partido.getEquipoLocal().getNombre() + " (>=0):", 0, 1000);
        if (golesLocal == null) return;
        Integer golesVisitante = pedirEnteroValido("Goles de " + partido.getEquipoVisitante().getNombre() + " (>=0):", 0, 1000);
        if (golesVisitante == null) return;

        try {
            partido.registrarResultados(golesLocal, golesVisitante);
            JOptionPane.showMessageDialog(this, "✅ Resultado registrado.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "⚠ " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}

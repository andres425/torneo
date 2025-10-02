import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainApp extends JFrame {
    private Torneo torneo;

    public MainApp() {
        torneo = new Torneo("Copa Intercolegial");
        boolean modoPrueba = true;
        if (modoPrueba) {
            DatosPrueba.cargar(torneo);
        }

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
        JButton btnCrearGrupos = new JButton("🏁 Crear Grupos");
        JButton btnIniciarSorteo = new JButton("⚽ Iniciar Sorteo");
        JButton btnSalir = new JButton("❌ Salir");

        panel.add(btnAgregarEquipo);
        panel.add(btnAgregarJugador);
        panel.add(btnProgramarPartido);
        panel.add(btnRegistrarResultado);
        panel.add(btnAgregarTarjeta);
        panel.add(btnMostrarTabla);
        panel.add(btnMostrarPartidos);
        panel.add(btnCrearGrupos);
        panel.add(btnIniciarSorteo);
        panel.add(btnSalir);

        add(panel);

        btnAgregarEquipo.addActionListener(e -> agregarEquipo());
        btnAgregarJugador.addActionListener(e -> torneo.agregarJugadorEquipo());
        btnProgramarPartido.addActionListener(e -> torneo.programarPartido());
        btnRegistrarResultado.addActionListener(e -> torneo.registrarResultado());
        btnMostrarTabla.addActionListener(e -> torneo.mostrarTablaPosiciones());
        btnMostrarPartidos.addActionListener(e -> torneo.mostrarPartidos());
        btnCrearGrupos.addActionListener(e -> torneo.crearGrupos());
        btnIniciarSorteo.addActionListener(e -> torneo.generarPartidosDeGrupos());
        btnSalir.addActionListener(e -> System.exit(0));

    }

    // ---------------- Helpers ----------------
    private String pedirTextoValido(String mensaje) {
        while (true) {
            String input = JOptionPane.showInputDialog(this, mensaje);
            if (input == null) {
                int resp = JOptionPane.showConfirmDialog(this, "¿Desea cancelar la operación?", "Confirmar",
                        JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.YES_OPTION)
                    return null;
                else
                    continue;
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
                int resp = JOptionPane.showConfirmDialog(this, "¿Desea cancelar la operación?", "Confirmar",
                        JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.YES_OPTION)
                    return null;
                else
                    continue;
            }
            input = input.trim();
            try {
                int val = Integer.parseInt(input);
                if (val < min || val > max) {
                    JOptionPane.showMessageDialog(this,
                            "Valor fuera de rango. Debe estar entre " + min + " y " + max + ".");
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
        if (nombre == null)
            return;

        try {
            Equipo equipo = new Equipo(nombre.trim());
            torneo.agregarEquipo(equipo);
            JOptionPane.showMessageDialog(this, "✅ Equipo agregado: " + nombre);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "⚠ " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}

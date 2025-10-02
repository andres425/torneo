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
        btnAgregarJugador.addActionListener(e -> agregarJugador());
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
        if (seleccionado == null)
            return;

        Equipo equipo = null;
        for (Equipo e : listaEquipos) {
            if (e.getNombre().equals(seleccionado)) {
                equipo = e;
                break;
            }
        }
        if (equipo == null)
            return;

        Integer cantidad = pedirEnteroValido("¿Cuántos jugadores desea agregar? (1-30)", 1, 30);
        if (cantidad == null)
            return;

        for (int i = 1; i <= cantidad; i++) {
            String nombre = pedirTextoValido("Nombre del jugador " + i + ":");
            if (nombre == null)
                return;

            Integer edad = pedirEnteroValido("Edad del jugador " + nombre + " (15-50):", 15, 50);
            if (edad == null)
                return;

            // 🔹 Selección de posición con JComboBox usando el enum
            Posicion[] opciones = Posicion.values();
            Posicion posicion = (Posicion) JOptionPane.showInputDialog(
                    this,
                    "Seleccione la posición del jugador " + nombre + ":",
                    "Posición",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]);
            if (posicion == null)
                return;

            Integer numero;
            while (true) {
                numero = pedirEnteroValido("Número del jugador " + nombre + " (1-99):", 1, 99);
                if (numero == null)
                    return;

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

        JOptionPane.showMessageDialog(this,
                "✅ Se agregaron " + cantidad + " jugadores al equipo " + equipo.getNombre());
    } 

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}

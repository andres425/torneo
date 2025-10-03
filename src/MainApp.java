import model.*;
import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {
    private Torneo torneo;
    private Eliminatoria eliminatoria;

    public MainApp() {
        torneo = new Torneo("Copa Intercolegial");
        eliminatoria = new Eliminatoria();
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
        JButton btnTablaGoleadores = new JButton("Tabla de goleadores");
        JButton btnMostrarTabla = new JButton("📊 Mostrar Tabla de Posiciones");
        JButton btnMostrarPartidos = new JButton("📋 Mostrar Partidos");
        JButton btnCrearGrupos = new JButton("🏁 Crear Grupos");
        JButton btnIniciarSorteo = new JButton("⚽ Iniciar Sorteo");
        JButton btnVerCuadro = new JButton("ver cuadro");
        JButton btnSalir = new JButton("❌ Salir");

        panel.add(btnAgregarEquipo);
        panel.add(btnAgregarJugador);
        panel.add(btnProgramarPartido);
        panel.add(btnRegistrarResultado);
        panel.add(btnTablaGoleadores);
        panel.add(btnMostrarTabla);
        panel.add(btnMostrarPartidos);
        panel.add(btnCrearGrupos);
        panel.add(btnIniciarSorteo);
        panel.add(btnVerCuadro);
        panel.add(btnSalir);
        add(panel);

        btnAgregarEquipo.addActionListener(e -> torneo.agregarEquipo());
        btnAgregarJugador.addActionListener(e -> torneo.agregarJugadorEquipo());
        btnProgramarPartido.addActionListener(e -> torneo.programarPartido(eliminatoria));
        btnRegistrarResultado.addActionListener(e -> torneo.registrarResultado(eliminatoria));
        btnTablaGoleadores.addActionListener(e -> torneo.mostrarGoleadores(eliminatoria));
        btnMostrarTabla.addActionListener(e -> torneo.mostrarTablaPosiciones());
        btnMostrarPartidos.addActionListener(e -> torneo.mostrarPartidos(eliminatoria));
        btnCrearGrupos.addActionListener(e -> torneo.crearGrupos());
        btnIniciarSorteo.addActionListener(e -> torneo.generarPartidosDeGrupos());
        btnVerCuadro.addActionListener(e -> eliminatoria.mostrarEliminatoria());
        btnSalir.addActionListener(e -> System.exit(0));

    }
     
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));

    }
}

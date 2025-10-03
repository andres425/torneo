import model.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainPruebas extends JFrame {
    private Torneo torneo;
    private Eliminatoria eliminatoria;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public MainPruebas() {
        torneo = new Torneo("Copa de Fútbol");
        eliminatoria = new Eliminatoria();
        DatosPrueba.cargar(torneo);

        setTitle("⚽ Sistema de Torneo de Fútbol");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // -------------------- HEADER --------------------
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(46, 125, 50)); // verde oscuro header
        header.setPreferredSize(new Dimension(1100, 120));

        JLabel titulo = new JLabel("🏆 " + torneo.getNombre(), SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 34));
        titulo.setForeground(Color.WHITE);
        header.add(titulo, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // -------------------- PANEL CENTRAL --------------------
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(234, 243, 237)); // verde muy suave

        mainPanel.add(crearDashboard(), "Dashboard");
        mainPanel.add(crearPanelEquipos(), "Equipos");
        mainPanel.add(crearPanelGrupos(), "Grupos");
        mainPanel.add(crearPanelPartidos(), "Partidos");
        mainPanel.add(crearPanelReportes(), "Reportes");

        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // -------------------- DASHBOARD --------------------
    private JPanel crearDashboard() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));
        panel.setBackground(new Color(168, 213, 186)); // verde claro suave

        panel.add(crearCardDashboard("⚽ Equipos", "Gestionar equipos y jugadores", "Equipos", "/imagenes/equipos.png"));
        panel.add(crearCardDashboard("🎲 Grupos", "Crear grupos y sorteo", "Grupos", "/imagenes/grupos.png"));
        panel.add(crearCardDashboard("📅 Partidos", "Programar y registrar resultados", "Partidos", "/imagenes/partidos.png"));
        panel.add(crearCardDashboard("📊 Reportes", "Ver estadísticas y eliminatoria", "Reportes", "/imagenes/reportes.png"));

        return panel;
    }

    private JPanel crearCardDashboard(String titulo, String subtitulo, String destino, String rutaImagen) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(207, 233, 216)); // verde suave
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(46, 125, 50), 2, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Imagen
        JLabel icono = new JLabel();
        icono.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            Image img = new ImageIcon(getClass().getResource(rutaImagen))
                    .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            icono.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            icono.setText("🖼️");
        }

        // Texto
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(0, 80, 0));

        JLabel lblSub = new JLabel(subtitulo, SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(0, 80, 0));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(new Color(207, 233, 216));
        textPanel.add(lblTitulo, BorderLayout.CENTER);
        textPanel.add(lblSub, BorderLayout.SOUTH);

        card.add(icono, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(170, 240, 180));
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(34, 97, 34), 2, true),
                        new EmptyBorder(20, 20, 20, 20)
                ));
            }

            public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(207, 233, 216));
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(46, 125, 50), 2, true),
                        new EmptyBorder(20, 20, 20, 20)
                ));
            }

            public void mouseClicked(MouseEvent e) {
                cardLayout.show(mainPanel, destino);
            }
        });

        return card;
    }

    // -------------------- PANEL BASE MODERNO --------------------
    private JPanel crearPanelBase(String titulo, JPanel... acciones) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(234, 243, 237)); // fondo verde muy suave

        // Header con botón volver
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(46, 125, 50)); // verde header
        header.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(Color.WHITE);

        JButton btnVolver = crearBotonVolver();
        btnVolver.setBackground(Color.WHITE);
        btnVolver.setForeground(new Color(46, 125, 50));
        btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 14));

        header.add(btnVolver, BorderLayout.WEST);
        header.add(lblTitulo, BorderLayout.CENTER);

        panel.add(header, BorderLayout.NORTH);

        // Panel de acciones con scroll
        JPanel accionesPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        accionesPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        accionesPanel.setBackground(new Color(234, 243, 237));

        for (JPanel accion : acciones) {
            accionesPanel.add(accion);
        }

        panel.add(new JScrollPane(accionesPanel), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearCardAccion(String titulo, java.awt.event.ActionListener action, String rutaImagen) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(207, 233, 216));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(46, 125, 50), 2, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel icono = new JLabel();
        icono.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            Image img = new ImageIcon(getClass().getResource(rutaImagen))
                    .getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            icono.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            icono.setText("🖼️");
        }

        JLabel lbl = new JLabel(titulo, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(new Color(0, 80, 0));

        card.add(icono, BorderLayout.CENTER);
        card.add(lbl, BorderLayout.SOUTH);

        // Hover
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(170, 240, 180));
                card.setBorder(BorderFactory.createLineBorder(new Color(34, 97, 34), 2, true));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(207, 233, 216));
                card.setBorder(BorderFactory.createLineBorder(new Color(46, 125, 50), 1, true));
            }
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(null);
            }
        });

        return card;
    }

    // -------------------- PANEL EQUIPOS --------------------
    private JPanel crearPanelEquipos() {
        return crearPanelBase("⚽ Gestión de Equipos",
                crearCardAccion("➕ Agregar Equipo", e -> torneo.agregarEquipo(), "/imagenes/equipos.png"),
                crearCardAccion("👤 Agregar Jugador", e -> torneo.agregarJugadorEquipo(), "/imagenes/jugador.png")
        );
    }

    private JPanel crearPanelGrupos() {
        return crearPanelBase("🎲 Fase de Grupos",
                crearCardAccion("📌 Crear Grupos", e -> torneo.crearGrupos(), "/imagenes/grupos.png"),
                crearCardAccion("🎯 Iniciar Sorteo", e -> torneo.generarPartidosDeGrupos(), "/imagenes/sorteo.png"),
                crearCardAccion("📋 Mostrar Tabla", e -> torneo.mostrarTablaPosiciones(), "/imagenes/tabla.png")
        );
    }

    private JPanel crearPanelPartidos() {
        return crearPanelBase("📅 Partidos y Resultados",
                crearCardAccion("📆 Programar Partido", e -> torneo.programarPartido(eliminatoria), "/imagenes/partido.png"),
                crearCardAccion("⚽ Registrar Resultado", e -> torneo.registrarResultado(eliminatoria), "/imagenes/resultado.png"),
                crearCardAccion("📊 Mostrar Partidos", e -> torneo.mostrarPartidos(eliminatoria), "/imagenes/mostrar.png")
        );
    }

    private JPanel crearPanelReportes() {
        return crearPanelBase("📊 Reportes del Torneo",
                crearCardAccion("🏆 Tabla de Posiciones", e -> torneo.mostrarTablaPosiciones(), "/imagenes/tabla.png"),
                crearCardAccion("🥇 Tabla de Goleadores", e -> torneo.mostrarGoleadores(eliminatoria), "/imagenes/goleadores.png"),
                crearCardAccion("🎯 Cuadro Eliminatoria", e -> eliminatoria.mostrarEliminatoria(), "/imagenes/eliminatoria.png")
        );
    }

    // -------------------- BOTÓN VOLVER --------------------
    private JButton crearBotonVolver() {
        JButton boton = new JButton("Volver");
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/imagenes/flechita.png"));
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            boton.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            boton.setText("⬅ Volver");
        }
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        return boton;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainPruebas::new);
    }
}

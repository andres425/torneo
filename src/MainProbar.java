import model.*;
import java.time.LocalTime;

public class MainProbar {
    public static void main(String[] args) {
        // 1. Crear el torneo
        Torneo torneo = new Torneo("Copa ChatGPT");

        // 2. Crear equipos
        Equipo equipo1 = new Equipo("Leones");
        Equipo equipo2 = new Equipo("Tigres");
        Equipo equipo3 = new Equipo("Águilas");

        // 3. Agregar jugadores a los equipos
        Jugador j1 = new Jugador("Carlos Pérez", 24, "Delantero", 9);
        Jugador j2 = new Jugador("Andrés López", 29, "Portero", 1);

        Jugador j3 = new Jugador("Juan Torres", 21, "Mediocampista", 10);
        Jugador j4 = new Jugador("Mario Sánchez", 27, "Defensa", 4);

        Jugador j5 = new Jugador("Luis Ramírez", 30, "Delantero", 11);
        Jugador j6 = new Jugador("Pedro Gómez", 25, "Defensa", 5);

        equipo1.agregarJugador(j1);
        equipo1.agregarJugador(j2);

        equipo2.agregarJugador(j3);
        equipo2.agregarJugador(j4);

        equipo3.agregarJugador(j5);
        equipo3.agregarJugador(j6);

        // 4. Registrar los equipos en el torneo
        torneo.agregarEquipo(equipo1);
        torneo.agregarEquipo(equipo2);
        torneo.agregarEquipo(equipo3);

        // 5. Programar partidos
        torneo.programarPartido(equipo1, equipo2, LocalTime.of(15, 30));
        torneo.programarPartido(equipo2, equipo3, LocalTime.of(17, 00));
        torneo.programarPartido(equipo3, equipo1, LocalTime.of(19, 00));

        // 6. Mostrar partidos programados
        torneo.mostrarPartidos();

        // 7. Jugar partidos
        torneo.getPartidos().get(0).registrarResultados(2, 1); // Leones 2 - 1 Tigres
        torneo.getPartidos().get(1).registrarResultados(0, 0); // Tigres 0 - 0 Águilas

        // Simulamos que algunos jugadores anotaron
        j1.setGoles(2); // Carlos Pérez metió 2 goles
        j3.setGoles(1); // Juan Torres metió 1 gol
        j5.setGoles(3); // Luis Ramírez metió 3 goles

        // 8. Mostrar partidos actualizados
        torneo.mostrarPartidos();

        // 9. Mostrar tabla de posiciones
        torneo.mostrarTablaPosiciones();

        // 10. Mostrar jugadores de un equipo
        System.out.println("\n👥 Jugadores del equipo " + equipo1.getNombre() + ":");
        equipo1.mostrarJugadores();

        // 11. Mostrar tabla de goleadores
        torneo.mostrarGoleadores();
    }
}

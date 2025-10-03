import model.*;
import java.util.Random;

public class DatosPrueba {

    public static void cargar(Torneo torneo) {
        String[] nombresEquipos = {
                "Leones FC", "Tigres Dorados", "Águilas Rojas", "Toros Negros", "Dragones Azules",
                "Cóndores", "Pumas", "Halcones", "Guerreros", "Titanes"
        };

        String[] nombresJugadores = {
                "Carlos", "Juan", "Pedro", "Luis", "Andrés", "Mateo", "Felipe", "Santiago", "Diego", "Nicolás",
                "Julián", "Sebastián", "David", "Daniel", "Fernando", "Martín", "Esteban", "Álvaro", "Cristian", "Simón"
        };

        String[] apellidos = {
                "Ramírez", "Torres", "Gómez", "Pérez", "Rodríguez", "Martínez", "Castro", "Hernández",
                "Moreno", "Vargas", "Suárez", "Cárdenas", "Jiménez", "Rojas", "Salazar"
        };

        Random rand = new Random();

        // 🔹 Crear 10 equipos
        for (String nombreEquipo : nombresEquipos) {
            Equipo equipo = new Equipo(nombreEquipo);

            // 🔹 Crear 10 jugadores por equipo
            for (int i = 1; i <= 10; i++) {
                String nombre = nombresJugadores[rand.nextInt(nombresJugadores.length)] + " " +
                                apellidos[rand.nextInt(apellidos.length)];

                int edad = 18 + rand.nextInt(15); // entre 18 y 32 años
                Posicion posicion = Posicion.values()[rand.nextInt(Posicion.values().length)];

                // 🔹 Generar número de camiseta único dentro del equipo
                int numero = 1 + rand.nextInt(99);
                boolean repetido;
                do {
                    repetido = false;
                    for (Jugador j : equipo.getJugadores()) {
                        if (j.getNumero() == numero) {
                            repetido = true;
                            numero = 1 + rand.nextInt(99);
                            break;
                        }
                    }
                } while (repetido);

                Jugador jugador = new Jugador(nombre, edad, posicion, numero);
               equipo.getJugadores().add(jugador);

            }

            torneo.getEquipos().add(equipo);
        }

        System.out.println("✅ Datos de prueba cargados: 10 equipos con 10 jugadores cada uno.");
    }
}

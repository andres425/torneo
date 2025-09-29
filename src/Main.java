import model.*;
import java.time.LocalTime;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("🏆 Bienvenido al sistema de gestión de torneos");
        System.out.print("Ingrese el nombre del torneo: ");
        String nombreTorneo = sc.nextLine();

        Torneo torneo = new Torneo(nombreTorneo);

        int opcion;
        do {
            System.out.println("\n===== MENÚ PRINCIPAL =====");
            System.out.println("1. Agregar equipo");
            System.out.println("2. Agregar jugador a un equipo");
            System.out.println("3. Programar partido");
            System.out.println("4. Registrar resultado de un partido");
            System.out.println("5. Mostrar partidos");
            System.out.println("6. Mostrar tabla de posiciones");
            System.out.println("7. Mostrar tabla de goleadores");
            System.out.println("8. Mostrar jugadores de un equipo");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = sc.nextInt();
            sc.nextLine(); // limpiar buffer

            switch (opcion) {
                case 1 -> {
                    System.out.print("Ingrese nombre del equipo: ");
                    String nombreEquipo = sc.nextLine();
                    torneo.agregarEquipo(new Equipo(nombreEquipo));
                    System.out.println("✅ Equipo agregado con éxito.");
                }
                case 2 -> {
                    if (torneo.getEquipos().isEmpty()) {
                        System.out.println("⚠ No hay equipos registrados todavía.");
                        break;
                    }

                    System.out.println("\n📌 Seleccione el equipo:");
                    for (int i = 0; i < torneo.getEquipos().size(); i++) {
                        System.out.println((i + 1) + ". " + torneo.getEquipos().get(i).getNombre());
                    }

                    System.out.print("Ingrese el número del equipo: ");
                    int opcionEquipo = sc.nextInt();
                    sc.nextLine(); // limpiar buffer

                    if (opcionEquipo < 1 || opcionEquipo > torneo.getEquipos().size()) {
                        System.out.println("❌ Opción inválida.");
                        break;
                    }

                    Equipo equipoSeleccionado = torneo.getEquipos().get(opcionEquipo - 1);

                    System.out.print("Ingrese nombre del jugador: ");
                    String nombreJugador = sc.nextLine();

                    System.out.print("Ingrese edad del jugador: ");
                    int edad = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Ingrese posición del jugador: ");
                    String posicion = sc.nextLine();

                    System.out.print("Ingrese número de camiseta: ");
                    int numero = sc.nextInt();

                    Jugador jugador = new Jugador(nombreJugador, edad, posicion, numero);
                    equipoSeleccionado.agregarJugador(jugador);

                    System.out.println("✅ Jugador agregado al equipo " + equipoSeleccionado.getNombre());
                    break;
                }
                case 3 -> {
                    System.out.print("Ingrese nombre del equipo local: ");
                    String local = sc.nextLine();
                    System.out.print("Ingrese nombre del equipo visitante: ");
                    String visitante = sc.nextLine();
                    System.out.print("Ingrese hora del partido (HH:mm): ");
                    String hora = sc.nextLine();

                    Equipo equipoLocal = torneo.buscarEquipo(local);
                    Equipo equipoVisitante = torneo.buscarEquipo(visitante);

                    if (equipoLocal != null && equipoVisitante != null) {
                        String[] partes = hora.split(":");
                        torneo.programarPartido(
                                equipoLocal,
                                equipoVisitante,
                                LocalTime.of(Integer.parseInt(partes[0]), Integer.parseInt(partes[1])));
                        System.out.println("✅ Partido programado con éxito.");
                    } else {
                        System.out.println("❌ Alguno de los equipos no existe.");
                    }
                }
                case 4 -> {
                    torneo.mostrarPartidos();
                    System.out.print("Seleccione número de partido: ");
                    int num = sc.nextInt();
                    sc.nextLine();
                    if (num > 0 && num <= torneo.getPartidos().size()) {
                        Partido partido = torneo.getPartidos().get(num - 1);
                        System.out.print("Goles de " + partido.getEquipoLocal().getNombre() + ": ");
                        int golesLocal = sc.nextInt();
                        System.out.print("Goles de " + partido.getEquipoVisitante().getNombre() + ": ");
                        int golesVisitante = sc.nextInt();
                        sc.nextLine();

                        partido.registrarResultados(golesLocal, golesVisitante);
                        System.out.println("✅ Resultado registrado.");
                    } else {
                        System.out.println("❌ Partido no válido.");
                    }
                }
                case 5 -> torneo.mostrarPartidos();
                case 6 -> torneo.mostrarTablaPosiciones();
                case 7 -> torneo.mostrarGoleadores();
                case 8 -> {
                    System.out.print("Ingrese nombre del equipo: ");
                    String nombreEquipo = sc.nextLine();
                    Equipo equipo = torneo.buscarEquipo(nombreEquipo);
                    if (equipo != null) {
                        equipo.mostrarJugadores();
                    } else {
                        System.out.println("❌ Equipo no encontrado.");
                    }
                }
                case 0 -> System.out.println("👋 Saliendo del sistema...");
                default -> System.out.println("❌ Opción no válida.");
            }
        } while (opcion != 0);

        sc.close();
    }
}

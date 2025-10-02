import model.*;

public class TestPrueba {
    public static void main(String[] args) {
        Torneo torneo = new Torneo("Copa Intercolegial");
        DatosPrueba.cargar(torneo);

        for (Equipo e : torneo.getEquipos()) {
            System.out.println("⚽ " + e.getNombre());
            for (Jugador j : e.getJugadores()) {
                System.out.println("   - " + j.getNombre() + " (" + j.getPosicion() + ", #" + j.getNumero() + ")");
            }
        }
    }
}

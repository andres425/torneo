package model;

public class Jugador {
    private String nombre;
    private int edad;
    private Posicion posicion;
    private int numero;
    private int goles;

    // tarjetas
    private int tarjetasAmarillas;
    private int tarjetasRojas;
    private boolean suspendido;
    
    public Jugador(String nombre, int edad, String posicion, int numero) {
    this(nombre, edad, Posicion.valueOf(posicion.toUpperCase()), numero);
}


    public Jugador(String nombre, int edad, Posicion posicion, int numero) {
        setNombre(nombre);
        setEdad(edad);
        setPosicion(posicion);
        setNumero(numero);
        this.goles = 0;
        this.tarjetasAmarillas = 0;
        this.tarjetasRojas = 0;
        this.suspendido = false;
    }

    // ---------------- Métodos de tarjetas ----------------
    public void agregarTarjetaAmarilla() {
        tarjetasAmarillas++;
        if (tarjetasAmarillas >= 2) {
            suspendido = true;
            tarjetasAmarillas = 0; // se resetean después de la suspensión
            System.out.println(nombre + " ha sido suspendido por acumulación de amarillas.");
        }
    }

    public void agregarTarjetaRoja() {
        tarjetasRojas++;
        suspendido = true;
        System.out.println(nombre + " ha sido expulsado y no podrá jugar el próximo partido.");
    }

    public boolean estaSuspendido() {
        return suspendido;
    }

    public void cumplirSuspension() {
        if (suspendido) {
            suspendido = false; // vuelve a estar disponible
            System.out.println(nombre + " ya cumplió su suspensión y puede volver a jugar.");
        }
    }

    // ---------------- Goles ----------------
    public int getGoles() {
        return goles;
    }

    public void setGoles(int goles) {
        if (goles < 0) throw new IllegalArgumentException("Los goles no pueden ser negativos");
        this.goles = goles;
    }

    public void anotarGol() {
        this.goles++;
    }

    // ---------------- Getters/Setters normales ----------------
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty() || !nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            throw new IllegalArgumentException("El nombre no puede estar vacío ni contener números/símbolos");
        }
        this.nombre = nombre;
    }

    public int getEdad() { return edad; }
    public void setEdad(int edad) {
        if (edad > 0) this.edad = edad;
        else throw new IllegalArgumentException("la edad debe ser mayor a 0");
    }

    public Posicion getPosicion() { return posicion; }
    public void setPosicion(Posicion posicion) {
        if (posicion == null) throw new IllegalArgumentException("La posición no puede ser nula");
        this.posicion = posicion;
    }

    public int getNumero() { return numero; }
    public void setNumero(int numero) {
        if (numero > 0 && numero <= 99) this.numero = numero;
        else throw new IllegalArgumentException("El numero debe ser mayor a 0 y menor o igual a 99");
    }

    // ---------------- Tarjetas getters ----------------
    public int getTarjetasAmarillas() { return tarjetasAmarillas; }
    public int getTarjetasRojas() { return tarjetasRojas; }

    @Override
    public String toString() {
        return "Nombre: " + nombre + ", Edad: " + edad +
                ", Posición: " + posicion + ", Número: " + numero +
                ", Goles: " + goles + ", Amarillas: " + tarjetasAmarillas +
                ", Rojas: " + tarjetasRojas +
                (suspendido ? " (SUSPENDIDO)" : "");
    }
}

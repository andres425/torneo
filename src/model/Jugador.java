package model;

public class Jugador {
    private String nombre;
    private int edad;
    private String posicion;
    private int numero;
    private int goles;

    public Jugador(String nombre, int edad, String posicion, int numero) {
        setNombre(nombre);
        setEdad(edad);
        setPosicion(posicion);
        setNumero(numero);
        this.goles=0;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty() || !nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            throw new IllegalArgumentException("El nombre no puede estar vacío ni contener números/símbolos");
        } else {
            this.nombre = nombre;
        }
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        if (edad > 0) {
            this.edad = edad;
        } else {
            throw new IllegalArgumentException("la edad debe ser mayor a 0");
        }
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        if (posicion == null || posicion.trim().isEmpty() || !posicion.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            throw new IllegalArgumentException("la posicion no puede estar vacia");
        } else {
            this.posicion = posicion;
        }
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        if (numero > 0 && numero <= 99) {
            this.numero = numero;
        } else {
            System.out.println("El numero debe ser mayor a 0 y menor o igual a 99");
        }
    }
        public int getGoles() {
        return goles;
    }

    public void setGoles(int goles) {
        if (goles < 0) {
            throw new IllegalArgumentException("Los goles no pueden ser negativos");
        }
        this.goles = goles;
    }
      public void anotarGol() {
        this.goles++; 
    }

    @Override
    public String toString() {
        return "Nombre: " + nombre + ", Edad: " + edad +
                ", Posición: " + posicion + ", Número: " + numero;
    }
    

}

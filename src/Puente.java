public class Puente {

    private static final int PESO_MAXIMO = 350;
    private static final int MAX_PERSONAS = 4;
    private static final int MAX_PERSONAS_POR_SENTIDO = 3;

    private int peso = 0; // Peso actual en el puente
    private int getNumPersonasIzquierda = 0;
    private int getNumPersonasDerecha = 0;
    private int personasIzquierdaCruzando = 0;
    private int personasDerechaCruzando = 0;

    //metodos sincronizados para evitar conflictos de acceso concurrente

    synchronized public int getPeso() {
        return peso;
    }

    synchronized public int getNumPersonasIzquierda() {
        return getNumPersonasIzquierda;
    }

    synchronized public int getNumPersonasDerecha() {
        return getNumPersonasDerecha;
    }

    // mtodo para autorizar el cruce de una persona
    synchronized public boolean autorizacionPaso(Persona persona) {
        boolean resultado;
        int lado = persona.getLadoPuente(); // obtiene de que lado esta cruzando (0 = izquierda, 1 = derecha)

        // si el peso total con la persona nueva no excede el limite y no hay más de 4
        // personas en el puente
        if (this.peso + persona.getPeso() <= Puente.PESO_MAXIMO
                && this.getNumPersonasIzquierda + this.getNumPersonasDerecha < Puente.MAX_PERSONAS) {
            // verificar si la persona esta en el lado izquierdo
            if (lado == 0) {
                // Limitar a maximo 3 personas cruzando desde el lado izquierdo
                if (personasIzquierdaCruzando < MAX_PERSONAS_POR_SENTIDO) {
                    this.getNumPersonasIzquierda++;
                    this.personasIzquierdaCruzando++; // incrementa el numero de personas cruzando desde la izquierda
                    this.peso += persona.getPeso(); // agrega el peso de la persona al puente
                    resultado = true; // autoriza el paso
                } else {
                    resultado = false; // no autoriza el paso porque ya hay 3 personas cruzando en ese sentido
                }
            } else {
                // limitar a maximo 3 personas cruzando desde el lado derecho
                if (personasDerechaCruzando < MAX_PERSONAS_POR_SENTIDO) {
                    this.getNumPersonasDerecha++;
                    this.personasDerechaCruzando++; // incrementa el numero de personas cruzando desde la derecha
                    this.peso += persona.getPeso(); // agrega el peso de la persona al puente
                    resultado = true; // autoriza el paso
                } else {
                    resultado = false; // no autoriza el paso porque ya hay 3 personas cruzando en ese sentido
                }
            }
        } else {
            resultado = false; // no autoriza el paso si se excede el peso o el numero total de personas
        }
        return resultado;
    }

    // metodo que se llama cuando una persona termina de cruzar el puente
    synchronized public void terminaPaso(Persona persona) {
        // Se reduce el peso total en el puente
        this.peso -= persona.getPeso();
        // reduce el numero de personas en el lado de donde cruzaba la persona
        if (persona.getLadoPuente() == 0) {
            this.getNumPersonasIzquierda--;
            this.personasIzquierdaCruzando--; // disminuye el numero de personas cruzando desde la izquierda
        } else {
            this.getNumPersonasDerecha--;
            this.personasDerechaCruzando--; // disminuye el numero de personas cruzando desde la derecha
        }
    }
}

import java.util.concurrent.ThreadLocalRandom;

public class Persona implements Runnable {

    private final String idPersona;
    private final int peso;
    private final int tMinPaso, tMaxPaso;
    private final Puente puente;
    private final int ladoPuente; //0 lado izquierdo, 1 lado derecho

    Persona(Puente puente, int peso, int tMinPaso, int tMaxPaso, String idP) {
        this.peso = peso;
        this.tMinPaso = tMinPaso;
        this.tMaxPaso = tMaxPaso;
        this.idPersona = idP;
        this.puente = puente;
        //lado del puente asignado aleatoriamente entre 0 y 1
        this.ladoPuente = ThreadLocalRandom.current().nextInt(2);
    }

    public int getPeso() {
        return this.peso;
    }

    public int getLadoPuente() {
        return this.ladoPuente;
    }

    //mi metodo run q se inicia cuando se crea un hilo de una persona
    @Override
    public void run() {
        //muestra la persona que quiere cruzar desde el lado izquierdo o derecho
        System.out.print("-- " + idPersona + " de " + peso + " kg quiere cruzar" +
                (getLadoPuente() == 0 ? " por la izquierda " : " por la derecha"));

        //estado del puente peso total y numero de personas en cada lado
        System.out.println("En el puente hay un peso de " + puente.getPeso() + " y " + puente.getNumPersonasIzquierda() +
                " persona" + ((puente.getNumPersonasIzquierda() == 1) ? "s" : "") + " en la izquierda y " +
                puente.getNumPersonasDerecha() + " persona" + ((puente.getNumPersonasDerecha() == 1) ? "s" : "") + " en la derecha");

        //Espera autorización para pasar
        boolean autorizado = false;
        while (!autorizado) {
            //sincroniza para que no haya confilcto de acceso al puente
            synchronized (this.puente) {
                //intenta obtener autorizacion
                autorizado = this.puente.autorizacionPaso(this);
                if (!autorizado) {
                    try {
                        //si esta autorizado espera a que el puente este disponible
                        System.out.println("~ " + idPersona + " tiene que esperar");
                        this.puente.wait();//espera hasta que sea notificado
                    } catch (InterruptedException e) {
                       
                        System.out.println("Interrupción mientras espera " + idPersona);
                    }
                }
            }
        }

        //si esta Autorizado puede pasar el puente
        System.out.print("> " + idPersona + " con peso " + peso + " puede cruzar.");

        //imprime el estado del puente al iniciar el cruce, dependiendo del lado
        if (ladoPuente == 0) {
            System.out.println("En el puente en el lado izquierdo hay un peso de " + puente.getPeso() + " y " + puente.getNumPersonasIzquierda() + " persona" + ((puente.getNumPersonasIzquierda() == 1) ? "s" : ""));
        } else {
            System.out.println("En el puente en el lado derecho hay un peso de " + puente.getPeso() + " y " + puente.getNumPersonasDerecha() + " persona" + ((puente.getNumPersonasDerecha() == 1) ? "s" : ""));
        }

        //tiempo de paso aleatorio
        int tiempoPaso = ThreadLocalRandom.current().nextInt(tMinPaso, tMaxPaso);
        try {
            System.out.println(idPersona + " tardará " + tiempoPaso + " en cruzar");
            Thread.sleep(1000 * tiempoPaso);
        } catch (InterruptedException e) {
           
            System.out.println("Interrupción mientras pasa " + idPersona);
        }

        // la persona Sale del puente 
        synchronized (this.puente) {
            //notifica al puente que termino de cruzar
            this.puente.terminaPaso(this);
            
            System.out.print("< " + idPersona + " con peso " + peso + " sale del puente.");

            //estado actual del puente
            System.out.println("En puente hay un peso de " + puente.getPeso() + " y " + puente.getNumPersonasIzquierda() +
                    " persona" + ((puente.getNumPersonasIzquierda() == 1) ? "s" : "") + " en la izquierda y " +
                    puente.getNumPersonasDerecha() + " persona" + ((puente.getNumPersonasDerecha() == 1) ? "s" : "") + " en la derecha");

            puente.notifyAll();//notifica a todas las personas que el puente se ha liberado
        }
    }

}

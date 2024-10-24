import java.util.concurrent.ThreadLocalRandom;

public class PasoPorElPuente {
    public static void main(String[] args)  {
        Puente elPuente=new Puente();
		
		int tMinNuevaPersona=1;
		int tMaxNuevaPersona=5;
		int tMinPasoPuente=5;
		int tMaxPasoPuente=10;
		
		int pesoMinpersona=40;
		int pesoMaxPersona=120;
		
		System.out.println("###################### Se abre el puente");
		int idPersona=1;
		
		while(true) {
			int tiempoNuevoPersona=ThreadLocalRandom.current().nextInt(tMinNuevaPersona, tMaxNuevaPersona);
			int pesoNuevaPersona=ThreadLocalRandom.current().nextInt(pesoMinpersona, pesoMaxPersona);
			
			System.out.println("Siguiente persona en "+tiempoNuevoPersona+" segundos");
			try {
				Thread.sleep(1000*tiempoNuevoPersona);
			} catch (InterruptedException e) {
				
				System.out.println("Interrumpido proceso princial");
			}
			Thread hiloPersona=new Thread(new Persona(elPuente,pesoNuevaPersona,tMinPasoPuente,tMaxPasoPuente,"Person"+idPersona));
			hiloPersona.start();
			idPersona++;
		}
	}
}


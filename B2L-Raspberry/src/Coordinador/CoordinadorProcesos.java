package Coordinador;

//import com.rapplogic.xbee.api.XBeeAddress16;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeException;

//import com.rapplogic.xbee.api.XBeeException;

public class CoordinadorProcesos {
	
	public static void main (String[] args){
		System.out.println("Hola");
		
		//crea objeto para conexion
		ConexionXBee myXBee = new ConexionXBee();
		
		//intenta conectarse a XBee
		while(myXBee.ConectarXBee()==false){
			System.out.println("Error: XBee no conectado");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("XBee conectado");
		
		//crear objeto Lista
		Lista miLista = new Lista();
		
		//pase de lista
		miLista.PasarListaCortaS2(myXBee);
		
		//ciclo infinito
		while(true){
			
			//definir mensaje
			int[] payload = new int[] { 1, 0 };
			
			//revisar caso de lista vacia
			if(miLista.listaModulosS2.size()==0)
				System.out.println("Lista vacia");
			
			//barrer lista S2
			for(int i = 0; i < miLista.listaModulosS2.size(); i++){
				//establecer destino
				XBeeAddress64 destination = miLista.listaModulosS2.get(i).direccion;
				
				//enviar comando
				System.out.println("Se enviara comando");
				myXBee.EnviarS2(payload, destination);
				
				// ignorar mensaje de confirmacion de envio
				try {
					myXBee.Recibir();
				} catch (XBeeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// tomar mensaje enviado por el Arduino
				MensajeXBeeRecibido mensajeRecibido;
				try {
					mensajeRecibido = myXBee.Recibir();
					/*if(mensajeRecibido.mensaje[1]==0){
						System.out.print(miLista.listaModulosS2.get(i).funcion);
						System.out.print(": ");
						System.out.println(mensajeRecibido.mensaje[0]);
						System.out.println("");
					}*/
					
					int salida = (mensajeRecibido.mensaje[1]<<8 & 0xFF00) ^ (mensajeRecibido.mensaje[0] & 0xFF);
					
					System.out.print(miLista.listaModulosS2.get(i).funcion);
					System.out.print(": ");
					System.out.println(salida);
					System.out.println(mensajeRecibido.mensaje[0]);
					System.out.println(mensajeRecibido.mensaje[1]);
					System.out.println("");
				} catch (XBeeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			
			//delay entre muestreo
			try {
				Thread.sleep(4000);
			} catch ( InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	

}

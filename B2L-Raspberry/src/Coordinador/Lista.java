package Coordinador;

import java.util.ArrayList;
import java.util.List;

import com.rapplogic.xbee.api.XBeeAddress16;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeException;

public class Lista {
	// lista para serie 1
	List<InfoModulo> listaModulosS1 = new ArrayList<InfoModulo>();
	
	//lista para serie 2
	List<InfoModuloS2> listaModulosS2 = new ArrayList<InfoModuloS2>();
	
	//pase de lista para serie 1
	public void PasarListaLarga16(ConexionXBee myXBee, int cupo){
		System.out.print("Iniciar pase de lista");
		int[] payload = new int[] { 2, 0 };
		
		for(int i = 1; i <= cupo; i++){
			// asigna direccion
			XBeeAddress16 destination = new XBeeAddress16(0x10, i);
			
			// enviar mensaje 
			System.out.print("Buscando XBee ");
			System.out.println(i);
			myXBee.EnviarS116(payload, destination);
			
			// ignorar mensaje de confirmacion de envio
			try {
				myXBee.Recibir();
			} catch (XBeeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			// tomar mensaje enviado por el Arduino
			MensajeXBeeRecibido mensajeRecibido = new MensajeXBeeRecibido();
			try {
				mensajeRecibido = myXBee.Recibir();
				
				// "presente"
				if(mensajeRecibido.mensaje[1]==1){
					System.out.print("XBee ");
					System.out.print(i);
					System.out.println(" presente");
					
					// crear elemento para guardar la informacion
					InfoModulo info = new InfoModulo();
					info.direccion = destination;
					
					// evaluar sensor presente en modulo
					switch (mensajeRecibido.mensaje[0]){
						case 1:
							//sensor de temperatura
							info.funcion="Temperatura";
							System.out.println(info.funcion);
							break;
						case 2:
							//sensor de personas
							info.funcion="Personas";
							System.out.println(info.funcion);
							break;
						case 3:
							//sensor de sonido
							info.funcion="Sonido";
							System.out.println(info.funcion);
							break;
						case 153:
							//sensor de prueba
							info.funcion="Prueba";
							System.out.println(info.funcion);
							break;
						default:
							//otro sensor
							info.funcion="Sensor";
							System.out.println(info.funcion);
							break;
					}
					listaModulosS1.add(info);
				}
				
			} catch (XBeeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		System.out.println("Pase de lista completo");
		System.out.println("");
	}
	
	public void PasarListaCortaS2(ConexionXBee myXBee){
		System.out.println("Iniciar pase de lista");
		
		//definir payload de pase de lista
		int[] payload = new int[] { 2, 0 };
		
		//enviar mensaje broadcast
		myXBee.EnviarS2(payload, XBeeAddress64.BROADCAST);
		
		try {
			Thread.sleep(2000);
		} catch ( InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//crear objeto para guardar mensajes
		MensajeXBeeRecibido mensajeRecibido = new MensajeXBeeRecibido();
		
		//recibir todos los mensajes en cola
		while(true){
			try{
				mensajeRecibido = myXBee.Recibir();
				
				// crear elemento para guardar la informacion
				InfoModuloS2 info = new InfoModuloS2();
				
				// evaluar mensaje
				if(mensajeRecibido.tipo == "S2"){
					// guardar direccion
					System.out.println("Modulo detectado:");
					System.out.print("direccion:  ");
					System.out.println(mensajeRecibido.direccion64);
					info.direccion = mensajeRecibido.direccion64;
					
					//evaluar funcion
					System.out.print("funcion:  ");
					switch (mensajeRecibido.mensaje[0]){
						case 1:
							//sensor de temperatura
							info.funcion="Temperatura";
							System.out.println(info.funcion);
							break;
						case 2:
							//sensor de personas
							info.funcion="Personas";
							System.out.println(info.funcion);
							break;
						case 3:
							//sensor de sonido
							info.funcion="Sonido";
							System.out.println(info.funcion);
							break;
						case 153:
							//sensor de prueba
							info.funcion="Prueba";
							System.out.println(info.funcion);
							break;
						default:
							//otro sensor
							info.funcion="Sensor";
							System.out.println(info.funcion);
							break;
					}
					listaModulosS2.add(info);
					System.out.println();
				}
				
				continue;
				
			} catch(XBeeException e){
				//tiempo excedido
				break;
			}
		}
		
		System.out.println("Pase de lista completo");
		System.out.println("");
	}
}

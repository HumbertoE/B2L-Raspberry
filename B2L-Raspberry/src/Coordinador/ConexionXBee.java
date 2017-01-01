package Coordinador;

import com.rapplogic.xbee.api.ApiId;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress16;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.XBeeTimeoutException;
import com.rapplogic.xbee.api.wpan.TxRequest16;
import com.rapplogic.xbee.api.wpan.TxStatusResponse;
import com.rapplogic.xbee.api.zigbee.ZNetRxResponse;
import com.rapplogic.xbee.api.zigbee.ZNetTxRequest;
//import com.rapplogic.xbee.util.ByteUtils;
//import com.rapplogic.xbee.api.wpan.RxResponse16;

public class ConexionXBee {
	XBee xbee = new XBee();
	
	public Boolean ConectarXBee(){
		
		// conectarse con XBee en el puerto USB
		try {
			// por USB
			//xbee.open("/dev/ttyUSB0", 9600);
			// por UART
			String SerialPortID = "/dev/ttyAMA0";
			System.setProperty("gnu.io.rxtx.SerialPorts", SerialPortID);
			xbee.open(SerialPortID, 9600);
			//xbee.open("/dev/ttyAMA0", 9600);
			return true;
		} catch (XBeeException e) {
			xbee.close();
			e.printStackTrace();
			return false;
		}
	}
	public Boolean EnviarS116(int[] msgPayload, XBeeAddress16 msgDestination){ 

		// tomar payload
		// Note: we are using the Java int data type, since the byte data type is not unsigned, but the payload is limited to bytes.  That is, values must be between 0-255.
		int[] payload = msgPayload;

		// tomar direccion
		XBeeAddress16 destination = msgDestination;

		 TxRequest16 tx= new TxRequest16(destination, payload);

		TxStatusResponse status;
		try {
			status = (TxStatusResponse) xbee.sendSynchronous(tx);
			System.out.println("Comando enviado");
			if (status.isSuccess()) {
			    // the Arduino XBee received our packet
				System.out.println("Comando recibido");
				return true;
			} else{
				return false;
			}
		// tiempo excedido
		} catch (XBeeTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		// problema de conexion
		} catch (XBeeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}
	public Boolean EnviarS2(int[] msgPayload, XBeeAddress64 msgDestination){
		// tomar payload
		// Note: we are using the Java int data type, since the byte data type is not unsigned, but the payload is limited to bytes.  That is, values must be between 0-255.
		int[] payload = msgPayload;

		// tomar direccion
		XBeeAddress64 destination = msgDestination;

		ZNetTxRequest tx= new ZNetTxRequest(destination, payload);

		try {
			xbee.sendAsynchronous(tx);
			System.out.println("Comando enviado");
			return true;

		// tiempo excedido
		} catch (XBeeTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		// problema de conexion
		} catch (XBeeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public MensajeXBeeRecibido Recibir() throws XBeeException{
		
		XBeeResponse response;
		
		response = xbee.getResponse(3000);
		
		MensajeXBeeRecibido miMensaje = new MensajeXBeeRecibido();
		
		//System.out.println(response.getPacketBytes().length);
		/*for (int i = 0; i < response.getPacketBytes().length; i++) {
			log.info("packet [" + i + "] " + ByteUtils.toBase16(response.getPacketBytes()[i]));
		}*/
		
		if (response.isError()) {
			System.out.println("Respuesta contiene errores");
			miMensaje.tipo = "error";
			
		} else if (response.getApiId() == ApiId.RX_16_RESPONSE) {
			//paquete recibido S1 16 bits
			System.out.print("Paquete: ");
			System.out.println(response);
				
			int[] paquete = response.getRawPacketBytes();
			/*for(int i=0;i<paquete.length;i=i+1){
				System.out.println(paquete[i]);
			}*/
			miMensaje.tipo = "S1_16bits";
			miMensaje.mensaje[0] = paquete[7];
			miMensaje.mensaje[1] = paquete[8];
			
			//log.info("Received RX 16 packet " + ((RxResponse16)response));
		/*} else if (response.getApiId() == ApiId.RX_64_RESPONSE) {
			System.out.println("Paquete 64");
			//log.info("Received RX 64 packet " + ((RxResponse64)response));
			return -1;*/
		} else if (response.getApiId() == ApiId.ZNET_RX_RESPONSE) {
			//paquete recibido S2
			ZNetRxResponse zNetPaquete = (ZNetRxResponse) response;
			//System.out.println(zNetPaquete.getRemoteAddress64());
			
			//System.out.println(ByteUtils.toBase16(zNetPaquete.getData()));
			int[] data = zNetPaquete.getData();
			miMensaje.tipo = "S2";
			miMensaje.direccion64=zNetPaquete.getRemoteAddress64();
			miMensaje.mensaje[0] = data[0];
			miMensaje.mensaje[1] = data[1];
		} else {
			System.out.println("Paquete misterioso");
			//log.info("Ignoring mystery packet " + response.toString());
			miMensaje.tipo = "paquete_misterioso";
		}
		/*} catch (XBeeTimeoutException e) {
			// TODO Auto-generated catch block
			System.out.println("Error, timeout");
			e.printStackTrace();
			int[] salida = {2,0};
			return salida;
		} catch (XBeeException e) {
			// TODO Auto-generated catch block
			System.out.println("Error al tratar de remover paquete de queue");
			e.printStackTrace();
			int[] salida = {3,0};
			return salida;
		}*/
		return miMensaje;
		
	}
	
	public void DesconectarXBee(){
		// desconectar XBee
		xbee.close();
	}

}

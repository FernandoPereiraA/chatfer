package Chat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

//Clase para crear nuevo grupo de chat con su constructor
public class Chat {

	MulticastSocket socket;
	String address;
	InetSocketAddress socketAdress;
	int port;
	String nombreChat;
    
	
	public Chat(String address,int port,String nombre) {		
		this.address=address;		
		this.nombreChat = nombre;
		this.port=port;
		//this.address = InetAddress.getByName(address);
	}
	
	
	 public void setSocket(int s) throws IOException {
		    this.socket= new MulticastSocket(s);		    
		  }
	
	
}

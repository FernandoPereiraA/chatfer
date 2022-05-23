package Chat;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

class ClienteThread extends Thread
{
    private MulticastSocket socket;
    private InetAddress group;
    private int port;
    private static final int MAX_LEN = 1000;
    private String nombre;
    
    ClienteThread(MulticastSocket socket,InetAddress group,int port)//,String nombre)
    {
        this.socket = socket;
        this.group = group;
        this.port = port;
        //this.nombre= nombre;
        
    }    

    @Override
    public void run()
    {
        while(!ServidorChat.finalizado)
        {
            byte[] buffer = new byte[ClienteThread.MAX_LEN];
            DatagramPacket datagram = new
                    DatagramPacket(buffer,buffer.length,group,port);
            String message;
            try
            {
                socket.receive(datagram);
                message = new
                        String(buffer,0,datagram.getLength(),"UTF-8");
                if(!message.startsWith(ServidorChat.name))
                    System.out.println(message);
            }
            catch(IOException e)
            {
                System.out.println("Socket cerrado!");
            }
        }
    }

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
package Chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ServidorChat 
{
    private static final String Terminar = "Salir";
    static String name;
    //static int port=10000;
    static volatile boolean finalizado = false;
    static Chat chatDeportes,chatModa,chatEconomia;
    //private static InetAddress group;
    private static InetSocketAddress grupo;
    static volatile ArrayList<String> salasChatString;
    static ArrayList<Chat> salasChatObjetos;   
    //static MulticastSocket socket;
   
    public static ArrayList<String> usuariosConectados = new ArrayList<String>();

    
    public static void main(String[] args)
    {  
    	//Creamos 3 salas de chat diferentes a parte del chat principal
    	CrearChats();
    	//Inicializamos variables
    	MulticastSocket socket=null;
    	InetAddress group=null;
    	int port=0; 	
    	   	
        {
            try
            {              
                //Pedimos al usuario el nombre
                Scanner sc = new Scanner(System.in);
                System.out.print("Introduce tu nombre: ");
                name = sc.nextLine();
                anadirUsuario(name);
                System.out.println("\n");
                
                //Le decimos al usuarios las salas de chat que hay creadas
                listarSalasCreadas();
                String chatElegido;
                chatElegido = sc.nextLine();
                
                //Miramos si hay usuarios conectados
                ImprimirUsuariosConectados();              
                
                if (chatElegido.contains("Moda")) 
                {
                	group = InetAddress.getByName(chatModa.address); 
                    port = chatModa.port;
                    socket = new MulticastSocket(port);
                    //socket.setTimeToLive(0);
                    socket.joinGroup(group);
                    Thread tModa = new Thread(new ClienteThread(socket,group,port));
                    tModa.start();            	                               
                    System.out.println("¡Conexión exitosa al grupo de moda y complementos!");
                }
                
                if (chatElegido.contains("Deportes")) 
                {
                    group = InetAddress.getByName(chatDeportes.address); 
                    port = chatDeportes.port;
                    socket = new MulticastSocket(port);
                    socket.setTimeToLive(0);
                    socket.joinGroup(group);
                    Thread tDep = new Thread(new ClienteThread(socket,group,port));
                    tDep.start();
                    System.out.println("¡Conexión exitosa al grupo de Deportes!");     
                }
                
                if (chatElegido.contains("Economia")) 
                {
                	group = InetAddress.getByName(chatEconomia.address); 
                    port = chatEconomia.port;
                    socket = new MulticastSocket(port);
                    socket.setTimeToLive(0);
                    socket.joinGroup(group);
                    Thread tEco = new Thread(new ClienteThread(socket,group,port));
                    tEco.start();             	                               
                    System.out.println("¡Conexión exitosa al grupo de Economía y Empresa!");
                }
                
              //El cliente no ha elegido o ha dicho no
                if (chatElegido.contains("Crear")) 
                {   
                	
                	Scanner sc1 = new Scanner(System.in);
                    System.out.print("Nombre del nuevo canal: ");
                    String canal = sc.nextLine();
                    
                    Chat nuevoChat=CrearNuevoChatUsuario(canal);
                	
                	group = InetAddress.getByName(nuevoChat.address); 
                    port = nuevoChat.port;
                    socket = new MulticastSocket(port);
                    socket.setTimeToLive(0);
                    socket.joinGroup(group);
                    Thread tNuevo = new Thread(new ClienteThread(socket,group,port));
                    tNuevo.start();             	                               
                    System.out.println("¡Conexión exitosa al grupo de "+ canal);                 	                               
                               
                   }
                
                //El cliente no ha elegido o ha dicho no
                if (chatElegido.contains("No")) 
                {   
                	port=10000;
                    group = InetAddress.getByName("231.0.0.1");                     
                    socket = new MulticastSocket(port);
                    socket.setTimeToLive(0);
                    socket.joinGroup(group);
                    Thread tEco = new Thread(new ClienteThread(socket,group,port));
                    tEco.start();    
                    System.out.println("¡Conexión exitosa al grupo principal!");                 	                               
                               
                   }
                                
                while(true)
                {               	
                	      	
                    String message;
                    message = sc.nextLine();
                    //Si el usuario escribe "Salir" sale del chat
                    if(message.equalsIgnoreCase(ServidorChat.Terminar))
                    {
                    	quitarUsuario(name);
                    	System.out.println("Hasta pronto " + name);                    	 
                        finalizado = true;
                        socket.leaveGroup(group);
                        socket.close();
                        break;
                    }                    
                    
                    message = name + ": " + message;
                    byte[] buffer = message.getBytes();
                    DatagramPacket datagram = new DatagramPacket(buffer,buffer.length,group,port);
                    socket.send(datagram);                    
                
            }
            }
            catch(SocketException se)
            {
                System.out.println("Error al crear socket");
                se.printStackTrace();
            }
            catch(IOException ie)
            {
                System.out.println("Error al leer o escribir en socket");
                ie.printStackTrace();
            }
        }      
        
    }
    
  //Método para crear tres salas de chat
    public static void CrearChats() {
    	
    	salasChatString= new ArrayList<String>();
    	salasChatObjetos= new ArrayList<Chat>();
    	
    	chatDeportes = new Chat("231.0.0.2",10001,"Chat Deportes");    	
    	salasChatString.add(chatDeportes.nombreChat);
    	salasChatObjetos.add(chatDeportes);
    	
    	chatModa = new Chat("231.0.0.3",10002,"Chat Moda");    	
    	salasChatString.add(chatModa.nombreChat);
    	salasChatObjetos.add(chatModa);
    	
    	chatEconomia = new Chat("231.0.0.4",10003,"Chat Economia");    	
    	salasChatString.add(chatEconomia.nombreChat);
    	salasChatObjetos.add(chatEconomia);
    	
    } 
    
    //Método que permite a usuario crear objeto Chat
    
    public static Chat CrearNuevoChatUsuario(String nombre) {
    	int random_int = (int)Math.floor(Math.random()*(10104-10004)+10004); //Numero random para crear el puerto
    	Chat chatACrear = new Chat("231.0.0.5",random_int,nombre);    	
    	salasChatString.add(chatACrear.nombreChat);
    	salasChatObjetos.add(chatACrear);    	
    	return chatACrear;
    }
    
    //public static void ConectarseAChat(int puerto,String direccion,String nombre) throws IOException {
    public static Thread ConectarseAChat(Chat chat, MulticastSocket socket) throws IOException {
    	int puerto=chat.port;
    	InetAddress addressChat = InetAddress.getByName(chat.address);
    	InetSocketAddress grupoM = new InetSocketAddress(addressChat, puerto);                	
    	Thread hilo = new Thread(new ClienteThread(socket,addressChat,puerto));
    	return hilo;
    	//hilo.start();
    }
    
    
    //Método para mostrar por pantalla las salas de chat disponible
    public static void listarSalasCreadas() {
    	System.out.println("Chats disponibles: ");
    for(int i=0;i<salasChatString.size();i++){    	
        System.out.println(salasChatString.get(i));  
           }
        System.out.println("¿A qué chat quiere conectarse? Escribe 'No' para quedarse en chat principal o 'Crear' para crear una nueva");
     }
    
  
    //Almacena nombre de los usuarios que se conectan
    public static void anadirUsuario(String nombre) {
        usuariosConectados.add(nombre);
    }
    
    public static void quitarUsuario(String nombre) {
    	usuariosConectados.remove(nombre);
    }
    
   //Booleano que nos dice si hay usuarios conectados
    public static boolean HayUsuarios() {
        return usuariosConectados.isEmpty();
        }
    public static ArrayList<String> NombreUsuariosConectados() {
        return usuariosConectados;
    }
    
    //Imprimir usuarios conectados en pantalla
    public static void ImprimirUsuariosConectados() {
        if (!HayUsuarios()) {
        	System.out.println("Usuarios conectados: " + usuariosConectados);
        } else {
        	System.out.println("No hay nadie conectado en este momento");
        }  
    
     }
}

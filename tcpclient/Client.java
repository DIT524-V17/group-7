
import java.net.*;
import java.io.*;
import java.util.*;
/**
 * @author Pontus Laestadius
 * Date format: DD-MM-YYYY
 * @since 20-03-2017
 * Maintained since: 17-04-2017
 */

public class Client {

	static int port = 9005;
	
	// Ip address of the Raspberry pi running on the ARC network. 
	// Change this IP if you change network, until the hotspot has been fixed.
	static String host = "192.168.0.120"; 
	static Boolean c = false;

	public static Transmitter init(String host, int port){
		return new Transmitter(host, port);
	}
}

class BaseSocket implements Runnable {
	private String host;
	private int port;
	DataOutputStream out;
	BufferedReader in;

	// A queue is used to handle all input commands so they go in the proper order and are not lost.
	Queue<String> input = new PriorityQueue<>();
	Queue<String> output = new PriorityQueue<>();
	Socket socket;

	// Macro for add.
	public void write(String s){

		try{
			// Will only send a command if it is send in increments of 30 miliseconds.
			if (System.currentTimeMillis() %30 != 0)
				out.writeUTF(s + "\n");
			out.flush();
		} catch (Exception e){
			e.printStackTrace();
		}

	}

	/**
	 *
	 * @return the first command in the queue.
	 */
	public String read(){

		try {
			// Java's way of declaring input stream is odd. Please ignore this line.
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			return in.readLine();
			
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			return ""; // Only occurs if an exception is thrown.
		}
	}
    
	BaseSocket(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void run() {}

	static void p(String s){
		System.out.println(s);
	}
}

class Transmitter extends BaseSocket implements Runnable {

	Transmitter(String host, int port) {
		super(host,port);
		try {
			socket = new Socket(host, port);
		} catch (IOException e){
			e.printStackTrace();
		}
		run();
	}

	@Override
	public void run() {
		try { // Catches IO exceptions

			// Out and input streams.
			out = new DataOutputStream(socket.getOutputStream());

		} catch (IOException e){
			e.printStackTrace();
			Client.c = false;
		}
	}
}
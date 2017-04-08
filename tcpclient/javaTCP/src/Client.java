import java.net.*;
import java.io.*;

import java.util.*;

/**
 * @author Pontus Laestadius
 * Date format: DD-MM-YYYY
 * @since 20-03-2017
 * Maintained since: 07-04-2017
 */

public class Client {

	private static int port = 9005;
	private static String host = "192.168.0.120";
	static Boolean c = false;

	public static void main(String [] args) {
		while (true){
			if (!c){
				init();
				c = true;
			}
		}
	}

	public static void init(){
		Transmitter r1 = init(host, port); // TODO: 06/04/2017 Use this for GUI reconnectability with some modifications
	}

	public static Transmitter init(String host, int port){
		Transmitter R1 = new Transmitter(host, port);
		R1.start();
		return R1;
	}
}

class BaseSocket implements Runnable {
	private Thread t;
	private String host;
	private int port;

	// A queue is used to handle all input commands so they go in the proper order and are not lost.
	Queue<String> input = new PriorityQueue<>();
	Queue<String> output = new PriorityQueue<>(); // TODO: 06/04/2017 make a read function for this. 
	Socket socket;

	// Macro for add.
	public void write(String s){
		// TODO: 06/03/2017 Add command vertification here. Preferably O(1).
		input.add(s);
	}

	/**
	 *
	 * @return the first command in the queue.
	 */
	public String read(){
		if (!output.isEmpty())
			return output.poll();
		else
			return "";
	}

	/**
	 *
	 * @return all the queued up output as an array of strings.
	 */
	public String[] readAll(){
		int i = 0;
		String[] res = new String[output.size()];
		while (!output.isEmpty())
			res[i++] = read();
		return res;
	}

	/**
	 *
	 * @return a formated version of all queued up output received.
	 */
	public String readAllFormated(){
		String[] format = readAll();
		String formatted = "";
		for (String f: format)
			formatted += f + ", ";
		return formatted.substring(0, formatted.length()-3);
	}

	BaseSocket(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void run() {}

	static void p(String s){
		System.out.println(s);
	}

	void start () {
		if (t == null) {
			t = new Thread (this);
			t.start ();
		}
	}
}

class Transmitter extends BaseSocket implements Runnable {
	private Thread t;

	Transmitter(String host, int port) {
		super(host,port);
		try {
			socket = new Socket(host, port);
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try { // Catches IO exceptions

			// Out and input streams.
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // I hate Java.

			p(this.getClass().toString() + " online.");

			try {
				while (Client.c){
					if (!socket.isInputShutdown())  // Checks if the socket is able to receive data.
						while (!input.isEmpty()) // Checks if the stack has any commands in it waiting.
							out.writeUTF(input.poll());

					out.flush();
					String fromServer;
					/*
					I like how java is like: 1 statement per line, Make it simple.
					Then they have this in the tutorial to save a single line
					https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
					 */
					while ((fromServer = in.readLine()) != null) {
						p(fromServer);
						output.add(fromServer);
					}
				}

			} finally {
				p("Abandon " + this.getClass().toString().substring(6) + ", It's going down!");
				Client.c = false;
			}

		} catch (IOException e){
			e.printStackTrace();
			Client.c = false;
		}
	}

	void start () {
		super.start();
	}
}
import java.net.*;
import java.io.*;

import java.util.*;

/**
 * @author Pontus Laestadius
 * Date: DD-MM-YYYY
 * @since 20-03-2017
 * Maintained since: 31-03-2017
 */

public class Client {

	private static int port = 9005;
	private static int port2 = 9000;
	private static String host = "192.168.0.120";
	static Boolean c = false;

	public static void main(String [] args) {
		while (true){
			if (!c){
				System.out.println("Connecting");
				init();
				c = true;
			}
		}
	}

	public static void init(){
		Receiver r2 = initReceiver(host, port2);
		Transmitter r1 = initTransmitter(host, port);
	}

	public static Transmitter initTransmitter(String host, int port){
		Transmitter R1 = new Transmitter(host, port);
		R1.start();
		return R1;
	}

	public static Receiver initReceiver(String host, int port){
		Receiver R1 = new Receiver(host, port);
		R1.start();
		return R1;
	}
}

class BaseSocket implements Runnable {
	private Thread t;
	private String host;
	private int port;
	Queue<String> input = new PriorityQueue<>();
	Socket socket;

	// Macro for add.
	public void write(String s){
		// TODO: 06/03/2017 Add command vertification here. Preferably O(1).
		input.add(s);
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
		write("cc"); // Enables the receiving socket. Do not remove.

		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			p(this.getClass().toString() + " online.");

			try {
				while (!socket.isInputShutdown() && Client.c)  // Checks if the socket is able to receive data.
					if (!input.isEmpty()) // Checks if the stack has any commands in it waiting.
						out.writeUTF(input.poll());
			} finally {
				p("Abandon Transmitter thread, It's going down!");
				Client.c = false;
			}

		} catch (IOException e){
			e.printStackTrace();
			Client.c = false;
		}

		p(this.getClass().toString() + " exiting.");
	}

	void start () {
		super.start();
	}
}

class Receiver extends BaseSocket implements Runnable {
	private Thread t;
	private ServerSocket ssocket;

	Receiver(String host, int port) {
		super(host,port);
		try {
			ssocket = new ServerSocket(port);
			ssocket.setSoTimeout(10000);
		} catch (IOException e){
			e.printStackTrace();
			Client.c = false;
		}
	}

	@Override
	public void run() {

		try {
			socket = ssocket.accept();
			DataInputStream in = new DataInputStream(socket.getInputStream());
			p(this.getClass().toString() + " online.");

			while (Client.c)
				p(in.readUTF()); // TODO: 27/03/2017 Replace with where you want the output to go.

		}catch(IOException e) {
			e.printStackTrace();
			Client.c = false;
		}

		p(this.getClass().toString() + " exiting.");
	}

	void start () {
		super.start();
	}
}
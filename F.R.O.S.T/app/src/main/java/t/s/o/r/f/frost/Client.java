package t.s.o.r.f.frost;// File Name GreetingClient.java
import java.net.*;
import java.io.*;

import java.net.*;
import java.io.*;

import java.util.*;

public class Client {

	static int port = 9005;
	static int port2 = 9000;
	static String host = "192.168.43.249";

	public static void main(String [] args) {

		/*
		try {
			host = InetAddress.getLocalHost().getHostName(); // TODO: 07/03/2017 replace with raspberry pi ip when implementing.
		} catch (UnknownHostException e){
			System.out.print("Unkown host");
		}
		*/

		Transmitter r1 = initTransmitter(host, port);
		// Receiver r2 = initReceiver(host, port2);

		// TODO: 05/03/2017 currently always keeps the client alive. Replace with automatic reconnection.
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

	public void run() {
	}

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
				while (!socket.isInputShutdown()) { // Checks if the socket is able to receive data.
					if (!input.isEmpty()){ // Checks if the st4ack has any commands in it waiting.
						String s = input.poll();
						out.writeUTF(s); // TODO: 05/03/2017 Needs to be replaced for android input.
					}
				}
			} finally {
				p("Abandon Transmitter thread, It's going down!");
			}

		} catch (IOException e){
			e.printStackTrace();
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
		}
	}

	@Override
	public void run() {

		try {
			socket = ssocket.accept();
			DataInputStream in = new DataInputStream(socket.getInputStream());
			p(this.getClass().toString() + " online.");

			while (true){
				p(in.readUTF());
			}

		}catch(IOException e) {
			e.printStackTrace();
		}

		p(this.getClass().toString() + " exiting.");
	}

	void start () {
		super.start();
	}
}
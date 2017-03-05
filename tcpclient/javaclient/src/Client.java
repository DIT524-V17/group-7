// File Name GreetingClient.java
import java.net.*;
import java.io.*;

import java.util.Scanner;
import java.util.Stack;

public class Client {

	static int port = 9005;
	static int port2 = 9000;
	static String host = "";

	public static void main(String [] args) {

		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e){
			System.out.print("Unkown host");
		}

		Transmitter R1 = initTransmitter(host, port);
		Receiver R2 = initReceiver(host, port2);

		while (true); // TODO: 05/03/2017 currently always keeps the client alive. Replace with automatic reconnection.
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
	Socket socket;
	Stack<String> input = new Stack<>();

	public void add(String s){
		input.add(s);
	}

	// Macro for add.
	public void write(String s){
		input.add(s);
	}

	BaseSocket(String host, int port) {
		this.host = host;
		this.port = port;
		try {
			socket = new Socket(host, port);
		} catch (IOException e){
			e.printStackTrace();
		}
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
	private Stack<String> input = new Stack<>();


	Transmitter(String host, int port) {
		super(host,port);
	}

	@Override
	public void run() {

		input.add(0, "CC"); // Don't touch this. It opens the receiving reading socket on the python server.

		// Test inputs, remove before implementation.
		input.add(0, "1");
		input.add(0, "2");
		input.add(0, "3");
		input.add(0, "4");
		input.add(0, "5");

		try {
			DataOutputStream out = new DataOutputStream(super.socket.getOutputStream());

			try {
				while (!super.socket.isInputShutdown()) // Checks if the socket is able to receive data.
					if (!input.isEmpty()) // Checks if the stack has any commands in it waiting.
						out.writeUTF(input.pop()); // TODO: 05/03/2017 Needs to be replaced for android input.
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
	private Stack<String> output = new Stack<>();


	Receiver(String host, int port) {
		super(host,port);
	}

	@Override
	public void run() {

		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());

			while (true)
				p(in.readUTF());

		}catch(IOException e) {
			e.printStackTrace();
		}

		p(this.getClass().toString() + " exiting.");
	}

	void start () {
		super.start();
	}
}
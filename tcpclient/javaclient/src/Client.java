import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.*;
import java.io.*;

import java.util.*;

public class Client {

	static Transmitter r1;

	static int port = 9005;
	static int port2 = 9000;
	static String host = "192.168.0.120";

	public static void main(String [] args) {

		/*
		try {
			host = InetAddress.getLocalHost().getHostName(); // TODO: 07/03/2017 replace with raspberry pi ip when implementing.
		} catch (UnknownHostException e){
			System.out.print("Unkown host");
		}
		*/

		r1 = initTransmitter(host, port);
		r1.run();
		// Receiver r2 = initReceiver(host, port2);

		// TODO: 05/03/2017 currently always keeps the client alive. Replace with automatic reconnection.

		Scanner s1 = new Scanner(System.in);
		while (true){
			r1.write(s1.next(), r1);
			r1.sendData();
		}

	}

	public static Transmitter initTransmitter(String host, int port){
		Transmitter R1 = new Transmitter(host, port);
		R1.run();
		return R1;
	}

	public static Receiver initReceiver(String host, int port){
		Receiver R1 = new Receiver(host, port);
		R1.run();
		return R1;
	}
}


class BaseSocket {
	private String host;
	private int port;
	Queue<String> input = new PriorityQueue<>();
	Socket socket;

	public void write(String s, Transmitter t){
		// TODO: 06/03/2017 Add command vertification here. Preferably O(1).
		try {
			t.out = new DataOutputStream(socket.getOutputStream());
			t.out.writeUTF(s); // TODO: 05/03/2017 Needs to be replaced for android input.
		} catch (Exception ex){

		}
	}

	BaseSocket(String host, int port) {
		this.host = host;
		this.port = port;
	}

	static void p(String s){
		System.out.println(s);
	}
}

class Transmitter extends BaseSocket {
	DataOutputStream out;


	Transmitter(String host, int port) {
		super(host,port);
		try {
			socket = new Socket(host, port);
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public void sendData() {
		try {
			while (!input.isEmpty()) // Checks if the st4ack has any commands in it waiting.
				out.writeUTF(input.poll()); // TODO: 05/03/2017 Needs to be replaced for android input.
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public void run() {
		input.add("cc"); // Enables the receiving socket. Do not remove.

		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			p(this.getClass().toString() + " online.");

			try {
				while (true) { // Checks if the socket is able to receive data.
					if (!input.isEmpty()){ // Checks if the st4ack has any commands in it waiting.
						out.writeUTF(input.poll()); // TODO: 05/03/2017 Needs to be replaced for android input.
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

}

class Receiver extends BaseSocket {
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
}

package t.s.o.r.f.frost;

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

	static int port = 9005;
	static String host;
	static Boolean c = false;

	public static Transmitter init(String host, int port){
		return new Transmitter(host, port);
	}
}

class BaseSocket implements Runnable {
	DataOutputStream out;
	BufferedReader in;
	Object host;
	int port;

	Socket socket;

	void write(String s){
		// TODO: 06/03/2017 Add command vertification here. Preferably O(1).

		try{
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

		String s;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // I hate Java.
			s = in.readLine();
			return s;
		} catch (IOException e){
			e.printStackTrace();
		}
		return ""; // Only occurs if an exception is thrown.
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
			try {
				//socket = new Socket("192.168.10.231", port);
				//socket = new Socket("DESKTOP-KMQH395", port);
				//socket = new Socket(InetAddress.getLocalHost(), port);
				// socket = new Socket("localhost", port);
				socket = new Socket("10.0.2.2", port);
			} catch (UnknownHostException e){
				e.printStackTrace();
			}
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
import java.net.*;
import java.io.*;
/**
 * @author Pontus Laestadius
 * Date format: DD-MM-YYYY
 * @since 20-03-2017
 * Maintained since: 10-05-2017
 * Version 2.2
 */

public class Client {

	// Don't change the port. As it is the same as on the raspberry pi.
	static int port = 9005;

	// Ip address of the Raspberry pi running on the ARC network.
	// Change this IP if you change network, until the hotspot has been fixed.
	static String host = "172.24.1.1";

	static Boolean c = false;
}

/**
* Class that holds all information regarding communication.
*/
class TCP {
	DataOutputStream out;
	BufferedReader in;

	Socket socket;

	/**
	 * Writes the given input to the socket.
	 * @param s the string to be written on to the socket.
	 */
	public void write(String s){

		try{
			char ch = s.charAt(0);

			// Eats the input from drive and turning commands to avoid over crowding.
			// Will only send a command if it is send in increments of 20 miliseconds.
			if ((ch == 'd' || ch == 'a') && (System.currentTimeMillis() %20 != 0))
				return;

			out.writeUTF(s + "\n");
			out.flush();

		} catch (Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * Reads a single line from the socket.
	 * @return the first command in the buffered input stream.
	 */
	public String read(){

		try {
			// Java's way of declaring input stream is odd. Please ignore this line.
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			return in.readLine();

		} catch (IOException e){
			e.printStackTrace();
		}

		// Only occurs if an exception is thrown.
		return "";
	}

	/**
	 *
	 * @param host where is the socket located, ip address.
	 * @param port on what port is it located, number over 9000.
	 */
	TCP(String host, int port) {
		try {
			socket = new Socket(host, port);
		} catch (IOException e){
			e.printStackTrace();
		}
		run();
	}

	private void run() {
		try { // Catches IO exceptions

			// Output stream.
			out = new DataOutputStream(socket.getOutputStream());

		} catch (IOException e){
			e.printStackTrace();
			Client.c = false;
		}
	}
}

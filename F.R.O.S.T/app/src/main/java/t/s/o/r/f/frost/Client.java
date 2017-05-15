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
	static String host = "172.24.1.1";
	static Boolean c = false;

	public static Transmitter init(String host, int port){
		return new Transmitter(host, port);
	}
}

class BaseSocket implements Runnable {
	private String host;
	private int port;
	DataOutputStream out;
	InputStream in;

	// A queue is used to handle all input commands so they go in the proper order and are not lost.
	Queue<String> input = new PriorityQueue<>();
	Queue<String> output = new PriorityQueue<>(); // TODO: 06/04/2017 make a read function for this.
	Socket socket;

	// Macro for add.
	public void write(String s){
		// TODO: 06/03/2017 Add command vertification here. Preferably O(1).

		try{
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
	public Strong read(){

		String s;
		try {
			in = socket.getInputStream(); // I hate Java.
			int index = 0;

			byte[] data = new byte[20000];
			int count = in.read(data);
			index += count;
			System.out.println("DATA " + Integer.valueOf(String.valueOf(0), 16) + " | " + Integer.valueOf(String.valueOf(data[0]), 16));
			if (Integer.valueOf(String.valueOf(data[0]), 16).equals(Integer.valueOf(String.valueOf(0), 16))){
				System.out.println("STARTED GETTING IMAGE");
				System.out.println("STARTED: " + Integer.valueOf(String.valueOf(data[index-1]), 16));

				while(Integer.valueOf(String.valueOf(data[index]), 16) != 0x02){
					byte[] data2 = new byte[20000];
					count = in.read(data2);
					int oldindex = index;
					index += count;
					for (int i = 1; oldindex +i < index; i++){
						data[oldindex+i] = data2[i];
					}
				}
				System.out.println("FINISHED GETTING IMAGE");
			}


			System.out.println("COUNT " + count);
			if (count < 1000){
				char[] newThings = new char[count];
				for(int i = 0; i < count; i++) {
					newThings[i] = (char)data[i];
				}
				// This should be obvious
				Strong string = new Strong(new String(newThings));
				return string;
			}
			//s = in.readLine();
			return new Strong(data);
		} catch (IOException e){
			e.printStackTrace();
		}
		return new Strong(""); // Only occurs if an exception is thrown.
	}

	/**
	 *
	 * @return all the queued up output as an array of strings.
	 */

    /*
    public String[] readAll(){
        int i = 0;
        String[] res = new String[output.size()];
        while (!output.isEmpty())
            res[i++] = read();
        return res;
    }
    */

	/**
	 *
	 * @return a formated version of all queued up output received.
	 */
    /*
    public String readAllFormated(){
        String[] format = readAll();
        String formatted = "";
        for (String f: format)
            formatted += f + ", ";
        return formatted.substring(0, formatted.length()-3);
    }
*/
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
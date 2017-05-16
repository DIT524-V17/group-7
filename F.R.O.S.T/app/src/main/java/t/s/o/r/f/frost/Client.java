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

			byte[] data = new byte[200000];
			int count = in.read(data);
			if (count == -1) return new Strong("");
			index += count;
			if ((char)data[0]== 'b'){
				System.out.println("STARTED GETTING IMAGE");
				int leng = -1;
				String str = "";
				for (int i = 1; i < 20; i++){
					if ((char) data[i] == 'b'){
						break;
					}
					str += (char) data[i];
				}
				leng = Integer.parseInt(str);
				if (leng < 50){
					return new Strong("");
				}
				System.out.println("LENGTH:" + leng);
				data = new byte[leng];
				while(index < leng) {
					byte[] data2 = new byte[40000];
					count = in.read(data2);
					if (count == -1) break;
					int oldindex = index;
					index += count;
					try {
						for (int i = 1; oldindex + i < index; i++) {
							if (oldindex+i > leng){
								break;
							}
							data[oldindex + i] = data2[i];
						}
					}catch(IndexOutOfBoundsException e){
						System.out.println("Ignore this");
					}
				}
				System.out.println("FINISHED GETTING IMAGE ");

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
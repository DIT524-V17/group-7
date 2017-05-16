package t.s.o.r.f.frost;

import android.os.AsyncTask;
import android.widget.ImageSwitcher;
import android.widget.TextView;
import android.view.View;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import static t.s.o.r.f.frost.Client.port;

/**
 * @author Pontus Laestadius
 * @since 05-05-2017
 */
public class Magic extends AsyncTask<String, Void, Void> {

    DataOutputStream out;
    InputStream in;
    Socket socket;
    boolean stupid = false;
    String last = "";
    long looptime = 0;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    public Void doInBackground(String... params) {

        long this_loop = looptime - System.currentTimeMillis();
        looptime = System.currentTimeMillis();

        System.out.println("Loop time: " + this_loop);


        if (!stupid) {
            try {
                socket = new Socket("10.0.2.2", port);
            } catch (Exception e) {
                e.printStackTrace();
            }
            stupid = true;
        }

        boolean bullshit = true;

        while (bullshit) {

            try { // Catches IO exceptions

                // Out and input streams.
                out = new DataOutputStream(socket.getOutputStream());

            } catch (IOException e) {
                e.printStackTrace();
            }
            // Write
            String command = MainActivity.sendMe;
            if (!command.equals("") && !command.equals(last)) {
                last = command;
                try {
                    System.out.println("Sending:" + command + " L: " + last);
                    out.writeUTF(command + "\n");
                    out.flush();
                    MainActivity.sendMe = "";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


        // Read

    public Strong read() {
        try {
            String s;

            in = socket.getInputStream(); // I hate Java.
            int index = 0;

            byte[] data = new byte[200000];
            int count = in.read(data);
            if (count == -1) return new Strong("");
            index += count;
            if ((char) data[0] == 'b') {
                System.out.println("STARTED GETTING IMAGE");
                int leng = -1;
                String str = "";
                for (int i = 1; i < 20; i++) {
                    if ((char) data[i] == 'b') {
                        break;
                    }
                    str += (char) data[i];
                }
                leng = Integer.parseInt(str);
                if (leng < 50) {
                    return new Strong("");
                }
                System.out.println("LENGTH:" + leng);
                data = new byte[leng];
                while (index < leng) {
                    byte[] data2 = new byte[40000];
                    count = in.read(data2);
                    if (count == -1) break;
                    int oldindex = index;
                    index += count;
                    try {
                        for (int i = 1; oldindex + i < index; i++) {
                            if (oldindex + i > leng) {
                                break;
                            }
                            data[oldindex + i] = data2[i];
                        }


                        System.out.println("FINISHED GETTING IMAGE ");

                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }


            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        return new Strong("");
    }

}

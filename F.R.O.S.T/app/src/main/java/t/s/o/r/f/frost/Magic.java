package t.s.o.r.f.frost;

import android.os.AsyncTask;
import android.widget.ImageSwitcher;
import android.widget.TextView;
import android.view.View;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static t.s.o.r.f.frost.Client.port;

/**
 * @author Pontus Laestadius
 * @since 05-05-2017
 */
public class Magic extends AsyncTask<String, Void, Void> {

    DataOutputStream out;
    BufferedReader in;
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


        if (!stupid){
            try {
                socket = new Socket("10.0.2.2", port);
            } catch (Exception e){
                e.printStackTrace();
            }
            stupid = true;
        }

        boolean bullshit = true;

        while (bullshit){

            try { // Catches IO exceptions

                // Out and input streams.
                out = new DataOutputStream(socket.getOutputStream());

            } catch (IOException e){
                e.printStackTrace();
            }
            // Write
            String command = MainActivity.sendMe;
            if (!command.equals("") && !command.equals(last)){
                last = command;
                try{
                    System.out.println("Sending:" + command + " L: " + last);
                    out.writeUTF(command + "\n");
                    out.flush();
                    MainActivity.sendMe = "";
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            // Read
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String s = in.readLine();

                if (s != null){
                    MainActivity.stupidVariable = s;
                }


            } catch (IOException e){
                e.printStackTrace();
            }

        }


        return null;
    }

}
package t.s.o.r.f.frost;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static t.s.o.r.f.frost.Client.port;

/**
 * Created by: Sebastian Fransson
 * AsyncTask used to allow sub-threading in the main application.
 * Initiates the Transmitter with the 'host' id and correct port.
 * TODO: Rewrite this to actually make sense. Override the two other methods if they are needed later.
 */
public class Magic extends AsyncTask<String, Void, Void> {
    /**
     * @author Pontus Laestadius
     * Date format: DD-MM-YYYY
     * @since 20-03-2017
     * Maintained since: 07-04-2017
     */
    DataOutputStream out;
    BufferedReader in;
    Socket socket;
    boolean stupid = false;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    public Void doInBackground(String... params) {
        if (!stupid){
            try {
                System.out.println("SOCKET");
                socket = new Socket("10.0.2.2", port);
            } catch (Exception e){
                e.printStackTrace();
            }
            stupid = true;
        }



        boolean bullshit = true;

        while (bullshit){
            System.out.println("BACKGROUND");

            try { // Catches IO exceptions

                // Out and input streams.
                out = new DataOutputStream(socket.getOutputStream());

            } catch (IOException e){
                e.printStackTrace();
            }

            // Write
            if (!MainActivity.sendMe.equals("")){
                try{
                    out.writeUTF(MainActivity.sendMe + "\n");
                    out.flush();
                    MainActivity.sendMe = "";
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            /*

            // Read
            String s = "";
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                s = in.readLine();
                System.out.println("read: " + s);
                if (!s.equals("") && s.length() < 2);
                MainActivity.stupidVariable = s;
            } catch (IOException e){
                e.printStackTrace();
            }
            */
        }


        return null;
    }

}
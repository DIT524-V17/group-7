package t.s.o.r.f.frost;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import static t.s.o.r.f.frost.Client.port;
import static t.s.o.r.f.frost.MainActivity.ImageSequence;
import static t.s.o.r.f.frost.MainActivity.ccValue;
import static t.s.o.r.f.frost.MainActivity.displayTemp;
import static t.s.o.r.f.frost.MainActivity.fireImage;
import static t.s.o.r.f.frost.MainActivity.updateCollisionIndicator;

/**
 * @author Pontus Laestadius
 * @since 05-05-2017
 */
public class Magic extends AsyncTask<String, Void, Bitmap> {

    DataOutputStream out;
    InputStream in;
    Socket socket;
    boolean stupid = false;
    String last = "";
    long looptime = 0;
    MainActivity tt;

    public void setMain(MainActivity ts){
        tt = ts;
    }

    // you may separate this or combined to caller class.
    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;

    public Magic(AsyncResponse delegate){
        this.delegate = delegate;
    }

    /*
    @Override //// TODO: 16/05/2017 ???????????????????????????????? 
    protected void onPostExecute(Bitmap v) {
        if (s.isStrong || s.isStrung){
            // Stuff??
        }
    }
    */

    @Override
    protected Bitmap doInBackground(String... params) {

        long this_loop = looptime - System.currentTimeMillis();
        looptime = System.currentTimeMillis();

        System.out.println("Loop time: " + this_loop);


        if (!stupid) {
            try {
                socket = new Socket("172.24.1.1", port);
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

            wizardofdos(read());
        }
        Bitmap n = null;
        return n;
    }

    void wizardofdos(final Strong s){
        if (s.isStrong || s.isStrung){

            tt.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int value;

                    try {

                        if (s.isStrong){
                            if (s.strong.length() < 2 || s.strong.length() > 7) return;
                            System.out.println("ISSTRONG " + s.strong);
                            // s.strong = s.strong.replaceAll(" ", "");
                            // s.strong = s.strong.replace("\n", "");
                        }

                        if (s.isStrung){
                            System.out.print("ISSTRUNG");

                            Bitmap bm = BitmapFactory.decodeByteArray(s.strung, 0 , s.strung.length);
                            ImageSequence.setImageBitmap(bm);

                            return;
                        }
                        System.out.println("SWITCH " + s.strong.charAt(0));
                        switch (s.strong.charAt(0)) {
                            case 'c': //Collision sensor input.
                                value = Integer.parseInt(s.strong.substring(1)); //Ignores the first character of the input.

                                updateCollisionIndicator(ccValue, value);
                                break;
                            case 't': //Temperature sensor input.
                                value = Integer.parseInt(s.strong.substring(1)); //Ignores the first character of the input.

                                displayTemp(value);
                                break;
                            case 'f': //Flame sensor input
                                value = Integer.parseInt(s.strong.substring(1)); //Ignores the first character of the input.

                                if(s.strong.charAt(3) == '1'){
                                    fireImage.setVisibility(View.VISIBLE);
                                } else {
                                    fireImage.setVisibility(View.INVISIBLE);
                                }
                                break;

                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    // Read
    public Strong read() {
        try {

            in = socket.getInputStream();
            int index = 0;

            byte[] data = new byte[5];
            int count = in.read(data);
            if (count == -1) return new Strong("");

            for (byte ins: data){
                if (ins > (int) 'a' && ins < (int) 'Z'){

                    if ((char) data[0] == 'b') {
                        processBytes(data, index, count);
                        break;
                    } else {
                        String str = "";
                        for (int i = 0; i < data.length; i++){
                            char d = (char) data[i];
                            if (d != '\n')
                                str += (char) data[i];
                            else {
                                break;
                            }
                        }
                        Strong s = new Strong(str);
                        s.isStrong = true;
                        return s;
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }


        return new Strong("");
    }

    Strong processBytes(byte[] data, int index, int count){

        index += count;
        System.out.println("STARTED GETTING IMAGE");
        int leng = -1;
        String str = "";


        boolean foundEndB = false;
        int i = 1;
        int tries = 0;
        while (!foundEndB){
            while (i++ < data.length) {
                if ((char) data[i] == 'b') {
                    foundEndB = true;
                    break;
                }
                str += (char) data[i];
            }

            // Nothing to be found here.
            if (tries++ > 2){
                return new Strong("");
            }

            // Reset and add 5 more bytes.
            data = new byte[5];
            i = 0;


            try{
                count = in.read(data);
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }

        // Saves the remaining bytes.
        byte[] rem = new byte[i - data.length];
        index = rem.length;
        for (int j = 0; j < rem.length; j++){
            rem[j] = data[i+j];
        }


        // Gets the amount of bytes we should read.
        try {
            leng = Integer.parseInt(str);
        } catch (NumberFormatException e){
            e.printStackTrace();
            return new Strong("");
        }

        // To small to be a picture.
        if (leng < 50) {
            return new Strong("");
        }


        // Here starts the image byte processing.
        System.out.println("LENGTH:" + leng);
        data = new byte[leng];

        // Copy over remaining bytes.
        for (int z = 0; z < rem.length; z++)
            data[z] = rem[z];

        while (index < leng) {


            // Make a new array with the number of bytes we need to read.
            byte[] data2 = new byte[leng - index];
            try{
                count = in.read(data2);
            } catch (IOException ex){
                ex.printStackTrace();
            }

            // If we reach end of stream. Something is fishy.
            if (count == -1) break;



            int oldindex = index;
            index += count;
            try {
                for (int k = 1; oldindex + k < index; k++) {
                    if (oldindex + k >= leng) {
                        break;
                    }
                    data[oldindex + k] = data2[k];
                }

            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        System.out.println("FINISHED GETTING IMAGE ");
        Strong picture = new Strong(data);
        picture.isStrung = true;
        return picture;
    }



}

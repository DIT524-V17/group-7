package t.s.o.r.f.frost;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

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

            long this_loop = looptime - System.currentTimeMillis();
            looptime = System.currentTimeMillis();

            if (this_loop > 100){
                System.out.println("Slow loop: 0." + this_loop + "s");
            }

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

            // wizardofdos(read());

            try{

                /*
                for (int i = 0; i < data.length ; i++) {// default len is a relative large number (8192 - readPosition)
                    int c = in.read();
                    if (c == -1) {
                        System.out.println("BREAK");
                        break;
                    }
                    data[index++] = (byte)c;
                }

                System.out.println("LENG: " + index);

                */

                in = socket.getInputStream();

                byte[] data = new byte[1024*180];
                int index = 0;
                int count;
                long mili1 = System.currentTimeMillis();
                final int MIN_BUFFER = 1024;

                if (in.available() > 0){

                    while ((count = in.read(data, index, MIN_BUFFER)) > 0){
                        System.out.print(" G: " + count);
                        index += count;
                        if (count < MIN_BUFFER) break;
                        while (mili1 > System.currentTimeMillis() + 4);
                        mili1 = System.currentTimeMillis();
                    }
                    if (index != 0){
                        System.out.println("INDEX: " + index);
                    }

                    wizardofdos(new Strong(Arrays.copyOfRange(data, 0, index)));

                }

            } catch (IOException ex){
                ex.printStackTrace();
            }

        }
        Bitmap n = null;
        return n;
    }

    void wizardofdos(final Strong s){
        if (s.isStrong || s.isStrung){

            tt.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (s.isStrung){
                        System.out.print("ISSTRUNG " + s.strung.length);

                        Bitmap bm = BitmapFactory.decodeByteArray(s.strung, 0 , s.strung.length);
                        ImageSequence.setImageBitmap(bm);

                        return;
                    }


                    int value;
                    if (s.strong.length() < 2 || s.strong.length() > 7) return;
                    System.out.println("ISSTRONG " + s.strong);
                    // s.strong = s.strong.replaceAll(" ", "");
                    // s.strong = s.strong.replace("\n", "");


                    try {


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

    /*
    // Read
    public Strong read() {
        try {

            in = socket.getInputStream();
            int index = 0;

            byte[] data = new byte[5];
            int count = in.read(data);
            if (count == -1) return new Strong("");

            int q = 0;
            for (byte ins: data){
                q++;
                System.out.print(" I:" + (int) ins);
                if (Character.isLowerCase((char) ins)){
                    //System.out.println("Char:" + (char) ins);


                    if ((char) ins == 'b') {
                        processBytes(data, index, count, q);
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
                            // Resets the loop while it has not found a new line.
                            if (i == data.length -1){
                                count = in.read(data);
                                if (count == -1) return new Strong("");
                                i = 0;
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

    Strong processBytes(byte[] data, int index, int count, int offset){ // TODO: 18/05/2017 b might not be first character.

        index += count;
        System.out.println("STARTED GETTING IMAGE");
        int leng = -1;
        String str = "";


        boolean foundEndB = false;
        int i = offset;
        int tries = 0;
        while (!foundEndB){
            while (i++ < data.length -1) {
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
        byte[] rem = new byte[data.length - i];
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

        // Fetch some more bytes
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

        System.out.println("FINISHED GETTING IMAGE " + data.length);
        Strong picture = new Strong(data);
        return picture;
    }
    */
}

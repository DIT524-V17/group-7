package t.s.o.r.f.frost;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
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
import java.util.concurrent.Executors;

import static t.s.o.r.f.frost.Client.port;/*

/**
 * @author Pontus Laestadius
 * @since 05-05-2017
 */
public class Magic extends AsyncTask<String, Void, Bitmap> {

    private DataOutputStream out;
    private DataInputStream in;
    private Socket socket;
    private boolean stupid = false;
    private String last = "";
    private long looptime = 0;
    private MainActivity tt;
    private AsyncResponse delegate = null;
    private boolean lastImgLeftOver = false;

    void setMain(MainActivity ts){
        tt = ts;
    }

    // you may separate this or combined to caller class.
    interface AsyncResponse {
        void processFinish(String output);
    }


    Magic(AsyncResponse delegate){
        this.delegate = delegate;
    }

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

            try{
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

                // start of image identifier identifiers
                byte ff = (byte) 0xFF; // FF byte identifier
                byte d8 = (byte) 0xD8;
                byte[] soi = {ff, d8};

                // end of image identifier
                byte d9 = (byte) 0xD9;
                byte[] eoi = {ff, d9};

                boolean parsingImage = false;

                byte[] data = new byte[1024*40];
                int index = 0;
                int count;
                final int MIN_BUFFER = 2; // Only reads in 2 byte increments. :( Bit sad.
                final int BIG_BUFFER = MIN_BUFFER*1024*2;
                int start = 0;

                long frameTime = System.currentTimeMillis();
                if (in.available() > 0){

                   try {
                       while ((count = in.read(data, index, MIN_BUFFER)) > 0){
                           index += count;

                           // Identifies start of image.
                           if (!parsingImage){
                               if ((data[index-2] == soi[0] && data[index-1] == soi[1]) || lastImgLeftOver){
                                   lastImgLeftOver = false;
                                   System.out.println("SOI: " + index);
                                   start = index-2;
                                   parsingImage = true;

                                   // Read a set number of kb without checking any bytes
                                   // for performance reasons.
                                   while ((count = in.read(data, index, BIG_BUFFER)) > 0){
                                       index += count;
                                       if (start+index >= 14000)
                                           break;
                                   }
                               } else {
                                   // If it's been trying to look for a starting byte for way to long.
                                   // Set it back to 0.
                                   if (index > 10000){
                                       index = 0;
                                   }
                               }
                           } else {

                               // Identifies end of image.
                               if (data[index-2] == eoi[0] || data[index-1] == eoi[0]){ // Looks for FF.
                                   if (data[index-1] == eoi[1]){ // Looks for D9
                                        System.out.println("EOI: index-1");
                                       // Reads to see if the start bytes exist afterwards for new img.

                                       if (in.available() > 0){
                                           index += in.read(data, index, MIN_BUFFER);
                                           if (data[index-2] == soi[0] && data[index-1] == soi[1]){
                                               lastImgLeftOver = true;
                                               break;
                                           }
                                       } else {
                                           break;
                                       }

                                   } else { // data[index-2] == eoi[0]
                                       // Reads the next byte to see if it 0xD9.
                                       index += in.read(data, index, 1);
                                       if (data[index-1] == eoi[1]){
                                           System.out.println("EOI: index");
                                           // Reads to see if the start bytes exist afterwards for new img.

                                           if (in.available() > 0){
                                               index += in.read(data, index, MIN_BUFFER);
                                               if (data[index-2] == soi[0] && data[index-1] == soi[1]){
                                                   lastImgLeftOver = true;
                                                   break;
                                               }
                                           } else {
                                               break;
                                           }

                                       }
                                   }
                               }
                           }
                       }
                   } catch (ArrayIndexOutOfBoundsException ex){
                       ex.printStackTrace();
                       index = data.length;
                   }
                    if (index != 0)
                        System.out.println("EOI: " + index + " IN " + (System.currentTimeMillis() - frameTime) + "ms");

                    final byte[] f_data = data;
                    final int f_start = start;
                    final int f_index = index;

                    // Copies the part of the array that was filled with data.
                    tt.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            wizardOfDos(new Strong(Arrays.copyOfRange(f_data, f_start, f_index)));
                        }
                    });

                }

            } catch (IOException ex){
                ex.printStackTrace();
            }

        }
        return null;
    }

    private void wizardOfDos(final Strong s){
        if (s.isStrong || s.isStrung){

            tt.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (s.isStrung){
                        System.out.println("ISSTRUNG " + s.strung.length);

                        Bitmap bm = BitmapFactory.decodeByteArray(s.strung, 0 , s.strung.length);
                        DisplayMetrics dm = new DisplayMetrics();

                        tt.getWindowManager().getDefaultDisplay().getMetrics(dm);
                        //tt.ImageSequence.setMinimumHeight(dm.heightPixels);
                        //tt.ImageSequence.setMinimumWidth(dm.widthPixels);
                        // tt.ImageSequence.setScaleType(ImageView.ScaleType.FIT_XY);
                        tt.ImageSequence.setImageBitmap(bm);

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

                                tt.updateCollisionIndicator(tt.ccValue, value);
                                break;
                            case 't': //Temperature sensor input.
                                value = Integer.parseInt(s.strong.substring(1)); //Ignores the first character of the input.

                                tt.displayTemp(value);
                                break;
                            case 'f': //Flame sensor input
                                value = Integer.parseInt(s.strong.substring(1)); //Ignores the first character of the input.

                                if(s.strong.charAt(3) == '1'){
                                    tt.fireImage.setVisibility(View.VISIBLE);
                                } else {
                                   tt.fireImage.setVisibility(View.INVISIBLE);
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
}

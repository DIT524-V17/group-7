package t.s.o.r.f.frost;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

/**
 * @author Pontus Laestadius
 * @since 05-05-2017
 */
public class Magic extends AsyncTask<String, Void, Bitmap> {

    private DataOutputStream out;
    private DataInputStream in;
    private Socket socket_camera;
    private Socket socket_commands;
    private boolean stupid = false;
    private String last = "";
    private long looptime = 0;
    private MainActivity tt;
    private boolean lastImgLeftOver = false;

    Magic(MainActivity ts){
        tt = ts;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        if (!stupid) {
            try {
                socket_camera = new Socket("172.24.1.1", 9005);
                socket_commands = new Socket("172.24.1.1", 9006);
            } catch (Exception e) {
                e.printStackTrace();
            }
            stupid = true;
        }

        boolean alwaysTrue = true;

        while (alwaysTrue) {

            long this_loop = looptime - System.currentTimeMillis();
            looptime = System.currentTimeMillis();

            if (this_loop > 100){
                System.out.println("Slow loop: 0." + this_loop + "s");
            }

            try { // Catches IO exceptions
                out = new DataOutputStream(socket_commands.getOutputStream());
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
                in = new DataInputStream(new BufferedInputStream(socket_camera.getInputStream()));

                // start of image identifier identifiers
                byte ff = (byte) 0xFF; // FF byte identifier
                byte d8 = (byte) 0xD8;
                byte[] soi = {ff, d8};

                // end of image identifier
                byte d9 = (byte) 0xD9;
                byte[] eoi = {ff, d9};

                boolean parsingImage = false;

                byte[] data = new byte[1024*64];
                int index = 0;
                int count;
                final int MIN_BUFFER = 2; // Only reads in 2 byte increments. :( Bit sad.
                final int BIG_BUFFER = MIN_BUFFER*1024;
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
                                       if (start+index >= 39000)
                                           break;
                                   }
                               } else {
                                   // If it's been trying to look for a starting byte for way to long.
                                   // Set it back to 0.
                                   if (index > 10000)
                                       index = 0;
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
                    if (index != 0){
                        System.out.println("EOI: " + index + " IN " + (System.currentTimeMillis() - frameTime) + "ms");
                        final byte[] f_data = data;
                        final int f_start = start;
                        final int f_index = index-(lastImgLeftOver?2:0);

                        // Copies the part of the array that was filled with data.
                        tt.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displayImage(Arrays.copyOfRange(f_data, f_start, f_index));
                            }
                        });
                    }
                }
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    private void displayImage(final byte[] arr){
        tt.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap bm = BitmapFactory.decodeByteArray(arr, 0 , arr.length);
                DisplayMetrics dm = new DisplayMetrics();
                tt.getWindowManager().getDefaultDisplay().getMetrics(dm);
                tt.ImageSequence.setImageBitmap(bm);
                return;
            }
        });
    }

    private void readCommand(final String s){
        tt.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                int value;
                if (s.length() < 2 || s.length() > 7) return;

                try {
                    switch (s.charAt(0)) {
                        case 'c': //Collision sensor input.
                            value = Integer.parseInt(s.substring(1)); //Ignores the first character of the input.

                            tt.updateCollisionIndicator(tt.ccValue, value);
                            break;
                        case 't': //Temperature sensor input.
                            value = Integer.parseInt(s.substring(1)); //Ignores the first character of the input.

                            tt.displayTemp(value);
                            break;
                        case 'f': //Flame sensor input
                            value = Integer.parseInt(s.substring(1)); //Ignores the first character of the input.

                            if(s.charAt(3) == '1'){
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

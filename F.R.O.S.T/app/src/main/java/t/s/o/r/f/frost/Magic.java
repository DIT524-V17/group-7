package t.s.o.r.f.frost;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * @author Pontus Laestadius
 * @since 05-05-2017
 * @version 3.2
 */
class Magic extends AsyncTask<String, Void, Bitmap> {

    private DataOutputStream out_commands;
    private InputStream in_commands;
    private DataInputStream in_camera;
    private Socket socket_camera;
    private Socket socket_commands;
    private boolean stupid = false;
    private String last = "";
    private MainActivity tt;
    private boolean lastImgLeftOver = false;
    private int BIG_READ = 30000;
    private int IMG_DIS = 20000;
    private int MIN_READ = 8000;
    private int REL_DEC = 1000;
    private int REL_INC = 500;
    // private byte[] leftOverRead = null;

    Magic(MainActivity ts){
        tt = ts;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        if (!stupid) {
            try {
                socket_camera = new Socket("172.24.1.1", 9005);
                System.out.println("Connected to 9005");
                socket_commands = new Socket("172.24.1.1", 9006);
                System.out.println("Connected to 9006");
            } catch (Exception e) {
                e.printStackTrace();
            }
            stupid = true;
        }

        boolean alwaysTrue = true;

        while (alwaysTrue) { // I wish the IDE wouldn't complain about always true always.

            try { // Catches IO exceptions
                out_commands = new DataOutputStream(socket_commands.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Write
            String command = MainActivity.sendMe;
            if (!command.equals("") && !command.equals(last)) {
                last = command;
                try {

                    System.out.println("Sending:" + command + " L: " + last);

                    if (command.equals("hai")){
                        out_commands.writeUTF("x000?");
                        out_commands.writeUTF("y000?");
                    } else {
                        out_commands.writeUTF(command);

                    }
                    out_commands.flush();
                    MainActivity.sendMe = "";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try{
                in_commands = socket_commands.getInputStream();
                if (in_commands.available() > 0){
                    byte[] incoming = new byte[4];
                    int count = in_commands.read(incoming);
                    String rec = "";
                    // boolean skip = false;

                    /*
                    This feature did not work so it is commented out.
                    It's intended purpose was to save left over parts of the commands
                    until next iteration when they get passed so we don't cut of
                    any commands.
                     */

                    /*
                    if (leftOverRead != null){
                        for (int i = 0; i < leftOverRead.length; i++){
                            if (leftOverRead[i] == '\n'){
                                rec += (char) incoming[i];
                                skip = true;
                                break;
                            }
                        }
                        if (!skip)
                            leftOverRead = null;
                    }
                    */

                    // if (!skip)
                    for (int i = 0; i < count; i++){
                        if (incoming[i] == '\n'){
                            //leftOverRead = Arrays.copyOfRange(incoming, i, count);
                            break;
                        }
                        rec += (char) incoming[i];
                    }
                    readCommand(rec);
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            /*
            Camera feed below
            * @author Pontus Laestadius
            * With help from Sebastian Fransson
            * @since 05-18-2017
            * @version 2.0
            *
            * Copyright 2017. Please don't steal this. It's under GPL 3.0
            *
            * There was no suitable solution. So I reinvented the wheel.
            * It decodes byte arrays received from the camera socket which
            * then interprets the starting and ending bytes using a self-made algorithm
            * Then processes a BitMap to display the picture.
            * If you want the details of how it works it's fully documented below as
            * this is just a brief overview.
             */

            try{
                in_camera = new DataInputStream(new BufferedInputStream(socket_camera.getInputStream()));

                // start of image identifier identifiers
                byte ff = (byte) 0xFF; // FF byte identifier
                byte d8 = (byte) 0xD8;
                byte[] soi = {ff, d8};

                // end of image identifier
                byte d9 = (byte) 0xD9;
                byte[] eoi = {ff, d9};

                boolean parsingImage = false;

                byte[] data = new byte[1024*(((BIG_READ/2000)+60)*2)];
                int index = 0;
                int count;
                final int MIN_BUFFER = 2; // Only reads in 2 byte increments. :( Bit sad.
                final int BIG_BUFFER = MIN_BUFFER*1024*2;
                int start = 0;

                long frameTime = System.currentTimeMillis();
                if (in_camera.available() > 0){

                    try {
                        while ((count = in_camera.read(data, index, MIN_BUFFER)) > 0){

                           /*
                           Each iteration increments the index with the number of bytes read.
                           That is then used to identify both the starting and ending bytes.
                            */
                            index += count;

                           /*
                           ParsesImage identifies if it is currently in the process of reading an image.
                           False by default until it manages to find the start of image (SOI) bytes.
                            */
                            if (!parsingImage){

                               /*
                               Looks for the starting bytes in the bytes just read from the stream.
                               Also passes if LastImgLeftOver is true due to the last image reading
                               the starting bytes of the upcoming frame.
                                */
                                if ((data[index-2] == soi[0] && data[index-1] == soi[1]) || lastImgLeftOver){
                                    // Either if it was true or false previously. Reset it.
                                    lastImgLeftOver = false;
                                   /*
                                   The start is always index-2 due to the the header bytes being
                                   two and due to the index+=count statement previously has to
                                   account for that change.
                                    */
                                    start = index-2;

                                    //
                                   /*
                                   Indicate that the image has started to process and the loop
                                   will be redirected to the top level else statement until the
                                   frame has been finished.
                                    */
                                    parsingImage = true;

                                    // Read a set number of kb without checking any bytes
                                    // for performance reasons.
                                    while ((count = in_camera.read(data, index, BIG_BUFFER)) > 0){
                                        index += count;
                                        // If it reads less than the intended amount.
                                        // Read less next time.

                                        if (start+index >= BIG_READ)
                                            break;
                                    }

                                   /*
                                   If we didn't find the starting bytes and we have attempted
                                   for more than 1000 bytes, we reset the index to avoid reaching
                                   an ArrayIndexOutOfBounds which slows the frame rate down.
                                   Due to the nature of catch().
                                    */
                                } else if (index > 1000) {
                                    index = 0;
                                }
                            } else {

                                // Identifies end of image.

                               /*
                               Matches to see if any of the bytes just read match the first
                               indication of the end of image (EOI).
                               0xFF or -1 in a signed byte like Java's implementation.
                                */
                                if (data[index-2] == eoi[0] || data[index-1] == eoi[0]){

                                   /*
                                   Here is a visual example of how this works:
                                   we read 2 bytes. X and Y.
                                   X or Y was just confirmed to be 0xFF.
                                   This next part identifies which one was 0xFF. Because they then
                                   Need to be handled differently. SO in the optimal testcase:
                                   If X was 0xFF then we look to see if Y is 0xD9.
                                   If it's not we know it's not the end of the frame.

                                   If Y was 0xFF then we need to read an additional byte. Because
                                   Y was the last byte read. And if that new byte Matches 0xD9
                                   Then we found the end of image.

                                   The followup comment describes the inner workings of applying
                                   the starting bytes for the following picture to make sure
                                   that the ending bytes aren't just a part of the actual picture.
                                    */

                                    if (data[index-1] == eoi[1]){ // Looks for D9
                                        // System.out.println("EOI: index-1");
                                        // Reads to see if the start bytes exist afterwards for new img.

                                       /* Check if there is any bytes to be read.
                                          This is because if there are no bytes to be read, that
                                          means it is the end of the image.
                                          But if there exists bytes to still be read.
                                          And if those new bytes are the starting bytes of the next
                                          frame then that also means it's the end of the image.
                                          The same logic is applied to the same code segment in the
                                          else statement.
                                        */
                                        if (in_camera.available() > 0){

                                            // Reads another set of bytes the size of MIN_BUFFER
                                            index += in_camera.read(data, index, MIN_BUFFER);

                                            // If those two bytes both match the starting bytes
                                            if (data[index-2] == soi[0] && data[index-1] == soi[1]){

                                                // Remeber for the next iteration that the starting
                                                // bytes have already been read from the stream.
                                                lastImgLeftOver = true;
                                                break;
                                            }
                                        } else {
                                            break;
                                        }

                                    } else {
                                        // Reads the next byte to see if it 0xD9.
                                        index += in_camera.read(data, index, 1);
                                        if (data[index-1] == eoi[1]){
                                            // System.out.println("EOI: index");
                                            // Reads to see if the start bytes exist afterwards for new img.

                                            if (in_camera.available() > 0){
                                                index += in_camera.read(data, index, MIN_BUFFER);
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

                        /*
                        Avoids ArrayIndexOutOfBounds Because it slows the program down
                        As it would cause a stacktrace and the system to slow down by about
                        100-300ms.
                         */

                        if (index+BIG_BUFFER >= data.length){
                            break;
                        }

                        /*
                        If AIOFBE occurs, I want to reduce the number of bytes should be pre-read
                        so it does not occur for the next image.
                         */
                    } catch (ArrayIndexOutOfBoundsException ex){
                        ex.printStackTrace();
                        BIG_READ-=REL_DEC;
                    }

                    /*
                    Dymanically changes the expected read amount to reduce latency between each image.
                    It will to to always be within the expected amount IMG_DIS. These values should
                    be optimized depending on what size the image you are attempting to send.
                    This is currently set to work for 30-50k with sudden drops of 4000 bytes.
                     */

                    if (BIG_READ < MIN_READ)
                        BIG_READ = MIN_READ;

                    if (index != 0){

                        if (index-IMG_DIS > BIG_READ){
                            BIG_READ += REL_INC;
                        } else if ((index-start) < BIG_READ+IMG_DIS){
                            BIG_READ -= REL_DEC;
                        }

                        final byte[] f_data = data;
                        final int f_start = start;
                        // If we used the lastImgLeftOver that means the
                        // Starting bytes have already been read and should be at position 2.
                        final int f_index = index-(lastImgLeftOver?2:0);

                        // Copies the part of the array that was filled with data.
                        // This part runs on a seperate thread to reduce usage on the background one.
                        // Copying the array takes some time and effort from the computer :(
                        tt.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displayImage(Arrays.copyOfRange(f_data, f_start, f_index));
                            }
                        });
                        // Prints the byte length of the piture and the duration it took to process.
                        System.out.println("BR:" + BIG_READ + " I: " +
                                (index-start) + " EOI: " + index + " IN " +
                                (System.currentTimeMillis() - frameTime) + "ms");
                    }
                }
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    /*
    Used for setting the BitMap to display the camera feed to the user.
    @Author Sebastian
     */
    private void displayImage(final byte[] arr){
        tt.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap bm = BitmapFactory.decodeByteArray(arr, 0 , arr.length);
                DisplayMetrics dm = new DisplayMetrics();
                tt.getWindowManager().getDefaultDisplay().getMetrics(dm);
                tt.ImageSequence.setImageBitmap(bm);
            }
        });
    }

    /*
    Pass any command that should interact with any UI element on the main thread.
    That is not the camera feed.
    Previously called:
    static void handleInput(String s)
     */
    private void readCommand(final String s){
        tt.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                int value;
                if (s.length() < 2 || s.length() > 7) return;

                System.out.println("FOUND: " + s);

                try {
                    switch (s.charAt(0)) {
                        case 'v':
                            value = Integer.parseInt(s.substring(1));
                            tt.battery.drawBattery(value / 1000.0);
                        case 'c': //Collision sensor input.
                            value = Integer.parseInt(s.substring(1)); //Ignores the first character of the input.

                            tt.updateCollisionIndicator(tt.ccValue, value);
                            break;
                        case 't': //Temperature sensor input.
                            value = Integer.parseInt(s.substring(1).replaceAll("\\D+","")); //Ignores the first character of the input.

                            tt.displayTemp(value);
                            break;
                        case 'f': //Flame sensor input
                            System.out.println("FOUND FLAME STUFF");

                            if(s.charAt(1) == '1') {
                                tt.toMuchLight.setVisibility(View.VISIBLE);
                                //Setting all indicators to invisible
                                for (ImageView tempName : tt.fireArray){
                                    tempName.setVisibility(View.INVISIBLE);
                                }
                            }else{
                                tt.toMuchLight.setVisibility(View.INVISIBLE);
                                tt.WhereFlameAt(Integer.parseInt(s.charAt(2) + ""), s.charAt(3));
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

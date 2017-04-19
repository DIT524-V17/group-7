package t.s.o.r.f.frost;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.view.View.OnTouchListener;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import java.io.IOException;
import java.util.Scanner;

import static t.s.o.r.f.frost.Client.*;

/**
 * Author: Sebastian Fransson
 * Last Updated: 19-04-2017
 */
public class MainActivity extends AppCompatActivity {

    //Views for collision animation.
    TextView tv;
    View v;
    static TextView ccValue;
    static TextView textElement;
    static ImageSwitcher SwitchImageTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new threads().execute(); //Executes the AsyncTask and establishes Client connection.
        tv = (TextView) findViewById(R.id.collision_text); //TextView for collision text.
        v = findViewById(R.id.view4);
        ccValue = (TextView) findViewById(R.id.ccValue); //TextView for collision distance value.
        animate();
       final Context context = this;

        Button forward = (Button) findViewById(R.id.button);
        //Sets the TouchListener to 'button' which in this case refers to the *FORWARD* button. (Check XML).
        forward.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        //Sends a "forward" commmand to the Raspberry pi (to be integrated)
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                threads.r1.write("d070?");
                            // System.out.println("Drive forward");
                        }
                        //Sends command to stop the current activity (to be integrated)
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            threads.r1.write("d090?");
                            //System.out.println("stop driving");
                        }
                    } catch (Exception e) {
                        System.out.println("MAH GOD WHY");
                        e.printStackTrace();
                    }
                    return false;
                }
            });

            //TouchListener for reverse button.
            Button reverse = (Button) findViewById(R.id.button2);
            //Sets the TouchListener to 'button2' which in this case refers to the *Reverse* button. (Check XML).
            reverse.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        //Sends a "reverse" commmand to the Raspberry pi (to be integrated)
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {

                            threads.r1.write("d110?");
                            // System.out.println("Reversing");
                        }
                        //Sends command to stop reversing (to be integrated)
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            threads.r1.write("d090?");
                            //  System.out.println("Stop reversing");
                        }
                    } catch (Exception e) {
                        System.out.println("MAH GOD WHY");
                        e.printStackTrace();
                    }
                    return false;
                }
            });

            //TouchListener for rightSteer button.
            Button rightSteer = (Button) findViewById(R.id.button4);
            //Sets the TouchListener to 'button4' which in this case refers to the *Right Steer* button. (Check XML).
            rightSteer.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        //Sends a "go right" commmand to the Raspberry pi (to be integrated)
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            threads.r1.write("a090?");
                            // System.out.println("Steer Right");
                        }
                        //Sends command to stop steering right (to be integrated)
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            threads.r1.write("a045?");
                            // System.out.println("Stop steering right");
                        }
                    } catch (Exception e) {
                        System.out.println("MAH GOD WHY");
                        e.printStackTrace();
                    }
                    return false;
                }
            });

            //TouchListener for leftSteer button.
            Button leftSteer = (Button) findViewById(R.id.button3);
            //Sets the TouchListener to 'button3' which in this case refers to the *Steer Left* button. (Check XML).
            leftSteer.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        //Sends a "go left" commmand to the Raspberry pi (to be integrated)
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            threads.r1.write("a000?");
                            //  System.out.println("Steering Left");
                        }
                        //Sends command to stop steering left (to be integrated)
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            threads.r1.write("a045?");
                            // System.out.println("Stop steering left");
                        }
                    } catch (Exception e) {
                        System.out.println("MAH GOD WHY");
                        e.printStackTrace();
                    }
                    return false;
                }
            });

        /**
         * Created by Anthony Path
         * Integration: Sebastian Fransson
         */
        // Experimental reconnection button. Done blindly, as I did not have car, needs testing
        Button recon = (Button) findViewById(R.id.button5);
        //on touch listener for reconnect button
        recon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    //attempts to reconnect app to server
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        //threads.closeConnection(); //can be commented out
                        System.out.println("Reconnection if is  entered.");
                        //Restarts the application to regain connection to the Client.
                        Intent restart = new Intent(context, MainActivity.class);
                        int pendingID= 123456;
                        PendingIntent p1 = PendingIntent.getActivity(context, pendingID, restart, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager m1 = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                        m1.set(AlarmManager.RTC, System.currentTimeMillis() + 10, p1);
                        System.exit(0);
                    }
                } catch (Exception e) {
                    System.out.println("Reconnect failed");
                    e.printStackTrace();
                }
                return false;
            }
        });
    }


    //Setting the temperature in the text element

    /**
     * Created by: Isabelle Törnqvist
     * Integrated by: Sebastian Fransson
     * @param degrees
     */
    static void displayTemp(int degrees){
        String text = degrees + "\u2103";
        textElement.setText(text) ;

        //Changing the temp image depending on temperature
        if(degrees > 50){
            SwitchImageTemp.setImageResource(R.drawable.temphot);
        }
        else if(degrees < 20){

            SwitchImageTemp.setImageResource(R.drawable.tempcold);
        }
        else{
            SwitchImageTemp.setImageResource(R.drawable.tempmedium);
        }
    }

    //Method for collision button animation.

    /**
     * Created by: Pontus Laestadius
     * Integrated by: Pontus Laestadius
     */
    void animate(){

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        // Animation rotationback = AnimationUtils.loadAnimation(this, R.anim.rotateback);

        AnimationSet s = new AnimationSet(true);//false means don't share interpolators
        s.addAnimation(rotation);
        // s.addAnimation(rotationback);
        rotation.setRepeatCount(Animation.INFINITE);
        v.startAnimation(s);
        //Instantiates the hidden collision button.
        Button button = (Button)findViewById(R.id.button7);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // click handling code
                if (tv.getText().equals("Collision"))
                    tv.setText("");
                else
                    tv.setText("Collision");
            }
        });
    }

    /**
     * Created by: Pontus Laestadius
     * Integrated by: Pontus Laestadius, Sebastian Fransson
     * Updated by: Sebastian Fransson
     */
    //Method for handling the received information from the Arduino sensors.
    static void handleInput(){
        try {
            String s = threads.r1.read();
            //System.out.print(threads.r1.read());
            if (s.length() < 2) return;
            System.out.println("HandleInput: " + s);
            int value = Integer.parseInt(s.substring(1)); //Ignores the first character of the input.
            switch (s.charAt(0)) {
                case 'c': //Collision sensor input.
                    updateCollisionIndicator(ccValue, value);
                    break;
                case 't': //Temperature sensor input.
                    displayTemp(value);
                    break;
                case 'f': //Flame sensor input
                    break;
            }
        }catch(Exception e){
            System.out.println("MAH GOD WHY");
            e.printStackTrace();
        }
    }

    //Updates the collision indicator text.
    static void updateCollisionIndicator(TextView view, int value){
        view.setText(value == 0 ? "+" : value + "");
    }


    /**
     * Created by: Sebastian Fransson
     * AsyncTask used to allow sub-threading in the main application.
     * Initiates the Transmitter with the 'host' id and correct port.
     * TODO: Rewrite this to actually make sense. Override the two other methods if they are needed later.
     */
   public static class threads extends AsyncTask<String, Void, Void> {
       static Transmitter r1;
       int yes = 1;

        @Override
        public Void doInBackground(String... params) {

            try {
                //Initializes the Transmitter 'r1' with the appropriate host and port.
                r1 = init(host, port);
                System.out.println("In AsyncTask");

                //Keeps infinitely looping to handle inputs from the sensors.
               while(yes == 1) {
                    handleInput();
                }

            } catch (Exception e) {
                System.out.println("Screw you");
                e.printStackTrace();
            }
            return null;
        }

        //closeConnection method for reconnecting without restarting the app. (Not working/used for now)
        //It should probably halt the current connection
        public static void closeConnection (){
            try {
                System.out.println("Entered closeConnection");
                r1 = null;
            }
            catch(Exception e){
                System.out.println("MAH GOD WHY");
                e.printStackTrace();
            }
        }
    }
}

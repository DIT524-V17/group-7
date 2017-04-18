package t.s.o.r.f.frost;

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

import java.io.IOException;
import java.util.Scanner;

import static t.s.o.r.f.frost.Client.*;

public class MainActivity extends AppCompatActivity {

    //Views for collision animation.
    TextView tv;
    View v;
    static TextView ccValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button forward = (Button) findViewById(R.id.button);
        new threads().execute();

        tv = (TextView) findViewById(R.id.collision_text);
        v = findViewById(R.id.view4);
        ccValue = (TextView) findViewById(R.id.ccValue);
        animate();
        //Transmitter r1 = threads.getR1();
        //final Transmitter r1 = initTransmitter(host, port);
        //final Transmitter r2 = initTransmitter(host, port2);


        //Sets the TouchListener to 'button' which in this case refers to the *FORWARD* button. (Check XML).
        forward.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        //Sends a "forward" commmand to the Raspberry pi (to be integrated)
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            System.out.println("Hello");
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


        // Experimental reconnection button. Done blindly, as I did not have car, needs testing
        Button recon = (Button) findViewById(R.id.button5);
        //on touch listener for reconnect button
        recon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    //attempts to reconnect app to server
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        threads.closeConnection(); //can be commented out
                        System.out.println("Reconnection if is  entered.");
                        new threads().execute();
                         /*new1.closeConnection()); //Another way to restart connection??
                          new1.execute();*/
                    }
                } catch (Exception e) {
                    System.out.println("Reconnect failed");
                    e.printStackTrace();
                }
                return false;
            }
        });




        /**
         * Temporary switch for setting a different speed.
         * TODO:Remove when the joystick is to be implemented.
         */
        /*
        Switch faster = (Switch) findViewById(R.id.switch_1);
        faster.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            String readRes = threads.r1.read();
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    workDammit = true;
                    System.out.println(threads.r1.read());
                }

                else{
                    workDammit = false;
                    System.out.println(threads.r1.read());
                }
            }
        });*/
    }


    void animate(){

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        // Animation rotationback = AnimationUtils.loadAnimation(this, R.anim.rotateback);

        AnimationSet s = new AnimationSet(true);//false means don't share interpolators
        s.addAnimation(rotation);
        // s.addAnimation(rotationback);
        rotation.setRepeatCount(Animation.INFINITE);
        v.startAnimation(s);

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

    void handleInput(String s){
        int value = Integer.parseInt(s.substring(1));
        switch (s.substring(0,1)){
            case "c":
                updateCollisionIndicator(value);
                break;

        }
    }

    void updateCollisionIndicator(int value){
        TextView ccValue = (TextView) findViewById(R.id.ccValue);
        ccValue.setText(value == 0 ? "+" : value + "");

    }


    /**
     * AsyncTask used to allow sub-threading in the main application.
     * Initiates the Transmitter with the 'host' id and correct port.
     * TODO: Rewrite this to actually make sense. Override the two other methods if they are needed later.
     */
   public static class threads extends AsyncTask<String, Void, Void> {
       static Transmitter r1;
        @Override
        public Void doInBackground(String... params) {

            try {
                //Initializes the Transmitter 'r1' with the appropriate host and port.
                r1 = init(host, port);
                

            } catch (Exception e) {
                System.out.println("Screw you");
                e.printStackTrace();
            }
            return null;
        }
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

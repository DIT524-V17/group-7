package t.s.o.r.f.frost;

import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import java.io.IOException;
import java.util.Scanner;

import static t.s.o.r.f.frost.Client.*;


public class MainActivity extends AppCompatActivity implements JoystickView.JoystickListener{


    TextView tv;
    View v;
    static TextView ccValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoystickView joystick = new JoystickView(this);
        setContentView(R.layout.activity_main);
        //new threads().execute();
        tv = (TextView) findViewById(R.id.collision_text);
        v = findViewById(R.id.view4);
        ccValue = (TextView) findViewById(R.id.ccValue);
        animate();

    }

    @Override
    public void onJoystickMoved(float speed, float angle, int id) {

        String speedCommand = "";
        String steerCommand = "";
        int speedPause = 0;
        int anglePause = 0;
        switch (id){
            case R.id.joystickCamera:

                speedCommand = "" + 0;
                steerCommand = "" + 0;

                // Sets the speed
                // threads.r1.write("d" + speedCommand + "?");


                // Sets the angle
               // threads.r1.write("a" + steerCommand + "?");
                // hreads.r1.write("a070?");

                //Log.d("Camera Joystick", "X percent: " + xPercent + " Y percent: " + yPercent);
                break;
            case R.id.joystickCar:


                speedPause++;
                anglePause++;
                // min 29
                // max 52
                // Translating input from joystick according to protocol
                speedCommand = "" + (int)-(speed/4 - 95);

                for(int i = 0; speedCommand.length() < 3; i++)
                    speedCommand = "0" + speedCommand;

                //::::for loop separator::::\\
                Log.e("ANGLEIFS", "" + angle);
                if(angle > 45 && angle <= 135 || angle > 225 && angle <= 315){
                   // threads.r1.write("a045?");
                }
                else if(angle > 135 && angle <= 225){
                   // threads.r1.write("a000?");
                }
                else {//if(angle > 180 && angle <=225 || angle > 315 && <= 45){
                  //  threads.r1.write("a090?");
                }
                //else i
                //steerCommand = angle >= 0 && angle <= 180? "" + (90 - (int) angle / 2) : "" + (int)(360-angle)/2;

                //for(int i = 0; steerCommand.length() < 3; i++)
                 //steerCommand = "0" + steerCommand;

                //::::for loop separator::::\\

                // Sets the speed
               // threads.r1.write("d" + speedCommand + "?");

               // threads.r1.write("d" + speedCommand + "?");
                Log.e("Speed", "d" + speedCommand + "?");

                //System.out.println("d" + speedCommand + "?");

                // Sets the angle

                //Sthreads.r1.write("a" + steerCommand + "?");
               // Log.e("Angle","a" + steerCommand + "?");
                break;
        }
    }

    //static String drive = "d070";
    /*@Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button forward = (Button) findViewById(R.id.button);
        new threads().execute();

       // final Transmitter r1 = initTransmitter(host, port);
        //final Transmitter r2 = initTransmitter(host, port2);
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
    }*/

    /**
     * AsyncTask used to allow sub-threading in the main application.
     * Initiates the Transmitter with the 'host' id and correct port.
     * TODO: Rewrite this to actually make sense. Override the two other methods if they are needed later.
     */
  /* public static class threads extends AsyncTask<String, Void, Void> {
       static Transmitter r1;
        @Override
        public Void doInBackground(String... params) {

            try {
                //Initializes the Transmitter 'r1' with ethe appropriate host and port.
                r1 = init(host, port);

                while (true){
                    handleInput();
                }


            } catch (Exception e) {
                System.out.println("Screw you");
                e.printStackTrace();
            }
            return null;
        }
    }*/


    /*public void forwardTest(View view){
        Button Forward = (Button) view;
        ((Button) view).setText("Does this happen?");
        System.out.println("OK");
    } */
   /* public void rightSteer(View view){
        Button rightSteer = (Button) view;
        ((Button) view).setText("Does this happen?");
        System.out.println("OK");
    } */

    /**
     * Author: Pontus Laestadius
     * Content: Collision control GUI and Input interface.
     */
    void animate(){

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        // Animation rotationback = AnimationUtils.loadAnimation(this, R.anim.rotateback);

        AnimationSet s = new AnimationSet(true);//false means don't share interpolators
        s.addAnimation(rotation);
        // s.addAnimation(rotationback);
        rotation.setRepeatCount(Animation.INFINITE);
        v.startAnimation(s);

        Button button = (Button)findViewById(R.id.button);
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

  /*  static void handleInput(){
        String s = threads.r1.read();
        if (s.length() < 2) return;
        System.out.println("HandleInput: " + s);
        int value = Integer.parseInt(s.substring(1));
        switch (s.charAt(0)){
            case 'c':
                updateCollisionIndicator(ccValue,value);
                break;
        }
    }*/

    static void updateCollisionIndicator(TextView view, int value){
        view.setText(value == 0 ? "+" : value + "");

    }
}

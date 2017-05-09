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


public class MainActivity extends AppCompatActivity implements JoystickView.JoystickListener {

    static boolean DEBUG = true;
    TextView tv;
    View v;
    static TextView ccValue;
    private static int speedPause;
    private static double oldCarSpeed;
    private static double oldCarAngle;
    private static double oldCameraAngle;

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
    public void onJoystickMoved(double speed, double angle, int id) {

        switch (id) {
            case R.id.joystickCamera:
                sendCameraCommand(angle);
                break;
            case R.id.joystickCar:
                sendCarCommandv1(angle, speed);
                sendCarCommandv2(angle, speed);
        break;
        }

    }

    private void sendCameraCommand(double angle) {

        String command = "";
        if(angle <= 45 || angle > 315 && !(oldCameraAngle <= 45 || oldCameraAngle > 315)){
            command = "x002?"; // threads.r1.write("x001?");
        }
        else if(angle <= 135 && angle > 45 && !(oldCameraAngle <= 135 && oldCameraAngle > 45)){
            command = "y001?"; // threads.r1.write("y002?");
        }
        else if(angle <= 225 && angle > 135 && !(oldCameraAngle <= 225 && oldCameraAngle > 135)){
            command = "x001?"; // threads.r1.write("x002?");
        }
        else if(angle <= 315 && angle > 225 && !(oldCameraAngle <= 315 && oldCameraAngle > 225)){
            command = "y002?"; // threads.r1.write("y001?");
        }
        oldCameraAngle = angle;
        Log.e("Camera command", command);

    }

    private void sendCarCommandv1(double angle, double speed){
        // The angle
        if(Math.abs(angle-oldCarAngle) < 10){
            String command = angle <= 180? "" + (90 - (int) angle / 2) : "" + (int)(360-angle)/2;

            for(int i = 0; command.length() < 3; i++)
                command = "0" + command;
            // threads.r1.write("d" + command + "?");
            if(DEBUG) Log.e("V1 Car speed", "d" + command + "?");
        }
        // The speed
        if (speed != oldCarSpeed && (speedPause % 5 == 0 || speed == 0)) {
            String command = "";
            // min 29
            // max 52
            // Translating input from joystick according to protocol
            command = "" + (int) -(speed / 4 - 95);

            for (int i = 0; command.length() < 3; i++)
                command = "0" + command;
            // threads.r1.write("d" + command + "?");
            oldCarSpeed = speed;
            speedPause++;
            if(DEBUG)Log.e("Car speed", "d" + command + "?");
        }

    }

    private void sendCarCommandv2(double angle, double speed){
        // The angle
        String command = "";

        if (angle <= 30 || angle > 330 && !(oldCarAngle <= 30 || oldCarAngle > 330)) {
            command = "090";// threads.r1.write("a090?");
        } else if (angle <= 60 && angle > 30 && !(oldCarAngle <= 60 && oldCarAngle > 30)) {
            command = "068";// threads.r1.write("a068?");
        } else if (angle <= 120 && angle > 60 && !(oldCarAngle <= 120 && oldCarAngle > 60)) {
            command = "045";// threads.r1.write("a045?");
        } else if (angle <= 150 && angle > 120 && !(oldCarAngle <= 150 && oldCarAngle > 120)) {
            command = "023";// threads.r1.write("a023?");
        } else if (angle <= 210 && angle > 150 && !(oldCarAngle <= 210 && oldCarAngle > 150)) {
            command = "000";// threads.r1.write("a000?");
        } else if (angle <= 240 && angle > 210 && !(oldCarAngle <= 240 && oldCarAngle > 210)) {
            command = "068";// threads.r1.write("a068?");
        } else if (angle <= 300 && angle > 240 && !(oldCarAngle <= 300 && oldCarAngle > 240)) {
            command = "045";// threads.r1.write("a045?");
        } else if (angle <= 330 && angle > 300 && !(oldCarAngle <= 330 && oldCarAngle > 300)) {
            command = "023";// threads.r1.write("a023?");
        }

        oldCarAngle = angle;
        if(DEBUG)Log.e("V2 Car angle", "a" + command + "?");

        // The speed
        command = "";
        if (speed != oldCarSpeed && (speedPause % 5 == 0 || speed == 0)) {
            // min 29
            // max 52
            // Translating input from joystick according to protocol
            command = "" + (int) -(speed / 4 - 95);

            for (int i = 0; command.length() < 3; i++)
                command = "0" + command;
                // threads.r1.write("d" + command + "?");
            oldCarSpeed = speed;
            speedPause++;
        }
        if(DEBUG)Log.e("Car speed", "d" + command + "?");
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

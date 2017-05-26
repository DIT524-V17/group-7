package t.s.o.r.f.frost;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * Author: Sebastian Fransson
 * Last Updated: 19-04-2017
 */
public class MainActivity extends AppCompatActivity {

    //Views for collision animation.
    View v;
    TextView tv;
    View tvbg;

    ImageView ImageSequence;
    ImageView fireImage;
    TextView ccValue;
    TextView textElement;
    ImageSwitcher SwitchImageTemp;

  //ImageViews for flame detection
    private ImageView fireImageNorth;
    private ImageView fireImageNorthNorthWest;
    private ImageView fireImageNorthWest;
    private ImageView fireImageNorthNorthEast;
    private ImageView fireImageNorthEast;
    private TextView toMuchLight;

    public Button button6;
    private Boolean item_flame_boolean = true;
    private Boolean item_temperature_boolean = true;
    private Boolean item_ultrasonic_boolean = true;
    private Boolean item_motor_boolean = true;
    private Boolean item_steering_boolean = true;
    private Boolean item_rip_boolean = true;
    private Boolean item_camera_horizontal_boolean = true;
    private Boolean item_camera_vertical_boolean = true;
    public static String sendMe = "";

    static ImageView[] fireArray = new ImageView[]{fireImageNorth, fireImageNorthNorthWest, fireImageNorthWest,
            fireImageNorthNorthEast, fireImageNorthEast};


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //new threads().execute(); //Executes the AsyncTask and establishes Client connection.
        Magic mg = new Magic(this); // TODO: 16/05/2017 understand this
        mg.execute();
        tv = (TextView) findViewById(R.id.collision_text); //TextView for collision text.
        v = findViewById(R.id.cc_view4);
        tvbg = findViewById(R.id.text_background);

        //added 2017-05-23 for flame detection using 5-channel
        fireImageNorth = (ImageView)findViewById(R.id.imageView1);
        fireImageNorth.setVisibility(View.INVISIBLE);

        fireImageNorthNorthWest = (ImageView)findViewById(R.id.imageView);
        fireImageNorthNorthWest.setVisibility(View.INVISIBLE);

        fireImageNorthWest = (ImageView)findViewById(R.id.imageView3);
        fireImageNorthWest.setVisibility(View.INVISIBLE);

        fireImageNorthNorthEast = (ImageView)findViewById(R.id.imageView2);
        fireImageNorthNorthEast.setVisibility(View.INVISIBLE);

        fireImageNorthEast = (ImageView)findViewById(R.id.imageView4);
        fireImageNorthEast.setVisibility(View.INVISIBLE);

        toMuchLight = (TextView)findViewById(R.id.textView4);
        toMuchLight.setVisibility(View.INVISIBLE);

        ccValue = (TextView) findViewById(R.id.ccValue); //TextView for collision distance value.
        animate();
        //Context used for the reconnect feature.
       final Context context = this;


        /*
         *  Menu for deactivating sensors either individually or in bulk.
         */
        button6 = (Button) findViewById(R.id.button6);
        //Sets the listener for the menu button
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creates the popup menu
                PopupMenu popup = new PopupMenu(MainActivity.this, button6);

                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu, popup.getMenu());

                //Creates the items of the popup menu
                final MenuItem item_flame = popup.getMenu().findItem(R.id.flame);
                final MenuItem item_temperature = popup.getMenu().findItem(R.id.temperature);
                final MenuItem item_ultrasonic = popup.getMenu().findItem(R.id.ultrasonic);
                final MenuItem item_motor = popup.getMenu().findItem(R.id.motor);
                final MenuItem item_steering = popup.getMenu().findItem(R.id.steering);
                final MenuItem item_rip = popup.getMenu().findItem(R.id.rip);
                final MenuItem item_camera_vertical = popup.getMenu().findItem(R.id.camera_vertical);
                final MenuItem item_camera_horizontal = popup.getMenu().findItem(R.id.camera_horizontal);

                //Adds listeners to all the buttons
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        try {
                            switch (item.getItemId()) {
                                //The case for when flame checkbox is pressed
                                case R.id.flame:
                                    item_flame_boolean = !item_flame_boolean;
                                    item.setChecked(item_flame_boolean);
                                    sendMe = ("F000?");
                                    System.out.println("Flame");
                                    break;
                                //The case for when the temperature checkbox is pressed
                                case R.id.temperature:
                                    item_temperature_boolean = !item_temperature_boolean;
                                    item.setChecked(item_temperature_boolean);
                                    sendMe = ("T000?");
                                    break;
                                //The case for when the ultrasonic checkbox is pressed
                                case R.id.ultrasonic:
                                    item_ultrasonic_boolean = !item_ultrasonic_boolean;
                                    item.setChecked(item_ultrasonic_boolean);
                                    sendMe = ("U000?");
                                    break;
                                //The case for when the motor checkbox is pressed
                                case R.id.motor:
                                    item_motor_boolean = !item_motor_boolean;
                                    item.setChecked(item_motor_boolean);
                                    sendMe = ("M000?");
                                    break;
                                //The case for when the steer checkbox is pressed
                                case R.id.steering:
                                    item_steering_boolean = !item_steering_boolean;
                                    item.setChecked(item_steering_boolean);
                                    sendMe = ("S000?");
                                    break;
                                //The case for when the "camera vertical" checkbox is pressed
                                case R.id.camera_vertical:
                                    item_camera_vertical_boolean = !item_camera_vertical_boolean;
                                    item.setChecked(item_camera_vertical_boolean);
                                    sendMe = ("Y000");
                                    break;
                                //The case for when the "camera horizontal" checkbox is pressed
                                case R.id.camera_horizontal:
                                    item_camera_horizontal_boolean = !item_camera_horizontal_boolean;
                                    item.setChecked(item_camera_horizontal_boolean);
                                    sendMe = ("X000");
                                    break;
                                //The case for when the RIP checkbox is pressed
                                case R.id.rip:
                                    item_rip_boolean = !item_rip_boolean;
                                    item_flame_boolean = item_rip_boolean;
                                    item_temperature_boolean = item_rip_boolean;
                                    item_ultrasonic_boolean = item_rip_boolean;
                                    item_motor_boolean = item_rip_boolean;
                                    item_steering_boolean = item_rip_boolean;
                                    item_camera_horizontal_boolean = item_rip_boolean;
                                    item_camera_vertical_boolean = item_rip_boolean;
                                    item_flame.setChecked(item_rip_boolean);
                                    item_temperature.setChecked(item_rip_boolean);
                                    item_ultrasonic.setChecked(item_rip_boolean);
                                    item_motor.setChecked(item_rip_boolean);
                                    item_steering.setChecked(item_rip_boolean);
                                    item.setChecked(item_rip_boolean);
                                    item_camera_horizontal.setChecked(item_rip_boolean);
                                    item_camera_vertical.setChecked(item_rip_boolean);
                                    sendMe = ("E000?");
                                    break;
                            }
                            /*Just a random command to make sure that the car doesnt get spammed with the
                            command for turning a sensor on/off*/
                            sendMe = ("0000?");

                        } catch (Exception e) {
                            System.out.println("Error in popup menu");
                            e.printStackTrace();
                        }
                        //Makes sure the menu stays open after you click in a checkbox
                        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                        item.setActionView(new View(getApplicationContext()));
                        return false;
                    }
                });

                //Sets the status of the checkboxes to either checked or not checked
                item_flame.setChecked(item_flame_boolean);
                item_temperature.setChecked(item_temperature_boolean);
                item_ultrasonic.setChecked(item_ultrasonic_boolean);
                item_motor.setChecked(item_motor_boolean);
                item_steering.setChecked(item_steering_boolean);
                item_rip.setChecked(item_rip_boolean);
                item_camera_horizontal.setChecked(item_camera_horizontal_boolean);
                item_camera_vertical.setChecked(item_camera_vertical_boolean);

                //Shows the popup menu
                popup.show();
            }
        });



        Button forward = (Button) findViewById(R.id.button);
        //Sets the TouchListener to 'button' which in this case refers to the *FORWARD* button. (Check XML).
        forward.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        //Sends a "forward" commmand to the Raspberry pi (to be integrated)
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                sendMe = ("d070?");
                            // System.out.println("Drive forward");
                        }
                        //Sends command to stop the current activity (to be integrated)
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            sendMe = ("d090?");
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

                            sendMe = ("d110?");
                            // System.out.println("Reversing");
                        }
                        //Sends command to stop reversing (to be integrated)
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            sendMe = ("d090?");
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
                            sendMe = ("a090?");
                            // System.out.println("Steer Right");
                        }
                        //Sends command to stop steering right (to be integrated)
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            sendMe = ("a045?");
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
                            sendMe = ("a000?");
                            //  System.out.println("Steering Left");
                        }
                        //Sends command to stop steering left (to be integrated)
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            sendMe = ("a045?");
                            // System.out.println("Stop steering left");
                        }
                    } catch (Exception e) {
                        System.out.println("MAH GOD WHY");
                        e.printStackTrace();
                    }
                    return false;
                }
            });

        /*
         * Created by Anthony Path
         * Integrated by: Sebastian Fransson
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
                    System.out.println("Reconnect failed :(((");
                    e.printStackTrace();
                }
                return false;
            }
        });

        //TouchListener for camera down button.
        Button camDown = (Button) findViewById(R.id.button10);
        //Sets the TouchListener to 'button10' which in this case refers to the *Reverse* button. (Check XML).
        camDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    //Sends a "reverse" commmand to the Raspberry pi (to be integrated)
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        sendMe = ("y001?");
                        // System.out.println("Reversing");
                    }
                    //Sends command to stop reversing (to be integrated)
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendMe = ("y000?");
                        //  System.out.println("Stop reversing");
                    }
                } catch (Exception e) {
                    System.out.println("MAH GOD WHY");
                    e.printStackTrace();
                }
                return false;
            }
        });

        //TouchListener for camera up button.
        Button camUp = (Button) findViewById(R.id.button9);
        //Sets the TouchListener to 'button9' which in this case refers to the *Reverse* button. (Check XML).
        camUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    //Sends a "reverse" commmand to the Raspberry pi (to be integrated)
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        sendMe = ("y002?");
                        // System.out.println("Reversing");
                    }
                    //Sends command to stop reversing (to be integrated)
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendMe = ("y000?");
                        //  System.out.println("Stop reversing");
                    }
                } catch (Exception e) {
                    System.out.println("MAH GOD WHY");
                    e.printStackTrace();
                }
                return false;
            }
        });

        //TouchListener for camera left button.
        Button cameraLeft = (Button) findViewById(R.id.button8);
        //Sets the TouchListener to 'button8' which in this case refers to the *Reverse* button. (Check XML).
        cameraLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    //Sends a "reverse" commmand to the Raspberry pi (to be integrated)
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        sendMe = ("x001?");
                        // System.out.println("Reversing");
                    }
                    //Sends command to stop reversing (to be integrated)
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendMe = ("x000?");
                        //  System.out.println("Stop reversing");
                    }
                } catch (Exception e) {
                    System.out.println("MAH GOD WHY");
                    e.printStackTrace();
                }
                return false;
            }
        });

        //TouchListener for camera right button.
        Button cameraRight = (Button) findViewById(R.id.button7);
        //Sets the TouchListener to 'button7' which in this case refers to the *Reverse* button. (Check XML).
        cameraRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    //Sends a "reverse" commmand to the Raspberry pi (to be integrated)
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        sendMe = ("x002?");
                        // System.out.println("Reversing");
                    }
                    //Sends command to stop reversing (to be integrated)
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        sendMe = ("x000?");
                        //  System.out.println("Stop reversing");
                    }
                } catch (Exception e) {
                    System.out.println("MAH GOD WHY");
                    e.printStackTrace();
                }
                return false;
            }
        });

        //Creating the text view where the temperature is show
        textElement = (TextView) findViewById(R.id.textView);
       // int theOutputFromTheSensor = 60; //Test value for method input.
        //Creating a ImageSwitcher, which allows for switching between different images
        SwitchImageTemp = (ImageSwitcher) findViewById(R.id.imgsw);
        SwitchImageTemp.setFactory(new ViewSwitcher.ViewFactory(){
            public View makeView(){
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                return imageView;
            }
        });
        //Calling the method "displayTemp" which is defined below
        //displayTemp(60); //Test method call.

    }


    //Setting the temperature in the text element

    /**
     * Created by: Isabelle Törnqvist
     * Integrated by: Sebastian Fransson
     * @param degrees
     */
     void displayTemp(int degrees){
         System.out.println("Setting Temp");
        String text = degrees + "\u2103";
        textElement.setText(text);

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
     * Author: Pontus Laestadius
     * Content: Collision control GUI and Input interface.
     */
    void animate(){

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        // Animation rotationback = AnimationUtils.loadAnimation(this, R.anim.rotateback);

        AnimationSet s = new AnimationSet(true);//false means don't share interpolators
        s.addAnimation(rotation);
        s.setDuration(3000);
        // s.addAnimation(rotationback);
        rotation.setRepeatCount(Animation.INFINITE);
        v.startAnimation(s);
        tvbg.setVisibility(View.INVISIBLE);

        Button button = (Button)findViewById(R.id.buttonCC);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
                animation1.setDuration(200);
                animation1.setStartOffset(20);
                animation1.setFillAfter(true);
                tv.startAnimation(animation1);

                // displayTemp((int)(Math.random()*70));

                // click handling code
                if (tv.getText().equals("Collision")){
                    tv.setText("");
                    tvbg.setVisibility(View.INVISIBLE);
                } else {
                    tvbg.setVisibility(View.VISIBLE);
                    tv.setText("Collision");
                }

            }
        });
    }

    /**
     * Author: Isabelle Törnqvist 2017-05-24
     * Content: Checks to see if a flame is detected and where, and shows the appropriate image
     * @param pos - position of the value imageView in the array of ImageViews
     * @param flameStatus
     */
    static void WhereFlameAt(int pos, char flameStatus){
        //ImageView I = fireArray[pos];

        if(flameStatus == '1'){
            fireArray[pos].setVisibility(View.VISIBLE);
        }else{
            fireArray[pos].setVisibility(View.INVISIBLE);
            //flameLocation.setImageAlpha(30);
        }
    }

    //Updates the collision indicator text.
     void updateCollisionIndicator(TextView view, int value){
        view.setText(value == 0 ? "+" : value + "");
    }
}

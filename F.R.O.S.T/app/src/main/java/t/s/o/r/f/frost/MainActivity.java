package t.s.o.r.f.frost;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.view.View.OnTouchListener;

import java.util.MissingResourceException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button forward = (Button)findViewById(R.id.button);
        //Sets the TouchListener to 'button' which in this case refers to the *FORWARD* button. (Check XML).
        forward.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                try {
                    //Sends a "forward" commmand to the Raspberry pi (to be integrated)
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        System.out.println("Drive forward");
                    }
                    //Sends command to stop the current activity (to be integrated)
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        System.out.println("stop driving");
                    }
                }catch(Exception e){
                    System.out.println("MAH GOD WHY");
                }
                return false;
            }
        });


        Button reverse= (Button)findViewById(R.id.button2);
        //Sets the TouchListener to 'button2' which in this case refers to the *Reverse* button. (Check XML).
        reverse.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                try {
                    //Sends a "reverse" commmand to the Raspberry pi (to be integrated)
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        System.out.println("Reversing");
                    }
                    //Sends command to stop reversing (to be integrated)
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        System.out.println("Stop reversing");
                    }
                }catch(Exception e){
                    System.out.println("MAH GOD WHY");
                }
                return false;
            }
        });


        Button rightSteer= (Button)findViewById(R.id.button4);
        //Sets the TouchListener to 'button4' which in this case refers to the *Right Steer* button. (Check XML).
        rightSteer.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                try {
                    //Sends a "go right" commmand to the Raspberry pi (to be integrated)
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        System.out.println("Steer Right");
                    }
                    //Sends command to stop steering right (to be integrated)
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        System.out.println("Stop steering right");
                    }
                }catch(Exception e){
                    System.out.println("MAH GOD WHY");
                }
                return false;
            }
        });

        Button leftSteer= (Button)findViewById(R.id.button3);
        //Sets the TouchListener to 'button3' which in this case refers to the *Steer Left* button. (Check XML).
        leftSteer.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                try {
                    //Sends a "go left" commmand to the Raspberry pi (to be integrated)
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        System.out.println("Steering Left");
                    }
                    //Sends command to stop steering left (to be integrated)
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        System.out.println("Stop steering left");
                    }
                }catch(Exception e){
                    System.out.println("MAH GOD WHY");
                }
                return false;
            }
        });
    }
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
}

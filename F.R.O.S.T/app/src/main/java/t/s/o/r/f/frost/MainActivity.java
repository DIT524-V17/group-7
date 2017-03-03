package t.s.o.r.f.frost;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.view.View.OnTouchListener;

public class MainActivity extends AppCompatActivity {

    // View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button2 = (Button)findViewById(R.id.button);
        button2.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                try {
                    while (event.getAction() == MotionEvent.ACTION_DOWN) {
                        System.out.println("YE!");
                        wait(3000);
                    }
                }catch(InterruptedException e){
                    System.out.println("MAH GOD WHY");
                }
                return false;
            }
        });
    }



    public void forwardTest(View view){
        Button Forward = (Button) view;
        ((Button) view).setText("Does this happen?");
        System.out.println("Hymn of the nodes!");

    }

/*

    public void rightSteer(View view){
        Button rightSteer = (Button) view;
        ((Button) view).setText("Does this happen?");
        System.out.println("Hymn of the nodes!");

    }*/
}

package t.s.o.r.f.frost;

/**
 * Created by Elaine on 2017-05-11.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.*;
/**
 * Created by Elaine on 2017-05-09.
 */

public class BatteryView extends SurfaceView{
    private float cell;
    private float divider;
    private float cellHeight;

    private void setupDimensions() {
        divider = getWidth() / 10;
        cell = getWidth() * 5;
        cellHeight = getHeight() - divider;
    }

    public BatteryView(Context context){
        super(context);
        setupDimensions();
        //setZOrderOnTop(true);
        //getHolder().addCallback(this);
        //getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    public BatteryView(Context context, AttributeSet attributes, int style){
        super(context, attributes, style);
        setupDimensions();
        //setZOrderOnTop(true);
        //getHolder().addCallback(this);
        //getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    public BatteryView(Context context, AttributeSet attributes){
        super(context, attributes);
        setupDimensions();
        //setZOrderOnTop(true);
        //getHolder().addCallback(this);
        //getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    public void drawBattery(double volt) {

        if (getHolder().getSurface().isValid()) {

            Canvas myCanvas = this.getHolder().lockCanvas(); //Stuff to draw
            Paint colors = new Paint();
            myCanvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR); //Clear the BG
            if(volt < 1.0){
                colors.setColor(Color.RED);
            }
            else{
                colors.setColor(Color.WHITE);
            }

            myCanvas.drawRect(2, 2, getWidth() - 2, getHeight() - 2, colors);
            double referanceValue = 1.25;
            for (int i = 1; i < 5; i++) {

                if(volt > referanceValue){
                    colors.setColor(Color.GREEN);
                }
                else{
                    colors.setColor(Color.BLACK);
                }
                myCanvas.drawRect(i*divider + (i-1)*cell, divider, cell, cellHeight, colors);
                switch (i){
                    case 1:
                        referanceValue = 1.23;
                        break;
                    case 2:
                        referanceValue = 1.2;
                        break;
                    case 3:
                        referanceValue = 1.0;
                        break;
                }

            }
            colors.setColor(Color.BLACK);
            myCanvas.drawRect(5 * divider, 2, cell, getHeight() / 5, colors);
            myCanvas.drawRect(5 * divider, getHeight() / 5 * 4, cell, getHeight() - 2, colors);
        }
    }
}
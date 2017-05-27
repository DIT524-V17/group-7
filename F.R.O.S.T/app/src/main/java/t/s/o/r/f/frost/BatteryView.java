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

public class BatteryView extends SurfaceView implements SurfaceHolder.Callback{
    private static BatteryLevelListener batteryCallback;
    private float cell;
    private float divider;

    private void setupDimensions() {
        divider = getWidth() / 30;
        cell = divider * 5;
    }

    public BatteryView(Context context){
        super(context);
        getHolder().addCallback(this);
        if(context instanceof BatteryLevelListener)
         batteryCallback = (BatteryLevelListener) context;
    }

    public BatteryView(Context context, AttributeSet attributes, int style){
        super(context, attributes, style);
        getHolder().addCallback(this);
        if(context instanceof BatteryLevelListener)
         batteryCallback = (BatteryLevelListener) context;
    }

    public BatteryView(Context context, AttributeSet attributes){
        super(context, attributes);
        getHolder().addCallback(this);
        if(context instanceof BatteryLevelListener)
         batteryCallback = (BatteryLevelListener) context;
    }

    public void drawBattery(double volt) {

        if (getHolder().getSurface().isValid()) {

            Canvas myCanvas = this.getHolder().lockCanvas(); //Stuff to draw
            Paint colors = new Paint();
            myCanvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR); //Clear the BG
            if (volt < 1.0) {
                colors.setColor(Color.RED);
            } else {
                colors.setColor(Color.WHITE);
            }
            // Setting white or red background
            float x1,y1;
            x1 = y1 = divider;
            float x2 = getWidth() - divider;
            float y2 = getHeight() - divider;
            myCanvas.drawRect(x1, y1, x2, y2, colors);

            // Draw 1st cell
            if (colors.getColor() == Color.RED){
                colors.setColor(Color.BLACK);
            }
            else{
                colors.setColor(Color.GREEN);
            }
            x1 = y1 = 2 * divider;
            x2 = x1 + cell;
            y2 = getHeight() - x1;
            myCanvas.drawRect(x1, y1, x2, y2, colors);

            // Draw 2nd cell
            if(1.2 > volt){
                colors.setColor(Color.BLACK);
            }
            x1 = x2 + divider;
            x2 = x1 + cell;
            myCanvas.drawRect(x1, y1, x2, y2, colors);

            // Draw 3rd cell
            if(1.23 > volt){
                colors.setColor(Color.BLACK);
            }
            x1 = x2 + divider;
            x2 = x1 + cell;
            myCanvas.drawRect(x1, y1, x2, y2, colors);

            // Draw 4th cell
            if(1.25 > volt){
                colors.setColor(Color.BLACK);
            }
            x1 = x2 + divider;
            x2 = x1 + cell;
            myCanvas.drawRect(x1, y1, x2, y2, colors);

            // Draw battery end
            colors.setColor(Color.BLACK);
            x1 = x2 + divider;
            x2 = getWidth();
            y1 = divider;
            y2 = getHeight() / 3 ;
            myCanvas.drawRect(x1, y1, x2, y2, colors);

            y1 = getHeight() / 3 * 2;
            y2 = getHeight();
            myCanvas.drawRect(x1, y1, x2, y2, colors);

            getHolder().unlockCanvasAndPost(myCanvas);

        }
    }

        @Override
        public void surfaceCreated(SurfaceHolder holder){

            setupDimensions();
            drawBattery(1.25);

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder){

        }

    public interface BatteryLevelListener{
        void updateBatteryLevel(double volt);

    }
}

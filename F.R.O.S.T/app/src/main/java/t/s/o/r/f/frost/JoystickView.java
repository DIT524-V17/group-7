package t.s.o.r.f.frost;

/**
 * Created by Elaine on 2017-03-07.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import static android.R.color.holo_green_dark;
import static android.R.color.holo_green_light;

public class JoystickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener{

    private static JoystickListener joystickCallback;
    private double centerX;
    private double centerY;
    private double baseRadius;
    private double hatRadius;

    private void setupDimensions(){

        centerX = getWidth() /2;
        centerY = getHeight() / 2;
        baseRadius = Math.min(getWidth(), getHeight()) / (float) 3;
        hatRadius = Math.min(getWidth(), getHeight()) / (float)6.4;

    }

    public JoystickView(Context context){
        super(context);
        setZOrderOnTop(true);
        getHolder().addCallback(this);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        setOnTouchListener(this);
        if(context instanceof JoystickListener)
            joystickCallback = (JoystickListener) context;
    }

    public JoystickView(Context context, AttributeSet attributes, int style){
        super(context, attributes, style);
        setZOrderOnTop(true);
        getHolder().addCallback(this);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        setOnTouchListener(this);
        if(context instanceof JoystickListener)
            joystickCallback = (JoystickListener) context;
    }

    public JoystickView(Context context, AttributeSet attributes){
        super(context, attributes);
        setZOrderOnTop(true);
        getHolder().addCallback(this);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        setOnTouchListener(this);
        if(context instanceof JoystickListener)
            joystickCallback = (JoystickListener) context;

    }

    private void drawJoystick(double newX, double newY){

        if(getHolder().getSurface().isValid()){

            Canvas myCanvas = this.getHolder().lockCanvas(); //Stuff to draw
            Paint colors = new Paint();
            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //Clear the BG
            //colors.setARGB(55,50,50,50); //Color of joystick base
            colors.setARGB(140, 152, 203, 0);
            myCanvas.drawCircle((float)centerX, (float)centerY, (float)baseRadius, colors); //Draw the joystick base
            colors.setColor(getResources().getColor(holo_green_dark));//ARGB(255,0,0,255); // Color of joystick itself
            myCanvas.drawCircle((float)newX, (float)newY, (float)hatRadius, colors); //Draw the joystick hat
            getHolder().unlockCanvasAndPost(myCanvas); //Write the new drawing to the SurfaceView

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){

        setupDimensions();
        drawJoystick(centerX, centerY);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){

    }

    public boolean onTouch(View view, MotionEvent e){

        if(view.equals(this)){

            if(e.getAction() != MotionEvent.ACTION_UP) {
                double displacement = Math.sqrt(Math.pow((e.getX() - centerX), 2) + Math.pow(e.getY() - centerY, 2));
                double speed = displacement;

                double hypotenuse = Math.sqrt(Math.pow(e.getX() - centerX, 2) + Math.pow(e.getY() - centerY, 2));

                if(displacement < baseRadius){
                    double sin = (e.getY() - centerY) / hypotenuse; //sin = o/h
                    double angle = 360.0 - (Math.toDegrees( Math.atan2(e.getY() - centerY, e.getX() - centerX)) + 360.0) % 360.0;
                    if(e.getY() - centerY > 0)
                        speed = -speed;

                    drawJoystick(e.getX(), e.getY());
                    //Log.e("Joystick angle", speed/baseRadius * 100 +"");
                    joystickCallback.onJoystickMoved(speed/baseRadius * 100, angle, getId());
                }
                else{
                    double sin = (e.getY() - centerY) / hypotenuse; //sin = o/h

                    double ratio = baseRadius / displacement;
                    double constrainedX = centerX + (e.getX() - centerX) * ratio;
                    double constrainedY = centerY + (e.getY() - centerY) * ratio;
                    double angle = 360.0 - (Math.toDegrees( Math.atan2(constrainedY - centerY, constrainedX - centerX)) + 360.0) % 360.0;
                    speed = 100;
                    if(e.getY() - centerY > 0)
                        speed = -speed;

                    drawJoystick(constrainedX, constrainedY);
                    joystickCallback.onJoystickMoved(speed, angle, getId());

                }

            }
            else{
                drawJoystick(centerX, centerY);
                joystickCallback.onJoystickMoved(20,90,getId());
            }
        }
        return true;
    }

    public interface JoystickListener{
        void onJoystickMoved(double speed, double angle, int id);//float xPrecent, float yPercent, int id);
    }
}
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
            colors.setARGB(130, 152, 203, 0); //Color of joystick base
            myCanvas.drawCircle((float)centerX, (float)centerY, (float)baseRadius, colors); //Draw the joystick base
            colors.setColor(getResources().getColor(holo_green_dark)); // Color of joystick itself
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

                if(displacement < baseRadius){

                    double angle = 360.0 - (Math.toDegrees( Math.atan2(e.getY() - centerY, e.getX() - centerX)) + 360.0) % 360.0;

                    drawJoystick(e.getX(), e.getY());
                    joystickCallback.onJoystickMoved(e.getY() / Math.abs(centerY - baseRadius), angle, getId());
                }
                else{

                    double ratio = baseRadius / displacement;
                    double constrainedX = centerX + (e.getX() - centerX) * ratio;
                    double constrainedY = centerY + (e.getY() - centerY) * ratio;
                    double angle = 360.0 - (Math.toDegrees( Math.atan2(constrainedY - centerY, constrainedX - centerX)) + 360.0) % 360.0;

                    if(e.getY() - centerY > 0){
                        joystickCallback.onJoystickMoved((centerY + baseRadius) / Math.abs(centerY - baseRadius), angle, getId());
                    }
                    else{
                        joystickCallback.onJoystickMoved((centerY - baseRadius) / Math.abs(centerY - baseRadius), angle, getId());
                    }

                    drawJoystick(constrainedX, constrainedY);

                }

            }
            else{
                drawJoystick(centerX, centerY);
                joystickCallback.onJoystickMoved(centerY / Math.abs(centerY - baseRadius),90,getId());
            }
        }
        return true;
    }

    public interface JoystickListener{
        void onJoystickMoved(double speed, double angle, int id);
    }
}
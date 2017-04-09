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

public class JoystickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener{

    private static JoystickListener joystickCallback;
    private float centerX;
    private float centerY;
    private float baseRadius;
    private float hatRadius;

    private void setupDimensions(){

        centerX = getWidth() /2;
        centerY = getHeight() / 2;
        baseRadius = Math.min(getWidth(), getHeight()) / (float) 2.4;
        hatRadius = Math.min(getWidth(), getHeight()) / (float)5.5;

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

    private void drawJoystick(float newX, float newY){

        if(getHolder().getSurface().isValid()){

            Canvas myCanvas = this.getHolder().lockCanvas(); //Stuff to draw
            Paint colors = new Paint();
            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //Clear the BG
            colors.setARGB(55,50,50,50); //Color of joystick base
            myCanvas.drawCircle(centerX, centerY, baseRadius, colors); //Draw the joystick base
            colors.setARGB(255,0,0,255); // Color of joystick itself
            myCanvas.drawCircle(newX, newY, hatRadius, colors); //Draw the joystick hat
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

            if(e.getAction() != e.ACTION_UP) {
                float displacement = (float) Math.sqrt(Math.pow(e.getX() - centerX, 2) + Math.pow(e.getY() - centerY, 2));
                float speed = displacement;
                float angle = (float) Math.toDegrees(Math.sin((displacement/e.getY() - centerY)));
                //if(e.getY() - centerY < 0)

                //Log.e("X", "" + e.getX());
                Log.e("Y", "" + (e.getY()-centerY));
                if (displacement < baseRadius) {

                    if(e.getY() - centerY > 0)
                        speed = -speed;

                    drawJoystick(e.getX(), e.getY());
                    //Log.e("Joystick angle", speed/baseRadius * 100 +"");
                    joystickCallback.onJoystickMoved(speed/baseRadius * 100, angle, getId());

                }
                else {

                    float ratio = baseRadius / displacement;
                    float constrainedX = centerX + (e.getX() - centerX) * ratio;
                    float constrainedY = centerY + (e.getY() - centerY) * ratio;

                    speed = 100;
                    if(e.getY() - centerY > 0)
                        speed = -speed;

                    drawJoystick(constrainedX, constrainedY);
                    joystickCallback.onJoystickMoved(speed, angle, getId());//(constrainedX - centerX) / baseRadius, (constrainedY - centerY) / baseRadius, getId());
                }
            }
            else{
                drawJoystick(centerX, centerY);
                joystickCallback.onJoystickMoved(0,0,getId());
            }
        }
        return true;
    }

    public interface JoystickListener{
        void onJoystickMoved(float speed, float angle, int id);//float xPrecent, float yPercent, int id);
    }
}


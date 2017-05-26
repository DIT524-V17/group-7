package t.s.o.r.f.frost;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import static android.R.color.holo_green_dark;

/**
 * Created by Elaine on 2017-05-25.
 */

public class CrossJoystickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener{

    private static CrossJoystickListener joystickCallback;
    private double centerX;
    private double centerY;
    private double baseRadius;
    private double hatRadius;
    private double crossWidth;

    private void setupDimensions(){

        centerX = getWidth() /2;
        centerY = getHeight() / 2;
        baseRadius = Math.min(getWidth(), getHeight()) / (float) 3;
        hatRadius = Math.min(getWidth(), getHeight()) / (float)6.4;
        crossWidth = Math.min(getWidth(), getHeight()) / (float) 7.8;

    }

    public CrossJoystickView(Context context){
        super(context);
        setZOrderOnTop(true);
        getHolder().addCallback(this);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        setOnTouchListener(this);
        if(context instanceof CrossJoystickListener)
            joystickCallback = (CrossJoystickListener) context;
    }

    public CrossJoystickView(Context context, AttributeSet attributes, int style){
        super(context, attributes, style);
        setZOrderOnTop(true);
        getHolder().addCallback(this);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        setOnTouchListener(this);
        if(context instanceof CrossJoystickListener)
            joystickCallback = (CrossJoystickListener) context;
    }

    public CrossJoystickView(Context context, AttributeSet attributes){
        super(context, attributes);
        setZOrderOnTop(true);
        getHolder().addCallback(this);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        setOnTouchListener(this);
        if(context instanceof CrossJoystickListener)
            joystickCallback = (CrossJoystickListener) context;

    }

    private void drawJoystick(double newX, double newY){

        if(getHolder().getSurface().isValid()){

            Canvas myCanvas = this.getHolder().lockCanvas(); //Stuff to draw
            Paint colors = new Paint();
            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //Clear the BG
            colors.setARGB(55,50,50,50); //Color of joystick base

            // Draw the cross base
            double x1,x2,y1,y2;
            x1 = centerX - baseRadius;
            y1 = centerY - crossWidth;
            x2 = centerX - crossWidth;
            y2 = centerY + crossWidth;
            myCanvas.drawRect((float) x1 ,(float) y1 ,(float) x2 ,(float) y2 ,colors);
            x1 = centerX + crossWidth;
            x2 = centerX + baseRadius;
            myCanvas.drawRect((float) x1, (float) y1, (float) x2, (float) y2, colors);
            x1 = centerX - crossWidth;
            y1 = centerY - baseRadius;
            x2 = centerX + crossWidth;
            y2 = centerY + baseRadius;
            myCanvas.drawRect((float) x1 ,(float) y1 ,(float) x2 ,(float) y2 ,colors);

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

        if(view.equals(this)) {

            if (e.getAction() != e.ACTION_UP) {
                // Restricts the joystick to the cross
                if(e.getX()> centerX - crossWidth && e.getY() > centerY - crossWidth &&
                        e.getX() < centerX + crossWidth && e.getY() < centerX + crossWidth){
                    drawJoystick(e.getX(),e.getY());
                }
                else if(e.getX() > centerX - baseRadius && e.getX() < centerX &&
                        e.getY() > centerY - crossWidth && e.getY() < centerY + crossWidth) {

                    drawJoystick(e.getX(), centerY);
                    joystickCallback.onCrossJoystickMoved(1, getId());

                }
                else if (e.getY() > centerY && e.getY() < centerY + baseRadius &&
                        e.getX() > centerX - crossWidth && e.getX() < centerX + crossWidth) {

                    drawJoystick(centerX, e.getY());
                    joystickCallback.onCrossJoystickMoved(4, getId());

                }
                else if (e.getX() > centerX && e.getX() < centerX + baseRadius &&
                        e.getY() > centerY - crossWidth && e.getY() < centerY + crossWidth) {

                    drawJoystick(e.getX(), centerY);
                    joystickCallback.onCrossJoystickMoved(3, getId());

                }
                else if (e.getY() > centerY - crossWidth && e.getY() < centerY &&
                        e.getX() > centerX - crossWidth && e.getX() < centerX + crossWidth) {

                    drawJoystick(centerX, e.getY());
                    joystickCallback.onCrossJoystickMoved(2, getId());

                }
                else if (e.getX() <= centerX - baseRadius &&
                        e.getY() > centerY - crossWidth && e.getY() < centerY + crossWidth){

                    drawJoystick(centerX - baseRadius, centerY);
                    joystickCallback.onCrossJoystickMoved(1, getId());

                }
                else if(e.getY() >= centerY + baseRadius &&
                        e.getX() > centerX - crossWidth && e.getX() < centerX + crossWidth){

                    drawJoystick(centerX, centerY + baseRadius);
                    joystickCallback.onCrossJoystickMoved(4, getId());

                }
                else if(e.getX() > centerX + baseRadius &&
                        e.getY() > centerY - crossWidth && e.getY() < centerY + crossWidth) {

                    drawJoystick(centerX + baseRadius, centerY);
                    joystickCallback.onCrossJoystickMoved(3, getId());

                }
                else if(e.getY() < centerY - baseRadius &&
                        e.getX() > centerX - crossWidth && e.getX() < centerX + crossWidth) {

                    drawJoystick(centerX, centerY - baseRadius);
                    joystickCallback.onCrossJoystickMoved(2, getId());

                }

            }
            else {
                drawJoystick(centerX, centerY);
                joystickCallback.onCrossJoystickMoved(0, getId());

            }
        }
        return true;
    }

    public interface CrossJoystickListener{
        void onCrossJoystickMoved(int direction, int id);
    }
}


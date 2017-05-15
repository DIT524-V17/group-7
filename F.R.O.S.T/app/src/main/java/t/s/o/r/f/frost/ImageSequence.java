package t.s.o.r.f.frost;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;

/**
 * Created by Sebas on 2017-05-15.
 */

public class ImageSequence extends AppCompatActivity {


    public static byte[] imageByte;
    //Imageview declaration when you have figured out what tha hell is goin on.
    ImageView ImageSequence = (ImageView) findViewById(R.id.imageSequence);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bitmap bm = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        DisplayMetrics dm = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        ImageSequence.setMinimumHeight(dm.heightPixels);
        ImageSequence.setMinimumWidth(dm.widthPixels);
        ImageSequence.setImageBitmap(bm);



    }

}

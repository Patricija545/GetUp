package pripremazagetup.riteh.hr.pripremazagetup;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;


public class CustomDrawableView extends View {
    String TAG = getClass().getSimpleName();

    private ShapeDrawable mDrawable;
    int mCanvasHeight, mCanvasWidth;
    Image mImage[] = new Image[3];


    void setmImage (Drawable image) {
        mImage[0] = new Image(image);
    }
    Image getImage() { return mImage[0];}

    public CustomDrawableView(Context context) {
        super(context);
    }


    protected void onDraw(Canvas canvas) {

        mCanvasHeight = canvas.getHeight();
        mCanvasWidth = canvas.getWidth();


        if (MainActivity.mFlagAddImage) {
            mImage[0].getmImage().draw(canvas);
        }
        if (MainActivity.mFlagTouched) {
            mImage[0].setImageBounds();
            mImage[0].getmImage().draw(canvas);
            // SCALE
            mImage[0].setmScaleRect();
            mImage[0].getScaleRect().draw(canvas);
            // ROTATE
            mImage[0].setmRotateRect();
            mImage[0].getmRotateRect().draw(canvas);
        }


    }



}
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
    int index = -1;


    void setmImage (Drawable image) {
        index++;
        mImage[index] = new Image(image);
    }
    Image getImage(int num) { return mImage[num];}

    public CustomDrawableView(Context context) {
        super(context);
    }


    protected void onDraw(Canvas canvas) {

        mCanvasHeight = canvas.getHeight();
        mCanvasWidth = canvas.getWidth();


        if (MainActivity.mFlagAddImage) {
            for (int i = 0; i <= index; i++) {
                mImage[i].getmImage().draw(canvas);
            }

        }

        if (MainActivity.mFlagTouched) {
            int currentIndex = MainActivity.currentIndex;

            //Log.d(TAG, "current: " + currentIndex);

            for (int i = 0; i <= index; i++) {
                if (i != currentIndex) {
                    mImage[i].setImageBounds();
                    mImage[i].getmImage().draw(canvas);
                }

            }


            mImage[currentIndex].setImageBounds();
            mImage[currentIndex].getmImage().draw(canvas);

            // SCALE
            mImage[currentIndex].setmScaleRect();
            mImage[currentIndex].getScaleRect().draw(canvas);
            // ROTATE
            mImage[currentIndex].setmRotateRect();
            mImage[currentIndex].getmRotateRect().draw(canvas);


        }
        else {
            for (int i = 0; i <= index; i++) {
                mImage[i].getmImage().draw(canvas);
            }
        }


    }



}
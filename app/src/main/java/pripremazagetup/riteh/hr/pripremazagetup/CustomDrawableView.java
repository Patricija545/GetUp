package pripremazagetup.riteh.hr.pripremazagetup;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.graphics.Matrix;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;


public class CustomDrawableView extends View {
    private ShapeDrawable mDrawable2;
    //private RotateDrawable mDrawable;

    String TAG = getClass().getSimpleName();

    int mCanvasHeight, mCanvasWidth;

    int mWidth = 400;
    int mHeight = 600;


    Point mBeginPt = new Point(0,0);
    Point mEndPt = new Point(mWidth,mHeight);
    Point mCenterPt = new Point(mBeginPt.x + mWidth/2,mBeginPt.y + mHeight/2);

    public CustomDrawableView(Context context) {
        super(context);
  }

    protected void onDraw(Canvas canvas) {

        /*
        AffineTransform tx = new AffineTransform();
        tx.rotate(0.5);
        Rectangle shape = new Rectangle(1, 1, 1, 1);
        Shape newShape = tx.createTransformedShape(shape);
        */

        mCenterPt.x = mBeginPt.x + mWidth/2;
        mCenterPt.y = mBeginPt.y + mHeight/2;

        mCanvasHeight = canvas.getHeight();
        mCanvasWidth = canvas.getWidth();

        Log.d(TAG, "ok");


        Resources res = this.getResources();
        Drawable myImage = ResourcesCompat.getDrawable(res, R.drawable.my_image, null);

/*
        ImageView image = new ImageView(getContext());
        image.setImageResource(R.drawable.my_image);
        Drawable d = image.getDrawable();
        //RotateDrawable myImage2 = (RotateDrawable)image.getDrawable();
        //myImage2.setLevel(90);
        image.draw(canvas);
        */

        if (MainActivity.mFlagTouched) {
            Log.d(TAG, "touched");

            myImage.setBounds(mBeginPt.x, mBeginPt.y , mEndPt.x, mEndPt.y);
            myImage.draw(canvas);


            // SCALE
            mDrawable2 = new ShapeDrawable(new RectShape());
            mDrawable2.getPaint().setColor(getResources().getColor(R.color.colorPrimary));
            mDrawable2.setBounds(mEndPt.x, mEndPt.y, mEndPt.x + 100, mEndPt.y + 100);
            mDrawable2.draw(canvas);

            // ROTATE
            mDrawable2 = new ShapeDrawable(new RectShape());
            mDrawable2.getPaint().setColor(getResources().getColor(R.color.colorPrimaryDark));
            mDrawable2.setBounds(mBeginPt.x - 100, mBeginPt.y - 100, mBeginPt.x, mBeginPt.y);
            mDrawable2.draw(canvas);


        }
        else if (MainActivity.mFlagScale) {
            myImage.setBounds(mBeginPt.x, mBeginPt.y , mEndPt.x, mEndPt.y);
            myImage.draw(canvas);
        }
        else if (MainActivity.mFlagRotate) {



            //ResourcesCompat.getDrawable(res, R.drawable.my_image, null));

            /*
            canvas.save();
            canvas.rotate(45);
            //canvas.drawRect(r,paint);
            canvas.restore();
            */

            //mDrawable2 = getResources().getDrawable(R.drawable.my_image);
            //mDrawable2.getToDegrees();


            myImage.setBounds(mBeginPt.x, mBeginPt.y , mEndPt.x, mEndPt.y);
            myImage.draw(canvas);

        }
        else {

            myImage.setBounds(mBeginPt.x, mBeginPt.y , mEndPt.x, mEndPt.y);
            myImage.draw(canvas);

        }

        /*
        if (MainActivity.mFlagTouched) {
            mDrawableTest = new ShapeDrawable(new RectShape());
            mDrawable.getPaint().setColor(getResources().getColor(R.color.colorPrimary));
            mDrawable.setBounds(mBeginPt.x + 500, mBeginPt.y + 300, mBeginPt.x + 500 + 20, mBeginPt.y + 300 + 20);
            mDrawable.draw(canvas);
        }*/


        /*
        // FIRST RECTANGLE
        int width = 500;
        int height = 300;
        mEndPt = new Point(mBeginPt.x + width, mBeginPt.y + height);

        mDrawable = new ShapeDrawable(new RectShape());
        // If the color isn't set, the shape uses black as the default.
        mDrawable.getPaint().setColor(getResources().getColor(R.color.yellow));
        // If the bounds aren't set, the shape can't be drawn.
        mDrawable.setBounds(mBeginPt.x, mBeginPt.y, mEndPt.x, mEndPt.y);
        mDrawable.draw(canvas);


        // SECOND RECTANGLE - REPRESENTS MIDDLE OF THE FIRST ONE
        width = 10;
        height = 10;
        mDrawableTest = new ShapeDrawable(new RectShape());
        mDrawable.getPaint().setColor(getResources().getColor(R.color.colorPrimary));
        mDrawable.setBounds(mBeginPt.x + 250 - (width/2), mBeginPt.y + 150 - (height/2), mBeginPt.x + 250 + width, mBeginPt.y + 150 + height);
        mDrawable.draw(canvas);

*/
    }

}
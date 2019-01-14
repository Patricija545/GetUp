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
    int mWidth = 400;
    int mHeight = 600;

    Point mBeginPt = new Point(0,0);
    Point mEndPt = new Point(mWidth,mHeight);
    Point mCenterPt = new Point(mBeginPt.x + mWidth/2,mBeginPt.y + mHeight/2);

    public CustomDrawableView(Context context) {
        super(context);
  }

    protected void onDraw(Canvas canvas) {

        mCenterPt.x = mBeginPt.x + mWidth/2;
        mCenterPt.y = mBeginPt.y + mHeight/2;

        mCanvasHeight = canvas.getHeight();
        mCanvasWidth = canvas.getWidth();

        Resources res = this.getResources();
        Drawable myImage = ResourcesCompat.getDrawable(res, R.drawable.my_image, null);

        if (MainActivity.mFlagTouched) {
            myImage.setBounds(mBeginPt.x, mBeginPt.y , mEndPt.x, mEndPt.y);
            myImage.draw(canvas);

            // SCALE
            mDrawable = new ShapeDrawable(new RectShape());
            mDrawable.getPaint().setColor(getResources().getColor(R.color.colorPrimary));
            mDrawable.setBounds(mEndPt.x, mEndPt.y, mEndPt.x + 100, mEndPt.y + 100);
            mDrawable.draw(canvas);

            // ROTATE
            mDrawable = new ShapeDrawable(new RectShape());
            mDrawable.getPaint().setColor(getResources().getColor(R.color.colorPrimaryDark));
            mDrawable.setBounds(mBeginPt.x - 100, mBeginPt.y - 100, mBeginPt.x, mBeginPt.y);
            mDrawable.draw(canvas);

        }
        else if (MainActivity.mFlagScale) {
            myImage.setBounds(mBeginPt.x, mBeginPt.y , mEndPt.x, mEndPt.y);
            myImage.draw(canvas);
        }
        else if (MainActivity.mFlagRotate) {

            myImage.setBounds(mBeginPt.x, mBeginPt.y , mEndPt.x, mEndPt.y);
            myImage.draw(canvas);

        }
        else {

            myImage.setBounds(mBeginPt.x, mBeginPt.y , mEndPt.x, mEndPt.y);
            myImage.draw(canvas);

        }
    }



    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getmHeight() {
        return mHeight;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public Point getmBeginPt() {
        return mBeginPt;
    }

    public void setmBeginPt(Point mBeginPt) {
        this.mBeginPt = mBeginPt;
    }

    public Point getmEndPt() {
        return mEndPt;
    }

    public void setmEndPt(Point mEndPt) {
        this.mEndPt = mEndPt;
    }

    public Point getmCenterPt() {
        return mCenterPt;
    }

    public void setmCenterPt(Point mCenterPt) {
        this.mCenterPt = mCenterPt;
    }

}
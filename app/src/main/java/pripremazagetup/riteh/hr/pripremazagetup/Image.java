package pripremazagetup.riteh.hr.pripremazagetup;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

public class Image {

    Drawable mImage;
    Drawable mScaleRect;

    int mWidth = 400;
    int mHeight = 600;

    Point mBeginPt = new Point(0,0);
    Point mEndPt = new Point(mWidth, mHeight);
    Point mCenterPt = new Point(mBeginPt.x + mWidth/2,mBeginPt.y + mHeight/2);

    public Image (Drawable image) {
        mImage = image;
        setImageBounds();
        setmScaleRect();
    }

    public void setImageBounds() {mImage.setBounds(mBeginPt.x, mBeginPt.y , mEndPt.x, mEndPt.y);}

    public void setmScaleRect() {
        mScaleRect = new ShapeDrawable(new RectShape());
        mScaleRect.setColorFilter(Color.WHITE,PorterDuff.Mode.SRC_OVER);
        mScaleRect.setBounds(mEndPt.x, mEndPt.y, mEndPt.x + 100, mEndPt.y + 100);
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

}

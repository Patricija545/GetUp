package pripremazagetup.riteh.hr.pripremazagetup;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

public class Image {

    Drawable mImage;
    Drawable mScaleRect;
    Drawable mDeleteRect;
    Bitmap mImageBitmap;

    int mWidth;
    int mHeight;

    Point mBeginPt = new Point(100,100);
    Point mEndPt; // = new Point(mWidth, mHeight);
    Point mCenterPt = new Point(mBeginPt.x + mWidth/2,mBeginPt.y + mHeight/2);

    public Image (Drawable image, Bitmap imageBitmap, Drawable scaleRect, Drawable deleteRect) {
        mScaleRect = scaleRect;
        mDeleteRect = deleteRect;
        mImage = image;
        mWidth = mImage.getIntrinsicWidth() + 100;
        mHeight = mImage.getIntrinsicHeight() + 100;
        mEndPt = new Point(mWidth, mHeight);
        mImageBitmap = imageBitmap;
        setImageBounds();
        setmScaleRect();
        setmDeleteRect();
    }

    public void setImageBounds() {mImage.setBounds(mBeginPt.x, mBeginPt.y , mEndPt.x, mEndPt.y);}


    public void setmScaleRect() {
        mScaleRect.setBounds(mEndPt.x - 50, mEndPt.y - 50, mEndPt.x + 50, mEndPt.y + 50);
    }


    public void setmDeleteRect() {
        mDeleteRect.setBounds(mEndPt.x - 50, mBeginPt.y - 50, mEndPt.x + 50, mBeginPt.y + 50);
    }

    public Bitmap getBitmap () {
        return mImageBitmap;
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

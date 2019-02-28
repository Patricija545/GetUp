package pripremazagetup.riteh.hr.pripremazagetup;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.Log;

public class Image {

    Drawable mImage;
    Drawable mScaleRect;
    Drawable mDeleteRect;
    Bitmap mImageBitmap;

    int mWidth;
    int mHeight;

    Point mBeginPt = new Point(100,100);
    Point mEndPt;

    public Image (Drawable image, Bitmap imageBitmap, Drawable scaleRect, Drawable deleteRect) {
        mScaleRect = scaleRect;
        mDeleteRect = deleteRect;
        mImage = image;
        int width = mImage.getIntrinsicWidth();
        int height = mImage.getIntrinsicHeight();
        scaleWithRatio(width, height);
        mEndPt = new Point(mWidth, mHeight);
        mImageBitmap = imageBitmap;
        setImageBounds();
        setmScaleRect();
        setmDeleteRect();
    }

    void scaleWithRatio(int width, int height) {
        if (height < width) {
            float ratio = (float)height/width;
            int newWidth = 600;
            int newHeight = (int)(ratio * (float)newWidth);
            mWidth = newWidth;
            mHeight = newHeight;
        }
        else {
            float ratio = (float)width/height;
            int newHeight = 600;
            int newWidth = (int)(ratio * (float)newHeight);
            mWidth = newWidth;
            mHeight = newHeight;
        }
    }

    public void setImageBounds() {mImage.setBounds(mBeginPt.x, mBeginPt.y , mEndPt.x, mEndPt.y);}

    public void setmScaleRect() {
        mScaleRect.setBounds(mEndPt.x - 50, mEndPt.y - 50, mEndPt.x + 50, mEndPt.y + 50);
    }

    public void setmDeleteRect() {
        mDeleteRect.setBounds(mEndPt.x - 50, mBeginPt.y - 50, mEndPt.x + 50, mBeginPt.y + 50);
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
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

}

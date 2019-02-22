package pripremazagetup.riteh.hr.pripremazagetup;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

public class MyText {
    private String mText;
    private Paint mPaint = new Paint();
    private Point mBeginPt;
    private Drawable mMoveRect;

    public MyText(String text, int color, int size, String fontFamily, Point beginPoint) {
        setPaint(size, color, fontFamily);
        mText = text;
        mBeginPt = beginPoint;
        setMoveRect();
    }

    void setPaint(int size, int color, String fontFamily) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(size);
        mPaint.setTypeface(Typeface.create(fontFamily, Typeface.NORMAL));
    }

    public void setMoveRect() {
        mMoveRect = new ShapeDrawable(new RectShape());
        mMoveRect.setColorFilter(Color.RED,PorterDuff.Mode.SRC_OVER);
        mMoveRect.setBounds(mBeginPt.x - 100, mBeginPt.y - 100, mBeginPt.x, mBeginPt.y);
    }


    void setBeginPt(Point point) {
        mBeginPt = point;
    }

    Drawable getMoveRect() {
        return mMoveRect;
    }

    String getText() {
        return mText;
    }

    Paint getPaint() {
        return mPaint;
    }

    Point getBeginPt() {
        return mBeginPt;
    }

}

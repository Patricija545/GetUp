package pripremazagetup.riteh.hr.pripremazagetup;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.time.chrono.MinguoEra;

public class MyText {
    private String mText;
    private Paint mPaint = new Paint();
    private Point mBeginPt;
    private Point mEndPt = new Point(0,0);
    private Drawable mMoveRect;
    private Drawable mDeleteRect;

    public MyText(String text, int color, int size, String fontFamily, Point beginPoint, Drawable moveRect, Drawable deleteRect) {
        mText = text;
        mBeginPt = beginPoint;
        setPaint(text, size, color, fontFamily);
        mMoveRect = moveRect;
        mDeleteRect = deleteRect;
        setMoveRect();
        setDeleteRect();
    }

    void setPaint(String text, int size, int color, String fontFamily) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(size);
        mPaint.setTypeface(Typeface.create(fontFamily, Typeface.NORMAL));

        String lines[] = text.split("\n");
        int maxNum = 0;
        for (int i = 0; i < lines.length; i++) {
            int lenght = (int) mPaint.measureText(lines[i]);
            if (lenght > maxNum) {
                maxNum = lenght;
            }
        }
        mEndPt.x = mBeginPt.x + maxNum;
        mEndPt.y = mBeginPt.y + size * (lines.length-1);
    }

    public void setMoveRect() {
        mMoveRect.setBounds(mEndPt.x, mEndPt.y, mEndPt.x + 100, mEndPt.y + 100);
    }


    public void setDeleteRect() {
        // donji desni kut
        mDeleteRect.setBounds(mBeginPt.x - 100, mBeginPt.y - 100, mBeginPt.x, mBeginPt.y);
        //mDeleteRect.setBounds(mEndPt.x, mBeginPt.y, mEndPt.x + 100, mBeginPt.y + 100);
    }

/*
    public void setDeleteRect() {
        mDeleteRect.setBounds(mEndPt.x - 100, mBeginPt.y - 100, mEndPt.x, mBeginPt.y);
        Log.d("MyText begin", "x: " + (mBeginPt.x-100) +", : " + mBeginPt.y);
        Log.d("MyText end", "x: " + (mEndPt.x-100) +", : " + mEndPt.y);
    }*/

    void setBeginPt(Point point) {
        mBeginPt = point;
    }

    void setEndPt(Point point) { mEndPt = point; }

    Drawable getMoveRect() {
        return mMoveRect;
    }

    Drawable getDeleteRect() { return mDeleteRect;}

    String getText() {
        return mText;
    }

    Paint getPaint() {
        return mPaint;
    }

    Point getBeginPt() {
        return mBeginPt;
    }

    Point getEndPt() {
        return mEndPt;
    }



}

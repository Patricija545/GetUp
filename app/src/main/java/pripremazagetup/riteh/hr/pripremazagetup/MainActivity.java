package pripremazagetup.riteh.hr.pripremazagetup;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity {
    String TAG = this.getClass().getSimpleName();

    LinearLayout mLinearLayout;
    CustomDrawableView mCustomDrawableView;

    Point mTouchedPt = new Point(0,0);
    Point mMovedPt = new Point(0,0);
    Point mDifferencePt = new Point(0,0);


    public static boolean mFlagTouched = false;
    public static boolean mFlagScale = false;
    public static boolean mFlagRotate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLinearLayout = findViewById(R.id.llayout1);
        mLinearLayout.setOnTouchListener(handleTouch);

        mCustomDrawableView = new CustomDrawableView(this);
        mLinearLayout.addView(mCustomDrawableView);
        mCustomDrawableView.invalidate();

    }


    private View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchedPt.x = (int) event.getX();
                    mTouchedPt.y = (int) event.getY();

                    Point beginPoint = mCustomDrawableView.getmBeginPt();
                    Point endPoint = mCustomDrawableView.getmEndPt();
                    int imageWidth = mCustomDrawableView.getmWidth();
                    int imageHeight = mCustomDrawableView.getHeight();

                    // IF IMAGE IS CLICKED
                    if ((mTouchedPt.x > beginPoint.x && mTouchedPt.y > beginPoint.y)
                            && (mTouchedPt.x < endPoint.x && mTouchedPt.y < endPoint.y))
                    {
                        mFlagTouched = true;
                        mFlagScale = false;
                        mFlagRotate = false;
                        mCustomDrawableView.invalidate();
                    }
                    // IF RECT FOR SCALING IS CLICKED
                    else if (mTouchedPt.x > endPoint.x
                            && mTouchedPt.y > endPoint.y
                            && mTouchedPt.x < (endPoint.x + 100)
                            && mTouchedPt.y < (endPoint.y + 100))
                    {
                        mFlagScale = true;
                        mFlagRotate = false;
                        Log.d(TAG, "SCALE");
                    }
                    // IF RECT FOR ROTATING IS CLICKED
                    else if (mTouchedPt.x > (beginPoint.x - 100)
                            && mTouchedPt.y > (beginPoint.y - 100)
                            && mTouchedPt.x < beginPoint.x
                            && mTouchedPt.y < beginPoint.y)
                    {
                            mFlagRotate = true;
                            mFlagScale = false;
                            Log.d(TAG, "ROTATE");
                    }
                    // IF CLICKED SOMEWHERE ON CANVAS
                    else {
                        mFlagTouched = false;
                        mFlagScale = false;
                        mFlagRotate = false;
                        mCustomDrawableView.invalidate();
                    }

/*
/                   // ROTATE IMAGE
                    if (mFlagRotate) {
                        mCustomDrawableView.invalidate();

                        if (mTouchedPt.x > (beginPoint.x - 100)
                                && mTouchedPt.y > (beginPoint.y - 100)
                                && mTouchedPt.x < beginPoint.y
                                && mTouchedPt.y < beginPoint.y)
                        {
                            double inDegrees = 90;
                            double angle = Math.toRadians(inDegrees);

                            // 1. FIND CENTER
                            Point centerPoint = mCustomDrawableView.getmCenterPt();


                            //mCustomDrawableView.setmBeginPt(new Point(beginPoint.x - centerPoint.x, beginPoint.y - centerPoint.y));
                            //mCustomDrawableView.setmEndPt(new Point(beginPoint.x - centerPoint.x, beginPoint.y - centerPoint.y));
                            Point testRotateBegin = mCustomDrawableView.getmBeginPt();
                            Point testRotateEnd = mCustomDrawableView.getmEndPt();


                            // 2. PRETEND RECTANGLE IS AT THE ORIGIN
                            testRotateBegin.x = testRotateBegin.x - centerPoint.x;
                            testRotateBegin.y = testRotateBegin.y - centerPoint.y;
                            testRotateEnd.x = testRotateEnd.x - centerPoint.x;
                            testRotateEnd.y = testRotateEnd.y - centerPoint.y;


                            int begin_x;
                            int begin_y;
                            int end_x;
                            int end_y;

                            // 3. ROTATION MATRICES
                            begin_x = testRotateBegin.x * (int)Math.cos(angle) - testRotateBegin.y * (int) Math.sin(angle);
                            begin_y = testRotateBegin.x * (int)Math.sin(angle) + testRotateBegin.y * (int) Math.cos(angle);
                            end_x =  testRotateEnd.x * (int)Math.cos(angle) - testRotateEnd.y * (int) Math.sin(angle);
                            end_y = testRotateEnd.x * (int)Math.sin(angle) + testRotateEnd.y * (int) Math.cos(angle);

                            mCustomDrawableView.setmBeginPt(
                                    new Point (begin_x + centerPoint.x - imageHeight,begin_y + centerPoint.y));
                            mCustomDrawableView.setmEndPt(
                                    new Point(end_x + centerPoint.x + imageHeight, end_y + centerPoint.y)
                            );

                            Log.d(TAG, "center x: " + centerPoint.x + ", y:" + centerPoint.y);
                            Log.d(TAG, "begin x: " + mCustomDrawableView.mBeginPt.x + ", y:" + mCustomDrawableView.mBeginPt.y);
                            Log.d(TAG, "end x: " + mCustomDrawableView.mEndPt.x  + ", y:" + mCustomDrawableView.mEndPt.y);

                            mCustomDrawableView.invalidate();

                        }

                }


                    break;*/

                case MotionEvent.ACTION_MOVE:

                    mMovedPt.x = (int) event.getX();
                    mMovedPt.y = (int) event.getY();

                    beginPoint = mCustomDrawableView.getmBeginPt();
                    endPoint = mCustomDrawableView.getmEndPt();
                    imageWidth = mCustomDrawableView.getmWidth();
                    imageHeight = mCustomDrawableView.getHeight();

                    //TODO: add limit on scaling outside the width and height of view

                    if (mFlagTouched == true) {

                        // SCALE IMAGE
                        if (mFlagScale) {

                                // IF RECT FOR SCALING IS STILL TOUCHED
                                if ((mMovedPt.x - beginPoint.x) > 200
                                        && (mMovedPt.y - beginPoint.y) > 200
                                        && (mMovedPt.x - beginPoint.x) < (mCustomDrawableView.mCanvasWidth - 200)
                                        && (mMovedPt.y - beginPoint.y) < (mCustomDrawableView.mCanvasHeight - 200))
                                {
                                    endPoint = new Point(mMovedPt.x, mMovedPt.y);
                                    mCustomDrawableView.setmEndPt(endPoint);
                                    mCustomDrawableView.setmWidth(endPoint.x - beginPoint.x);
                                    mCustomDrawableView.setmHeight(endPoint.y - beginPoint.y);

                                    mCustomDrawableView.invalidate();

                                }

                        }

                        // MOVE IMAGE
                        else {
                            mDifferencePt.x = mMovedPt.x - mTouchedPt.x;
                            mDifferencePt.y = mMovedPt.y - mTouchedPt.y;

                            int testBeginX = beginPoint.x + mDifferencePt.x;
                            int testBeginY = beginPoint.y + mDifferencePt.y;

                            imageWidth = mCustomDrawableView.getmWidth();
                            imageHeight = mCustomDrawableView.getmHeight();

                            // IF INSIDE CANVAS
                            if ((testBeginX  >= 0) && (testBeginY >= 0)
                                    && ((testBeginX + imageWidth) < mCustomDrawableView.mCanvasWidth)
                                    && ((testBeginY + imageHeight) < mCustomDrawableView.mCanvasHeight)) {

                                mCustomDrawableView.setmBeginPt(new Point(testBeginX, testBeginY));
                                mCustomDrawableView.setmEndPt(new Point(endPoint.x + mDifferencePt.x, endPoint.y + mDifferencePt.y));
                                mCustomDrawableView.invalidate();

                                mTouchedPt.x = mMovedPt.x;
                                mTouchedPt.y = mMovedPt.y;
                            }

                        }

                    }

                    break;

                case MotionEvent.ACTION_UP:
                    break;
            }

            return true;
        }
    };

}

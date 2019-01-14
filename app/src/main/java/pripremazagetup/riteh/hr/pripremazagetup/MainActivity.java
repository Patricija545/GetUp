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

        //mLinearLayout = new LinearLayout(this);
//        mLinearLayout = (LinearLayout)findViewById(R.id.llayout);
        setContentView(R.layout.activity_main);

        mLinearLayout = findViewById(R.id.llayout1);
        mLinearLayout.setOnTouchListener(handleTouch);

        mCustomDrawableView = new CustomDrawableView(this);
        mLinearLayout.addView(mCustomDrawableView);
        mCustomDrawableView.invalidate();

/*      //DODAVANJE SLIKE
        ImageView i = new ImageView(this);

        //DRAWABLE
        Resources res = this.getResources();
        Drawable myImage = ResourcesCompat.getDrawable(res, R.drawable.my_image, null);

        i.setImageResource(R.drawable.my_image);
        //bolje bez??
        //i.setAdjustViewBounds(true);
        i.setLayoutParams(new Gallery.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mLinearLayout.addView(i);
        setContentView(mLinearLayout);
        */

    }


    private View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchedPt.x = (int) event.getX();
                    mTouchedPt.y = (int) event.getY();


                    if ((mTouchedPt.x > mCustomDrawableView.mBeginPt.x
                            && mTouchedPt.y > mCustomDrawableView.mBeginPt.y)
                            && (mTouchedPt.x < (mCustomDrawableView.mBeginPt.x+ mCustomDrawableView.mWidth)
                            && mTouchedPt.y < (mCustomDrawableView.mBeginPt.y+ mCustomDrawableView.mHeight)))
                    {
                        mFlagTouched = true;
                        mFlagScale = false;
                        mFlagRotate = false;
                        mCustomDrawableView.invalidate();
                    }
                    else if (mTouchedPt.x > mCustomDrawableView.mEndPt.x
                            && mTouchedPt.y > mCustomDrawableView.mEndPt.y
                            && mTouchedPt.x < (mCustomDrawableView.mEndPt.x + 100)
                            && mTouchedPt.y < (mCustomDrawableView.mEndPt.y + 100))
                    {
                        mFlagScale = true;
                        mFlagRotate = false;
                        //mFlagTouched = false;
                        Log.d(TAG, "SCALE");
                    }
                    else if (mTouchedPt.x > (mCustomDrawableView.mBeginPt.x - 100)
                            && mTouchedPt.y > (mCustomDrawableView.mBeginPt.y - 100)
                            && mTouchedPt.x < mCustomDrawableView.mBeginPt.y
                            && mTouchedPt.y < mCustomDrawableView.mBeginPt.y)
                    {
                            mFlagRotate = true;
                            mFlagScale = false;
                            Log.d(TAG, "ROTATE");
                    }
                    else {
                        mFlagTouched = false;
                        mFlagScale = false;
                        mFlagRotate = false;
                        mCustomDrawableView.invalidate();
                    }


                    if (mFlagRotate) {
                        mCustomDrawableView.invalidate();


                    if (mTouchedPt.x > (mCustomDrawableView.mBeginPt.x - 100)
                            && mTouchedPt.y > (mCustomDrawableView.mBeginPt.y - 100)
                            && mTouchedPt.x < mCustomDrawableView.mBeginPt.y
                            && mTouchedPt.y < mCustomDrawableView.mBeginPt.y)
                    {
                        double inDegrees = 90;
                        double angle = Math.toRadians(inDegrees);

                        int test = (int) Math.sin(angle);

                        // 1. FIND CENTER
                        int mid_x = mCustomDrawableView.mCenterPt.x;
                        int mid_y = mCustomDrawableView.mCenterPt.y;

                        // 2. PRETEND RECTANGLE IS AT THE ORIGIN
                        mCustomDrawableView.mBeginPt.x = mCustomDrawableView.mBeginPt.x - mid_x;
                        mCustomDrawableView.mBeginPt.y = mCustomDrawableView.mBeginPt.y - mid_y;
                        mCustomDrawableView.mEndPt.x = mCustomDrawableView.mEndPt.x - mid_x;
                        mCustomDrawableView.mEndPt.y = mCustomDrawableView.mEndPt.y - mid_y;

                        int begin_x = mCustomDrawableView.mBeginPt.x;
                        int begin_y = mCustomDrawableView.mBeginPt.y;
                        int end_x = mCustomDrawableView.mEndPt.x;
                        int end_y = mCustomDrawableView.mEndPt.y;


                        // 3. ROTATION MATRICES
                        mCustomDrawableView.mBeginPt.x = begin_x * (int)Math.cos(angle) - begin_y * (int) Math.sin(angle);
                        mCustomDrawableView.mBeginPt.y = begin_x * (int)Math.sin(angle) + begin_y * (int) Math.cos(angle);
                        mCustomDrawableView.mEndPt.x =  end_x * (int)Math.cos(angle) - end_y * (int) Math.sin(angle);
                        mCustomDrawableView.mEndPt.y = end_x * (int)Math.sin(angle) + end_y * (int) Math.cos(angle);

                        mCustomDrawableView.mBeginPt.x = mCustomDrawableView.mBeginPt.x + mid_x - mCustomDrawableView.mHeight;
                        mCustomDrawableView.mBeginPt.y = mCustomDrawableView.mBeginPt.y + mid_y;// + mid_x; //* (-1) + mid_y;
                        mCustomDrawableView.mEndPt.x = mCustomDrawableView.mEndPt.x + mid_x + mCustomDrawableView.mHeight;
                        mCustomDrawableView.mEndPt.y = mCustomDrawableView.mEndPt.y + mid_y;// - mid_x; //* (-1) + mid_y;

                        Log.d(TAG, "middle x: " + mid_x + ", y:" + mid_y);
                        Log.d(TAG, "begin x: " + mCustomDrawableView.mBeginPt.x + ", y:" + mCustomDrawableView.mBeginPt.y);
                        Log.d(TAG, "end x: " + mCustomDrawableView.mEndPt.x  + ", y:" + mCustomDrawableView.mEndPt.y);

                        mCustomDrawableView.invalidate();

                    }

                }

                    break;

                case MotionEvent.ACTION_MOVE:

                    mMovedPt.x = (int) event.getX();
                    mMovedPt.y = (int) event.getY();


                    if (mFlagTouched == true) {

                        if (mFlagScale) {


                                if ((mMovedPt.x - mCustomDrawableView.mBeginPt.x) > 200
                                        && (mMovedPt.y - mCustomDrawableView.mBeginPt.y) > 200
                                        && (mMovedPt.x - mCustomDrawableView.mBeginPt.x) < (mCustomDrawableView.mCanvasWidth-200)
                                        && (mMovedPt.y - mCustomDrawableView.mBeginPt.y) < (mCustomDrawableView.mCanvasHeight-200))
                                {
                                    mCustomDrawableView.mEndPt.x = mMovedPt.x;
                                    mCustomDrawableView.mEndPt.y = mMovedPt.y;

                                    mCustomDrawableView.mWidth = mCustomDrawableView.mEndPt.x - mCustomDrawableView.mBeginPt.x;
                                    mCustomDrawableView.mHeight = mCustomDrawableView.mEndPt.y - mCustomDrawableView.mBeginPt.y;
                                    mCustomDrawableView.invalidate();

                                }


                        }



                        else {
                            mDifferencePt.x = mMovedPt.x - mTouchedPt.x;
                            mDifferencePt.y = mMovedPt.y - mTouchedPt.y;

                            int test_x = mCustomDrawableView.mBeginPt.x + mDifferencePt.x;
                            int test_y = mCustomDrawableView.mBeginPt.y + mDifferencePt.y;

                            //Log.d(TAG, "Begin point x: " + test_x + ",y :" + test_y);
                            //Log.d(TAG, "End point x: " + (test_x + mCustomDrawableView.mWidth) + ",y :" + (test_y + mCustomDrawableView.mHeight));

                            if ((test_x  >= 0) && (test_y >= 0) && ((test_x + mCustomDrawableView.mWidth) < mCustomDrawableView.mCanvasWidth) && ((test_y + mCustomDrawableView.mHeight) < mCustomDrawableView.mCanvasHeight)) {

                                mCustomDrawableView.mBeginPt.x = mCustomDrawableView.mBeginPt.x + mDifferencePt.x;
                                mCustomDrawableView.mBeginPt.y = mCustomDrawableView.mBeginPt.y + mDifferencePt.y;
                                mCustomDrawableView.mEndPt.x = mCustomDrawableView.mEndPt.x + mDifferencePt.x; //mCustomDrawableView.mBeginPt.x + mCustomDrawableView.mWidth;
                                mCustomDrawableView.mEndPt.y = mCustomDrawableView.mEndPt.y + mDifferencePt.y; //mCustomDrawableView.mBeginPt.y + mCustomDrawableView.mHeight;
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

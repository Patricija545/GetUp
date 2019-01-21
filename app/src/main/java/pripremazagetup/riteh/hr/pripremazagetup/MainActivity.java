package pripremazagetup.riteh.hr.pripremazagetup;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity {
    String TAG = this.getClass().getSimpleName();

    LinearLayout mLinearLayout;
    Button mBtnAddImage;
    Button mBtnAddImage2;

    Button mBtnDeleteImage;
    CustomDrawableView mCustomDrawableView;
    Drawable myImage, myImage2, myImage3;
    Image mImage;
    Dialog mAddImageDialog;
    int index = -1;
    public static int currentIndex = -1;

    Point mTouchedPt = new Point(0,0);
    Point mMovedPt = new Point(0,0);
    Point mDifferencePt = new Point(0,0);


    public static boolean mFlagTouched = false;
    public static boolean mFlagScale = false;
    public static boolean mFlagRotate = false;
    public static boolean mFlagAddImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // INITIALIZE VIEWS
        mLinearLayout = findViewById(R.id.llayout1);
        mBtnAddImage = findViewById(R.id.btnAdd);
        mBtnDeleteImage = findViewById(R.id.btnDelete);
        mAddImageDialog = new Dialog(this);
        mCustomDrawableView = new CustomDrawableView(this);


        // ADD VIEWS TO LAYOUT
        mLinearLayout.addView(mCustomDrawableView);
        mCustomDrawableView.invalidate();

        // LISTENERS
        mLinearLayout.setOnTouchListener(handleTouch);
        mBtnAddImage.setOnClickListener(handleClickAddImage);
        //mBtnDeleteImage.setOnClickListener(handleClickRemoveImage);

    }

    private View.OnClickListener handleClickAddImage = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            mAddImageDialog.setContentView(R.layout.dialog_add_image);

            // IMAGES
            Resources res = getApplication().getResources();
            myImage = ResourcesCompat.getDrawable(res, R.drawable.my_image, null);
            myImage2 = ResourcesCompat.getDrawable(res, R.drawable.my_image2, null);
            myImage3 = ResourcesCompat.getDrawable(res, R.drawable.my_image3, null);


            //mAddImageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mAddImageDialog.show();

            // TODO: Make one handle for all three buttons
            Button btn = (Button) mAddImageDialog.findViewById(R.id.btnImage);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    index++;
                    mFlagAddImage = true;
                    mCustomDrawableView.setmImage(myImage);
                    mImage = mCustomDrawableView.getImage(index);
                    mCustomDrawableView.invalidate();
                    mAddImageDialog.dismiss();
                }
            });


            Button btn2 = (Button) mAddImageDialog.findViewById(R.id.btnImage2);
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    index++;
                    mFlagAddImage = true;
                    mCustomDrawableView.setmImage(myImage2);
                    mImage = mCustomDrawableView.getImage(index);
                    mCustomDrawableView.invalidate();
                    mAddImageDialog.dismiss();
                }
            });

            Button btn3 = (Button) mAddImageDialog.findViewById(R.id.btnImage3);
            btn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    index++;
                    mFlagAddImage = true;
                    mCustomDrawableView.setmImage(myImage3);
                    mImage = mCustomDrawableView.getImage(index);
                    mCustomDrawableView.invalidate();
                    mAddImageDialog.dismiss();
                }
            });


        }
    };

    // TODO: delete image
/*
    private View.OnClickListener handleClickRemoveImage = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            if (index > -1) {
                mFlagAddImage = false;
                mFlagTouched = false;
                mCustomDrawableView.invalidate();
                index--;
            }

        }
    };
*/
    private View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchedPt.x = (int) event.getX();
                    mTouchedPt.y = (int) event.getY();

                    Point beginPoint;
                    Point endPoint;

                     //for (int i = 0; i <= index; i++) {
                    int i = 0;
                    while (i <= index && (mFlagScale == false && mFlagTouched == false && mFlagRotate == false)) {
                        mImage = mCustomDrawableView.getImage(i);

                        beginPoint = mImage.getmBeginPt();
                        endPoint = mImage.getmEndPt();
                        //int imageWidth = mImage[index].getmWidth();
                        //int imageHeight = mImage[index].getmHeight();

                        // IF IMAGE IS CLICKED
                        if ((mTouchedPt.x > beginPoint.x && mTouchedPt.y > beginPoint.y)
                                && (mTouchedPt.x < endPoint.x && mTouchedPt.y < endPoint.y))
                        {
                            mFlagTouched = true;
                            mFlagScale = false;
                            mFlagRotate = false;
                            mCustomDrawableView.invalidate();
                            currentIndex = i;
                            break;
                        }
                        // IF RECT FOR SCALING IS CLICKED
                        else if (mTouchedPt.x > endPoint.x
                                && mTouchedPt.y > endPoint.y
                                && mTouchedPt.x < (endPoint.x + 100)
                                && mTouchedPt.y < (endPoint.y + 100))
                        {
                            mFlagScale = true;
                            mFlagRotate = false;
                            currentIndex = i;
                            Log.d(TAG, "SCALE INDEX: " + currentIndex);
                            break;
                        }
                        // IF RECT FOR ROTATING IS CLICKED
                        else if (mTouchedPt.x > (beginPoint.x - 100)
                                && mTouchedPt.y > (beginPoint.y - 100)
                                && mTouchedPt.x < beginPoint.x
                                && mTouchedPt.y < beginPoint.y)
                        {
                            mFlagRotate = true;
                            mFlagScale = false;
                            currentIndex = i;
                            Log.d(TAG, "ROTATE");
                            break;
                        }
                        // IF CLICKED SOMEWHERE ON CANVAS
                        else {
                            mFlagTouched = false;
                            mFlagScale = false;
                            mFlagRotate = false;
                            mCustomDrawableView.invalidate();
                        }

                        i++;
                    }

                    Log.d(TAG, "Current index: " + currentIndex + ": " + mFlagScale);




/*                   // ROTATE IMAGE
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

                            mImage.setImageBounds();
                            mCustomDrawableView.invalidate();

                        }

                }

*/
                    break;

                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "Current index MOVE: " + currentIndex + ": " + mFlagScale);


                    mMovedPt.x = (int) event.getX();
                    mMovedPt.y = (int) event.getY();

                    beginPoint = mImage.getmBeginPt();
                    endPoint = mImage.getmEndPt();
                    int imageWidth = mImage.getmWidth();
                    int imageHeight = mImage.getmHeight();

                    // TODO: add limit on scaling outside the width and height of view
                    // TODO: scale "active" image

                    // MOVE
                    if (mFlagTouched == true) {

                            mImage = mCustomDrawableView.getImage(currentIndex);

                            mDifferencePt.x = mMovedPt.x - mTouchedPt.x;
                            mDifferencePt.y = mMovedPt.y - mTouchedPt.y;

                            int testBeginX = beginPoint.x + mDifferencePt.x;
                            int testBeginY = beginPoint.y + mDifferencePt.y;

                            imageWidth = mImage.getmWidth();
                            imageHeight = mImage.getmHeight();

                            // IF INSIDE CANVAS
                            if ((testBeginX  >= 0) && (testBeginY >= 0)
                                    && ((testBeginX + imageWidth) < mCustomDrawableView.mCanvasWidth)
                                    && ((testBeginY + imageHeight) < mCustomDrawableView.mCanvasHeight)) {

                                mImage.setmBeginPt(new Point(testBeginX, testBeginY));
                                mImage.setmEndPt(new Point(endPoint.x + mDifferencePt.x, endPoint.y + mDifferencePt.y));
                                mImage.setImageBounds();
                                mCustomDrawableView.invalidate();

                                mTouchedPt.x = mMovedPt.x;
                                mTouchedPt.y = mMovedPt.y;
                            }

                    }
                    // SCALE
                    else if (mFlagScale == true){
                        mImage = mCustomDrawableView.getImage(currentIndex);
                        beginPoint = mImage.getmBeginPt();

                        Log.d(TAG, "FLAG SCALE :" + (mMovedPt.x - beginPoint.x));

                        // IF RECT FOR SCALING IS STILL TOUCHED
                        if ((mMovedPt.x - beginPoint.x) > 100
                                && (mMovedPt.y - beginPoint.y) > 100
                                && (mMovedPt.x - beginPoint.x) < (mCustomDrawableView.mCanvasWidth - 100)
                                && (mMovedPt.y - beginPoint.y) < (mCustomDrawableView.mCanvasHeight - 100))
                        {
                            endPoint = new Point(mMovedPt.x, mMovedPt.y);
                            mImage.setmEndPt(endPoint);
                            mImage.setmWidth(endPoint.x - beginPoint.x);
                            mImage.setmHeight(endPoint.y - beginPoint.y);
                            mImage.setImageBounds();
                            mCustomDrawableView.invalidate();

                        }
                    }

                    break;


                case MotionEvent.ACTION_UP:
                    mFlagTouched = false;
                    mFlagScale = false;
                    mFlagRotate = false;
                    break;
            }

            return true;
        }
    };

}

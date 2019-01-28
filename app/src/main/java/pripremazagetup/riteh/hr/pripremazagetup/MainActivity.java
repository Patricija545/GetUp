package pripremazagetup.riteh.hr.pripremazagetup;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String TAG = this.getClass().getSimpleName();

    LinearLayout mLinearLayout;
    Button mBtnAddImage;
    Button mBtnDeleteImage;
    Button mBtnAddText;
    CustomDrawableView mCustomDrawableView;
    Drawable myImage, myImage2, myImage3;
    Image mImage;
    Dialog mAddImageDialog, mAddTextDialog;
    EditText mText;
    TextView mTextCanvas;
    TextView mTextPreview;
    Spinner spinnerFontFamily;
    String mFontFamilyName;
    int mFontSize = 18;
    int index = -1;
    int mFontColorID;
    int colors[] = {R.color.white, R.color.red, R.color.orange, R.color.yellow, R.color.green, R.color.turquoise, R.color.lightBlue, R.color.darkBlue, R.color.purple, R.color.pink};

    public static int currentIndex = -1;
    public static int maxImageNum = 3;

    Point mTouchedPt = new Point(0,0);
    Point mMovedPt = new Point(0,0);
    Point mDifferencePt = new Point(0,0);

    public static boolean mFlagTouched = false;
    public static boolean mFlagScale = false;
    public static boolean mFlagRotate = false;
    public static boolean mFlagAddImage = false;
    public static boolean mFlagDialogFirstOpen = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // INITIALIZE VIEWS
        mLinearLayout = findViewById(R.id.drawLayout);
        mBtnAddImage = findViewById(R.id.btnAdd);
        mBtnDeleteImage = findViewById(R.id.btnDelete);
        mBtnAddText = findViewById(R.id.btnText);
        mAddImageDialog = new Dialog(this);
        mAddTextDialog = new Dialog(this);
        mAddTextDialog.setContentView(R.layout.dialog_add_text);
        mCustomDrawableView = new CustomDrawableView(this);
        mTextCanvas = findViewById(R.id.tvTextCanvas);
        spinnerFontFamily = mAddTextDialog.findViewById(R.id.spinnerFontFamily);
        spinnerFontFamily.setOnItemSelectedListener(this);

        // ADD VIEWS TO LAYOUT
        mLinearLayout.addView(mCustomDrawableView);
        mCustomDrawableView.invalidate();

        // LISTENERS
        mLinearLayout.setOnTouchListener(handleTouch);
        mBtnAddImage.setOnClickListener(handleClickAddImage);
        mBtnDeleteImage.setOnClickListener(handleClickRemoveImage);
        mBtnAddText.setOnClickListener(handleClickAddText);
    }


    // ADD TEXT DIALOG
    private final View.OnClickListener handleClickAddText = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            // SHOW DIALOG FOR ADDING TEXT
            mAddTextDialog.show();

            // INITIALIZE VIEWS
            mTextPreview = mAddTextDialog.findViewById(R.id.previewText);

            // SHOW COLORS ON LINEAR LAYOUT
            LinearLayout layoutColors = mAddTextDialog.findViewById(R.id.llayoutcolor);

            // ADD BUTTONS FOR COLOR PICKING ON FIRST OPENING
            if (mFlagDialogFirstOpen) {

                // CREATE ARRAY OF BUTTONS FOR DISPLAYING COLOR
                Button colorButton[] = new Button[colors.length];

                // CREATE BUTTON AND GIVE IT PARAMS AND BACKGROUND
                for (int i = 0; i < colors.length; i++) {
                    colorButton[i] = new Button(getApplicationContext());
                    Resources res = getApplication().getResources();
                    Drawable roundButton = ResourcesCompat.getDrawable(res, R.drawable.roundbutton, null);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100, (float)0.1);
                    params.setMargins(5,5,5,5);
                    colorButton[i].setLayoutParams(params);
                    colorButton[i].setBackground(roundButton);
                    colorButton[i].setId(i);
                }

                // SET PROPER COLOR OF BUTTON
                for (int i = 0; i < colors.length; i++) {
                    colorButton[i].getBackground().setColorFilter(getResources().getColor(colors[i]), PorterDuff.Mode.SRC_OVER);
                }

                // ADD BUTTON TO VIEW
                for (int i = 0; i < colors.length; i++) {
                    layoutColors.addView(colorButton[i]);
                }

                // COLOR PICKER LISTENER
                for (int i = 0; i < colors.length; i++) {
                    colorButton[i].setOnClickListener(handleClickChooseColor);
                }

                //todo: SET THIS FLAG TO TRUE WHEN CLICKING BACK AND GOING OUT OF APPLICATION
                mFlagDialogFirstOpen = false;
            }


            // GET TEXT SIZE
            SeekBar fontSize = mAddTextDialog.findViewById(R.id.seekbar_font_size);
            fontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    // MINIMUM FONT SIZE IZ 14 (MINIMUM OF SEEKBAR IS 0)
                    mFontSize = i + 14;
                    mTextPreview.setTextSize(mFontSize);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });

            // LISTENER FOR INPUT TEXT TO WRITE MOTIVATIONAL WORDS TO YOURSELF
            mText = mAddTextDialog.findViewById(R.id.textCanvas);
            mText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // GET TEXT WHICH WILL BE PUT ON CANVAS AND SHOW IT IN PREVIEW SECTION
                    String mTextString = mText.getText().toString();
                    mTextPreview.setText(mTextString);
                }

                @Override
                public void afterTextChanged(Editable editable) { }
            });


            // DONE WITH DIALOG FOR TEXT ADD
            Button btnAddTextToCanvas = mAddTextDialog.findViewById(R.id.btnAddText2);
            btnAddTextToCanvas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // SET ALL SELECTED TO TEXT ON CANVAS
                    mTextCanvas.setText(mText.getText().toString());
                    mTextCanvas.setTextSize(mFontSize);
                    mTextCanvas.setTypeface(Typeface.create(mFontFamilyName, Typeface.NORMAL));
                    mTextCanvas.setTextColor(getResources().getColor(mFontColorID));
                    mTextCanvas.setVisibility(View.VISIBLE);

                    // CLOSE DIALOG
                    mAddTextDialog.dismiss();
                }
            });

        }
    };

    // SPINNER (SELECT FONT FAMILY) LISTENER
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // GET SELECTED FONT FAMILY
        String item = parent.getItemAtPosition(position).toString();

        // SET FONT FAMILY NAME OF PREVIEW TEXT AND SAVE THAT FONT FAMILY NAME
        switch (item) {
            case "sans-serif":
                mTextPreview.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
                mFontFamilyName = "sans-serif";
                break;
            case "serif":
                mTextPreview.setTypeface(Typeface.create("serif", Typeface.NORMAL));
                mFontFamilyName = "serif";
                break;
            case "monospace":
                mTextPreview.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
                mFontFamilyName = "monospace";
                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }


    private final View.OnClickListener handleClickChooseColor = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // SAVE FONT COLOR ID

            for (int i = 0; i < colors.length; i++) {
                if (view.getId() == i) {
                    // IF BUTTON IS CLICKED, SAVE ITS COLOR ID
                    mTextPreview.setTextColor(getResources().getColor(colors[i]));
                    mFontColorID = colors[i];
                }
            }

        }
    };

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
                    add_image(myImage);
                }
            });


            Button btn2 = (Button) mAddImageDialog.findViewById(R.id.btnImage2);
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    add_image(myImage2);
                }
            });

            Button btn3 = (Button) mAddImageDialog.findViewById(R.id.btnImage3);
            btn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    add_image(myImage3);
                }
            });


        }
    };


    void add_image(Drawable img) {

        if (index < (maxImageNum - 1)) {
            index++;
            mFlagAddImage = true;
            mCustomDrawableView.setmImage(img);
            mImage = mCustomDrawableView.getImage(index);
            mCustomDrawableView.invalidate();
            mAddImageDialog.dismiss();
        }


    }

    // TODO: delete touched image
    private View.OnClickListener handleClickRemoveImage = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            if (index > -1) {
                mFlagAddImage = false;
                mFlagTouched = false;
                index--;
                mCustomDrawableView.index = mCustomDrawableView.index - 1;
                mCustomDrawableView.invalidate();
            }

        }
    };

    private View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchedPt.x = (int) event.getX();
                    mTouchedPt.y = (int) event.getY();

                    Point beginPoint;
                    Point endPoint;

                    int i = 0;
                    while (i <= index && (mFlagScale == false && mFlagTouched == false && mFlagRotate == false)) {
                        mImage = mCustomDrawableView.getImage(i);

                        beginPoint = mImage.getmBeginPt();
                        endPoint = mImage.getmEndPt();
                        //int imageWidth = mImage[mImageIndex].getmWidth();
                        //int imageHeight = mImage[mImageIndex].getmHeight();

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

                    Log.d(TAG, "Current mImageIndex: " + currentIndex + ": " + mFlagScale);




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
                    Log.d(TAG, "Current mImageIndex MOVE: " + currentIndex + ": " + mFlagScale);


                    mMovedPt.x = (int) event.getX();
                    mMovedPt.y = (int) event.getY();

                    beginPoint = mImage.getmBeginPt();
                    endPoint = mImage.getmEndPt();
                    int imageWidth = mImage.getmWidth();
                    int imageHeight = mImage.getmHeight();

                    // TODO: add limit on scaling outside the width and height of view
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

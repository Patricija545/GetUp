package pripremazagetup.riteh.hr.pripremazagetup;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.PersistableBundle;
import android.provider.MediaStore;
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
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String TAG = this.getClass().getSimpleName();

    CustomDrawableView mCustomDrawableView;
    Image mImage;
    Dialog mAddImageDialog, mAddTextDialog;
    TextView mTextCanvas;
    TextView mTextPreview;
    Spinner spinnerFontFamily;
    String mFontFamilyName;

    private int maxImageNum = 9;
    Point mTouchedPt = new Point(0,0);

    public static boolean mFlagTouched = false;
    boolean mFlagScale = false;
    boolean mFlagDialogFirstOpen = true;
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    private int currentIndex = 0;
    private int mFontSize = 18;
    private int mImgNum = 0;
    private int mFontColorID = R.color.white;
    private int colors[] = {R.color.white, R.color.red, R.color.orange, R.color.yellow, R.color.green, R.color.turquoise, R.color.lightBlue, R.color.darkBlue, R.color.purple, R.color.pink};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // INITIALIZE VIEWS
        LinearLayout mLinearLayout = findViewById(R.id.drawLayout);
        Button mBtnAddImage = findViewById(R.id.btnAdd);
        Button mBtnDeleteImage = findViewById(R.id.btnDelete);
        Button mBtnAddText = findViewById(R.id.btnText);
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
        mLinearLayout.setOnTouchListener(handleTouchCanvas);
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
           EditText  mText = mAddTextDialog.findViewById(R.id.textCanvas);
            mText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // GET TEXT WHICH WILL BE PUT ON CANVAS AND SHOW IT IN PREVIEW SECTION
                    String mTextString = charSequence.toString();
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
                    mTextCanvas.setText(mTextPreview.getText().toString());
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
            mAddImageDialog.show();

            // GET IMAGE FROM GALLERY
            Button imageFromGallery = (Button) mAddImageDialog.findViewById(R.id.imageFromGallery);
            imageFromGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mImgNum < maxImageNum) {
                        get_image_from_phone_gallery();
                    }
                    else {
                        Toast.makeText(getApplication(), "Maximum number of images is " + maxImageNum,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    };

    // ADD IMAGE TO CANVAS
    void add_image(Drawable img) {
        mCustomDrawableView.setmImage(img);
        mImage = mCustomDrawableView.getImage(mImgNum);
        mImgNum ++;
        mCustomDrawableView.invalidate();
        mAddImageDialog.dismiss();
    }

    void get_image_from_phone_gallery() {
        /*
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
        */

        // CHOOSE IMAGE FROM IMAGE FOLDER
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_PICTURE);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == SELECT_PICTURE)
            {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getImagePath(selectedImageUri);

                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    Drawable myImage = Drawable.createFromStream(inputStream, selectedImageUri.toString() );
                    add_image(myImage);

                } catch (FileNotFoundException e) { }

            }
        }
    }

    public String getImagePath(Uri uri) {

        if( uri == null ) {
            Toast.makeText(this, "Can't get that image, sorry. :( Please try another one.",
                    Toast.LENGTH_LONG).show();
            return null;
        }

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }

        return uri.getPath();
    }

    private View.OnClickListener handleClickRemoveImage = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (mImgNum > 0) {
                mFlagTouched = false;
                mCustomDrawableView.deleteImage(currentIndex);
                mImgNum--;
                mCustomDrawableView.invalidate();
            }

        }
    };

    boolean imageTouched(Image img) {
        Point beginPoint = img.getmBeginPt();
        Point endPoint = img.getmEndPt();

        if ((mTouchedPt.x > beginPoint.x && mTouchedPt.y > beginPoint.y)
                && (mTouchedPt.x < endPoint.x && mTouchedPt.y < endPoint.y))
        {
            mImage = img;
            return true;
        }
        return false;
    }

    boolean imageScale (Image img ) {
        Point endPoint = img.getmEndPt();

        if (mTouchedPt.x > (endPoint.x - 50) && mTouchedPt.y > (endPoint.y - 50)
                && mTouchedPt.x < (endPoint.x + 50) && mTouchedPt.y < (endPoint.y + 50))
        {
            mImage = img;
            return true;
        }

        return  false;
    }

    void moveImage (Point movedPt) {
        Point beginPoint = mImage.getmBeginPt();
        Point endPoint = mImage.getmEndPt();

        Point mDifferencePt = new Point((movedPt.x - mTouchedPt.x), (movedPt.y - mTouchedPt.y));

        int testBeginX = beginPoint.x + mDifferencePt.x;
        int testBeginY = beginPoint.y + mDifferencePt.y;

        int imageWidth = mImage.getmWidth();
        int imageHeight = mImage.getmHeight();

        // IF INSIDE CANVAS
        if ((testBeginX  >= 0) && (testBeginY >= 0)
                && ((testBeginX + imageWidth) < mCustomDrawableView.mCanvasWidth)
                && ((testBeginY + imageHeight) < mCustomDrawableView.mCanvasHeight)) {

            mImage.setmBeginPt(new Point(testBeginX, testBeginY));
            mImage.setmEndPt(new Point(endPoint.x + mDifferencePt.x, endPoint.y + mDifferencePt.y));
            mImage.setImageBounds();
            mImage.setmScaleRect();
            //mImage.setmDeleteRect();

            mCustomDrawableView.invalidate();

            // IMPORTANT FOR SMOOTH AND PRECISE MOVING OF IMAGE
            mTouchedPt.x = movedPt.x;
            mTouchedPt.y = movedPt.y;
        }
    }

    void scaleImage(Point movedPt) {
        Point beginPoint = mImage.getmBeginPt();

        // IF RECT FOR SCALING IS STILL TOUCHED
        if ((movedPt.x - beginPoint.x) > 100
                && (movedPt.y - beginPoint.y) > 100
                && (movedPt.x - beginPoint.x) < (mCustomDrawableView.mCanvasWidth - 100)
                && (movedPt.y - beginPoint.y) < (mCustomDrawableView.mCanvasHeight - 100))
        {
            Point endPoint = new Point(movedPt.x, movedPt.y);
            mImage.setmEndPt(endPoint);
            mImage.setmWidth(endPoint.x - beginPoint.x);
            mImage.setmHeight(endPoint.y - beginPoint.y);
            mImage.setImageBounds();
            mImage.setmScaleRect();
            //mImage.setmDeleteRect();
            mCustomDrawableView.invalidate();

        }
    }

    int getCurrentIndex() {
        return currentIndex;
    }

    void setCurrentIndex(int index) {
        currentIndex = index;
    }

    private View.OnTouchListener handleTouchCanvas = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchedPt.x = (int) event.getX();
                    mTouchedPt.y = (int) event.getY();

                    // CHECK WHICH IMAGE IS TOUCHED
                    int imgNum = mCustomDrawableView.imageBuffer.size();
                    ArrayList<Image> mImageArray = mCustomDrawableView.getImageBuffer();

                    for (int i = (imgNum-1); i >= 0; i--) {

                        // IMAGE IS TOUCHED
                        if (imageTouched(mImageArray.get(i))) {
                            mFlagTouched = true;
                            setCurrentIndex(i);
                            mCustomDrawableView.invalidate();
                            break;
                        }
                        // RECT FOR SCALE IS TOUCHED
                        else if (imageScale(mImageArray.get(i))) {
                            mFlagScale = true;
                            setCurrentIndex(i);
                            break;
                        }
                        // TOUCHED SOMEWHERE ON THE CANVAS
                        else {
                            mFlagTouched = false;
                            mFlagScale = false;
                            mCustomDrawableView.invalidate();
                        }
                    }

                    break;

                case MotionEvent.ACTION_MOVE:
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    // MOVE IMAGE
                    if (mFlagTouched) { moveImage(new Point(x, y)); }
                    // SCALE IMAGE
                    else if (mFlagScale){ scaleImage(new Point(x,y));}

                    break;


                case MotionEvent.ACTION_UP:

                    if (mFlagTouched) {
                        mCustomDrawableView.putTouchedImageFirst(getCurrentIndex());
                    }

                    mFlagTouched = false;
                    mFlagScale = false;

                    break;
            }

            return true;
        }
    };



}

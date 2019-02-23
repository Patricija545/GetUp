package pripremazagetup.riteh.hr.pripremazagetup;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String TAG = this.getClass().getSimpleName();

    CustomDrawableView mCustomDrawableView;
    Object mObjectFromCanvas = null;
    Bitmap mImageTestBitmap;
    Dialog mAddImageDialog, mAddTextDialog, mAddImageGoogleDialog;
    TextView mTextPreview;
    Spinner spinnerFontFamily;
    String mFontFamilyName;

    private int maxImageNum = 9;
    private int maxTextNum = 9;
    Point mTouchedPt = new Point(0,0);

    boolean mFlagTouched = false;
    public static boolean mFlagCanvasClicked = false;
    boolean mFlagScale = false;
    boolean mFlagDelete = false;
    boolean mFlagDialogFirstOpen = true;
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    private int currentIndex = 0;
    private int mFontSize = 18;
    private int mImgNum = 0;
    private int mTextNum = 0;
    private int mFontColorID = R.color.white;
    private int colors[] = {R.color.white, R.color.red, R.color.orange, R.color.yellow, R.color.green, R.color.turquoise, R.color.lightBlue, R.color.darkBlue, R.color.purple, R.color.pink};
    static private int imagesFromGoogleNum = 16;

    private ArrayList<Bitmap> mImagesBitmap = new ArrayList<>();

    static ArrayList<String> imagesFromGoogle;
    ProgressBar imageSearchProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagesFromGoogle = new ArrayList<>();

        // INITIALIZE VIEWS
        LinearLayout mLinearLayout = findViewById(R.id.drawLayout);
        Button mBtnAddImage = findViewById(R.id.btnAdd);
        Button mBtnDeleteImage = findViewById(R.id.btnDelete);
        Button mBtnAddText = findViewById(R.id.btnText);
        mAddImageDialog = new Dialog(this,android.R.style.Theme_Light_NoTitleBar);//ThemeOverlay_Material_Dark); //Theme_Black_NoTitleBar_Fullscreen);
        mAddTextDialog = new Dialog(this,android.R.style.Theme_Light_NoTitleBar);
        mAddImageGoogleDialog = new Dialog(this);

        mAddTextDialog.setContentView(R.layout.dialog_add_text);
        mCustomDrawableView = new CustomDrawableView(this);
        //mTextCanvas = findViewById(R.id.tvTextCanvas);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mImagesBitmap.size() > 0) {
            outState.putParcelableArrayList("bitmap", mImagesBitmap);
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {

            mImagesBitmap = savedInstanceState.getParcelableArrayList("bitmap");

            mImgNum = mImagesBitmap.size();
            for (int i = 0; i < mImgNum; i++) {
                mCustomDrawableView.setmImage(new BitmapDrawable(getResources(), mImagesBitmap.get(i)), mImagesBitmap.get(i));
                mCustomDrawableView.invalidate();
            }

        }

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
                mTextPreview.setTextColor(getResources().getColor(R.color.white));

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
            SeekBar seekBarFontSize = mAddTextDialog.findViewById(R.id.seekbar_font_size);
            seekBarFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    // MINIMUM FONT SIZE IZ 14 (MINIMUM OF SEEKBAR IS 0)
                    mFontSize = i + 8;
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
                String beforeChanged;
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    beforeChanged = charSequence.toString();
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // GET TEXT WHICH WILL BE PUT ON CANVAS AND SHOW IT IN PREVIEW SECTION
                    String textString = charSequence.toString();

                    int lines = 0;
                    for (int z = 0; z < textString.length(); z++) {
                        char c = textString.charAt(z);
                        if (c == '\n') {
                            lines++;
                        }
                    }

                    if (lines >= 4){
                        mText.setText(beforeChanged);
                        mText.setSelection(beforeChanged.length()-1);
                        showToast("Maximum number of lines is 4. You can split your text in more parts by adding another text for second part. :)");
                    }
                    else if (textString.length() == 95) {
                        showToast("Maximum number of characters is 100. You have 5 characters left.");
                    }
                    else if (textString.length() == 100) {
                        showToast("Maximum number of characters is 100. You can split your text in more parts by adding another text for second part. :)");
                    }
                    else {
                        mTextPreview.setText(textString);
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) { }
            });





            // DONE WITH DIALOG FOR TEXT ADD
            Button btnAddTextToCanvas = mAddTextDialog.findViewById(R.id.btnAddText2);
            btnAddTextToCanvas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String text = mTextPreview.getText().toString();
                    Paint p = new Paint();
                    float vel = p.measureText(text);
                    int color = getResources().getColor(mFontColorID);

                    if (mTextNum < maxTextNum) {
                        add_text(text, color, (mFontSize + 30), mFontFamilyName, new Point(100, 100));

                    /*
                    // SET ALL SELECTED TO TEXT ON CANVAS
                    mTextCanvas.setText(mTextPreview.getText().toString());
                    mTextCanvas.setTextSize(mFontSize);
                    mTextCanvas.setTypeface(Typeface.create(mFontFamilyName, Typeface.NORMAL));
                    mTextCanvas.setTextColor(getResources().getColor(mFontColorID));
                    mTextCanvas.setVisibility(View.VISIBLE);
                    */
                    }
                    else {
                        showToast("Maximum number of texts is " + maxImageNum);
                    }

                    // CLOSE DIALOG
                    mAddTextDialog.dismiss();

                    // SET
                    mText.setText("");
                    spinnerFontFamily.setSelection(0);
                    mFontFamilyName = "sans-serif";
                    seekBarFontSize.setProgress(18);
                    mTextPreview.setTextColor(getResources().getColor(R.color.white));

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


    // ADD TEXT TO CANVAS
    void add_text(String text, int color, int fontSize, String fontFamily, Point beginPt) {
        Log.d(TAG, "size of text: " + text.length());
        mCustomDrawableView.setText(text, color, fontSize, fontFamily, beginPt);
        mTextNum++;
        mCustomDrawableView.invalidate();
        mAddImageDialog.dismiss();
        //mImagesBitmap.add(imgBitmap);
    }

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
                        showToast("Maximum number of images is " + maxImageNum);
                    }
                }
            });

            Button imageFromGoogle = (Button) mAddImageDialog.findViewById(R.id.imageFromGoogle);
            imageFromGoogle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mImgNum < maxImageNum) {
                        mAddImageDialog.setContentView(R.layout.dialog_add_image_google);
                        get_image_from_google();
                    }
                    else {
                        showToast("Maximum number of images is " + maxImageNum);
                    }
                }
            });

        }
    };


    // ADD IMAGE TO CANVAS
    void add_image(Drawable img, Bitmap imgBitmap) {

        Log.d(TAG, "size drawable, height: " + img.getIntrinsicHeight() + ", width: " + img.getIntrinsicWidth());
        Log.d(TAG, "size bitmap, height: " + imgBitmap.getHeight() + ", width: " + imgBitmap.getWidth());

        mCustomDrawableView.setmImage(img, imgBitmap);
        Image image = (Image) mCustomDrawableView.objectBuffer.get(currentIndex);
        //image.setmWidth(500);//(img.getIntrinsicWidth());
        //image.setmHeight(500);//(img.getIntrinsicHeight());

        mImgNum ++;
        mCustomDrawableView.invalidate();
        mAddImageDialog.dismiss();
        mImagesBitmap.add(imgBitmap);
    }

    void get_image_from_google() {
        EditText imageName = mAddImageDialog.findViewById(R.id.imageName);
        imageSearchProgress = mAddImageDialog.findViewById(R.id.progressBar2);
        imageSearchProgress.setVisibility(View.INVISIBLE);

        Button search = mAddImageDialog.findViewById(R.id.btnSearch);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!imageName.getText().toString().equals("")) {
                    imagesFromGoogle.clear();
                    imageSearchProgress.setVisibility(View.VISIBLE);
                    LinearLayout showImages = mAddImageDialog.findViewById(R.id.linearLayout);
                    showImages.removeAllViews();


                    String search_url = "https://pixabay.com/en/photos/" + //"https://www.google.com/search?site=imghp&tbm=isch&q=" +
                            imageName.getText().toString();
                    get_search_images search_task = new get_search_images(MainActivity.this, search_url);
                    search_task.execute();
                }
            }
        });

    }

    private static class get_search_images extends AsyncTask<Void, Void, Void> {
        private WeakReference<MainActivity> activityWeakRef;
        private String url;

        private Context mContext;

        get_search_images(MainActivity context, String url) {
            this.activityWeakRef = new WeakReference<>(context);
            this.url = url;

            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            getImages(this.url);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            MainActivity activity = activityWeakRef.get();
            LinearLayout showImages = activity.mAddImageDialog.findViewById(R.id.linearLayout);


            if (imagesFromGoogle.size() > 10) {
                for (int i = 0; i < imagesFromGoogle.size(); i = i + 2) {

                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            0.5f
                    );
                    param.gravity = Gravity.CENTER;

                    LinearLayout ll = new LinearLayout(activity.getBaseContext());
                    ll.setOrientation(LinearLayout.HORIZONTAL);
                    ll.setWeightSum(1);
                    ll.setPadding(0,5, 0, 5);

                    ImageView image1 = new ImageView(activity.getBaseContext());
                    image1.setLayoutParams(param);
                    Picasso.with(mContext).load(imagesFromGoogle.get(i)).into(image1);

                    ImageView image2 = new ImageView(activity.getBaseContext());
                    image2.setLayoutParams(param);
                    Picasso.with(mContext).load(imagesFromGoogle.get(i+1)).into(image2);

                    ll.addView(image1);
                    ll.addView(image2);

                    showImages.addView(ll);

                    image1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Drawable drawable = image1.getDrawable();
                            Bitmap bitmap = ((BitmapDrawable)image1.getDrawable()).getBitmap();
                            activity.add_image(drawable, bitmap);
                            activity.mCustomDrawableView.invalidate();
                            activity.mAddImageDialog.dismiss();

                        }
                    });

                    image2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Drawable drawable = image2.getDrawable();
                            Bitmap bitmap = ((BitmapDrawable)image2.getDrawable()).getBitmap();
                            activity.add_image(drawable, bitmap);
                            activity.mCustomDrawableView.invalidate();
                            activity.mAddImageDialog.dismiss();

                        }
                    });

                }
            }
            else {
                Toast.makeText(activity, "I'm sorry but we can't find that kind of image. Please use english words and try to be more concrete. :)", Toast.LENGTH_LONG).show();
            }


            //TextView textView = activity.mAddImageDialog.findViewById(R.id.textView5);
            //textView.setVisibility(View.GONE);

            ProgressBar imageSearchProgress = activity.mAddImageDialog.findViewById(R.id.progressBar2);
            imageSearchProgress.setVisibility(View.INVISIBLE);

        }
    }

    private static void getImages(String url) {
        // JSOUP - JAVA HTML PARSER
        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    //.userAgent("Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36")
                    .get();
        } catch (IOException e) { }

        if (doc != null) {
            imagesFromGoogle.clear();

            Elements imgs = doc.select("img");
            int i = 0;
            for (Element img : imgs) {
                String img_url = img.attr("src");
                if (!(img_url.isEmpty()) &&  i < imagesFromGoogleNum) {
                    imagesFromGoogle.add(img_url);
                    i++;
                }
            }
        }
    }

    void get_image_from_phone_gallery() {
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
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    //bitmap = Bitmap.createScaledBitmap(bitmap, 400, 600,true);
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    add_image(drawable, bitmap);

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
            int objectNum = mCustomDrawableView.objectBuffer.size();
            if (objectNum > 0) {

                try {
                    mFlagTouched = false;
                    mCustomDrawableView.deleteObject(currentIndex);
                    mImgNum--;
                    mCustomDrawableView.invalidate();
                }
                catch (Exception e) {
                    showToast("Please first click on object you want to delete");
                }
            }
        }
    };

    boolean objectTouch(Object objectFromCanvas) {

        if (objectFromCanvas instanceof Image) {
            Image img = (Image)objectFromCanvas;

            Point beginPoint = img.getmBeginPt();
            Point endPoint = img.getmEndPt();

            if ((mTouchedPt.x > beginPoint.x && mTouchedPt.y > beginPoint.y)
                    && (mTouchedPt.x < endPoint.x && mTouchedPt.y < endPoint.y))
            {
                mObjectFromCanvas = img;
                return true;
            }
        }
        else if (objectFromCanvas instanceof  MyText){
            MyText text = (MyText) objectFromCanvas;

            Point beginPoint = text.getBeginPt();
            Point endPoint = text.getEndPt();

            if ((mTouchedPt.x >= (beginPoint.x - 100) && mTouchedPt.y >= (beginPoint.y - 100))
                    && (mTouchedPt.x <= endPoint.x && mTouchedPt.y <= endPoint.y))
            {
                mObjectFromCanvas = text;
                return true;
            }
        }

        return false;
    }


    boolean objectScale(Object objectFromCanvas) {

        if (objectFromCanvas instanceof Image) {
            Image img = (Image) objectFromCanvas;
            Point endPoint = img.getmEndPt();

            if (mTouchedPt.x > (endPoint.x - 50) && mTouchedPt.y > (endPoint.y - 50)
                    && mTouchedPt.x < (endPoint.x + 50) && mTouchedPt.y < (endPoint.y + 50))
            {
                mObjectFromCanvas = img;
                return true; // !!!
            }
        }

        // SCALE OF TEXT?


        return  false;
    }

    boolean objectDelete(Object objectFromCanvas, int index) {
        if (objectFromCanvas instanceof Image) {
            Image img = (Image) objectFromCanvas;
            Point beginPoint = img.getmBeginPt();
            Point endPoint = img.getmEndPt();

            if (mTouchedPt.x > (endPoint.x - 50) && mTouchedPt.y > (beginPoint.y - 50)
                    && mTouchedPt.x < (endPoint.x + 50) && mTouchedPt.y < (beginPoint.y + 50))
            {
                //mObjectFromCanvas = img;

                int objectNum = mCustomDrawableView.objectBuffer.size();
                if (objectNum > 0)
                {
                    mFlagTouched = false;
                    mCustomDrawableView.deleteObject(index);
                    mImgNum--;
                    mCustomDrawableView.invalidate();
                }
                return true; // !!!

            }
        }
        if (objectFromCanvas instanceof MyText) {
            MyText text = (MyText) objectFromCanvas;
            Point beginPoint = text.getBeginPt();
            Point endPoint = text.getEndPt();

            //mDeleteRect.setBounds(mBeginPt.x - 100, mBeginPt.y - 100, mBeginPt.x, mBeginPt.y);
            if ((mTouchedPt.x >= (beginPoint.x - 100) && mTouchedPt.y >= (beginPoint.y - 100))
                    && (mTouchedPt.x <= beginPoint.x && mTouchedPt.y <= beginPoint.y))
            {
                //mObjectFromCanvas = img;

                int objectNum = mCustomDrawableView.objectBuffer.size();
                if (objectNum > 0)
                {
                    mFlagTouched = false;
                    mCustomDrawableView.deleteObject(index);
                    mTextNum--;
                    mCustomDrawableView.invalidate();
                }
                return true; // !!!

            }
        }


        return false;
    }


    void moveObject (Point movedPt) {
        if (mObjectFromCanvas instanceof Image) {
            Image img = (Image) mObjectFromCanvas;
            Point beginPoint = img.getmBeginPt();
            Point endPoint = img.getmEndPt();
            Point mDifferencePt = new Point((movedPt.x - mTouchedPt.x), (movedPt.y - mTouchedPt.y));

            int testBeginX = beginPoint.x + mDifferencePt.x;
            int testBeginY = beginPoint.y + mDifferencePt.y;
            int imageWidth = img.getmWidth();
            int imageHeight = img.getmHeight();

            // IF INSIDE CANVAS
            if ((testBeginX  >= 0) && (testBeginY >= 0)
                    && ((testBeginX + imageWidth) < mCustomDrawableView.mCanvasWidth)
                    && ((testBeginY + imageHeight) < mCustomDrawableView.mCanvasHeight)) {

                img.setmBeginPt(new Point(testBeginX, testBeginY));
                img.setmEndPt(new Point(endPoint.x + mDifferencePt.x, endPoint.y + mDifferencePt.y));
                img.setImageBounds();
                img.setmScaleRect();
                img.setmDeleteRect();

                mCustomDrawableView.invalidate();

                // IMPORTANT
                mTouchedPt.x = movedPt.x;
                mTouchedPt.y = movedPt.y;
            }
        }

        else if (mObjectFromCanvas instanceof MyText) {
            MyText text = (MyText) mObjectFromCanvas;
            Point beginPoint = text.getBeginPt();
            Point endPoint = text.getEndPt();
            Point mDifferencePt = new Point((movedPt.x - mTouchedPt.x), (movedPt.y - mTouchedPt.y));

            int testBeginX = beginPoint.x + mDifferencePt.x;
            int testBeginY = beginPoint.y + mDifferencePt.y;

            int testEndX = endPoint.x + mDifferencePt.x;
            int testEndY = endPoint.y + mDifferencePt.y;
            float textSize = text.getPaint().getTextSize();


            // IF INSIDE CANVAS

            if ((testBeginX  >= 0) && (testBeginY >= 0)
                    && (testEndX < mCustomDrawableView.mCanvasWidth)
                    && (testEndY < mCustomDrawableView.mCanvasHeight))
            {
                text.setBeginPt(new Point(testBeginX, testBeginY));
                text.setEndPt(new Point(endPoint.x + mDifferencePt.x, endPoint.y + mDifferencePt.y));
                text.setMoveRect();
                text.setDeleteRect();

                Object textObject = text;
                mCustomDrawableView.objectBuffer.set(currentIndex, textObject);

                mCustomDrawableView.invalidate();
            }

            // IMPORTANT
            mTouchedPt.x = movedPt.x;
            mTouchedPt.y = movedPt.y;
            }

    }

    void scaleObject(Point movedPt) {

        if (mObjectFromCanvas instanceof Image) {
            Image img = (Image) mObjectFromCanvas;
            Point beginPoint = img.getmBeginPt();

            // IF RECT FOR SCALING IS STILL TOUCHED
            if ((movedPt.x - beginPoint.x) > 100
                    && (movedPt.y - beginPoint.y) > 100
                    && (movedPt.x - beginPoint.x) < (mCustomDrawableView.mCanvasWidth - 100)
                    && (movedPt.y - beginPoint.y) < (mCustomDrawableView.mCanvasHeight - 100))
            {
                Point endPoint = new Point(movedPt.x, movedPt.y);
                img.setmEndPt(endPoint);
                img.setmWidth(endPoint.x - beginPoint.x);
                img.setmHeight(endPoint.y - beginPoint.y);
                img.setImageBounds();
                img.setmScaleRect();
                img.setmDeleteRect();
                mCustomDrawableView.invalidate();

            }

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

                    // CHECK WHICH OBJECT IS TOUCHED
                    int objectNum = mCustomDrawableView.objectBuffer.size();
                    ArrayList<Object> objectBuffer = mCustomDrawableView.getObjectBuffer();

                    for (int i = (objectNum-1); i >= 0; i--) {

                        // RECT FOR DELETE IS TOUCHED
                        if (objectDelete(objectBuffer.get(i),i)) { break; }
                        // OBJECT IS TOUCHED
                        else if (objectTouch(objectBuffer.get(i))) {
                            mFlagTouched = true;
                            setCurrentIndex(i);
                            //mCustomDrawableView.invalidate();
                            break;
                        }
                        // RECT FOR SCALE IS TOUCHED
                        else if (objectScale(objectBuffer.get(i))) {
                            mFlagScale = true;
                            setCurrentIndex(i);
                            break;
                        }
                        // TOUCHED SOMEWHERE ON THE CANVAS
                        else {
                            mFlagTouched = false;
                            mFlagScale = false;
                            mFlagDelete = false;
                            mCustomDrawableView.invalidate();
                        }
                    }

                    break;

                case MotionEvent.ACTION_MOVE:
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    // MOVE OBJECT
                    if (mFlagTouched) { moveObject(new Point(x, y)); }
                    // SCALE OBJECT
                    else if (mFlagScale){ scaleObject(new Point(x,y));}

                    break;


                case MotionEvent.ACTION_UP:

                    if (mFlagTouched) {
                       mCustomDrawableView.putTouchedObjectFirst(getCurrentIndex());
                       mCustomDrawableView.invalidate();
                        mFlagCanvasClicked = false;
                    }
                    else {
                        mCustomDrawableView.invalidate();
                        mFlagCanvasClicked = true;
                    }

                    // VERY IMPORTANT FOR SCALE TO WORK
                    mFlagTouched = false;
                    mFlagScale = false;

                    break;
            }

            return true;
        }
    };


    void showToast(String text) {
        Toast.makeText(getApplication(), text, Toast.LENGTH_LONG).show();
    }

}

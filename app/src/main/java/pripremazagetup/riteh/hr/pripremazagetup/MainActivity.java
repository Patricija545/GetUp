package pripremazagetup.riteh.hr.pripremazagetup;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
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
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String TAG = this.getClass().getSimpleName();

    public static boolean mFlagCanvasClicked = false;
    private static int imagesFromGoogleNum = 16;
    private static final int SELECT_PICTURE = 1;

    private int maxImageNum = 9;
    private int maxTextNum = 9;
    private int mImgNum = 0;
    private int mTextNum = 0;

    private boolean mFlagTouched = false;
    private boolean mFlagScale = false;
    private boolean mFlagDialogFirstOpen = true;

    private Point mTouchedPt = new Point(0,0);

    private String selectedImagePath;
    private int currentIndex = 0;
    private int mFontSize = 18;
    private int mFontColorID = R.color.black;


    private int colors[] = {R.color.black, R.color.white, R.color.red, R.color.orange, R.color.yellow, R.color.green, R.color.lightBlue, R.color.darkBlue, R.color.purple, R.color.pink};

    //private ArrayList<Bitmap> mImagesBitmap = new ArrayList<>();
    private static ArrayList<String> imagesFromGoogle = new ArrayList<>();

    private ProgressBar imageSearchProgress;
    private CustomDrawableView mCustomDrawableView;
    private Dialog mAddImageDialog, mAddTextDialog;
    private TextView mTextPreview;
    private Spinner spinnerFontFamily;
    private Object mObjectFromCanvas = null;
    private String mFontFamilyName;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SET ORIENTATION TO PORTRET
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // INITIALIZE VIEWS
        ConstraintLayout mLinearLayout = findViewById(R.id.drawLayout);
        FloatingActionButton fabAddImage = findViewById(R.id.fab_add_image);
        FloatingActionButton fabAddText = findViewById(R.id.fab_add_text);
        FloatingActionButton fabSave = findViewById(R.id.fab_save);
        FloatingActionButton fabHelp = findViewById(R.id.fab_help);
        mAddImageDialog = new Dialog(this,android.R.style.Theme_Light_WallpaperSettings);//ThemeOverlay_Material_Dark); //Theme_Black_NoTitleBar_Fullscreen);
        mAddTextDialog = new Dialog(this,android.R.style.Theme_Light_Panel);
        mAddTextDialog.setContentView(R.layout.dialog_add_text);
        mCustomDrawableView = new CustomDrawableView(this);
        spinnerFontFamily = mAddTextDialog.findViewById(R.id.spinnerFontFamily);
        spinnerFontFamily.setOnItemSelectedListener(this);

        // ADD VIEWS TO LAYOUT
        mLinearLayout.addView(mCustomDrawableView);
        mCustomDrawableView.invalidate();

        // LISTENERS
        mLinearLayout.setOnTouchListener(handleTouchCanvas);
        fabAddImage.setOnClickListener(handleClickAddImage);
        fabAddText.setOnClickListener(handleClickAddText);

        Dialog helpDialog = new Dialog(this, android.R.style.Theme_Light_NoTitleBar);

        fabHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpDialog.setContentView(R.layout.dialog_help);
                helpDialog.show();

                Button btnExit = helpDialog.findViewById(R.id.btnExit);
                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        helpDialog.dismiss();
                    }
                });
            }
        });

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { saveImage(); }
        });

    }

    // SAVE IMAGE TO EXTERNAL STORAGE
    void saveImage() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        Bitmap img = getBitmapFromView(mCustomDrawableView);
        saveImageToExternalStorage(img);
        showToast("Image saved in the gallery.");
    }
    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }
    private void saveImageToExternalStorage(Bitmap finalBitmap) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root  + "/GetUp");
        myDir.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentTimeStamp = dateFormat.format(new Date());
        String fname = "GETUP_" + currentTimeStamp + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // NEEDED FOR KITKAT (19-20) AND NEWER
        MediaScannerConnection.scanFile(this, new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    // ALL FOR DIALOG FOR ADDING TEXT
    private final View.OnClickListener handleClickAddText = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            add_text_dialog(null);
        }
    };

    void add_text_dialog(MyText textChange) {

        if (mTextNum < maxTextNum || textChange!= null) {
            // SHOW DIALOG FOR ADDING TEXT
            mAddTextDialog.show();
        }
        else {
            showToast("Maximum number of texts is " + maxTextNum);
        }

        // INITIALIZE VIEWS
        mTextPreview = mAddTextDialog.findViewById(R.id.previewText);
        LinearLayout layoutColors = mAddTextDialog.findViewById(R.id.llayoutcolor);
        SeekBar seekBarFontSize = mAddTextDialog.findViewById(R.id.seekbar_font_size);
        EditText  mText = mAddTextDialog.findViewById(R.id.textCanvas);
        Button btnAddTextToCanvas = mAddTextDialog.findViewById(R.id.btnAddText2);

        if (textChange != null) {
            mText.setText(textChange.getText());
            int size = (int)(textChange.getPaint().getTextSize() - 38);
            seekBarFontSize.setProgress(size);

            mTextPreview.setTextColor(textChange.getPaint().getColor());
            mTextPreview.setText(textChange.getText());
            mTextPreview.setTextSize(textChange.getPaint().getTextSize() - 30);

            mFontFamilyName = textChange.getFontFamily();
            if (mFontFamilyName.equals("sans-serif")) spinnerFontFamily.setSelection(0);
            else if (mFontFamilyName.equals("serif")) spinnerFontFamily.setSelection(1);
            else if (mFontFamilyName.equals("monospace")) spinnerFontFamily.setSelection(2);

            btnAddTextToCanvas.setText("Change");
        }
        else {
            // SET
            mText.setText("");
            seekBarFontSize.setProgress(8);
            spinnerFontFamily.setSelection(0);
            mFontFamilyName = "sans-serif";
            mFontColorID = R.color.black;
            mTextPreview.setTextColor(getResources().getColor(R.color.black));

            btnAddTextToCanvas.setText("Add");
        }

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

                int lines = 1;
                for (int z = 0; z < textString.length(); z++) {
                    char c = textString.charAt(z);
                    if (c == '\n') {
                        lines++;
                    }
                }
                mTextPreview.setLines(lines);

                if (lines > 4){
                    mText.setText(beforeChanged);
                    mText.setSelection(beforeChanged.length()-1);
                    mTextPreview.setText(beforeChanged);
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
        btnAddTextToCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mTextPreview.getText().toString();
                int color = getResources().getColor(mFontColorID);

                if (textChange != null) {
                    add_text(true, text, color, (mFontSize + 30), mFontFamilyName, textChange.getBeginPt());
                    mCustomDrawableView.deleteObject(currentIndex);
                    mCustomDrawableView.invalidate();
                    mObjectFromCanvas = mCustomDrawableView.getObjectBuffer().get(currentIndex);
                }
                else {
                    add_text(false, text, color, (mFontSize + 30), mFontFamilyName, new Point(100, 100));
                }

                // CLOSE DIALOG
                mAddTextDialog.dismiss();

            }
        });
    }

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

    // BUTTON LISTENER FOR COLOR PICKING
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
    void add_text(boolean change, String text, int color, int fontSize, String fontFamily, Point beginPt) {
        mCustomDrawableView.setText(text, color, fontSize, fontFamily, beginPt);
        mCustomDrawableView.invalidate();
        mAddImageDialog.dismiss();

        if (!change) {
            mTextNum++;
        }
    }

    // EDIT TEXT WHICH IS ALREADY ON CANVAS
    boolean textEdit (MyText text, int index) {
        Point endPoint = text.getEndPt();

        if ((mTouchedPt.x > endPoint.x && mTouchedPt.y > endPoint.y)
                && (mTouchedPt.x < (endPoint.x  + 100) && mTouchedPt.y < (endPoint.y + 100)))
        {
            mObjectFromCanvas = text;
            add_text_dialog(text);
            currentIndex = index;
            return true;
        }

        return false;
    }


    // ADD IMAGE DIALOG
    private View.OnClickListener handleClickAddImage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mAddImageDialog.setContentView(R.layout.dialog_add_image);

            if (mImgNum < maxImageNum) {
                mAddImageDialog.show();
            }
            else {
                showToast("Maximum number of images is " + maxImageNum);
            }

            Button imageFromGallery = mAddImageDialog.findViewById(R.id.imageFromGallery);
            Button imageFromGoogle = mAddImageDialog.findViewById(R.id.imageFromGoogle);

            imageFromGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    get_image_from_phone_gallery();
                }
            });

            imageFromGoogle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        mAddImageDialog.setContentView(R.layout.dialog_add_image_google);
                        get_image_from_google();
                }
            });

        }
    };

    // ADD IMAGE TO CANVAS
    void add_image(Drawable img, Bitmap imgBitmap) {
        mCustomDrawableView.setmImage(img, imgBitmap);
        mImgNum ++;
        mCustomDrawableView.invalidate();
        mAddImageDialog.dismiss();
        //mImagesBitmap.add(imgBitmap);

        if (mImgNum == 1) {
            showToast("Well done. Nice start with defining your goals :)");
        }
        if (mImgNum == maxImageNum) {
            showToast("It seems like you are slowly coming to the end of defining your goals.");
        }
    }

    // IMAGE FROM GOOGLE
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

                    // PIXABAY - FREE IMAGES
                    String search_url = "https://pixabay.com/en/photos/" + //"https://www.google.com/search?site=imghp&tbm=isch&q=" +
                            imageName.getText().toString();
                    showFoundImages search_task = new showFoundImages(MainActivity.this, search_url);
                    search_task.execute();
                }
            }
        });

    }
    private static void getImagesJSOUP(String url) {
        // JSOUP - JAVA HTML PARSER
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
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
    private static class showFoundImages extends AsyncTask<Void, Void, Void> {
        private WeakReference<MainActivity> activityWeakRef;
        private String url;

        private Context mContext;

        showFoundImages(MainActivity context, String url) {
            this.activityWeakRef = new WeakReference<>(context);
            this.url = url;

            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            getImagesJSOUP(this.url);
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

                    // WHEN IMAGEVIEW IS CLICKED, PUT IT ON CANVAS
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


            TextView textView = activity.mAddImageDialog.findViewById(R.id.textView5);
            textView.setVisibility(View.GONE);

            ProgressBar imageSearchProgress = activity.mAddImageDialog.findViewById(R.id.progressBar2);
            imageSearchProgress.setVisibility(View.GONE);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.5f
            );


        }
    }

    // IMAGE FROM PHONE GALLERY
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
                Log.d("ExternalStorage", "pathh: " + selectedImagePath);

                try {
                    // PUT IMAGE IN DRAWABLE AND BITMAP
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
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


    // WHICH OBJECT FOR SCALING
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

        return  false;
    }

    // DELETE
    boolean objectDelete(Object objectFromCanvas, int index) {
        if (objectFromCanvas instanceof Image) {
            Image img = (Image) objectFromCanvas;
            Point beginPoint = img.getmBeginPt();
            Point endPoint = img.getmEndPt();

            if (mTouchedPt.x > (endPoint.x - 50) && mTouchedPt.y > (beginPoint.y - 50)
                    && mTouchedPt.x < (endPoint.x + 50) && mTouchedPt.y < (beginPoint.y + 50))
            {
                int objectNum = mCustomDrawableView.objectBuffer.size();
                if (objectNum > 0)
                {
                    mFlagTouched = false;
                    mCustomDrawableView.deleteObject(index);
//                    mImagesBitmap.remove(index);
                    mImgNum--;
                    mCustomDrawableView.invalidate();
                }
                return true; // IMPORTANT

            }
        }
        if (objectFromCanvas instanceof MyText) {
            MyText text = (MyText) objectFromCanvas;
            Point beginPoint = text.getBeginPt();

            if ((mTouchedPt.x >= (beginPoint.x - 100) && mTouchedPt.y >= (beginPoint.y - 100))
                    && (mTouchedPt.x <= beginPoint.x && mTouchedPt.y <= beginPoint.y))
            {
                int objectNum = mCustomDrawableView.objectBuffer.size();
                if (objectNum > 0)
                {
                    mFlagTouched = false;
                    mCustomDrawableView.deleteObject(index);
                    mTextNum--;
                    mCustomDrawableView.invalidate();
                }
                return true; // IMPORTANT

            }
        }


        return false;
    }

    // MOVE
    void moveObject (Point movedPt) {
        if (mObjectFromCanvas instanceof Image) {
            Image img = (Image) mObjectFromCanvas;
            Point beginPoint = img.getmBeginPt();
            Point endPoint = img.getmEndPt();
            Point mDifferencePt = new Point((movedPt.x - mTouchedPt.x), (movedPt.y - mTouchedPt.y));

            int testBeginX = beginPoint.x + mDifferencePt.x;
            int testBeginY = beginPoint.y + mDifferencePt.y;

            int testEndX = endPoint.x + mDifferencePt.x;
            int testEndY = endPoint.y + mDifferencePt.y;


            // IF INSIDE CANVAS
            if ((testBeginX  >= -10) && (testBeginY >= -10)
                    && (testEndX < (mCustomDrawableView.mCanvasWidth + 10))
                    && (testEndY < (mCustomDrawableView.mCanvasHeight + 10))) {

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

            // IF INSIDE CANVAS
            if ((testBeginX  >= 0) && (testBeginY >= 0)
                    && (testEndX < mCustomDrawableView.mCanvasWidth)
                    && (testEndY < mCustomDrawableView.mCanvasHeight))
            {
                text.setBeginPt(new Point(testBeginX, testBeginY));
                text.setEndPt(new Point(endPoint.x + mDifferencePt.x, endPoint.y + mDifferencePt.y));
                text.setEditRect();
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

    // SCALE
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

    // WHICH OBJECT IS TOUCHED
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

    // DETECT WHICH OBJECT IS TOUCHED AND WHAT PART OF IT (RECT FOR MOVE, SCALE, DELETE,...)
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
                        Object objectFromCanvas = objectBuffer.get(i);

                        // RECT FOR EDIT TEXT IS TOUCHED
                        if ((objectFromCanvas instanceof MyText) && (textEdit((MyText)objectFromCanvas, i))) {
                            currentIndex = i;
                            break;
                        }
                        // RECT FOR DELETE IS TOUCHED
                        else if (objectDelete(objectFromCanvas,i)) { break; }
                        // OBJECT IS TOUCHED
                        else if (objectTouch(objectFromCanvas)) {
                            mFlagTouched = true;
                            currentIndex = i;
                            break;
                        }
                        // RECT FOR SCALE IS TOUCHED
                        else if (objectScale(objectFromCanvas)) {
                            mFlagScale = true;
                            currentIndex = i;
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

                    // MOVE OBJECT
                    if (mFlagTouched) { moveObject(new Point(x, y)); }
                    // SCALE OBJECT
                    else if (mFlagScale){ scaleObject(new Point(x,y));}

                    break;


                case MotionEvent.ACTION_UP:

                    if (mFlagTouched) {
                       mCustomDrawableView.putTouchedObjectFirst(currentIndex);
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

    // TOAST
    public void showToast (String message){
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        mToast.show();
    }

    @Override
    protected void onDestroy() {
        deleteCache(this);
        super.onDestroy();
    }

    // DELETE CACHE OF APPLICATION
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

}

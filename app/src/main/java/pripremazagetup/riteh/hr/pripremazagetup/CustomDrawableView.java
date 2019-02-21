package pripremazagetup.riteh.hr.pripremazagetup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;
import java.util.ArrayList;

public class CustomDrawableView extends View {
    String TAG = getClass().getSimpleName();

    int mCanvasHeight, mCanvasWidth;
    ArrayList<Image> imageBuffer = new ArrayList<>();
    ArrayList<MyText> textBuffer = new ArrayList<>();

    public CustomDrawableView(Context context) {super(context); }

    protected void onDraw(Canvas canvas) {
        //canvas.drawColor(Color.WHITE);
        mCanvasHeight = getHeight();
        mCanvasWidth = getWidth();

        if (imageBuffer.size() > 0) {
            for (int i = 0; i < imageBuffer.size(); i++) {
                imageBuffer.get(i).mImage.draw(canvas);
            }

            imageBuffer.get(imageBuffer.size()-1).mImage.draw(canvas);
            imageBuffer.get(imageBuffer.size()-1).mScaleRect.draw(canvas);
            //imageBuffer.get(imageBuffer.size()-1).mDeleteRect.draw(canvas);

        }

        if (textBuffer.size() > 0) {
            for (int i = 0; i < textBuffer.size(); i++) {
                MyText myText = textBuffer.get(i);
                canvas.drawText(myText.getText(), myText.getBeginPoint().x, myText.getBeginPoint().y, myText.getPaint());
            }

        }

    }

    void setmImage (Drawable image, Bitmap imageBitmap) {
        Image img = new Image(image, imageBitmap);
        imageBuffer.add(img);
    }

    Image getImage(int num) { return imageBuffer.get(num);}

    void setText (String text, int color, int size, String fontFamily) {
        MyText myText = new MyText(text, color, size, fontFamily, new Point(100, 100));
        textBuffer.add(myText);
    }

    MyText getText(int num) { return textBuffer.get(num);}

    void deleteImage(int index) { imageBuffer.remove(index); }

    void putTouchedImageFirst (int index) {
        Image tmp = imageBuffer.get(index);
        imageBuffer.remove(index);
        imageBuffer.add(tmp);
    }

    ArrayList<Image> getImageBuffer() {return imageBuffer;}



}
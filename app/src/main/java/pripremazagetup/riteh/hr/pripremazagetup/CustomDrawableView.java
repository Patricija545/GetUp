package pripremazagetup.riteh.hr.pripremazagetup;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import java.util.ArrayList;

public class CustomDrawableView extends View {
    String TAG = getClass().getSimpleName();

    int mCanvasHeight, mCanvasWidth;
    ArrayList<Image> imageBuffer = new ArrayList<>();

    public CustomDrawableView(Context context) {super(context); }

    protected void onDraw(Canvas canvas) {
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

    }

    void setmImage (Drawable image) {
        Image img = new Image(image);
        imageBuffer.add(img);
    }

    Image getImage(int num) { return imageBuffer.get(num);}

    void deleteImage(int index) { imageBuffer.remove(index); }

    void putTouchedImageFirst (int index) {
        Image tmp = imageBuffer.get(index);
        imageBuffer.remove(index);
        imageBuffer.add(tmp);
    }

    ArrayList<Image> getImageBuffer() {return imageBuffer;}



}
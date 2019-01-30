package pripremazagetup.riteh.hr.pripremazagetup;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import java.util.ArrayList;

public class CustomDrawableView extends View {
    String TAG = getClass().getSimpleName();

    int mCanvasHeight, mCanvasWidth;
    ArrayList<Image> drawOrderImg = new ArrayList<>();

    public CustomDrawableView(Context context) {super(context); }

    protected void onDraw(Canvas canvas) {
        mCanvasHeight = canvas.getHeight();
        mCanvasWidth = canvas.getWidth();


        if (MainActivity.mFlagTouched) {
            for (int i = 0; i < drawOrderImg.size(); i++) {
                drawOrderImg.get(i).mImage.draw(canvas);
                drawOrderImg.get(i).mScaleRect.draw(canvas);
            }
        }

        else {
            for (int i = 0; i < drawOrderImg.size(); i++) {
                drawOrderImg.get(i).mImage.draw(canvas);
            }
        }

    }

    void setmImage (Drawable image) {
        Image img = new Image(image);
        drawOrderImg.add(img);
    }

    Image getImage(int num) { return drawOrderImg.get(num);}

    void deleteImage(int index) { drawOrderImg.remove(index); }

    void putTouchedImageFirst (int index) {
        Image tmp = drawOrderImg.get(index);
        drawOrderImg.remove(index);
        drawOrderImg.add(tmp);
    }



}
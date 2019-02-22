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
    // BOTH TEXT AND IMAGE OBJECTS
    ArrayList<Object> objectBuffer = new ArrayList<>();

    public CustomDrawableView(Context context) {super(context); }

    protected void onDraw(Canvas canvas) {
        //canvas.drawColor(Color.WHITE);
        mCanvasHeight = getHeight();
        mCanvasWidth = getWidth();

        if (objectBuffer.size() > 0) {

            for (int i = 0; i < objectBuffer.size(); i++) {
                if (objectBuffer.get(i) instanceof  Image) {
                    Image img = (Image)objectBuffer.get(i);
                    img.mImage.draw(canvas);
                }

                else if (objectBuffer.get(i) instanceof  MyText) {
                    MyText myText = (MyText) objectBuffer.get(i);

                    String text = myText.getText();
                    String[] lines = text.split("\n");

                    int begin = 0;
                    for (int j = 0; j < lines.length; j++) {
                        canvas.drawText(lines[j], myText.getBeginPt().x, myText.getBeginPt().y + begin, myText.getPaint());
                        begin = begin + (int)myText.getPaint().getTextSize();
                    }
                }
            }

            if (!MainActivity.mFlagCanvasClicked) {
                if (objectBuffer.get(objectBuffer.size()-1) instanceof Image) {
                    Image img = (Image) objectBuffer.get(objectBuffer.size()-1);
                    img.mScaleRect.draw(canvas);
                }
                else {
                    MyText myText = (MyText) objectBuffer.get(objectBuffer.size()-1);
                    myText.getMoveRect().draw(canvas);
                }
            }

        }

    }

    void setmImage (Drawable image, Bitmap imageBitmap) {
        Image img = new Image(image, imageBitmap);
        objectBuffer.add(img);
    }

    void setText (String text, int color, int size, String fontFamily, Point beginPoint) {
        MyText myText = new MyText(text, color, size, fontFamily, beginPoint);
        objectBuffer.add(myText);
    }

    void deleteObject(int index) { objectBuffer.remove(index); }

    void putTouchedObjectFirst (int index) {
        Object tmpObject = objectBuffer.get(index);
        objectBuffer.remove(index);
        objectBuffer.add(tmpObject);
    }

    ArrayList<Object> getObjectBuffer() {return objectBuffer;}



}
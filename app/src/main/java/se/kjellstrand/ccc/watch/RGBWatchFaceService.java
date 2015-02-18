package se.kjellstrand.ccc.watch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;

/**
 * Created by erbsman on 2/13/15.
 */
public class RGBWatchFaceService extends AbstractCCCWatchFaceService {

    @Override
    public Engine onCreateEngine() {
        /* provide your watch face implementation */
        return new Engine();
    }

    private class Engine extends AbstractEngine {

        Paint son = new Paint();
        Paint soff = new Paint();
        Paint mon = new Paint();
        Paint moff = new Paint();
        Paint hon = new Paint();
        Paint hoff = new Paint();

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            son.setColor(0xffff0000);
            soff.setColor(0xff440000);
            mon.setColor(0xff00ff00);
            moff.setColor(0xff004400);
            hon.setColor(0xff0000ff);
            hoff.setColor(0xff000044);
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);

            int width = bounds.width();
            int height = bounds.height();

            // Find the center. Ignore the window insets so that, on round watches
            // with a "chin", the watch face is centered on the entire screen, not
            // just the usable portion.
            int centerScreenX = (int) (width / 2f);
            int centerScreenY = (int) (height / 2f);

            int seconds = time.second;
            int minutes = time.minute;
            int hours = time.hour;

            paintCircles.setColor(Color.RED);
            int boxSize = 36;
            int boxDistance = (int) (boxSize * 0.2);
            Rect rect = new Rect(0, 0, boxSize, boxSize);
            rect.offset(centerScreenX - boxSize / 2, centerScreenY - boxSize / 2);
            rect.offset((int) ((boxSize + boxDistance) * 2.5), (int) (-(boxSize + boxDistance) * 1f));

            for (int i = 0; i < 6; i++) {
                if (seconds % 2 == 1) {
                    canvas.drawRect(rect, son);
                } else {
                    canvas.drawRect(rect, soff);
                }
                rect.offset(0, boxSize + boxDistance);

                if (minutes % 2 == 1) {
                    canvas.drawRect(rect, mon);
                } else {
                    canvas.drawRect(rect, moff);
                }
                rect.offset(0, boxSize + boxDistance);

                if (hours % 2 == 1) {
                    canvas.drawRect(rect, hon);
                } else {
                    canvas.drawRect(rect, hoff);
                }

                rect.offset(-(boxSize + boxDistance), -(boxSize + boxDistance) * 2);
                seconds = seconds >> 1;
                minutes = minutes >> 1;
                hours = hours >> 1;
            }

            // TODO rita 3 rader som överlappar lite och blendar vid överlappningarna, mörkare för 0 och ljusare för 1.

        }
    }

}

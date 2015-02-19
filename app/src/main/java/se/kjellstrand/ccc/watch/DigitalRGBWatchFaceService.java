package se.kjellstrand.ccc.watch;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.SurfaceHolder;

/**
 * Created by erbsman on 2/13/15.
 */
public class DigitalRGBWatchFaceService extends AbstractCCCWatchFaceService {

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

            int boxSize = 36;
            int boxDistance = (int) (boxSize * 0.15);
            Rect rect = new Rect(0, 0, boxSize, boxSize);
            rect.offset(centerScreenX - boxSize / 2, centerScreenY - boxSize / 2);
            rect.offset((int) ((boxSize + boxDistance) * 2.5), (int) (-(boxSize + boxDistance) * 1f));

            for (int i = 0; i < 6; i++) {
                if (seconds % 2 == 1) {
                    //canvas.drawRect(rect, son);
                    canvas.drawPath(transform(rect, centerScreenX, centerScreenY), son);
                } else {
                    canvas.drawPath(transform(rect, centerScreenX, centerScreenY), soff);
                }
                rect.offset(0, boxSize + boxDistance);

                if (minutes % 2 == 1) {
                    canvas.drawPath(transform(rect, centerScreenX, centerScreenY), mon);
                } else {
                    canvas.drawPath(transform(rect, centerScreenX, centerScreenY), moff);
                }
                rect.offset(0, boxSize + boxDistance);

                if (hours % 2 == 1) {
                    canvas.drawPath(transform(rect, centerScreenX, centerScreenY), hon);
                } else {
                    canvas.drawPath(transform(rect, centerScreenX, centerScreenY), hoff);
                }

                rect.offset(-(boxSize + boxDistance), -(boxSize + boxDistance) * 2);
                seconds = seconds >> 1;
                minutes = minutes >> 1;
                hours = hours >> 1;
            }

        }

        int[] p = new int[8];

        private Path transform(Rect rect, int centerScreenX, int centerScreenY) {
            p[0] = rect.right;
            p[1] = rect.top;
            p[2] = rect.right;
            p[3] = rect.bottom;
            p[4] = rect.left;
            p[5] = rect.bottom;
            p[6] = rect.left;
            p[7] = rect.top;

            for (int i = 0; i < 4; i++) {
                p[i * 2] += Math.sin((centerScreenY - p[i * 2 + 1]) / 30f) * 20f;
                p[i * 2 + 1] += Math.sin((centerScreenX - p[i * 2]) / 30f) * 20f;
            }

            Path transformed = new Path();
            transformed.rMoveTo(p[0], p[1]);
            transformed.rLineTo(p[2] - p[0], p[3] - p[1]);
            transformed.rLineTo(p[4] - p[2], p[5] - p[3]);
            transformed.rLineTo(p[6] - p[4], p[7] - p[5]);
            transformed.close();

            return transformed;
        }
    }

}

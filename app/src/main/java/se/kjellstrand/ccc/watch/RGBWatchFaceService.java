package se.kjellstrand.ccc.watch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
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

        public static final int HOUR_ON_COLOR = 0xffcc0000;
        public static final int HOUR_OFF_COLOR = 0xff770000;
        public static final int MINUTE_ON_COLOR = 0xff00cc00;
        public static final int MINUTE_OFF_COLOR = 0xff007700;
        public static final int SECOND_ON_COLOR = 0xff0000cc;
        public static final int SECOND_OFF_COLOR = 0xff000077;
        public static final int TEXT_COLOR = 0xff222222;
        public static final int DEFAULT_CIRCLE_BACKGROUND_COLOR = TEXT_COLOR;
        public static final int OUTER_CIRCLE_COLOR = 0xffbbbbbb;

        /**
         * Holds the current colors of each digit, used while calculating the color
         * state of the clock in each update.
         */
        public final int[] DIGITS_COLOR = new int[10];

        public Rect chrBounds = new Rect();

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            paintCircles.setAntiAlias(true);
            paintText.setAntiAlias(true);
            paintText.setTextSize(22);

            float mx[] = {
                    -1.0f, -1.0f, -1.0f, 0.0f, 255.0f,
                    -1.0f, -1.0f, -1.0f, 0.0f, 255.0f,
                    -1.0f, -1.0f, -1.0f, 0.0f, 255.0f,
                    0.0f, 0.0f, 0.0f, 1.0f, 0.0f
            };
            ColorMatrix cm = new ColorMatrix(mx);

            paintText.setColorFilter(new ColorMatrixColorFilter(cm));

            paintOuterCircles.setAntiAlias(true);
            paintOuterCircles.setStrokeWidth(2.0f);
            paintOuterCircles.setColor(OUTER_CIRCLE_COLOR);
            paintOuterCircles.setStyle(Paint.Style.STROKE);
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

            paintCircles.setColor(Color.RED);
            int boxSize = 40;
            int boxDistance = (int) (boxSize * 0.2);
            Rect rect = new Rect(0,0,boxSize,boxSize);
            rect.offset(centerScreenX-boxSize/2, centerScreenY-boxSize/2);
            rect.offset((int) ((boxSize+boxDistance)*2.5), 0);

            for (int i = 0; i < 6; i++) {
                if (seconds%2==1) {
                    canvas.drawRect(rect, paintCircles);
                } else {
                    canvas.drawRect(rect, paintOuterCircles);
                }
                rect.offset(-(boxSize+boxDistance), 0);
                seconds = seconds >> 1;
            }

            // TODO rita 3 rader som överlappar lite och blendar vid överlappningarna, mörkare för 0 och ljusare för 1.

        }
    }

}

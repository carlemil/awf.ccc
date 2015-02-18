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
public class CCCWatchFaceService extends AbstractCCCWatchFaceService {

    @Override
    public Engine onCreateEngine() {
        /* provide your watch face implementation */
        return new Engine();
    }

    private class Engine extends AbstractEngine {

        public static final int HOUR_X0_COLOR = 0xffcc0000;
        public static final int HOUR_0X_COLOR = 0xff770000;
        public static final int MINUTE_X0_COLOR = 0xff00cc00;
        public static final int MINUTE_0X_COLOR = 0xff007700;
        public static final int SECOND_X0_COLOR = 0xff0000cc;
        public static final int SECOND_0X_COLOR = 0xff000077;
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
            float centerScreenX = width / 2f;
            float centerScreenY = height / 2f;

            // Find out what boxes are 'active'
            int hoursX0 = time.hour / 10;
            int hours0X = time.hour % 10;
            int minutesX0 = time.minute / 10;
            int minutes0X = time.minute % 10;
            int secondsX0 = time.second / 10;
            int seconds0X = time.second % 10;

            // Reset all boxes to zero/black.
            for (int i = 0; i <= 9; i++) {
                DIGITS_COLOR[i] = 0;
            }

            // Update the color of the boxes with 'active' digits in them..
            DIGITS_COLOR[hoursX0] = setOrBlendDigitColorWithColor(DIGITS_COLOR[hoursX0], HOUR_X0_COLOR);
            DIGITS_COLOR[hours0X] = setOrBlendDigitColorWithColor(DIGITS_COLOR[hours0X], HOUR_0X_COLOR);
            DIGITS_COLOR[minutesX0] = setOrBlendDigitColorWithColor(DIGITS_COLOR[minutesX0], MINUTE_X0_COLOR);
            DIGITS_COLOR[minutes0X] = setOrBlendDigitColorWithColor(DIGITS_COLOR[minutes0X], MINUTE_0X_COLOR);
            DIGITS_COLOR[secondsX0] = setOrBlendDigitColorWithColor(DIGITS_COLOR[secondsX0], SECOND_X0_COLOR);
            DIGITS_COLOR[seconds0X] = setOrBlendDigitColorWithColor(DIGITS_COLOR[seconds0X], SECOND_0X_COLOR);

            // For boxes without a color, set the default background color.
            for (int i = 0; i <= 9; i++) {
                if (DIGITS_COLOR[i] == 0) {
                    DIGITS_COLOR[i] = DEFAULT_CIRCLE_BACKGROUND_COLOR;
                }
            }

            paintCircles.setColor(Color.BLACK);
            float faceRadius = (centerScreenX + centerScreenY) / 3.2f;
            float radius = faceRadius / 4;

            for (int i = 0; i < 10; i++) {
                paintCircles.setColor(DIGITS_COLOR[i]);
                paintText.setColor(DIGITS_COLOR[i]);

                float offsetX = (float) (Math.sin(-i / 10f * Math.PI * 2 + Math.PI) * faceRadius);
                float offsetY = (float) (Math.cos(-i / 10f * Math.PI * 2 + Math.PI) * faceRadius);

                float centerDigitX = centerScreenX + offsetX;
                float centerDigitY = centerScreenY + offsetY;

                canvas.drawCircle(centerDigitX, centerDigitY, radius, paintCircles);
                canvas.drawOval(centerDigitX - radius, centerDigitY - radius,
                        centerDigitX + radius, centerDigitY + radius, paintOuterCircles);

                // Move to outside this method and make lookup array of chrBounds
                String chr = "" + i;
                paintText.getTextBounds(chr, 0, chr.length(), chrBounds);

                float textOffsetX = centerScreenX + offsetX - chrBounds.width() / 2;
                float textOffsetY = centerScreenY + offsetY + chrBounds.height() / 2;
                canvas.drawText(chr, textOffsetX, textOffsetY, paintText);
            }
        }
    }

}

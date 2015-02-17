package se.kjellstrand.ccc.watch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.view.SurfaceHolder;

import java.util.TimeZone;

/**
 * Created by erbsman on 2/13/15.
 */
public class CCCWatchFaceService extends CanvasWatchFaceService {

    @Override
    public Engine onCreateEngine() {
        /* provide your watch face implementation */
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine {

        static final int MSG_UPDATE_TIME = 0;
        static final int INTERACTIVE_UPDATE_RATE_MS = 1000;

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
        private final int[] DIGITS_COLOR = new int[10];

        /* a time object */
        private Time time;

        /* device features */
        private boolean lowBitAmbient;

        private boolean registeredTimeZoneReceiver;

        /**
         * Defines how the colors will be blended.
         */
        private int sBlendMode = R.string.screen_blend;

        private Paint paintCircles = new Paint();
        private Paint paintOuterCircles = new Paint();
        private Paint paintText = new Paint();

        /* handler to update the time once a second in interactive mode */
        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        invalidate();
                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = INTERACTIVE_UPDATE_RATE_MS
                                    - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                            mUpdateTimeHandler
                                    .sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };
        /* receiver to update the time zone */
        final BroadcastReceiver timeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                time.clear(intent.getStringExtra("time-zone"));
                time.setToNow();
            }
        };
        private Rect chrBounds = new Rect();

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
             /* configure the system UI */
            setWatchFaceStyle(new WatchFaceStyle.Builder(CCCWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle
                            .BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

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

            /* allocate an object to hold the time */
            time = new Time();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            /* get device features (burn-in, low-bit ambient) */
            lowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
//            burnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION,
//                    false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            /* the time changed */
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            /* the wearable switched between modes */

            if (lowBitAmbient) {
                boolean antiAlias = !inAmbientMode;
                paintCircles.setAntiAlias(antiAlias);
                paintText.setAntiAlias(antiAlias);
                paintOuterCircles.setAntiAlias(antiAlias);
            }
            invalidate();
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            /* draw your watch face */
            // Update the time
            time.setToNow();

            int width = bounds.width();
            int height = bounds.height();

            canvas.drawColor(Color.BLACK);

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

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            /* the watch face became visible or invisible */

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                time.clear(TimeZone.getDefault().getID());
                time.setToNow();
            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible and
            // whether we're in ambient mode), so we may need to start or stop the timer
            updateTimer();
        }


        /**
         * Blends the two colors unless the first color is pitch black with 0 alpha,
         * for that corner-case it will return the second color
         *
         * @param c1 first color.
         * @param c2 second color.
         * @return a blend of the two color, unless above stated condition applies.
         */
        private int setOrBlendDigitColorWithColor(int c1, int c2) {
            switch (sBlendMode) {
                case R.string.screen_blend:
                    if (c1 != 0) {
                        return ColorUtil.screenBlendTwoColors(c1, c2);
                    } else {
                        return c2;
                    }

                case R.string.multiply_blend:
                    if (c1 != 0) {
                        return ColorUtil.multiplyBlendTwoColors(c1, c2);
                    } else {
                        return c2;
                    }

                case R.string.average_blend:
                    if (c1 != 0) {
                        return ColorUtil.averageBlendTwoColors(c1, c2);
                    } else {
                        return c2;
                    }
            }
            return c1;
        }

        private void registerReceiver() {
            if (registeredTimeZoneReceiver) {
                return;
            }
            registeredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            CCCWatchFaceService.this.registerReceiver(timeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!registeredTimeZoneReceiver) {
                return;
            }
            registeredTimeZoneReceiver = false;
            CCCWatchFaceService.this.unregisterReceiver(timeZoneReceiver);
        }
    }

}

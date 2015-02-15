package se.kjellstrand.ccc.watch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.util.Log;
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

    /* implement service callback methods */
    private class Engine extends CanvasWatchFaceService.Engine {

        static final int MSG_UPDATE_TIME = 0;
        static final int INTERACTIVE_UPDATE_RATE_MS = 1000;


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


        private boolean burnInProtection;

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
                    .setShowSystemUiTime(true)
                    .build());

            /* load the background image */
//            Resources resources;
//            resources = CCCWatchFaceService.this.getResources();
//            Drawable backgroundDrawable = resources.getDrawable(R.drawable.close_button);
//            backgroundBitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();

            /* create graphic styles */
//            hourPrimaryPaint = new Paint();
//            hourPrimaryPaint.setARGB(255, 200, 000, 000);
//            hourPrimaryPaint.setStrokeWidth(5.0f);
//            hourPrimaryPaint.setAntiAlias(true);
//            hourPrimaryPaint.setStrokeCap(Paint.Cap.ROUND);
//            minutePrimaryPaint = new Paint();
//            minutePrimaryPaint.setARGB(255, 000, 200, 000);
//            minutePrimaryPaint.setStrokeWidth(5.0f);
//            minutePrimaryPaint.setAntiAlias(true);
//            minutePrimaryPaint.setStrokeCap(Paint.Cap.ROUND);
//            secondPrimaryPaint = new Paint();
//            secondPrimaryPaint.setARGB(255, 000, 000, 200);
//            secondPrimaryPaint.setStrokeWidth(5.0f);
//            secondPrimaryPaint.setAntiAlias(true);
//            secondPaint.setStrokeCap(Paint.Cap.ROUND);
//

            /* allocate an object to hold the time */
            time = new Time();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            /* get device features (burn-in, low-bit ambient) */
            lowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            burnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION,
                    false);
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
//                hourPaint.setAntiAlias(antiAlias);
//                minutePaint.setAntiAlias(antiAlias);
//                secondPaint.setAntiAlias(antiAlias);
//                tickPaint.setAntiAlias(antiAlias);
                //TODO what to do???
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

            // Draw the background, scaled to fit.
//            if (backgroundScaledBitmap == null
//                    || backgroundScaledBitmap.getWidth() != width
//                    || backgroundScaledBitmap.getHeight() != height) {
//                backgroundScaledBitmap = Bitmap.createScaledBitmap(backgroundBitmap,
//                        width, height, true /* filter */);
//            }
//            canvas.drawBitmap(backgroundScaledBitmap, 0, 0, null);

            canvas.drawColor(Color.BLACK);

            // Find the center. Ignore the window insets so that, on round watches
            // with a "chin", the watch face is centered on the entire screen, not
            // just the usable portion.
            float centerX = width / 2f;
            float centerY = height / 2f;

            // Compute rotations and lengths for the clock hands.
            float secRot = time.second / 30f * (float) Math.PI;
            int minutes = time.minute;
            float minRot = minutes / 30f * (float) Math.PI;
            float hrRot = ((time.hour + (minutes / 60f)) / 6f ) * (float) Math.PI;


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
            DIGITS_COLOR[hoursX0] = setOrBlendDigitColorWithColor(DIGITS_COLOR[hoursX0], 0xffcc0000);
            DIGITS_COLOR[hours0X] = setOrBlendDigitColorWithColor(DIGITS_COLOR[hours0X], 0xff770000);
            DIGITS_COLOR[minutesX0] = setOrBlendDigitColorWithColor(DIGITS_COLOR[minutesX0], 0xff00cc00);
            DIGITS_COLOR[minutes0X] = setOrBlendDigitColorWithColor(DIGITS_COLOR[minutes0X], 0xff007700);
            DIGITS_COLOR[secondsX0] = setOrBlendDigitColorWithColor(DIGITS_COLOR[secondsX0], 0xff0000cc);
            DIGITS_COLOR[seconds0X] = setOrBlendDigitColorWithColor(DIGITS_COLOR[seconds0X], 0xff000077);

            // For boxes without a color, set the default background color.
            for (int i = 0; i <= 9; i++) {
                if (DIGITS_COLOR[i] == 0) {
                    DIGITS_COLOR[i] = 0xff222222;
                }
            }

            Paint paint = new Paint();
            //paint.setStrokeWidth(5.0f);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);

            Paint paintText = new Paint();
            paintText.setTextSize(20);
            paintText.setAntiAlias(true);
            paint.setColor(Color.CYAN);
            float faceRadius=(centerX+centerY)/2.75f;
            float radius = faceRadius/4;
            for (int i=0;i<10;i++){
                float offsetX = (float) (Math.sin(-i/10f*Math.PI * 2+Math.PI)*faceRadius);
                float offsetY = (float) (Math.cos(-i / 10f * Math.PI * 2+Math.PI)*faceRadius);
                paint.setColor(DIGITS_COLOR[i]);
                canvas.drawCircle(centerX+offsetX, centerY+offsetY, radius, paint);
                canvas.drawText(""+i,centerX+offsetX, centerY+offsetY, paintText);
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

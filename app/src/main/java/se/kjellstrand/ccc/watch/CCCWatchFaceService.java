package se.kjellstrand.ccc.watch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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

        /* a time object */
        private Time time;

        /* device features */
        private boolean lowBitAmbient;

        private boolean registeredTimeZoneReceiver;

        /* graphic objects */
//        private Bitmap backgroundBitmap;
//        private Bitmap backgroundScaledBitmap;
//        private BitmapDrawable tickPaint;
        private Paint hourPaint;
        private Paint minutePaint;
        private Paint secondPaint;

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
                    .setShowSystemUiTime(false)
                    .build());

            /* load the background image */
//            Resources resources;
//            resources = CCCWatchFaceService.this.getResources();
//            Drawable backgroundDrawable = resources.getDrawable(R.drawable.close_button);
//            backgroundBitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();

            /* create graphic styles */
            hourPaint = new Paint();
            hourPaint.setARGB(255, 200, 000, 000);
            hourPaint.setStrokeWidth(5.0f);
            hourPaint.setAntiAlias(true);
            hourPaint.setStrokeCap(Paint.Cap.ROUND);
            minutePaint = new Paint();
            minutePaint.setARGB(255, 000, 200, 000);
            minutePaint.setStrokeWidth(5.0f);
            minutePaint.setAntiAlias(true);
            minutePaint.setStrokeCap(Paint.Cap.ROUND);
            secondPaint = new Paint();
            secondPaint.setARGB(255, 000, 000, 200);
            secondPaint.setStrokeWidth(5.0f);
            secondPaint.setAntiAlias(true);
            secondPaint.setStrokeCap(Paint.Cap.ROUND);


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
                hourPaint.setAntiAlias(antiAlias);
                minutePaint.setAntiAlias(antiAlias);
                secondPaint.setAntiAlias(antiAlias);
//                tickPaint.setAntiAlias(antiAlias);
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

            float secLength = centerX - 20;
            float minLength = centerX - 40;
            float hrLength = centerX - 80;

            float secX = (float) Math.sin(secRot) * secLength;
            float secY = (float) -Math.cos(secRot) * secLength;
            canvas.drawLine(centerX, centerY, centerX + secX, centerY +
                    secY, secondPaint);

            // Draw the minute and hour hands.
            float minX = (float) Math.sin(minRot) * minLength;
            float minY = (float) -Math.cos(minRot) * minLength;
            canvas.drawLine(centerX, centerY, centerX + minX, centerY + minY,
                    minutePaint);
            float hrX = (float) Math.sin(hrRot) * hrLength;
            float hrY = (float) -Math.cos(hrRot) * hrLength;
            canvas.drawLine(centerX, centerY, centerX + hrX, centerY + hrY,
                    hourPaint);

            Paint paint = new Paint();
            paint.setARGB(255, 100, 250, 000);
            paint.setStrokeWidth(5.0f);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);
            float faceRadius=(centerX+centerY)/2.75f;
            float radius = faceRadius/4;
            for (int i=0;i<10;i++){
                float offsetX = (float) (Math.sin(i/10f*Math.PI * 2)*faceRadius);
                float offsetY = (float) (Math.cos(i / 10f * Math.PI * 2)*faceRadius);
                canvas.drawCircle(centerX+offsetX, centerY+offsetY, radius, paint);
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

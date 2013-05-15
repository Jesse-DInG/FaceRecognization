package com.dxj.face_recognization.activity;

import java.text.DecimalFormat;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class FpsMeter {
    private static final String TAG       = "Sample::FpsMeter";
    int                         step;
    int                         framesCouner;
    double                      freq;
    long                        prevFrameTime;
    String                      strfps;
    DecimalFormat               twoPlaces = new DecimalFormat("0.00");
    Paint                       paint;

    public void init() {
    	step=10;
        framesCouner = 0;
        prevFrameTime = System.currentTimeMillis();
        strfps = "";

        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize(50);
        Log.i(TAG, "init");
    }

    public void measure() {
        framesCouner++;
        if (framesCouner % step == 0) {
            long time = System.currentTimeMillis();
            double fps = (double)step * 1000 / (time - prevFrameTime);
            prevFrameTime = time;
            DecimalFormat twoPlaces = new DecimalFormat("0.00");
            strfps = twoPlaces.format(fps) + " FPS";
            Log.i(TAG, strfps);
        }
    }

    public void draw(Canvas canvas, float offsetx, float offsety) {
        canvas.drawText(strfps, 20 + offsetx, 10 + 50 + offsety, paint);
    }

}

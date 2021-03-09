package com.example.rc_controller;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ControlView extends View {
    private final Controller controller;
    private final Paint paint;
    private final RectF drawRange;

    private float distanceCalculation(float x, float y){
        return (float) Math.sqrt(Math.pow(x,2)+ Math.pow(y,2));
    }


    public ControlView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint=new Paint();
        drawRange=new RectF();
        controller =new Controller();
        controller.setControlView(this);
    }

    public Controller getController() {
        return controller;
    }

    @Override
    public boolean performClick() {
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Log.d("ControlView","@onDraw");
        super.onDraw(canvas);
        float centerX=this.getWidth()/2.0f;
        float centerY=this.getHeight()/2.0f;
        float maxLength=distanceCalculation(centerX,centerY);

        canvas.drawColor(Color.WHITE);

        canvas.save();
        canvas.rotate((float) (-90+controller.getRoll()),centerX,centerY);
        paint.setColor(Color.LTGRAY);
        drawRange.top=centerY;
        drawRange.left=centerX-maxLength;
        drawRange.right=maxLength*2;
        drawRange.bottom=maxLength*2;
        canvas.drawRect(drawRange,paint);
        canvas.restore();
    }

}
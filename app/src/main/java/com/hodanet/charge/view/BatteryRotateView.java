package com.hodanet.charge.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.hodanet.charge.R;
import com.hodanet.charge.utils.ScreenUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */

public class BatteryRotateView extends View {

    private int angles;
    private boolean isAnimation;
    private int dotRadius;
    private Timer timer;
    private TimerTask task;
    private Bitmap bitmap;
    private Matrix matrix;
    private Paint paint;

    public BatteryRotateView(Context context) {
        super(context);
        initParams();
    }

    public BatteryRotateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initParams();
    }

    public BatteryRotateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParams();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(isAnimation){
            int width = getWidth() - dotRadius * 2;
            float x = (float) (width / 2 *  Math.cos(angles / 180.0 * Math.PI) );
            float y = (float) (width/ 2 *  Math.sin(angles / 180.0 * Math.PI) );
            canvas.translate(getWidth() / 2 + x - dotRadius , getWidth() / 2 + y - dotRadius);
            if(matrix != null && bitmap != null){
                canvas.drawBitmap(bitmap, matrix, paint);
            }

        }
    }


    public void initParams(){
        dotRadius = ScreenUtil.dipTopx(getContext(), 8);
        this.bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.charge_circle_out_dot);
        int width  = this.bitmap.getWidth();
        int height = this.bitmap.getHeight();
        float scaleX = (float) ( 2.0 * dotRadius / width);
        float scaleY = (float) ( 2.0 * dotRadius / height);
        matrix = new Matrix();
        matrix.postScale(scaleX, scaleY);
        paint = new Paint();
        paint.setAntiAlias(false);

    }

    public void rotate(boolean showAnimation){
        if(showAnimation){
            if(isAnimation) return;
            isAnimation = true;
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    angles += 3;
                    postInvalidate();
                }
            };
            timer.scheduleAtFixedRate(task, 0, 80);
        }else{
            isAnimation = false;
            if(timer != null){
                timer.cancel();
                timer = null;
                task.cancel();
                task = null;
            }
        }
    }
}

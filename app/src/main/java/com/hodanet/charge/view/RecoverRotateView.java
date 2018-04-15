package com.hodanet.charge.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.hodanet.charge.R;
import com.hodanet.charge.utils.LogUtil;
import com.hodanet.charge.utils.ScreenUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */

public class RecoverRotateView extends View {

    private static final String TAG = RecoverRotateView.class.getName();
    private Matrix matrix;
    private int dotRadius;
    private int dotPosition;
    private Bitmap bitmap;
    private Paint paint;
    private float scaleX;
    private float scaleY;

    public RecoverRotateView(Context context) {
        super(context);
        initParams();
    }

    public RecoverRotateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initParams();
    }

    public RecoverRotateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParams();
    }

    private void initParams() {

        paint = new Paint();
        paint.setAntiAlias(false);
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.charge_circle_out_dot);

        int width  = this.bitmap.getWidth();
        int height = this.bitmap.getHeight();

        dotRadius = ScreenUtil.dipTopx(getContext(), 12);
        scaleX = (float) ( 2.0 * dotRadius / width);
        scaleY = (float) ( 2.0 * dotRadius / height);
        matrix = new Matrix();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画旋转圆点
        int length = 2 * (getWidth() + getHeight()) - dotRadius * 2 * 4;
        LogUtil.e(TAG, "长度：" + dotPosition);
        matrix.reset();
        matrix.preScale(scaleX, scaleY);
        if(dotPosition < getWidth() - 2 * dotRadius){
            matrix.postTranslate(dotPosition, 0);
        }else if (dotPosition < getWidth() + getHeight() - dotRadius * 2 * 2){
            matrix.postTranslate(getWidth() - 2 * dotRadius
                    , dotPosition - (getWidth() - 2 * dotRadius));
        }else if(dotPosition < getWidth() * 2 + getHeight() - dotRadius * 2 * 3){
            matrix.postTranslate(getWidth() - dotRadius * 2 - (dotPosition - (getWidth() + getHeight() - dotRadius * 2 * 2))
                    , getHeight() - dotRadius * 2);
        }else if(dotPosition < getWidth() * 2 + getHeight() * 2 - dotRadius * 2 * 4){
            matrix.postTranslate(0
                    , getHeight() - dotRadius * 2 - (dotPosition - (getWidth() * 2 + getHeight() - dotRadius * 2 * 3)));
        }
        canvas.drawBitmap(bitmap, matrix, paint);
        dotPosition += 5;
        dotPosition = dotPosition % length;
    }

    public void start(){
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                postInvalidate();
            }
        };
        timer.schedule(task, 0 , 50);
    }
}

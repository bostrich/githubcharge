package com.hodanet.charge.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.hodanet.charge.R;
import com.hodanet.charge.utils.ScreenUtil;

import java.util.TimerTask;

/**
 *
 */

public class BatteryRotateView extends View {

    private int angles;
    private boolean isAnimation;
    private int dotRadius;

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
            canvas.translate(getWidth() / 2 + x , getWidth() / 2 + y );
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.white));
            paint.setAntiAlias(false);
            canvas.drawCircle(0, 0, dotRadius, paint);
        }
    }


    public void initParams(){
        dotRadius = ScreenUtil.dipTopx(getContext(), 5);
    }

    public void rotate(boolean showAnimation){
        if(showAnimation){
            isAnimation = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(isAnimation){
                        angles += 2;
                        postInvalidate();
                        try {
                            Thread.sleep(80);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }else{
            isAnimation = false;
        }
    }
}

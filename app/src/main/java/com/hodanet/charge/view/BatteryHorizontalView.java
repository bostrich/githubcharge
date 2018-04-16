package com.hodanet.charge.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
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

public class BatteryHorizontalView extends View{

    private Paint paintBg;
    private Paint paintForground;
    private Rect rect;
    private RectF rectF;
    private int percent;
    private boolean isChecking;
    private int tem = 0;
    private int radius;


    public BatteryHorizontalView(Context context) {
        super(context);
        initParams();
    }

    public BatteryHorizontalView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initParams();
    }

    public BatteryHorizontalView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParams();
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
        invalidate();
    }


    private void initParams() {
        paintBg = new Paint();
        paintBg.setAntiAlias(false);
        paintBg.setColor(getResources().getColor(R.color.recover_battery_bg));

        radius = ScreenUtil.dipTopx(getContext(), 4);

        paintForground = new Paint();
        paintForground.setAntiAlias(false);
        paintForground.setColor(getResources().getColor(R.color.recover_battery_forground));
        rect = new Rect();
        rectF = new RectF();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        int centerWidth = (getWidth() - ScreenUtil.dipTopx(getContext(), 10)) / 2;
        int centerHeight = getHeight() / 2;
        int width = ScreenUtil.dipTopx(getContext(), 207);
        int height = ScreenUtil.dipTopx(getContext(), 107);

//        rect.set(centerWidth -  width / 2, centerHeight - height /2
//                , centerWidth +  width / 2, centerHeight + height /2);
//        canvas.drawRect(rect, paintBg);
        if(isChecking){
            paintForground.setColor(getResources().getColor(R.color.bg_main_color_23));
            int widthStart = (int) (getWidth() * percent / 100.0) + ScreenUtil.dipTopx(getContext(), 6);
            int startX = (int) (width * percent / 66.0 - width / 2);

            int endX = (startX + width / 2) > width ? width + radius: startX + width /2 + radius;

            startX = startX > 0 ? startX + radius : radius;
//            int end = getWidth() / 2 + widthStart < centerWidth + width / 2 ? getWidth() / 2 + widthStart: centerWidth + width / 2;
            rect.set(startX, centerHeight - height / 2 , endX, centerHeight + height / 2);

            canvas.drawRect(rect, paintForground);

            percent = Math.abs(tem % 100);
            tem += 3;

        }else{

            int widthPercent = (int) ((percent / 100.0) * width);
            if(percent < 60){
                paintForground.setColor(getResources().getColor(R.color.recover_battery_forground));
            }else if(percent < 80){
                paintForground.setColor(getResources().getColor(R.color.recover_battery_80));
            }else{
                paintForground.setColor(getResources().getColor(R.color.recover_battery_100));
            }


            rectF.set(centerWidth -  width / 2, centerHeight - height /2
                    , centerWidth -  width / 2 + widthPercent, centerHeight + height /2);
            canvas.drawRoundRect(rectF, radius, radius, paintForground);
        }


    }

    private Timer timer;
    private TimerTask task;

    public void setChecking(boolean isChecking){
        if(isChecking){
            tem = 0;
            this.isChecking = true;
            if(timer != null){
                timer.cancel();
                timer = null;
            }
            if(task != null){
                task.cancel();
                task = null;
            }
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    postInvalidate();
                }
            };
            timer.scheduleAtFixedRate(task, 0, 50);
        }else{
            this.isChecking = false;
            if(timer != null){
                timer.cancel();
                timer = null;
            }
            if(task != null){
                task.cancel();
                task = null;
            }
        }
    }
}

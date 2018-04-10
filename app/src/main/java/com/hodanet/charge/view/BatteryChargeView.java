package com.hodanet.charge.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
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

public class BatteryChargeView extends View {

    private static final String TAG = BatteryChargeView.class.getName();

    private int centerWidth;
    private int centerHeight;
    private float thickness;
    private Paint paint;

    private int percent;//电量百分比
    private int fakePercent;//充电时的假电量
    private boolean isCharging;//判断是否在充电
    private RectF rectF;
    private TimerTask task;
    private Timer timer;

    public BatteryChargeView(Context context) {
        super(context);
        initParams();
    }


    public BatteryChargeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initParams();
    }

    public BatteryChargeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParams();
    }

    private void initParams() {
        paint = new Paint();
        paint.setAntiAlias(false);
        paint.setColor(getResources().getColor(R.color.charge_battery_red));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        centerWidth = getWidth() / 2;
        centerHeight = (int)((getHeight() + ScreenUtil.dipTopx(getContext(), 5)) / 2);
        int stripWidth = ScreenUtil.dipTopx(getContext(), 18);
        thickness = ScreenUtil.dipTopx(getContext(), 9);
        int interval = ScreenUtil.dipTopx(getContext(), 6);
        int top = (int)(centerHeight + 1.5 * thickness + 2 * interval);
        int radius = ScreenUtil.dipTopx(getContext(), 3);

        Path path = new Path();
        rectF = new RectF(centerWidth - stripWidth, top - 4 * interval - 4 * thickness
                , centerWidth + stripWidth, top + thickness);
        path.addRoundRect(rectF, radius, radius, Path.Direction.CW);
        canvas.clipPath(path);

        if(isCharging){//判断是否在充电中
            paint.setColor(getResources().getColor(R.color.bg_main_color));
            LogUtil.e(TAG, "假分数:" + fakePercent);
            for (int i = 1; i < 6; i++) {
                if(fakePercent / 20  >= i){
                    paint.setColor(getResources().getColor(R.color.bg_main_color));
                }else{
                    paint.setColor(getResources().getColor(R.color.battery_empty));
                }
                rectF = new RectF(centerWidth - stripWidth, top
                        , centerWidth + stripWidth, top + thickness);
                canvas.drawRoundRect(rectF, 0, 0, paint);
                top -= interval + thickness;
            }
            fakePercent += 20;
            if(fakePercent >= 120) fakePercent = percent;

        }else{
            if(percent <= 20){
                paint.setColor(getResources().getColor(R.color.charge_battery_red));
            }else{
                paint.setColor(getResources().getColor(R.color.bg_main_color));
            }
            rectF = new RectF(centerWidth - stripWidth, top
                    , centerWidth + stripWidth, top + thickness);
            canvas.drawRoundRect(rectF, 0, 0, paint);

            if(percent <= 20){
                paint.setColor(getResources().getColor(R.color.battery_empty));
            }else{
                paint.setColor(getResources().getColor(R.color.bg_main_color));
            }

            top -= interval + thickness;
            rectF = new RectF(centerWidth - stripWidth, top
                    , centerWidth + stripWidth, top + thickness);
            canvas.drawRoundRect(rectF, 0, 0, paint);

            if(percent <= 40){
                paint.setColor(getResources().getColor(R.color.battery_empty));
            }else{
                paint.setColor(getResources().getColor(R.color.bg_main_color));
            }

            top -= interval + thickness;
            rectF = new RectF(centerWidth - stripWidth, top
                    , centerWidth + stripWidth, top + thickness);
            canvas.drawRoundRect(rectF, 0, 0, paint);

            if(percent <= 60){
                paint.setColor(getResources().getColor(R.color.battery_empty));
            }else{
                paint.setColor(getResources().getColor(R.color.bg_main_color));
            }

            top -= interval + thickness;
            rectF = new RectF(centerWidth - stripWidth, top
                    , centerWidth + stripWidth, top + thickness);
            canvas.drawRoundRect(rectF, 0, 0, paint);

            if(percent <= 80){
                paint.setColor(getResources().getColor(R.color.battery_empty));
            }else{
                paint.setColor(getResources().getColor(R.color.bg_main_color));
            }

            top -= interval + thickness;
            rectF = new RectF(centerWidth - stripWidth, top
                    , centerWidth + stripWidth, top + thickness);
            canvas.drawRoundRect(rectF, 0, 0, paint);
        }

    }

    public void setBattery(int percent, boolean isCharging){
        this.percent = percent;
        if(!isCharging){
            invalidate();
            if(timer != null){
                timer.cancel();
                timer = null;
                task.cancel();
                task = null;
            }
            this.isCharging = isCharging;
        }else{
            LogUtil.e(TAG, "进行动画");
            if(!this.isCharging){//防止重复进入
                this.isCharging = isCharging;
                fakePercent = percent;
                timer = new Timer();
                task = new TimerTask() {
                    @Override
                    public void run() {
                        postInvalidate();
                    }
                };
                timer.scheduleAtFixedRate(task, 0, 1000L);
            }
        }
    }

    public void setBattery(boolean isCharging){
        if(!isCharging){
            LogUtil.e(TAG, "停止动画");
            invalidate();
            if(timer != null){
                timer.cancel();
                timer = null;
                task.cancel();
                task = null;
            }
            this.isCharging = isCharging;
        }else{
            LogUtil.e(TAG, "进行动画");
            if(!this.isCharging){//防止重复进入
                this.isCharging = isCharging;
                fakePercent = percent;
                timer = new Timer();
                task = new TimerTask() {
                    @Override
                    public void run() {
                        postInvalidate();
                    }
                };
                timer.scheduleAtFixedRate(task, 0, 1000L);
            }
        }
    }



}

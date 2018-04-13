package com.hodanet.charge.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
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

public class BatteryChargeView2 extends View {

    private boolean charging;
    private boolean accelerate;
    private float power;
    private float fakePower;

    private Paint paintBg;
    private Paint paintForground;
    private Paint paintText;
    private Rect rect;
    private Rect rectText;

    private int start;
    private int space;
    private int normalWidth;
    private int borderWidth;
    private int width;
    private int height;

    private TimerTask task;
    private Timer timer;

    public BatteryChargeView2(Context context) {
        super(context);
        initParams();
    }

    public BatteryChargeView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initParams();
    }

    public BatteryChargeView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParams();
    }

    private void initParams() {
        paintBg = new Paint();
        paintBg.setAntiAlias(false);
        paintBg.setColor(getResources().getColor(R.color.charge_battery_bg));

        paintForground = new Paint();
        paintForground.setAntiAlias(false);

        paintText = new Paint();
        paintText.setAntiAlias(false);
        paintText.setColor(getResources().getColor(R.color.white));
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setTextSize(ScreenUtil.dipTopx(getContext(), 25));

        rectText = new Rect();
        rect = new Rect();

        start = ScreenUtil.dipTopx(getContext(), 16);
        space = ScreenUtil.dipTopx(getContext(), 40);
        normalWidth = ScreenUtil.dipTopx(getContext(), 21);
        borderWidth = ScreenUtil.dipTopx(getContext(), 26);
        height = ScreenUtil.dipTopx(getContext(), 88);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(accelerate) {
            width = borderWidth;
        }else{
            width = normalWidth;
        }

        if (power <= 60) {
            paintForground.setColor(getResources().getColor(R.color.recover_battery_forground));
        } else if (power <= 80) {
            paintForground.setColor(getResources().getColor(R.color.recover_battery_80));
        } else {
            paintForground.setColor(getResources().getColor(R.color.recover_battery_100));
        }
        for (int i = 0; i < 5; i++) {
            rect.set(start + i * space, (getHeight() - height) / 2
                    , start + i * space + width, (getHeight() + height) / 2);

            if(charging){
                if(fakePower - (i + 1) * 20 >= 0){
                    canvas.drawRect(rect, paintForground);
                } else {
                    canvas.drawRect(rect, paintBg);
                }
            }else{
                if (power - i * 20 >= 0) {
                    canvas.drawRect(rect, paintForground);
                } else {
                    canvas.drawRect(rect, paintBg);
                }
            }
        }

        if(charging){
            fakePower += 20;
            if(fakePower > 120) fakePower = power;
        }

        //写进度文字
        int textWidth = (getWidth() - ScreenUtil.dipTopx(getContext(), 10)) / 2;
        String content = (int) power + " " + "%";
        paintText.getTextBounds(content, 0, content.length(), rectText);
        canvas.drawText(content, textWidth, (getHeight() + rectText.height()) / 2, paintText);
    }

    public void setPower(int power){
        this.power = power;
        invalidate();
    }

    public void setCharging(boolean isCharging){
        if(isCharging){//充电中
            if(!charging){
                charging = true;
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
                timer.scheduleAtFixedRate(task, 0 , 500);
            }
        }else{
            charging = false;
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

    public void setAccelerate(boolean accele){
        if(accele){
            if(!accelerate){
                accelerate = true;
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
                timer.scheduleAtFixedRate(task, 0 , 300);
            }
        }else{
            accelerate = false;
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
            timer.scheduleAtFixedRate(task, 0 , 500);
        }
    }

}

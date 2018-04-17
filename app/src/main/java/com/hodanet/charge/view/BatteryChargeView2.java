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

    public static final int BATTERY_NOCHARGE = 0;
    public static final int BATTERY_CHARGE_NOMAL = 1;
    public static final int BATTERY_OPEN_ACCELERATE = 2;
    public static final int BATTERY_CHARGE_ACCELERATE = 3;

    private int state;
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

        if(state == BATTERY_CHARGE_ACCELERATE) {
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

            if(state >= BATTERY_CHARGE_NOMAL){
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

        switch(state){
            case BATTERY_OPEN_ACCELERATE:
                fakePower += 100;
                if(fakePower > 110) fakePower = 0;
                break;
            case BATTERY_CHARGE_NOMAL:
                fakePower += 20;
                if(fakePower > 120) fakePower = power;
                break;
            case BATTERY_CHARGE_ACCELERATE:
                fakePower += 20;
                if(fakePower > 120) fakePower = power;
                break;
        }

        //写进度文字
        String content = "";
//        if(openAccelerate){
//            content = "正在开启充电加速";
//        }else{
            content = (int) power + " " + "%";
//        }
        int textWidth = (getWidth() - ScreenUtil.dipTopx(getContext(), 10)) / 2;
        paintText.getTextBounds(content, 0, content.length(), rectText);
        canvas.drawText(content, textWidth, (getHeight() + rectText.height()) / 2, paintText);
    }

    public void setPower(int power){
        this.power = power;
        invalidate();
    }


    public void setState(int state){
        if(state == BATTERY_NOCHARGE){
            if(timer != null){
                timer.cancel();
                timer = null;
            }
            if(task != null){
                task.cancel();
                task = null;
            }
        }else if (state == BATTERY_CHARGE_NOMAL){
            if(this.state !=  BATTERY_CHARGE_NOMAL){
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
        }else if(state == BATTERY_OPEN_ACCELERATE){


        }else if(state == BATTERY_CHARGE_ACCELERATE){
            if(this.state != BATTERY_CHARGE_ACCELERATE){
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
                timer.scheduleAtFixedRate(task, 0 , 250);
            }
        }
        this.state = state;
    }

}

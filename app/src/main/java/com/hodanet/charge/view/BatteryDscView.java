package com.hodanet.charge.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
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

public class BatteryDscView extends View{

    private Paint paintBg;
    private Paint paintForground;
    private Paint paintText;
    private RectF rectF;
    private float progress;
    private Rect rectText;
    private String content = "";

    //状态值
    private boolean charging;
    private boolean accelerate;

    private TimerTask task;
    private Timer timer;


    public BatteryDscView(Context context) {
        super(context);
        initParams();
    }

    public BatteryDscView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initParams();
    }

    public BatteryDscView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParams();
    }

    private void initParams(){
        paintBg = new Paint();
        paintBg.setAntiAlias(false);
        paintBg.setColor(getResources().getColor(R.color.white));

        paintForground = new Paint();
        paintForground.setAntiAlias(false);
        paintForground.setColor(getResources().getColor(R.color.charge_btn_50));

        paintText = new Paint();
        paintText.setAntiAlias(false);
        paintText.setColor(getResources().getColor(R.color.charge_btn));
        paintText.setTextSize(ScreenUtil.dipTopx(getContext(), 20));
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setTypeface(Typeface.DEFAULT);

        rectF = new RectF();
        rectText = new Rect();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        rectF.set(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(rectF, getHeight() / 2, getHeight() / 2, paintBg);

        if(accelerate){
            rectF.set(0, 0, (float) (getWidth() / 100.0 * progress), getHeight());
            canvas.drawRoundRect(rectF, getHeight() /2 , getHeight() / 2, paintForground);
        }


        if(!charging){
            content = getResources().getString(R.string.charge_btn_consumer);
        }else{
            if(!accelerate){
                content = getResources().getString(R.string.charge_btn_open_accelerate);
            }else{
                if((int)progress / 33 == 0){
                    content = getResources().getString(R.string.charge_btn_accelerate1);
                }else if ((int)progress / 33 == 1){
                    content = getResources().getString(R.string.charge_btn_accelerate2);
                }else if((int)progress / 33 == 2){
                    content = getResources().getString(R.string.charge_btn_accelerate3);
                }
            }
        }
        progress += 2;
        if(progress > 100) progress = 0;
        int textWidth = getWidth() / 2;
        paintText.getTextBounds(content, 0, content.length(), rectText);
        canvas.drawText(content, textWidth, (getHeight() + rectText.height()) / 2, paintText);

    }


    public void setCharging(boolean isCharging) {
        if(isCharging){//充电中
            if(!charging){
                charging = true;
//                if(timer != null){
//                    timer.cancel();
//                    timer = null;
//                }
//                if(task != null){
//                    task.cancel();
//                    task = null;
//                }
//                timer = new Timer();
//                task = new TimerTask() {
//                    @Override
//                    public void run() {
//                        postInvalidate();
//                    }
//                };
//                timer.scheduleAtFixedRate(task, 0 , 50);
            }
        }else{
            charging = false;
//            if(timer != null){
//                timer.cancel();
//                timer = null;
//            }
//            if(task != null){
//                task.cancel();
//                task = null;
//            }
        }
    }

    public void setAccelerate(boolean accele) {
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
                timer.scheduleAtFixedRate(task, 0 , 25);
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
            timer.scheduleAtFixedRate(task, 0 , 50);
        }
    }
}

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

public class RecoveryDscView extends View{

    private Paint paintBg;
    private Paint paintForground;
    private Paint paintText;
    private RectF rectF;
    private float progress;
    private Rect rectText;
    private String content = "";



    public RecoveryDscView(Context context) {
        super(context);
        initParams();
    }

    public RecoveryDscView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initParams();
    }

    public RecoveryDscView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        paintText.setTypeface(Typeface.SANS_SERIF);

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

        if(progress > 0){
            rectF.set(0, 0, (getWidth() * progress), getHeight());
            canvas.drawRoundRect(rectF, getHeight() /2 , getHeight() / 2, paintForground);
        }


        int textWidth = getWidth() / 2;
        paintText.getTextBounds(content, 0, content.length(), rectText);
        canvas.drawText(content, textWidth, (getHeight() + rectText.height()) / 2, paintText);

    }

    public void setProgress(float progress, String content){
        this.progress = progress;
        this.content = content;
        invalidate();
    }

}

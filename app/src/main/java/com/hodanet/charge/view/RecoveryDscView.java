package com.hodanet.charge.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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
    private Rect rect;
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
        rect = new Rect();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {


        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas1 = new Canvas(bitmap);
        rectF.set(0, 0, getWidth(), getHeight());
        canvas1.drawRoundRect(rectF,getHeight() / 2, getHeight() / 2, paintBg);
        if(progress > 0){
            rect.set(0, 0, (int) (getWidth() * progress), getHeight());
            paintBg.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
            paintBg.setColor(getResources().getColor(R.color.charge_btn_50));
            canvas1.drawRect(rect, paintBg);
            paintBg.setXfermode(null);
        }
        paintBg.setColor(getResources().getColor(R.color.white));
        canvas.drawBitmap(bitmap, 0, 0, paintBg);


        Paint.FontMetricsInt fontMetrics = paintText.getFontMetricsInt();
        int baseline = (getHeight() - fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText(content, getWidth() / 2, baseline, paintText);

    }

    public void setProgress(float progress, String content){
        this.progress = progress;
        this.content = content;
        invalidate();
    }

}

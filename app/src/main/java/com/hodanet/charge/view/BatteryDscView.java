package com.hodanet.charge.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class BatteryDscView extends View {

    private Paint paintBg;
    private Paint paintForground;
    private Paint paintText;
    private RectF rectF;
    private float progress;
    private Rect rect;
    private String content = "";

    public static final int STATUS_NOTCHARGE = 0;
    public static final int STATUS_CHARGING = 1;
    public static final int STATUS_OPEN_ACCELERATE = 2;
    public static final int STATUS_ACCELERATE = 3;
    private int state;

    //状态值
    private Bitmap bitmap;
    private boolean charging;
    private float openingProgress;
    private Canvas can;

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

    private void initParams() {
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

        bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
        can = new Canvas(bitmap);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        rectF.set(0, 0, getWidth(), getHeight());
        paintBg.setColor(getResources().getColor(R.color.white));
        canvas.drawRoundRect(rectF, getHeight() / 2, getHeight() / 2, paintBg);

        //加速开启进度
        if(state == STATUS_OPEN_ACCELERATE){
            paintBg.setColor(getResources().getColor(R.color.charge_btn_50));
            rect.set(0, 0, (int) (getWidth() / 100.0 * openingProgress), getHeight());
            paintBg.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
            canvas.drawRect(rect, paintBg);
            paintBg.setXfermode(null);
        }

        //设置文字
        switch (state) {
            case STATUS_NOTCHARGE:
                content = getResources().getString(R.string.charge_btn_consumer);
                break;
            case STATUS_CHARGING:
                content = getResources().getString(R.string.charge_btn_open_accelerate);
                break;
            case STATUS_OPEN_ACCELERATE:
                content = getResources().getString(R.string.charge_btn_opening);
                break;
            case STATUS_ACCELERATE:
                content = getResources().getString(R.string.charge_btn_accelerate_stop);
                break;
        }
        Paint.FontMetricsInt fontMetrics = paintText.getFontMetricsInt();
        int baseline = (getHeight() - fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText(content, getWidth() / 2, baseline, paintText);
    }

    public void setStatus(int status) {
        this.state = status;
        openingProgress = 50;
        invalidate();
    }

}

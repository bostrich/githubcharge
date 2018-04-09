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
import com.hodanet.charge.utils.ScreenUtil;

/**
 *
 */

public class BatteryChargeView extends View {

    private int centerWidth;
    private int centerHeight;
    private float thickness;
    private Paint paint;

    private int percentage;
    private RectF rectF;

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

        rectF = new RectF(centerWidth - stripWidth, top
                , centerWidth + stripWidth, top + thickness);
        canvas.drawRoundRect(rectF, 0, 0, paint);

        top -= interval + thickness;
        rectF = new RectF(centerWidth - stripWidth, top
                , centerWidth + stripWidth, top + thickness);
        canvas.drawRoundRect(rectF, 0, 0, paint);

        top -= interval + thickness;
        rectF = new RectF(centerWidth - stripWidth, top
                , centerWidth + stripWidth, top + thickness);
        canvas.drawRoundRect(rectF, 0, 0, paint);

        top -= interval + thickness;
        rectF = new RectF(centerWidth - stripWidth, top
                , centerWidth + stripWidth, top + thickness);
        canvas.drawRoundRect(rectF, 0, 0, paint);

        top -= interval + thickness;
        rectF = new RectF(centerWidth  - stripWidth, top
                , centerWidth + stripWidth, top + thickness);
        canvas.drawRoundRect(rectF, 0, 0, paint);

    }



}

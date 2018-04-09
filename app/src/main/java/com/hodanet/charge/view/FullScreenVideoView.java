package com.hodanet.charge.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 *
 */
public class FullScreenVideoView extends VideoView {
    private int width;
    private int height;

    public FullScreenVideoView(Context context) {
        super(context);
    }

    public FullScreenVideoView(Context context, AttributeSet paramAttributeSet) {
        super(context, paramAttributeSet);
    }

    public FullScreenVideoView(Context context, AttributeSet paramAttributeSet, int paramInt) {
        super(context, paramAttributeSet, paramInt);
    }

    public int getVideoHeight() {
        return this.height;
    }

    public void setVideoHeight(int height) {
        this.height = height;
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width_set = getDefaultSize(this.width, widthMeasureSpec);
//        int height_set = getDefaultSize(this.height,heightMeasureSpec);
//        if(width_set > 0 && height_set > 0){
//            if(height_set * this.width <= width_set * this.height){
//                width_set = height_set * this.width / this.width;
//            }else{
//                height_set = width_set * this.height / this.width;
//            }
//        }
//        setMeasuredDimension(width_set,height_set);
//    }

    public int getVideoWidth() {
        return this.width;
    }

    public void setVideoWidth(int width) {
        this.width = width;
    }
}

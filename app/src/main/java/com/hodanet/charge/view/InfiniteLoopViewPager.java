package com.hodanet.charge.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.hodanet.charge.R;


public class InfiniteLoopViewPager extends ViewPager {

    private long delayTime = 3000;
    private boolean isRun = true;
    private boolean isDown = false;
    private boolean isAutoScroll = false;
    private boolean isAdapteView = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    int cur = getCurrentItem();
                    setCurrentItem(cur + 1, true);
                    if (isRun && !isDown && isAutoScroll) {
                        this.sendEmptyMessage(1);
                    }
                    break;

                case 1:
                    if (isRun && !isDown && isAutoScroll) {
                        this.sendEmptyMessageDelayed(0, delayTime);
                    }
                    break;
            }
        }
    };

    public InfiniteLoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.InfiniteLoopViewPager, 0, 0);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int index = array.getIndex(i);
            switch (index) {
                case R.styleable.InfiniteLoopViewPager_delayTime:
                    int delayTime = array.getInt(index, 3000);
                    setDelayTime(delayTime);
                    break;
                case R.styleable.InfiniteLoopViewPager_isAutoScroll:
                    boolean autoScroll = array.getBoolean(index, false);
                    setAutoScroll(autoScroll);
                    break;
            }
        }
        array.recycle();
    }

    public InfiniteLoopViewPager(Context context) {
        super(context);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {

        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isAutoScroll) {
            mHandler.removeCallbacksAndMessages(null);
            isRun = true;
            mHandler.sendEmptyMessage(1);
        } else {
            isRun = false;
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        //设置当前展示的位置
        if (adapter != null && adapter.getCount() > 0) {
            setCurrentItem(0);
            if (adapter.getCount() == 1) {
                setAutoScroll(false);
            }
            if (adapter instanceof InfiniteLoopViewPagerAdapter) {
                if (((InfiniteLoopViewPagerAdapter) adapter).getRealCount() == 1) {
                    setAutoScroll(false);
                }
            }
        }
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public void setAutoScroll(boolean isAutoScroll) {
        this.isAutoScroll = isAutoScroll;
    }

    public void setInfinateAdapter(PagerAdapter adapter) {
        setAdapter(adapter);
    }

    @Override
    public void setCurrentItem(int item) {
        item = getOffsetAmount() + (item % getAdapter().getCount());
        super.setCurrentItem(item);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isAdapteView) {
            int height = 0;
            // 下面遍历所有child的高度
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                // 采用最大的view的高度
                if (h > height) {
                    height = h;
                }
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                    MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    /**
     * 从0开始向左可以滑动的次数
     *
     * @return
     */
    private int getOffsetAmount() {
        if (getAdapter() instanceof InfiniteLoopViewPagerAdapter) {
            InfiniteLoopViewPagerAdapter infiniteAdapter = (InfiniteLoopViewPagerAdapter) getAdapter();
            // 允许向前滚动100000次
            return infiniteAdapter.getRealCount() * 100000;
        } else {
            return 0;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (isAutoScroll) {
            if (action == MotionEvent.ACTION_DOWN) {
                isRun = false;
                isDown = true;
                mHandler.removeCallbacksAndMessages(null);
            } else if (action == MotionEvent.ACTION_MOVE) {
                isDown = true;
                isRun = false;
                mHandler.removeCallbacksAndMessages(null);
            } else if (action == MotionEvent.ACTION_UP) {
                isRun = true;
                isDown = false;
                mHandler.removeCallbacksAndMessages(null);
                mHandler.sendEmptyMessage(1);
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void setOffscreenPageLimit(int limit) {
        super.setOffscreenPageLimit(limit);
    }


    public interface InfiniteLoopViewPagerClickListener {
        void doClickViewPager(int position);
    }

    public static class InfiniteLoopViewPagerAdapter extends PagerAdapter {
        private PagerAdapter adapter;

        public InfiniteLoopViewPagerAdapter(PagerAdapter adapter) {
            super();
            this.adapter = adapter;
        }

        @Override
        public int getCount() {
            if (adapter != null && adapter.getCount() > 0) {
                return Integer.MAX_VALUE;
            }
            return 0;
        }

        public int getRealCount() {
            return adapter.getCount();
        }

        public int getRealItemPosition(Object object) {
            return adapter.getItemPosition(object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            int realPosition = position % getRealCount();
            adapter.destroyItem(container, realPosition, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int realPosition = position % getRealCount();
            return adapter.instantiateItem(container, realPosition);
        }

        /*
         * start 传递给包装adapter
         */
        @Override
        public void finishUpdate(ViewGroup container) {
            adapter.finishUpdate(container);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return adapter.isViewFromObject(view, object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            adapter.restoreState(state, loader);
        }

        @Override
        public Parcelable saveState() {
            return adapter.saveState();
        }

        @Override
        public void startUpdate(ViewGroup container) {
            adapter.startUpdate(container);
        }
    }
}

package com.wenzl.bannerview;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

public class BannerView extends ViewPager {
    private BannerAdapterWrapper bannerAdapterWrapper;
    private PagerAdapter pagerAdapter;
    private LooperHandler looperHandler;
    private BannerIndicatorView bannerIndicatorView;

    private int scrollTime = 300;
    private int intervalTime = 3000;

    private Timer timer;

    public BannerView(Context context) {
        super(context);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置适配器的时候做初始化工作
     */
    @Override
    public void setAdapter(PagerAdapter adapter) {
        this.pagerAdapter = adapter;
        this.pagerAdapter.registerDataSetObserver(new BannerPagerObserver());
        bannerAdapterWrapper = new BannerAdapterWrapper(adapter);
        super.setAdapter(bannerAdapterWrapper);
        addOnPageChangeListener(new BannerPageChangeListener());
        looperHandler = new LooperHandler(this);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(bannerAdapterWrapper.toWrapperPosition(item), smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(bannerAdapterWrapper.toWrapperPosition(item));
    }

    @Override
    public int getCurrentItem() {
        return bannerAdapterWrapper.bannerToAdapterPosition(super.getCurrentItem());
    }

    /**
     * 设置指示器，需要在setAdapter之后
     */
    public void setIndicator(BannerIndicatorView bannerIndicatorView) {
        this.bannerIndicatorView = bannerIndicatorView;
        if (pagerAdapter != null) {
            bannerIndicatorView.setCount(pagerAdapter.getCount());
        }
    }

    /**
     * 适配器的包装类---------------------------------------------------------
     */
    private class BannerAdapterWrapper extends PagerAdapter {
        private PagerAdapter pagerAdapter;

        public BannerAdapterWrapper(PagerAdapter pagerAdapter) {
            this.pagerAdapter = pagerAdapter;
        }

        @Override
        public int getCount() {
            return pagerAdapter.getCount() > 1 ? pagerAdapter.getCount() + 2 : pagerAdapter.getCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return pagerAdapter.instantiateItem(container, bannerToAdapterPosition(position));
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            pagerAdapter.destroyItem(container, position, object);
        }

        /**
         * 展示出的position和实际的position 转换
         */
        public int bannerToAdapterPosition(int position) {
            int adapterCount = pagerAdapter.getCount();
            if (adapterCount <= 1) return 0;
            int adapterPosition = (position - 1) % adapterCount;
            if (adapterPosition < 0) adapterPosition += adapterCount;
            return adapterPosition;
        }

        public int toWrapperPosition(int position) {
            return position + 1;
        }
    }

    /**
     * 监听翻页----------------------------------------------------------------
     */
    private class BannerPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            // 在这里同步指示器
            if (bannerIndicatorView != null) {
                bannerIndicatorView.setSelect(bannerAdapterWrapper.bannerToAdapterPosition(position));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            int position = BannerView.super.getCurrentItem();
            // 无限轮播的跳转
            if (state == ViewPager.SCROLL_STATE_IDLE &&
                    (position == 0 || position == bannerAdapterWrapper.getCount() - 1)) {
                setCurrentItem(bannerAdapterWrapper.bannerToAdapterPosition(position), false);
            }
            // 手指拖动翻页的时候暂停自动轮播
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (timer == null) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            looperHandler.sendEmptyMessage(0);
                        }
                    }, intervalTime + scrollTime, intervalTime + scrollTime);
                }
            } else if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
            }
        }
    }

    /**
     * 数据刷新 传递刷新信号-----------------------------------------------------
     */
    private class BannerPagerObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            super.onChanged();
            dataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            dataSetChanged();
        }
    }

    /**
     * 刷新数据方法
     */
    private void dataSetChanged() {
        if (bannerAdapterWrapper != null && pagerAdapter.getCount() > 0) {
            bannerAdapterWrapper.notifyDataSetChanged();
            bannerIndicatorView.setCount(pagerAdapter.getCount());
            setCurrentItem(0);
        }
    }

    /**
     * 设置滚动时间  利用反射
     */
    public void setScrollTime(int scrollTime) {
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(getContext(),
                    new AccelerateInterpolator());
            field.set(this, scroller);
            scroller.setScrollDuration(scrollTime);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置间隔时间 并开始Timer任务
     */
    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                looperHandler.sendEmptyMessage(0);
            }
        }, intervalTime + scrollTime, intervalTime + scrollTime);
    }

    /**
     * 处理定时任务-------------------------------------------------------------------
     */
    private static class LooperHandler extends Handler {
        private WeakReference<BannerView> weakReference;

        public LooperHandler(BannerView bannerView) {
            this.weakReference = new WeakReference<>(bannerView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            weakReference.get().setCurrentItem(weakReference.get().getCurrentItem() + 1);
        }
    }

    /**
     * 重新计算高度------------------------------------------------------------
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        int maxHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int measuredHeight = child.getMeasuredHeight();
            if (measuredHeight > maxHeight) {
                maxHeight = measuredHeight;
            }
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 修改ViewPager的滑动动画时间-----------------------------------------------------------
     */
    private class FixedSpeedScroller extends Scroller {
        private int duration = 300;

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, this.duration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, this.duration);
        }

        public void setScrollDuration(int duration) {
            this.duration = duration;
        }
    }
}

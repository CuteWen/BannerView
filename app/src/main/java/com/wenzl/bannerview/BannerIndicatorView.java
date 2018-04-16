package com.wenzl.bannerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by AmStrong on 2018/3/1.
 */

public class BannerIndicatorView extends View {
    private int count;
    private int select;

    private Paint pointPaint;
    private Paint selectPaint;
    private String selectColor = "#FFFFFF";
    private String normalColor = "#80FFFFFF";

    private int radius = 10;
    private int interval = 10;

    public BannerIndicatorView(Context context) {
        this(context, null);
    }

    public BannerIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        pointPaint = new Paint();
        pointPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(Color.parseColor(normalColor));
        pointPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        selectPaint = new Paint();
        selectPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        selectPaint.setColor(Color.parseColor(selectColor));
        selectPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < count; i++) {
            if (i == select) {
                canvas.drawCircle(radius + i * (radius * 2 + interval), getHeight() / 2, radius, selectPaint);
            } else {
                canvas.drawCircle(radius + i * (radius * 2 + interval), getHeight() / 2, radius, pointPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = count * radius * 2 + (count - 1) * interval;
        int height = radius * 2;
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setSelect(int select) {
        this.select = select;
        invalidate();
    }

    /**
     * 设置个数
     */
    public void setCount(int c) {
        count = c;
    }

    public void setSelectColor(String selectColor) {
        this.selectColor = selectColor;
    }

    public void setNormalColor(String normalColor) {
        this.normalColor = normalColor;
    }
}

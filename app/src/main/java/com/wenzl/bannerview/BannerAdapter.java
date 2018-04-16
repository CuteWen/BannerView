package com.wenzl.bannerview;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blue on 2018/4/12.
 */

public class BannerAdapter extends PagerAdapter {
    private List<String> data;
    private Context context;

    public BannerAdapter(Context context) {
        this.context = context;
        data = new ArrayList<>();
    }

    public void addData(List<String> data){
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View contentView = LayoutInflater.from(context)
                .inflate(R.layout.item_banner, container, false);
        container.addView(contentView);
        return contentView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}

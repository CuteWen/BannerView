package com.wenzl.bannerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private BannerView bannerView;
    private BannerIndicatorView indicatorView;
    private BannerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bannerView = findViewById(R.id.bv_activity_banner);
        indicatorView = findViewById(R.id.biv_activity_banner);
        adapter = new BannerAdapter(this);
        bannerView.setAdapter(adapter);
        bannerView.setIndicator(indicatorView);
        bannerView.setScrollTime(500);
        bannerView.setIntervalTime(3000);
        ArrayList<String> data = new ArrayList<>();
        data.add("1111");
        data.add("2222");
        data.add("1111");
        data.add("2222");
        adapter.addData(data);

    }
}

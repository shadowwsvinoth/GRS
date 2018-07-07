package com.app.grs.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.grs.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

public class SliderAdapter extends PagerAdapter {

    private Context context;
    LayoutInflater layoutInflater;
    private ArrayList<HashMap<String,String>> bannerList;

    public SliderAdapter(Context context, ArrayList<HashMap<String, String>> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);

    }

    @Override
    public int getCount() {
        return bannerList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {



        View view = layoutInflater.inflate(R.layout.pager_layout, container, false);

        ImageView imageView = view.findViewById(R.id.iv_slider);

        HashMap<String,String> itemmap = bannerList.get(position);

        Glide.with(context).load(itemmap.get("image_url") + itemmap.get("image_name")).into(imageView);

        container.addView(view);

        return  view;

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}

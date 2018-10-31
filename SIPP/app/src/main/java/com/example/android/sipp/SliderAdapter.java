package com.example.android.sipp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater mLayoutInflater;

    public SliderAdapter(Context context){
        this.context = context;
    }

    //slider_image
    public int[] slider_images  ={R.mipmap.sipp_launcher,
            R.drawable.swipe_right,
            R.drawable.swipe_left};

    //sliderHeading
    public  String[] slider_heading = {"SIPP Corp.",
            "Upvote","Downvote"};

    //sliderDes
    public String[] slider_des = {"SIPP that ranks people's selfie based on the vote. Isn't that Simple. Let's Fight",
    "Swipe Right to Upvote",
    "Swipe Left to Downvote"};

    @Override
    public int getCount() {
        return slider_heading.length;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = mLayoutInflater.inflate(R.layout.activity_slide_walkthrough,container,false);

        ImageView slideImage = view.findViewById(R.id.img_slide_main);
        TextView slideTextHeading = view.findViewById(R.id.txt_slide_heading);
        TextView slideTextDes = view.findViewById(R.id.txt_slide_description);

        slideImage.setImageResource(slider_images[position]);
        slideTextHeading.setText(slider_heading[position]);
        slideTextDes.setText(slider_des[position]);
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (RelativeLayout) o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}

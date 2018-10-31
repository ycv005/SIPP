package com.example.android.sipp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Walkthrough extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private SliderAdapter mSliderAdapter;
    private TextView[] mDots;
    private Button mNextButton;
    private Button mBackButton;
    private int mCurrentPage;
    private Button mSkipButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);

        mSkipButton = findViewById(R.id.btn_slide_skip);
        mBackButton = (Button) findViewById(R.id.btn_slide_back);
        mNextButton = (Button) findViewById(R.id.btn_slide_next);
        mSliderAdapter = new SliderAdapter(this);

        mDotLayout = findViewById(R.id.layout_dots);
        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mSliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(mSliderAdapter);

        mSlideViewPager.addOnPageChangeListener(viewListener);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlideViewPager.setCurrentItem(mCurrentPage+1);
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlideViewPager.setCurrentItem(mCurrentPage-1);
            }
        });
        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Walkthrough.this, SignUpSignIn.class);
                startActivity(intent);
                finish();
            }
        });
        }


    public void addDotsInd(int position){
        mDots = new TextView[3];
        mDotLayout.removeAllViews();
        for(int i=0;i<mDots.length;i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextColor(getColor(R.color.colorTranparentWhite));
            mDotLayout.addView(mDots[i]);
        }
        if(mDots.length>0){
            mDots[position].setTextColor(getColor(R.color.colorWhite));
        }
    }
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsInd(i);
            mCurrentPage = i;
            if(i ==0){
                mBackButton.setEnabled(false);
                mBackButton.setVisibility(View.INVISIBLE);
                mNextButton.setEnabled(true);
            }
            else if(i==mDots.length-1){
                mNextButton.setEnabled(true);
                mBackButton.setEnabled(true);
                mBackButton.setVisibility(View.VISIBLE);
                mNextButton.setText("Finish");
                mBackButton.setText("Back");

                mNextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Walkthrough.this, SignUpSignIn.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
            else{
                mBackButton.setEnabled(true);
                mNextButton.setEnabled(true);
                mBackButton.setVisibility(View.VISIBLE);
                mNextButton.setText("Next");
                mBackButton.setText("Back");
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
}

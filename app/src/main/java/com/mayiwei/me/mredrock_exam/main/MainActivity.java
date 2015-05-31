package com.mayiwei.me.mredrock_exam.main;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mayiwei.me.mredrock_exam.R;
import com.mayiwei.me.mredrock_exam.app.BaseActivity;
import com.mayiwei.me.mredrock_exam.util.UpdateManager;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {
    private View slider;
    private ViewPager viewPager;
    private TextView textView1,textView2,textView3;
    private View1_Fragment fragment1;
    private View2_Fragment fragment2;
    private View3_Fragment fragment3;
    private List<Fragment> fragments;

    private UpdateManager mUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar(true);
        InitSlider();
        InitViewPager();
        InitTextView();
    }
    private void InitTextView() {
        textView1 = (TextView) findViewById(R.id.text1);
        textView2 = (TextView) findViewById(R.id.text2);
        textView3 = (TextView) findViewById(R.id.text3);

        textView1.setOnClickListener(new MyOnClickListener(0));
        textView2.setOnClickListener(new MyOnClickListener(1));
        textView3.setOnClickListener(new MyOnClickListener(2));
    }
    private void InitViewPager(){

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragments = new ArrayList<Fragment>();
        fragment1 = new View1_Fragment();
        fragment2 = new View2_Fragment();
        fragment3 = new View3_Fragment();
        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new MyOnPagerChangeListener());
    }
    private void InitSlider(){
        slider = findViewById(R.id.slider);
        slider.post(new Runnable() {
            @Override
            public void run() {
                slider.setLayoutParams(new FrameLayout.LayoutParams(slider.getWidth() / 3, slider.getHeight(), Gravity.BOTTOM));
            }
        });
    }
    private class MyOnClickListener implements View.OnClickListener {
        private int index=0;
        public MyOnClickListener(int i){
            index=i;
        }
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }
    }
    public class MyViewPagerAdapter extends FragmentPagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    class MyOnPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int i, float v, int i1) {
            int w = slider.getWidth();
            float x = w*i+w*v;
            slider.setX(x);
        }

        @Override
        public void onPageSelected(int i) {

        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_updata) {
            //这里来检测版本是否需要更新
            mUpdateManager = new UpdateManager(this);
            mUpdateManager.checkUpdateInfo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.yyy.xxx.airspace;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.cloudinary.Cloudinary;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        ViewPager.OnPageChangeListener,
        ACTIVITY_REQUEST{

    private static final String TAG = MainActivity.class.getName();

    @BindView(R.id.container)
    ViewPager mViewPager;

    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    ViewPageAdapter mViewPageAdapter;

    /**
     * 클라우너리 Config 함수
     * @return
     */
    public static Cloudinary onConfigCloudinary(){
        Map config = new HashMap();
        config.put("cloud_name", "defcu7txp");
        config.put("api_key", "937956612612147");
        config.put("api_secret", "jC6ZeVvYMCKeeVQEX2He_WAJ8A8");
        Cloudinary cloudinary = new Cloudinary(config);
        return cloudinary;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mViewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPageAdapter);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setCurrentItem(0);
        prevBottomNavigation = navigation.getMenu().getItem(0);
        mViewPager.setOffscreenPageLimit(3);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        mViewPager.setCurrentItem(0);
                        mViewPager.invalidate();
                        Log.d(TAG, "Page 0 selected");
                        return true;
                    case R.id.navigation_dashboard:
                        mViewPager.setCurrentItem(1);
                        mViewPager.invalidate();
                        Log.d(TAG, "Page 1 selected");
                        return true;
//                    case R.id.navigation_notifications:
//                        mViewPager.setCurrentItem(2);
//                        mViewPager.invalidate();
//                        Log.d(TAG, "Page 2 selected");
//                        return true;
                }
                return false;
            }
        });

    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d(TAG, "running on PageScroll");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.removeOnPageChangeListener(this);
    }

    private MenuItem prevBottomNavigation;

    @Override
    public void onPageSelected(int position) {
        if (prevBottomNavigation != null){
            prevBottomNavigation.setChecked(false);
        }
        prevBottomNavigation = navigation.getMenu().getItem(position);
        prevBottomNavigation.setChecked(true);
        mViewPageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode){
            case RESULT_OK_INPUT_BOARD :
                //TODO 리프레쉬?
                Log.d(TAG, "Refresh");
            }
        }
    }
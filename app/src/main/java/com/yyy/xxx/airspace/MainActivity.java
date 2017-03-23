package com.yyy.xxx.airspace;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import net.daum.mf.map.api.MapPoint;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, MapFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getName();

    private TextView mTextMessage;


    private MapFragment mMapFragment;

//    @BindView(R.id.container)
    ViewPager mViewPager;

//    @BindView(R.id.toolbar)
    Toolbar toolbar;

//    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    ViewPageAdapter mViewPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.container);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
                        return true;
                    case R.id.navigation_dashboard:
                        mViewPager.setCurrentItem(1);
                        return true;
                    case R.id.navigation_notifications:
                        mViewPager.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        });

    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

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
    public void onFragmentInteraction(MapPoint point) {
        mViewPager.setCurrentItem(1);
        BoardFragment.newInstance(point, null);
    }
}

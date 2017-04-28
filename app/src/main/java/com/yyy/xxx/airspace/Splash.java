package com.yyy.xxx.airspace;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by len on 2017. 4. 19..
 */

public class Splash extends AppCompatActivity {


    public static final String TAG = "Splash";
    public static final String WIFE_STATE = "WIFE";
    public static final String MOBILE_STATE = "MOBILE";
    public static final String NONE_STATE = "NONE";
    private static final int REQUEST_GPS = 10;
    private LocationManager locationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!isNetWork()){
            Log.d(TAG, "Net Ok");

            new AlertDialog.Builder(Splash.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("네트워크 연결 오류").setMessage("네트워크 연결후 다시한번 시도해 주시기 바랍니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        } else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            gpsConnectCheck();
            Log.d(TAG, "GPS Ok");
        } else if(!PermissionChecker.checkForGPS(Splash.this)){
            Log.d(TAG, "Perm Ok");
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }else {
            Log.d(TAG, "Perm Ok");
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }


    private Boolean isNetWork(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        boolean isMobileAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
        boolean isMobileConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWifiAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
        boolean isWifiConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        if ((isWifiAvailable && isWifiConnect) || (isMobileAvailable && isMobileConnect)){
            return true;
        }else{
            return false;
        }
    }
        /*
        * GPS 연결 다이얼로그
        * */
    public void gpsConnectCheck(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("위치 서비스 설정");
        alertDialog.setMessage("앱에서 위치정보를 사용하기 하려면 위치 서비스 권한을 설정해 주세요.");

        alertDialog.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // GPS설정 화면으로 이동
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUEST_GPS);
            }
        });

        alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Splash.this, "GPS 위성 사용을 체크하셔야 앱에 지도기능을 사용할 수 있습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}



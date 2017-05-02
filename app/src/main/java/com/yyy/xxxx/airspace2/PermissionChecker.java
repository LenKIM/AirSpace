package com.yyy.xxxx.airspace2;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by brilcom on 2016-07-26.
 */
public class PermissionChecker implements ACTIVITY_REQUEST{

    private static final int REQUEST_PERMISSION = 2;

    public static final String[] permissionList = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static final int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;

    public static boolean checkForGPS(Activity activity){
        final String[] permissions = {
                permissionList[0],
                permissionList[1]};

        if (ActivityCompat.checkSelfPermission(activity, permissions[0]) != PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, permissions[1]) != PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, permissionList, REQUEST_PERMISSION);
            return false;
        }

        return true;
    }

    public static boolean checkForGallery(Activity activity){
        final String[] permissions = {
                permissionList[2],
                permissionList[3]};

        if (ActivityCompat.checkSelfPermission(activity, permissions[0]) != PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(activity, permissions[1]) != PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, permissionList, REQUEST_PERMISSION);
            return false;
        }

        return true;
    }

    public static boolean checkALL(Activity activity){
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, permissionList, REQUEST_PERMISSION);
            return false;
        }

        return true;
    }
}

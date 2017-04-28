package com.yyy.xxx.airspace.Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by len on 2017. 3. 22..
 */

public class Board {

    private UUID mUUID;
    private String mMapPoint;
    private String mDescription;
    private String mTitle;
    private Date mDate;
    private String photoUri;

    public Board() {
        this(UUID.randomUUID());
    }

    public Board(UUID id) {
        mUUID = id;
        mTitle = null;
        mDescription = null;
        mMapPoint = null;
        mDate = new Date();
    }

    public void cleanBoard(){
        mUUID = null;
        mTitle = null;
        mDescription = null;
        mMapPoint = null;
        mDate = null;
    }
    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public UUID getUUID() {
        return mUUID;
    }

    public String getMapPoint() {
        return mMapPoint;
    }

    public void setMapPoint(String mapPoint) {
        mMapPoint = mapPoint;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDate() {
        //  Date로 들어온 데이터를 String으로 변환시켜줌
        SimpleDateFormat stringToDate = new SimpleDateFormat("yyyy-MM-dd");
        String StringDate = stringToDate.format(mDate);
        return StringDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}


package com.yyy.xxx.airspace.Model;

/**
 * Created by len on 2017. 3. 22..
 */

public class Board {

    private String mMapPoint;
    private String name;
    private String description;

    private static Board instance = new Board();

    public static Board getInstance(){
        return instance;
    }

    public Board() {
        name = null;
        description = null;
        mMapPoint = null;
    }

    public String getMapPoint() {
        return mMapPoint;
    }

    public void setMapPoint(String mapPoint) {
        mMapPoint = mapPoint;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}


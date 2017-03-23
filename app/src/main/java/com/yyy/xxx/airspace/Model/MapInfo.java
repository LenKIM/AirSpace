package com.yyy.xxx.airspace.Model;

import net.daum.mf.map.api.MapPoint;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by len on 2017. 3. 20..
 */

public class MapInfo implements Serializable {

    private UUID uuid;
    private MapPoint mMapPoint;

    private static MapInfo instance = new MapInfo();

    public MapInfo() {
        this.uuid = UUID.randomUUID();
        this.mMapPoint = null;

    }

    public static MapInfo getInstance(){
        return instance;
    }

    public MapInfo(UUID uuid) {
        this.uuid = uuid;

    }

    public UUID getUuid() {
        return uuid;
    }

    public MapPoint getMapPoint() {
        return mMapPoint;
    }

    public void setMapPoint(MapPoint mapPoint) {
        mMapPoint = mapPoint;
    }


}


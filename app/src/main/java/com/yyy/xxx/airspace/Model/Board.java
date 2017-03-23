package com.yyy.xxx.airspace.Model;

import android.graphics.Bitmap;

import java.util.UUID;

/**
 * Created by len on 2017. 3. 22..
 */

public class Board {

    private UUID mUUID;
    private String name;
    private String description;
    private Bitmap image;

    private static Board instance = new Board();

    public static Board getInstance(){
        return instance;
    }

    public Board() {
        name = null;
        description = null;
        image = null;
    }

    public UUID getUUID() {
        return mUUID;
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

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}


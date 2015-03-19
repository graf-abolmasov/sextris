package com.dreamjteam.android.sextries;

import android.graphics.Point;

class TetrisPieceRotation {
    public int mWidth;
    public int mHeight;
    public int mCenterX;
    public int mCenterY;
    public String mMap;

    private int mImageId;

    public Point[] mZone;

    public int getImageId() {
        return mImageId;
    }

    //Constructor
    public TetrisPieceRotation(int width, int height, int centerX, int centerY, String map, int imageId, Point[] zone) {
        mWidth = width;
        mHeight = height;
        mCenterX = centerX;
        mCenterY = centerY;
        mMap = map;
        mImageId = imageId;
        mZone = zone;
    }
}

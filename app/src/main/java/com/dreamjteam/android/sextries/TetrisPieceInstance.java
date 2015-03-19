package com.dreamjteam.android.sextries;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

class TetrisPieceInstance {
    public int centerX;
    public int centerY;
    private Integer drawCenterX = null;
    private Integer drawCenterY = null;
    public TetrisPiece piece;
    public int rotationIdx;

    TetrisPieceInstance() {
    }

    public TetrisPieceInstance(int centerX, int centerY, TetrisPiece piece, int rotationIdx) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.piece = piece;
        this.rotationIdx = rotationIdx;
    }

    public Integer getDrawCenterX() {
        return drawCenterX == null ? centerX : drawCenterX;
    }

    public void setDrawCenterX(Integer drawCenterX) {
        this.drawCenterX = drawCenterX;
    }

    public Integer getDrawCenterY() {
        return drawCenterY == null ? centerY : drawCenterY;
    }

    public void setDrawCenterY(Integer drawCenterY) {
        this.drawCenterY = drawCenterY;
    }

    public TetrisPieceRotation getTetrisPieceRotation() {
        return piece.mRotations[rotationIdx % piece.mRotations.length];
    }

    public List<Point> getEroZones() {
        final TetrisPieceRotation tetrisPieceRotation = getTetrisPieceRotation();
        final ArrayList<Point> result = new ArrayList<Point>(tetrisPieceRotation.mZone.length);
        for (Point p : tetrisPieceRotation.mZone)
            result.add(new Point(centerX + p.x, centerY + p.y));
        return result;
    }

    public boolean isApt(final TetrisPieceInstance otherInstance) {
        final List<Point> otherEroZones = otherInstance.getEroZones();
        for (Point otherEroZone : otherEroZones) {
            if (otherEroZone.x != centerX)
                continue;
            final int offsetY = otherEroZone.y - centerY;
            if (offsetY < 0)
                continue;

            final List<Point> myEroZones = getEroZones();
            for (Point myEroZone : myEroZones)
                if (myEroZone.x == otherInstance.centerX && myEroZone.y+offsetY == otherInstance.centerY) {
                    final int drawCenterY1 = centerY + offsetY < TileView.mYTileCount ? centerY + offsetY : TileView.mYTileCount - 1;
                    if (myEroZone.x == otherEroZone.x) {
//                        return false;
                        final int offsetX = 0;// piece.mColorCenter == Constants.GREEN_IMG ? -1 : 1;
                        final int drawCenterX1 = centerX + offsetX;
                        if (drawCenterX1 < TileView.mXTileCount && drawCenterX1 >= 0)
                            setDrawCenterX(drawCenterX1);
                    }
                    setDrawCenterY(drawCenterY1);
                    return true;
                }
        }
        return false;
    }
}

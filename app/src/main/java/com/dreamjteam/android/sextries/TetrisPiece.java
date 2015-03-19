package com.dreamjteam.android.sextries;

class TetrisPiece {
    public int mColor;
    public int mColorCenter;
    public TetrisPieceRotation[] mRotations;

    public TetrisPiece(int color, int colorCenter, TetrisPieceRotation[] rotations) {
        mColor = color;
        mColorCenter = colorCenter;
        mRotations = rotations;
    }
}

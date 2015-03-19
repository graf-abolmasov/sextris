package com.dreamjteam.android.sextries;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Map;
import java.util.Set;


/**
 * TileView: a View-variant designed for handling arrays of "icons" or other
 * drawables.
 */
public class TileView extends View {

    /**
     * Parameters controlling the size of the tiles and their range within view.
     * Width/Height are in pixels, and Drawable will be scaled to fit to these
     * dimensions. X/Y Tile Counts are the number of tiles that will be drawn.
     */

    protected static int mTileSize;

    protected static int mXTileCount;
    protected static int mYTileCount;

    protected static int mXOffset;
    protected static int mYOffset;

    private Bitmap background;


    /**
     * A hash that maps integer handles specified by the subclasses to the
     * drawable that will be used for that reference
     */
    private Bitmap[] mTileArray;

    /**
     * A two-dimensional array of integers in which the number represents the
     * index of the tile that should be drawn at that locations
     */
    private int[][] mTileGrid;

    protected final Paint mPaintTile = new Paint();
    protected final Paint mPaintBackground = new Paint();

    public TileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPaintBackground.setColor(Color.WHITE);
        mPaintBackground.setStrokeWidth(3);
        initTileView();
    }

    public TileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaintBackground.setColor(Color.WHITE);
        mPaintBackground.setStyle(Paint.Style.STROKE);
        mPaintBackground.setStrokeWidth(2);
        initTileView();
    }

    private void initTileView() {
        int w = getWidth() - Constants.BORDER*2;
        int h = getHeight()- Constants.BORDER*2;

        if (w > 0 && h > 0) {
            mXTileCount = Constants.X_TILE_COUNT;
            mYTileCount = Constants.Y_TYLE_COUNT;

            int xSize = (int) Math.floor(w / mXTileCount);
            int ySize = (int) Math.floor(h / mYTileCount);

            mTileSize = Math.min(xSize, ySize);

            mXOffset = ((w - (mTileSize * mXTileCount)) / 2);
            mYOffset = ((h - (mTileSize * mYTileCount)) / 2);

            mTileGrid = new int[mXTileCount][mYTileCount];
            clearTiles();
        } else {
            mXTileCount = 0;
            mYTileCount = 0;
            mTileSize = 0;
        }

    }

    public void setBackground(Bitmap background) {
        this.background = background;
    }

    /**
     * Rests the internal array of Bitmaps used for drawing tiles, and
     * sets the maximum index of tiles to be inserted
     *
     * @param tilecount
     */

    private void resetTiles(int tilecount) {
        mTileArray = new Bitmap[tilecount];
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        initTileView();
    }

    /**
     * Function to set the specified Drawable as the tile for a particular
     * integer key.
     *
     * @param key
     * @param tile
     */
    private void loadTile(int key, Drawable tile) {
        Bitmap bitmap = Bitmap.createBitmap(mTileSize, mTileSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tile.setBounds(0, 0, mTileSize, mTileSize);
        tile.draw(canvas);

        mTileArray[key] = bitmap;
    }

    /**
     * Resets all tiles to 0 (empty)
     */
    private void clearTiles() {
        for (int x = 0; x < mXTileCount; x++) {
            for (int y = 0; y < mYTileCount; y++) {
                setTile(0, x, y);
            }
        }
    }

    /**
     * Used to indicate that a particular tile (set with loadTile and referenced
     * by an integer) should be drawn at the given x/y coordinates during the
     * next invalidate/draw cycle.
     *
     * @param tileindex
     * @param x
     * @param y
     */
    public void setTile(int tileindex, int x, int y) {
        mTileGrid[x][y] = tileindex;
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (background != null) {
            canvas.drawBitmap(background, (int) (-(background.getWidth() - getWidth()) / 2.0), 0, mPaintBackground);
        }
        canvas.drawRect(mXOffset, mYOffset, mXOffset + mXTileCount * mTileSize, mYOffset + mYTileCount * mTileSize, mPaintBackground);
        for (int x = 0; x < mXTileCount; x++) {
            for (int y = 0; y < mYTileCount; y++) {
                if (mTileGrid[x][y] >= 0) {
                    canvas.drawBitmap(mTileArray[mTileGrid[x][y]],
                    		mXOffset + x * mTileSize,
                    		mYOffset + y * mTileSize,
                            mPaintTile);
                }
            }
        }

    }

    protected void loadTetrisImage(Map<Integer, Drawable> images) {
        resetTiles(Constants.IMG_COUNT);
        final Set<Integer> keys = images.keySet();
        for (Integer key : keys)
            loadTile(key, images.get(key));
    }
}

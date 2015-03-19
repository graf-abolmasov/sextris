package com.dreamjteam.android.sextries;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.*;

public class ResourcesCache {
    private Context mContext;
    private List<MediaPlayer> sounds = new ArrayList<MediaPlayer>();
    private Map<Integer, Drawable> tilesImages = new HashMap<Integer, Drawable>();
    private TetrisPiece[] tetrisPieces;

    private Map<Integer, Bitmap> figuresCache = new HashMap<Integer, Bitmap>();
    private Map<Integer, Bitmap> backgroundsCache = new HashMap<Integer, Bitmap>();

    private int[] backgroundIds = new int[] {R.drawable.b0, R.drawable.b1, R.drawable.b2};

    public static final Random RANDOM = new Random();

    public ResourcesCache(Context context) {
        mContext = context;
        loadSounds();
        loadTilesImage();
        initTetrisPieces();
    }

    public MediaPlayer getRandomSound() {
        if (sounds.size() == 0)
            return null;
        final int idx = RANDOM.nextInt(sounds.size());
        return sounds.get(idx);
    }

    public Map<Integer, Drawable> getTilesImages() {
        return tilesImages;
    }

    public TetrisPiece getRandomTetrisPiece() {
        return tetrisPieces[RANDOM.nextInt(tetrisPieces.length)];
    }

    public Bitmap getFigureImage(int tileSize, TetrisPieceRotation pieceRotation) {
        Bitmap bitmap;
        if ((bitmap = figuresCache.get(pieceRotation.getImageId())) == null)
            bitmap = loadFigureImage(tileSize, pieceRotation);
        return bitmap;
    }

    public Bitmap getRandomBackground(int height) {
        final int imageId = backgroundIds[RANDOM.nextInt(backgroundIds.length)];
        final Bitmap bitmap = backgroundsCache.get(imageId);
        return bitmap == null ? loadBackgroundImage(height, imageId) : bitmap;
    }

    public void reset() {
        figuresCache.clear();
        backgroundsCache.clear();
    }

    private void loadSounds() {
        try {
            for (MediaPlayer mediaPlayer : sounds)
                mediaPlayer.release();
            sounds.clear();
            sounds.add(MediaPlayer.create(mContext, R.raw.s0));
            sounds.add(MediaPlayer.create(mContext, R.raw.s1));
            sounds.add(MediaPlayer.create(mContext, R.raw.s2));
            sounds.add(MediaPlayer.create(mContext, R.raw.s3));
            sounds.add(MediaPlayer.create(mContext, R.raw.s4));
            sounds.add(MediaPlayer.create(mContext, R.raw.s5));
            sounds.add(MediaPlayer.create(mContext, R.raw.s6));
            sounds.add(MediaPlayer.create(mContext, R.raw.s7));
            sounds.add(MediaPlayer.create(mContext, R.raw.s8));
        } catch (Exception e) {
            Log.e("TetrisTileView", e.getLocalizedMessage(), e);
        }
    }

    private void loadTilesImage() {
        Resources r = mContext.getResources();
        tilesImages.put(Constants.BACK_IMG, r.getDrawable(R.drawable.tetris_back));
        tilesImages.put(Constants.MAGENTA_IMG, r.getDrawable(R.drawable.tetris_magenta));
        tilesImages.put(Constants.YELLOW_IMG, r.getDrawable(R.drawable.tetris_yellow));
        tilesImages.put(Constants.GREEN_IMG, r.getDrawable(R.drawable.tetris_green));
        tilesImages.put(Constants.BLUE_IMG, r.getDrawable(R.drawable.tetris_blue));
    }

    private void initTetrisPieces() {
        final ArrayList<TetrisPiece> tetrisPieceList = new ArrayList<TetrisPiece>();

        final Point[] point_3_0 = {new Point(-1, 0), new Point(0, 1), new Point(1, 0)};
        final Point[] point_3_90 = {new Point(-1, 0), new Point(0, -1), new Point(0, 1)};
        final Point[] point_3_180 = {new Point(-1, 0), new Point(0, -1), new Point(1, 0)};
        final Point[] point_3_270 = {new Point(0, -1), new Point(0, 1), new Point(1, 0)};

        //1 W
        TetrisPieceRotation[] tmpRotation = new TetrisPieceRotation[4];
        tmpRotation[0] = new TetrisPieceRotation(2, 3, 0, 2, "101011", R.drawable.w_1_0, point_3_0);
        tmpRotation[1] = new TetrisPieceRotation(3, 2, 0, 0, "111100", R.drawable.w_1_90, point_3_90);
        tmpRotation[2] = new TetrisPieceRotation(2, 3, 1, 0, "110101", R.drawable.w_1_180, point_3_180);
        tmpRotation[3] = new TetrisPieceRotation(3, 2, 2, 1, "001111", R.drawable.w_1_270, point_3_270);
        tetrisPieceList.add(new TetrisPiece(Constants.MAGENTA_IMG, Constants.YELLOW_IMG, tmpRotation));

        //1 W L
        tmpRotation = new TetrisPieceRotation[4];
        tmpRotation[0] = new TetrisPieceRotation(2, 3, 1, 2, "010111", R.drawable.w_1_0_l, point_3_0);
        tmpRotation[1] = new TetrisPieceRotation(3, 2, 0, 1, "100111", R.drawable.w_1_90_l, point_3_90);
        tmpRotation[2] = new TetrisPieceRotation(2, 3, 0, 0, "111010", R.drawable.w_1_180_l, point_3_180);
        tmpRotation[3] = new TetrisPieceRotation(3, 2, 2, 0, "111001", R.drawable.w_1_270_l, point_3_270);
        tetrisPieceList.add(new TetrisPiece(Constants.MAGENTA_IMG, Constants.YELLOW_IMG, tmpRotation));

        //5 M
        tmpRotation = new TetrisPieceRotation[4];
        tmpRotation[0] = new TetrisPieceRotation(2, 4, 1, 2, "01010111", R.drawable.m_5_0, new Point[]{new Point(1, 0), new Point(0, 1)});
        tmpRotation[1] = new TetrisPieceRotation(4, 2, 1, 1, "10001111", R.drawable.m_5_90, new Point[]{new Point(-1, 0), new Point(0, 1)});
        tmpRotation[2] = new TetrisPieceRotation(2, 4, 0, 1, "11101010", R.drawable.m_5_180, new Point[]{new Point(-1, 0), new Point(0, -1)});
        tmpRotation[3] = new TetrisPieceRotation(4, 2, 2, 0, "11110001", R.drawable.m_5_270, new Point[]{new Point(1, 0), new Point(0, -1)});
        tetrisPieceList.add(new TetrisPiece(Constants.BLUE_IMG, Constants.GREEN_IMG, tmpRotation));

//        //2 W L
//        tmpRotation = new TetrisPieceRotation[4];
//        tmpRotation[0] = new TetrisPieceRotation(2, 2, 1, 1, "1111", R.drawable.w_2_0, new Point[]{new Point(1, 0), new Point(0, 1)});
//        tmpRotation[1] = new TetrisPieceRotation(2, 2, 0, 1, "1111", R.drawable.w_2_90, new Point[]{new Point(-1, 0), new Point(0, 1)});
//        tmpRotation[2] = new TetrisPieceRotation(2, 2, 0, 0, "1111", R.drawable.w_2_180, new Point[]{new Point(-1, 0), new Point(0, -1)});
//        tmpRotation[3] = new TetrisPieceRotation(2, 2, 1, 0, "1111", R.drawable.w_2_270, new Point[]{new Point(0, -1), new Point(1, 0)});
//        tetrisPieceList.add(new TetrisPiece(Constants.MAGENTA_IMG, Constants.YELLOW_IMG, tmpRotation));

        //7 M
        tmpRotation = new TetrisPieceRotation[4];
        tmpRotation[0] = new TetrisPieceRotation(2, 4, 0, 2, "10101101", R.drawable.m_7_0, new Point[]{new Point(1, 0), new Point(0, 1)});
        tmpRotation[1] = new TetrisPieceRotation(4, 2, 1, 0, "01111100", R.drawable.m_7_90, new Point[]{new Point(-1, 0), new Point(0, 1)});
        tmpRotation[2] = new TetrisPieceRotation(2, 4, 1, 1, "10110101", R.drawable.m_7_180, new Point[]{new Point(-1, 0), new Point(0, -1)});
        tmpRotation[3] = new TetrisPieceRotation(4, 2, 2, 1, "00111110", R.drawable.m_7_270, new Point[]{new Point(1, 0), new Point(0, -1)});
        tetrisPieceList.add(new TetrisPiece(Constants.BLUE_IMG, Constants.GREEN_IMG, tmpRotation));

        //4 W
        tmpRotation = new TetrisPieceRotation[4];
        tmpRotation[0] = new TetrisPieceRotation(3, 3, 0, 2, "100100111", R.drawable.w_4_0, point_3_0);
        tmpRotation[1] = new TetrisPieceRotation(3, 3, 0, 0, "111100100", R.drawable.w_4_90, point_3_90);
        tmpRotation[2] = new TetrisPieceRotation(3, 3, 2, 0, "111001001", R.drawable.w_4_180, point_3_180);
        tmpRotation[3] = new TetrisPieceRotation(3, 3, 2, 2, "001001111", R.drawable.w_4_270, point_3_270);
        tetrisPieceList.add(new TetrisPiece(Constants.MAGENTA_IMG, Constants.YELLOW_IMG, tmpRotation));

        //4 W L
        tmpRotation = new TetrisPieceRotation[4];
        tmpRotation[0] = new TetrisPieceRotation(3, 3, 2, 2, "001001111", R.drawable.w_4_0_l, point_3_0);
        tmpRotation[1] = new TetrisPieceRotation(3, 3, 0, 2, "100100111", R.drawable.w_4_90_l, point_3_90);
        tmpRotation[2] = new TetrisPieceRotation(3, 3, 0, 0, "111100100", R.drawable.w_4_180_l, point_3_180);
        tmpRotation[3] = new TetrisPieceRotation(3, 3, 2, 0, "111001001", R.drawable.w_4_270_l, point_3_270);
        tetrisPieceList.add(new TetrisPiece(Constants.MAGENTA_IMG, Constants.YELLOW_IMG, tmpRotation));

        //8 M
        tmpRotation = new TetrisPieceRotation[4];
        tmpRotation[0] = new TetrisPieceRotation(2, 4, 0, 2, "10101110", R.drawable.m_8_0, new Point[]{new Point(1, 0), new Point(0, 1)});
        tmpRotation[1] = new TetrisPieceRotation(4, 2, 1, 0, "11110100", R.drawable.m_8_90, new Point[]{new Point(-1, 0), new Point(0, 1)});
        tmpRotation[2] = new TetrisPieceRotation(2, 4, 1, 1, "01110101", R.drawable.m_8_180, new Point[]{new Point(-1, 0), new Point(0, -1)});
        tmpRotation[3] = new TetrisPieceRotation(4, 2, 2, 1, "00101111", R.drawable.m_8_270, new Point[]{new Point(1, 0), new Point(0, -1)});
        tetrisPieceList.add(new TetrisPiece(Constants.BLUE_IMG, Constants.GREEN_IMG, tmpRotation));

//        //10 W
//        tmpRotation = new TetrisPieceRotation[4];
//        tmpRotation[0] = new TetrisPieceRotation(3, 2, 0, 1, "110111", R.drawable.w_10_0, new Point[]{new Point(-1, 0), new Point(0, 1)});
//        tmpRotation[1] = new TetrisPieceRotation(2, 3, 0, 0, "111110", R.drawable.w_10_90, new Point[]{new Point(-1, 0), new Point(0, -1)});
//        tmpRotation[2] = new TetrisPieceRotation(3, 2, 2, 0, "111011", R.drawable.w_10_180, new Point[]{new Point(0, -1), new Point(1, 0)});
//        tmpRotation[3] = new TetrisPieceRotation(2, 3, 1, 2, "011111", R.drawable.w_10_270, new Point[]{new Point(1, 0), new Point(0, 1)});
//        tetrisPieceList.add(new TetrisPiece(Constants.MAGENTA_IMG, Constants.YELLOW_IMG, tmpRotation));

        //9 M
        tmpRotation = new TetrisPieceRotation[4];
        tmpRotation[0] = new TetrisPieceRotation(3, 3, 0, 2, "100100111", R.drawable.m_9_0, new Point[]{new Point(1, 0), new Point(0, 1)});
        tmpRotation[1] = new TetrisPieceRotation(3, 3, 0, 0, "111100100", R.drawable.m_9_90, new Point[]{new Point(-1, 0), new Point(0, 1)});
        tmpRotation[2] = new TetrisPieceRotation(3, 3, 2, 0, "111001001", R.drawable.m_9_180, new Point[]{new Point(-1, 0), new Point(0, -1)});
        tmpRotation[3] = new TetrisPieceRotation(3, 3, 2, 2, "001001111", R.drawable.m_9_270, new Point[]{new Point(1, 0), new Point(0, -1)});
        tetrisPieceList.add(new TetrisPiece(Constants.BLUE_IMG, Constants.GREEN_IMG, tmpRotation));

        //11 W
        tmpRotation = new TetrisPieceRotation[4];
        tmpRotation[0] = new TetrisPieceRotation(3, 3, 0, 2, "100111100", R.drawable.w_11_0, point_3_0);
        tmpRotation[1] = new TetrisPieceRotation(3, 3, 0, 0, "111010010", R.drawable.w_11_90, point_3_90);
        tmpRotation[2] = new TetrisPieceRotation(3, 3, 2, 0, "001111001", R.drawable.w_11_180, point_3_180);
        tmpRotation[3] = new TetrisPieceRotation(3, 3, 2, 2, "010010111", R.drawable.w_11_270, point_3_270);
        tetrisPieceList.add(new TetrisPiece(Constants.MAGENTA_IMG, Constants.YELLOW_IMG, tmpRotation));

        //11 W L
        tmpRotation = new TetrisPieceRotation[4];
        tmpRotation[0] = new TetrisPieceRotation(3, 3, 2, 2, "001111001", R.drawable.w_11_0_l, point_3_0);
        tmpRotation[1] = new TetrisPieceRotation(3, 3, 0, 2, "010010111", R.drawable.w_11_90_l, point_3_90);
        tmpRotation[2] = new TetrisPieceRotation(3, 3, 0, 0, "100111100", R.drawable.w_11_180_l, point_3_180);
        tmpRotation[3] = new TetrisPieceRotation(3, 3, 2, 0, "111010010", R.drawable.w_11_270_l, point_3_270);
        tetrisPieceList.add(new TetrisPiece(Constants.MAGENTA_IMG, Constants.YELLOW_IMG, tmpRotation));

        //12 W
        tmpRotation = new TetrisPieceRotation[4];
        tmpRotation[0] = new TetrisPieceRotation(2, 4, 0, 2, "10101110", R.drawable.w_12_0, point_3_0);
        tmpRotation[1] = new TetrisPieceRotation(4, 2, 1, 0, "11110100", R.drawable.w_12_90, point_3_90);
        tmpRotation[2] = new TetrisPieceRotation(2, 4, 1, 1, "01110101", R.drawable.w_12_180, point_3_180);
        tmpRotation[3] = new TetrisPieceRotation(4, 2, 2, 1, "00101111", R.drawable.w_12_270, point_3_270);
        tetrisPieceList.add(new TetrisPiece(Constants.MAGENTA_IMG, Constants.YELLOW_IMG, tmpRotation));

        //12 W L
        tmpRotation = new TetrisPieceRotation[4];
        tmpRotation[0] = new TetrisPieceRotation(2, 4, 1, 2, "01011101", R.drawable.w_12_0_l, point_3_0);
        tmpRotation[1] = new TetrisPieceRotation(4, 2, 1, 1, "01001111", R.drawable.w_12_90_l, point_3_90);
        tmpRotation[2] = new TetrisPieceRotation(2, 4, 0, 1, "10111010", R.drawable.w_12_180_l, point_3_180);
        tmpRotation[3] = new TetrisPieceRotation(4, 2, 2, 0, "11110010", R.drawable.w_12_270_l, point_3_270);
        tetrisPieceList.add(new TetrisPiece(Constants.MAGENTA_IMG, Constants.YELLOW_IMG, tmpRotation));

        //3 M L
        tmpRotation = new TetrisPieceRotation[4];
        tmpRotation[0] = new TetrisPieceRotation(1, 5, 0, 2, "11111", R.drawable.m_3_0, new Point[]{new Point(-1, 0), new Point(0, 1)});
        tmpRotation[1] = new TetrisPieceRotation(5, 1, 2, 0, "11111", R.drawable.m_3_90, new Point[]{new Point(-1, 0), new Point(0, -1)});
        tmpRotation[2] = new TetrisPieceRotation(1, 5, 0, 2, "11111", R.drawable.m_3_180, new Point[]{new Point(0, -1), new Point(1, 0)});
        tmpRotation[3] = new TetrisPieceRotation(5, 1, 2, 0, "11111", R.drawable.m_3_270, new Point[]{new Point(0, 1), new Point(1, 0)});
        tetrisPieceList.add(new TetrisPiece(Constants.BLUE_IMG, Constants.GREEN_IMG, tmpRotation));

        //13 W
        tmpRotation = new TetrisPieceRotation[4];
        tmpRotation[0] = new TetrisPieceRotation(2, 3, 0, 2, "111011", R.drawable.w_13_0, point_3_0);
        tmpRotation[1] = new TetrisPieceRotation(3, 2, 0, 0, "111101", R.drawable.w_13_90, point_3_90);
        tmpRotation[2] = new TetrisPieceRotation(2, 3, 1, 0, "110111", R.drawable.w_13_180, point_3_180);
        tmpRotation[3] = new TetrisPieceRotation(3, 2, 2, 1, "101111", R.drawable.w_13_270, point_3_270);
        tetrisPieceList.add(new TetrisPiece(Constants.MAGENTA_IMG, Constants.YELLOW_IMG, tmpRotation));

        //13 W L
        tmpRotation = new TetrisPieceRotation[4];
        tmpRotation[0] = new TetrisPieceRotation(2, 3, 1, 2, "110111", R.drawable.w_13_0_l, point_3_0);
        tmpRotation[1] = new TetrisPieceRotation(3, 2, 0, 1, "101111", R.drawable.w_13_90_l, point_3_90);
        tmpRotation[2] = new TetrisPieceRotation(2, 3, 0, 0, "111011", R.drawable.w_13_180_l, point_3_180);
        tmpRotation[3] = new TetrisPieceRotation(3, 2, 2, 0, "111101", R.drawable.w_13_270_l, point_3_270);
        tetrisPieceList.add(new TetrisPiece(Constants.MAGENTA_IMG, Constants.YELLOW_IMG, tmpRotation));

        //6 W
        tmpRotation = new TetrisPieceRotation[4];
        tmpRotation[0] = new TetrisPieceRotation(2, 4, 0, 2, "10101101", R.drawable.w_6_0, point_3_0);
        tmpRotation[1] = new TetrisPieceRotation(4, 2, 1, 0, "01111100", R.drawable.w_6_90, point_3_90);
        tmpRotation[2] = new TetrisPieceRotation(2, 4, 1, 1, "10110101", R.drawable.w_6_180, point_3_180);
        tmpRotation[3] = new TetrisPieceRotation(4, 2, 2, 1, "00111110", R.drawable.w_6_270, point_3_270);
        tetrisPieceList.add(new TetrisPiece(Constants.MAGENTA_IMG, Constants.YELLOW_IMG, tmpRotation));

        //6 W L
        tmpRotation = new TetrisPieceRotation[4];
        tmpRotation[0] = new TetrisPieceRotation(2, 4, 1, 2, "01011110", R.drawable.w_6_0_l, point_3_0);
        tmpRotation[1] = new TetrisPieceRotation(4, 2, 1, 1, "11000111", R.drawable.w_6_90_l, point_3_90);
        tmpRotation[2] = new TetrisPieceRotation(2, 4, 0, 1, "01111010", R.drawable.w_6_180_l, point_3_180);
        tmpRotation[3] = new TetrisPieceRotation(4, 2, 2, 0, "11100011", R.drawable.w_6_270_l, point_3_270);
        tetrisPieceList.add(new TetrisPiece(Constants.MAGENTA_IMG, Constants.YELLOW_IMG, tmpRotation));

        tetrisPieces = new TetrisPiece[tetrisPieceList.size()];
        tetrisPieces = tetrisPieceList.toArray(tetrisPieces);
    }

    private Bitmap loadFigureImage(int tileSize, TetrisPieceRotation pieceRotation) {
        final Bitmap bitmap = Bitmap.createBitmap(tileSize*pieceRotation.mWidth, tileSize*pieceRotation.mHeight, Bitmap.Config.ARGB_8888);
        final Canvas tempCanvas = new Canvas(bitmap);
        final Resources r = mContext.getResources();
        final Drawable drawable = r.getDrawable(pieceRotation.getImageId());
        drawable.setBounds(0, 0, tileSize * pieceRotation.mWidth, tileSize * pieceRotation.mHeight);
        drawable.draw(tempCanvas);
        figuresCache.put(pieceRotation.getImageId(), bitmap);
        return bitmap;
    }

    private Bitmap loadBackgroundImage(int height, int imageId) {
        final Resources r = mContext.getResources();
        final Drawable image = r.getDrawable(imageId);

        double scale = height / 1.0 / image.getIntrinsicHeight();
        int width = (int)(image.getIntrinsicWidth() * scale);

        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas tempCanvas = new Canvas(bitmap);
        image.setBounds(0, 0, width, height);
        image.draw(tempCanvas);
        backgroundsCache.put(imageId, bitmap);
        return bitmap;
    }
}

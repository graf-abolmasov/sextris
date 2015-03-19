package com.dreamjteam.android.sextries;

import java.util.*;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class TetrisTileView extends TileView {
    public static final Random RND = new Random(System.currentTimeMillis());

    private final ResourcesCache resourcesCache;

    private int mMergeTimes;
    private ArrayList<TetrisPieceInstance> mMergeFigures = new ArrayList<TetrisPieceInstance>();
    private MergeHandler mMergeHandler = new MergeHandler();

    private int[][] mBoard;
    private Map<Point, TetrisPieceInstance> mBoardMap;
    private Map<Point, TetrisPieceInstance> mFigureCentresMap;

    private int mControlMethod;
    private boolean mIsTouchMove = false;
    private long mTouchDownTime;
    private float mTouchDownX, mTouchDownY;

    private RefreshHandler mRedrawHandler = new RefreshHandler();

    private OnGameEventListener mGameEventListener;

    private int mMode = Constants.READY;

    private boolean mFalling = false;
    private TetrisPiece mNextPiece = null;
    private TetrisPieceInstance mFallingPieceInstance;

    public TetrisTileView(Context context, AttributeSet attris) {
        super(context, attris);
        resourcesCache = new ResourcesCache(context);
        initTetrisView();
    }

    private int score;
    private long speed;

    public TetrisTileView(Context context, AttributeSet attris, int defStyle) {
        super(context, attris, defStyle);
        resourcesCache = new ResourcesCache(context);
        initTetrisView();
    }

    void clearTetrisPiece(int centerX, int centerY, TetrisPiece piece, int rotationIndex) {
        TetrisPieceRotation rotation = piece.mRotations[rotationIndex];

        int xStart = centerX - rotation.mCenterX;
        int x = xStart;
        int y = centerY - rotation.mCenterY;
        int dataPos = 0;

        //for rows
        for (int r = 0; r < rotation.mHeight; r++) {
            //for columns
            for (int c = 0; c < rotation.mWidth; c++) {
                char flag = rotation.mMap.charAt(dataPos);
                if (flag == '1')
                    setTile(Constants.BACK_IMG, x, y);
                x++;
                dataPos++;
            }
            x = xStart;
            y++;
        }
    }

    void drawTetrisPiece(final TetrisPieceInstance pieceInstance) {
        if (pieceInstance == null)
            return;

        TetrisPieceRotation rotation = pieceInstance.getTetrisPieceRotation();

        int xStart = pieceInstance.centerX - rotation.mCenterX;
        int x = xStart;
        int y = pieceInstance.centerY - rotation.mCenterY;
        int dataPos = 0;

        //for rows
        for (int r = 0; r < rotation.mHeight; r++) {
            //for columns
            for (int c = 0; c < rotation.mWidth; c++) {
                char flag = rotation.mMap.charAt(dataPos);
                if (flag == '1') {
                    setTile(x == pieceInstance.centerX && y == pieceInstance.centerY ? pieceInstance.piece.mColorCenter : pieceInstance.piece.mColor, x, y);
                }
                x++;
                dataPos++;
            }
            x = xStart;
            y++;
        }
    } //drawPiece

    boolean canExists(TetrisPieceRotation rotation, int centerX, int centerY) {
        int xStart = centerX - rotation.mCenterX;
        int x = xStart;
        int y = centerY - rotation.mCenterY;
        int dataPos = 0;

        //for rows
        for (int r = 0; r < rotation.mHeight; r++) {
            //for columns
            for (int c = 0; c < rotation.mWidth; c++) {
                if (x >= 0 && x < mXTileCount && y >= 0 && y < mYTileCount) {
                    char flag = rotation.mMap.charAt(dataPos);
                    if (flag == '1') {
                        if (mBoard[x][y] != Constants.BACK_IMG)
                            return false;
                    }
                } else //out of board boundary
                    return false;
                x++;
                dataPos++;
            }
            x = xStart;
            y++;
        }
        return true;
    }

    private boolean moveDown(TetrisPieceInstance pieceInstance) {
        if (pieceInstance == null)
            return false;

        if (canExists(pieceInstance.getTetrisPieceRotation(), pieceInstance.centerX, pieceInstance.centerY + 1)) {
            pieceInstance.centerY++;
            return true;
        }
        return false;
    }

    private boolean moveLeft() {
        if (mFallingPieceInstance == null)
            return false;

        if (canExists(mFallingPieceInstance.getTetrisPieceRotation(), mFallingPieceInstance.centerX - 1, mFallingPieceInstance.centerY)) {
            mFallingPieceInstance.centerX--;
            return true;
        }
        return false;
    }

    private boolean moveRight() {
        if (mFallingPieceInstance == null)
            return false;

        if (canExists(mFallingPieceInstance.getTetrisPieceRotation(), mFallingPieceInstance.centerX + 1, mFallingPieceInstance.centerY)) {
            mFallingPieceInstance.centerX++;
            return true;
        }
        return false;
    }

    private boolean rotate() {
        if (mFallingPieceInstance == null)
            return false;

        int rotation = mFallingPieceInstance.rotationIdx - 1;
        if (rotation == -1)
            rotation = mFallingPieceInstance.piece.mRotations.length - 1;
        if (canExists(mFallingPieceInstance.piece.mRotations[rotation], mFallingPieceInstance.centerX, mFallingPieceInstance.centerY)) {
            mFallingPieceInstance.rotationIdx = rotation;
            return true;
        }
        return false;
    }

    private void merge(final TetrisPieceInstance pieceInstance) {
        int img = pieceInstance.piece.mColor;
        int imgCenter = pieceInstance.piece.mColorCenter;
        final TetrisPieceRotation rotation = pieceInstance.getTetrisPieceRotation();

        int xStart = (pieceInstance.centerX - rotation.mCenterX);
        int x = xStart;
        int y = (pieceInstance.centerY - rotation.mCenterY);
        int dataPos = 0;

        //for rows
        for (int r = 0; r < rotation.mHeight; r++) {
            //for columns
            for (int c = 0; c < rotation.mWidth; c++) {
                char flag = rotation.mMap.charAt(dataPos);
                if (flag == '1') {
                    mBoard[x][y] = (x == pieceInstance.centerX && y == pieceInstance.centerY ? imgCenter : img);
                    mBoardMap.put(new Point(x,y), pieceInstance);
                }
                x++;
                dataPos++;
            }
            x = xStart;
            y++;
        }

        mFigureCentresMap.put(new Point(pieceInstance.centerX, pieceInstance.centerY), pieceInstance);
    }

    private void mergeAnimations() {
        if (mMode == Constants.RUNNING) {
            switch (mMergeTimes) {
                case 0:
                    final MediaPlayer randomSound = resourcesCache.getRandomSound();
                    if (randomSound != null)
                        randomSound.start();
                case 2:
                case 4: //Disappear
                    for (TetrisPieceInstance tmpFigure : mMergeFigures) {
                        final TetrisPieceRotation rotation = tmpFigure.getTetrisPieceRotation();

                        int xStart = (tmpFigure.centerX - rotation.mCenterX);
                        int x = xStart;
                        int y = (tmpFigure.centerY - rotation.mCenterY);
                        int dataPos = 0;

                        //for rows
                        for (int r = 0; r < rotation.mHeight; r++) {
                            //for columns
                            for (int c = 0; c < rotation.mWidth; c++) {
                                char flag = rotation.mMap.charAt(dataPos);
                                if (flag == '1') {
                                    mBoard[x][y] = Constants.BACK_IMG;
                                }
                                x++;
                                dataPos++;
                            }
                            x = xStart;
                            y++;
                        }
                    }
                    break;
                case 1:
                case 3://Re-appear
                    for (TetrisPieceInstance tmpFigure : mMergeFigures) {
                        int img = tmpFigure.piece.mColor;
                        int imgCenter = tmpFigure.piece.mColorCenter;
                        final TetrisPieceRotation rotation = tmpFigure.getTetrisPieceRotation();

                        int xStart = (tmpFigure.centerX - rotation.mCenterX);
                        int x = xStart;
                        int y = (tmpFigure.centerY - rotation.mCenterY);
                        int dataPos = 0;

                        //for rows
                        for (int r = 0; r < rotation.mHeight; r++) {
                            //for columns
                            for (int c = 0; c < rotation.mWidth; c++) {
                                char flag = rotation.mMap.charAt(dataPos);
                                if (flag == '1') {
                                    mBoard[x][y] = (x == tmpFigure.centerX && y == tmpFigure.centerY ? imgCenter : img);
                                }
                                x++;
                                dataPos++;
                            }
                            x = xStart;
                            y++;
                        }
                    }
                    break;
                case 5: //Remove merging lines
                    for (TetrisPieceInstance tmpFigure : mMergeFigures) {
                        final TetrisPieceRotation rotation = tmpFigure.getTetrisPieceRotation();

                        int xStart = (tmpFigure.centerX - rotation.mCenterX);
                        int x = xStart;
                        int y = (tmpFigure.centerY - rotation.mCenterY);
                        int dataPos = 0;

                        //for rows
                        for (int r = 0; r < rotation.mHeight; r++) {
                            //for columns
                            for (int c = 0; c < rotation.mWidth; c++) {
                                char flag = rotation.mMap.charAt(dataPos);
                                if (flag == '1') {
                                    mBoard[x][y] = Constants.BACK_IMG;
                                    mBoardMap.remove(new Point(x,y));
                                }
                                x++;
                                dataPos++;
                            }
                            x = xStart;
                            y++;
                        }
                        mFigureCentresMap.remove(new Point(tmpFigure.centerX, tmpFigure.centerY));
                    }
                    break;
                case 6: //Start running again
                    update();
                    return;

            }
            mMergeTimes++;
            drawBoard();
            invalidate();
            mMergeHandler.sleep(Constants.MERGE_ANIMATION_TIME);
        } else if (mMode == Constants.PAUSE) {
            mMergeHandler.sleep(Constants.PAUSE_REFRESH_DELAY);
        }
    }

    private boolean findMergingFigures() {
        mMergeFigures.clear();
        final Collection<TetrisPieceInstance> closeFigures = findSiblingFigures(mFallingPieceInstance);
        for (TetrisPieceInstance closeFigure : closeFigures)
            if (mFallingPieceInstance.isApt(closeFigure)) {
                mMergeFigures.add(closeFigure);
                break;
            }
        if (mMergeFigures.isEmpty())
            return false;
        mMergeFigures.add(mFallingPieceInstance);
        mMergeTimes = 0;
        return true;
    }

    private Collection<TetrisPieceInstance> findSiblingFigures(TetrisPieceInstance pieceInstance) {
        final Set<TetrisPieceInstance> result = new HashSet<TetrisPieceInstance>();
        final Set<Point> checkedPoints = new HashSet<Point>();

        final TetrisPieceRotation rotation = pieceInstance.getTetrisPieceRotation();
        int xStart = (pieceInstance.centerX - rotation.mCenterX);
        int x = xStart;
        int y = (pieceInstance.centerY - rotation.mCenterY);
        int dataPos = 0;

        //for rows
        for (int r = 0; r < rotation.mHeight; r++) {
            //for columns
            for (int c = 0; c < rotation.mWidth; c++) {
                char flag = rotation.mMap.charAt(dataPos);
                if (flag == '1') {
                    checkedPoints.add(new Point(x + 1, y));
                    checkedPoints.add(new Point(x + 1, y - 1));
                    checkedPoints.add(new Point(x, y - 1));
                    checkedPoints.add(new Point(x - 1, y - 1));
                    checkedPoints.add(new Point(x - 1, y));
                    checkedPoints.add(new Point(x - 1, y + 1));
                    checkedPoints.add(new Point(x, y + 1));
                    checkedPoints.add(new Point(x + 1, y + 1));
                }
                x++;
                dataPos++;
            }
            x = xStart;
            y++;
        }

        for (Point p : checkedPoints) {
            final TetrisPieceInstance siblingFigure = mBoardMap.get(p);
            if (siblingFigure != null && pieceInstance.piece.mColorCenter != siblingFigure.piece.mColorCenter)
                result.add(siblingFigure);
        }
        return result;
    }

    private void clearBoard() {
        for (int x = 0; x < mXTileCount; x++) {
            for (int y = 0; y < mYTileCount; y++) {
                mBoard[x][y] = Constants.BACK_IMG;
            }
        }
        if (mFigureCentresMap != null)
            mFigureCentresMap.clear();
        if (mBoardMap != null)
            mBoardMap.clear();
    }

    private void drawBoard() {
        if (mBoard == null)
            return;
        for (int x = 0; x < mXTileCount; x++) {
            for (int y = 0; y < mYTileCount; y++) {
                setTile(mBoard[x][y], x, y);
            }
        }
    }

    private void initTetrisView() {
        int w = getWidth();
        int h = getHeight();

        if (w > 0 && h > 0) {
            loadTetrisImage(resourcesCache.getTilesImages());
            setBackground(resourcesCache.getRandomBackground(h));
            mBoard = new int[mXTileCount][mYTileCount];
            mBoardMap = new HashMap<Point, TetrisPieceInstance>(mXTileCount*mYTileCount);
            mFigureCentresMap = new HashMap<Point, TetrisPieceInstance>();
            clearBoard();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        resourcesCache.reset();

        if (mMode != Constants.READY) { //Not Initialization
            setBackground(resourcesCache.getRandomBackground(h));
            loadTetrisImage(resourcesCache.getTilesImages());
            drawBoard();
            drawTetrisPiece(mFallingPieceInstance);
        } else
            initTetrisView();
    }

    public void setControlMethod(int method) {
        mControlMethod = method;
        if (method == Constants.CONTROL_KEYBOARD) {
            setFocusable(true);
            setFocusableInTouchMode(true);
        } else {
            setFocusable(false);
            setFocusableInTouchMode(false);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mMode != Constants.RUNNING || mControlMethod != Constants.CONTROL_TOUCH)
            return true;

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsTouchMove = false;
                mTouchDownTime = System.currentTimeMillis();
                mTouchDownX = x;
                mTouchDownY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - mTouchDownX;
                float dy = y - mTouchDownY;

                //A valid move
                if (Math.abs(dx) >= mTileSize || Math.abs(dy) >= mTileSize) {
                    mIsTouchMove = true;
                    mTouchDownX = x;
                    mTouchDownY = y;

                    //Move in X direction
                    if (Math.abs(dx) >= Math.abs(dy)) {
                        int tetrisMoveX = (int) (dx / (float) (mTileSize));
                        //move right
                        if (tetrisMoveX > 0) {
                            for (int moveX = 0; moveX < tetrisMoveX; moveX++) {
                                if (moveRight()) {
                                    clearTetrisPiece(mFallingPieceInstance.centerX - 1, mFallingPieceInstance.centerY, mFallingPieceInstance.piece, mFallingPieceInstance.rotationIdx);
                                    drawTetrisPiece(mFallingPieceInstance);
                                    invalidate();
                                } else
                                    break;
                            }
                        }
                        //move left
                        else if (tetrisMoveX < 0) {
                            for (int moveX = 0; moveX > tetrisMoveX; moveX--) {
                                if (moveLeft()) {
                                    clearTetrisPiece(mFallingPieceInstance.centerX + 1, mFallingPieceInstance.centerY, mFallingPieceInstance.piece, mFallingPieceInstance.rotationIdx);
                                    drawTetrisPiece(mFallingPieceInstance);
                                    invalidate();
                                } else
                                    break;
                            }
                        }

                    }
                    //Move in Y direction (Only move down, no up)
                    else if (dy > 0) {
                        int tetrisMoveY = (int) (dy / (float) mTileSize);
                        if (tetrisMoveY != 0) {
                            for (int moveY = 0; moveY < tetrisMoveY; moveY++) {
                                if (moveDown(mFallingPieceInstance)) {
                                    clearTetrisPiece(mFallingPieceInstance.centerX, mFallingPieceInstance.centerY - 1, mFallingPieceInstance.piece, mFallingPieceInstance.rotationIdx);
                                    drawTetrisPiece(mFallingPieceInstance);
                                    invalidate();
                                } else
                                    break;
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                long now = System.currentTimeMillis();

                //A valid rotation
                if (mFallingPieceInstance != null && !mIsTouchMove && now - mTouchDownTime < Constants.TOUCH_UP_THRESHOLD) {
                    int oldRotation = mFallingPieceInstance.rotationIdx;
                    if (rotate()) {
                        clearTetrisPiece(mFallingPieceInstance.centerX, mFallingPieceInstance.centerY, mFallingPieceInstance.piece, oldRotation);
                        drawTetrisPiece(mFallingPieceInstance);
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) //ESC
            setFocusable(false);
        if (mMode != Constants.RUNNING || mControlMethod != Constants.CONTROL_KEYBOARD)
            return true;

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            int oldRotation = mFallingPieceInstance.rotationIdx;
            if (rotate()) {
                clearTetrisPiece(mFallingPieceInstance.centerX, mFallingPieceInstance.centerY, mFallingPieceInstance.piece, oldRotation);
                drawTetrisPiece(mFallingPieceInstance);
                invalidate();
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (moveLeft()) {
                clearTetrisPiece(mFallingPieceInstance.centerX + 1, mFallingPieceInstance.centerY, mFallingPieceInstance.piece, mFallingPieceInstance.rotationIdx);
                drawTetrisPiece(mFallingPieceInstance);
                invalidate();
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (moveRight()) {
                clearTetrisPiece(mFallingPieceInstance.centerX - 1, mFallingPieceInstance.centerY, mFallingPieceInstance.piece, mFallingPieceInstance.rotationIdx);
                drawTetrisPiece(mFallingPieceInstance);
                invalidate();
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (moveDown(mFallingPieceInstance)) {
                clearTetrisPiece(mFallingPieceInstance.centerX, mFallingPieceInstance.centerY - 1, mFallingPieceInstance.piece, mFallingPieceInstance.rotationIdx);
                drawTetrisPiece(mFallingPieceInstance);
                invalidate();
            }
        }

        return true;
    }

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            TetrisTileView.this.update();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }

    private void newGame() {
        clearBoard();
        drawBoard();
        mFalling = false;
        mFallingPieceInstance = null;
        mNextPiece = null;
        score = 0;
        speed = Constants.MOVE_DELAY;
        if (mGameEventListener != null) {
            mGameEventListener.onScoreChange(score);
            mGameEventListener.onLevelChange(score / 10 + 1);
        }
        invalidate();
    }

    public void update() {

        if (mMode == Constants.PAUSE) {
            mRedrawHandler.sleep(Constants.PAUSE_REFRESH_DELAY);
        } else if (mMode != Constants.RUNNING) {
        } else {
            if (!mFalling) {
                mFalling = true;

                //select random piece and its random rotation
                mFallingPieceInstance = new TetrisPieceInstance();
                if (mNextPiece == null) {
                    mFallingPieceInstance.piece = resourcesCache.getRandomTetrisPiece();
                    mNextPiece = resourcesCache.getRandomTetrisPiece();
                } else {
                    mFallingPieceInstance.piece = mNextPiece;
                    do {
                        mNextPiece = resourcesCache.getRandomTetrisPiece();
                    } while (mFallingPieceInstance.piece.mColorCenter == mNextPiece.mColorCenter);
                }

                mFallingPieceInstance.rotationIdx = 0;
                mFallingPieceInstance.centerX = RND.nextInt(mXTileCount - mFallingPieceInstance.getTetrisPieceRotation().mWidth + 1)
                        + mFallingPieceInstance.getTetrisPieceRotation().mCenterX;
                mFallingPieceInstance.centerY = mFallingPieceInstance.getTetrisPieceRotation().mCenterY;

                if (!canExists(mFallingPieceInstance.getTetrisPieceRotation(), mFallingPieceInstance.centerX, mFallingPieceInstance.centerY)) {
                    setMode(Constants.LOSE);
                    invalidate();
                    return;
                } else {
                    drawTetrisPiece(mFallingPieceInstance);
                }
            } else { //Falling
                mFalling = moveDown(mFallingPieceInstance);
                if (mFalling) {
                    clearTetrisPiece(mFallingPieceInstance.centerX, mFallingPieceInstance.centerY - 1, mFallingPieceInstance.piece, mFallingPieceInstance.rotationIdx);
                    drawTetrisPiece(mFallingPieceInstance);
                }
                //start to merge
                else {
                    merge(mFallingPieceInstance);
                    if (findMergingFigures()) {
                        mFallingPieceInstance = null;
                        if (mGameEventListener != null) {
                            mGameEventListener.onScoreChange(++score);
                            mGameEventListener.onLevelChange(score / 10 + 1);
                            if (score % 10 == 0)
                                speed -= Constants.SPEED_DECREMENT;
                        }
                        mergeAnimations();
                        return;
                    } else {
                        mFallingPieceInstance = null;
                        drawBoard();
                    }
                }
            }
            invalidate();
            mRedrawHandler.sleep(speed);
        }
    }

    public void setMode(int newMode) {
        //Only can set mode to READY after LOSE
        if (mMode == Constants.LOSE && newMode != Constants.READY)
            return;

        int oldMode = mMode;
        mMode = newMode;


        //Start running a new game
        if (newMode == Constants.RUNNING & oldMode == Constants.READY) {
            setBackground(resourcesCache.getRandomBackground(getHeight()));
            update();
            return;
        }

        if (newMode == Constants.PAUSE) {
        }
        if (newMode == Constants.READY) {
            newGame();
        }
        if (newMode == Constants.LOSE) {
            if (mGameEventListener != null) {
                mGameEventListener.onGameOver(score, score / 10 + 1);
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mMode == Constants.LOSE) {
            final Resources r = getContext().getResources();
            final Drawable image = r.getDrawable(R.drawable.game_over);

            final int width = mTileSize*mXTileCount-2;
            double scale = width / 1.0 / image.getIntrinsicWidth();
            int height = (int)(image.getIntrinsicHeight() * scale);

            final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            final Canvas tempCanvas = new Canvas(bitmap);
            image.setBounds(0, 0, width, height);
            image.draw(tempCanvas);
            canvas.drawBitmap(bitmap, mXOffset+1, (int)(-(bitmap.getHeight() - getHeight()) / 2.0), mPaintBackground);
            return;
        }

        TetrisPieceRotation pieceRotation;
        Bitmap bitmap;
        final Set<Point> centers = mFigureCentresMap.keySet();
        for (Point center : centers) {
            final TetrisPieceInstance tpi = mFigureCentresMap.get(center);
            pieceRotation = tpi.getTetrisPieceRotation();

            int someOffset = 0;
            if (mMergeFigures.contains(tpi)) {
                if (mMergeTimes == 0 || mMergeTimes == 4) {
                    someOffset = tpi.piece.mColorCenter == Constants.GREEN_IMG ? 2 : -2;
                } else if (mMergeTimes == 2) {
                    someOffset = tpi.piece.mColorCenter == Constants.GREEN_IMG ? -2 : 2;
                }
            }

            bitmap = resourcesCache.getFigureImage(mTileSize, pieceRotation);
            canvas.drawBitmap(bitmap,
                    mXOffset + (tpi.getDrawCenterX() - (pieceRotation.mCenterX)) * mTileSize,
                    mYOffset + someOffset + (tpi.getDrawCenterY() - (pieceRotation.mCenterY)) * mTileSize,
                    mPaintTile);
        }

        if (mFallingPieceInstance == null)
            return;

        pieceRotation = mFallingPieceInstance.getTetrisPieceRotation();
        bitmap = resourcesCache.getFigureImage(mTileSize, pieceRotation);
        canvas.drawBitmap(bitmap,
                mXOffset + (mFallingPieceInstance.getDrawCenterX() - (pieceRotation.mCenterX)) * mTileSize,
                mYOffset + (mFallingPieceInstance.getDrawCenterY() - (pieceRotation.mCenterY)) * mTileSize,
                mPaintTile);
    }

    public interface OnGameEventListener {
        public void onGameOver(int score, int level);
        public void onScoreChange(int newScore);
        public void onLevelChange(int newLevel);
    }

    public void setOnGameEventListener(OnGameEventListener onGameEventListener) {
        this.mGameEventListener = onGameEventListener;
    }

    private class MergeHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            mergeAnimations();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }

    public int getMode() {
        return mMode;
    }
}

package com.example.zes.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;


public class MyView extends ImageView {

    private Paint mPaint;
    private int width, height;
    private Matrix matrix;
    private Point prePivot, lastPivot, downPoint;
    private float deltaX = 0, deltaY = 0; // 位移值
    private Bitmap mBitmap;
    private Bitmap bgBitmap;
    private Point lastPoint;
    private RectF dstRect, srcRect, controlRect;
    private final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG
            | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
            | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
            | Canvas.CLIP_TO_LAYER_SAVE_FLAG;
    private Bitmap martixBitmap;
    private Paint framePaint, controlPaint;
    private int controlRadius = 20;
    private float scaleValue = 1;
    private int FIRST_QUADRANT = 1;
    private int SECOND_QUADRANT = 2;
    private int THRID_QUADRANT = 3;
    private int FOURTH_QUADRANT = 4;
    private int TRANSALTE_MODE = 1;
    private int SCALE_MODE = 2;
    private int ROTATE_MODE = 3;
    private int CONTROL_MODE = 4;
    private int MODE = 0;
    private float tmp1 = 1;

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context) {
        this(context, null);

    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mPaint = new Paint();
        framePaint = new Paint();
        controlPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Style.FILL);
        mPaint.setXfermode(new PorterDuffXfermode(Mode.XOR));
        mPaint.setStrokeWidth(4);
        matrix = new Matrix();
        lastPoint = new Point(0, 0);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tree);
        bgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.jjy);
        martixBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
                mBitmap.getHeight(), matrix, true);
        srcRect = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        dstRect = new RectF();
        prePivot = new Point(mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
        lastPivot = new Point(mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
        downPoint = new Point();
        this.setBackgroundResource(R.drawable.jjy);
        tmp1 = getPointDistance(martixBitmap.getWidth() / 2 + lastPivot.x,
                martixBitmap.getHeight() / 2 + lastPivot.y);
    }

    /**
     * 平移模板
     *
     * @param ex
     * @param ey
     */
    public void translate(int ex, int ey) {

        prePivot.x += ex - lastPoint.x;
        prePivot.y += ey - lastPoint.y;
        deltaX = prePivot.x - lastPivot.x;
        deltaY = prePivot.y - lastPivot.y;
        lastPivot.x = prePivot.x;
        lastPivot.y = prePivot.y;
        matrix.postTranslate(deltaX, deltaY);
        Log.e("tran", deltaX + " " + deltaY);

    }

    /**
     * 放大缩小模板
     */
    private void scale() {
        // matrix.posts

        matrix.reset();
        Log.e("del", deltaX + " " + deltaY);
        matrix.postTranslate(lastPivot.x - martixBitmap.getWidth() / 2,
                lastPivot.y - martixBitmap.getHeight() / 2);
        invalidate();
        matrix.postScale(scaleValue, scaleValue, lastPivot.x, lastPivot.y);
        // Log.e("SCALE", scaleValue + "");
    }

    /**
     * 判断触摸点是否在模板上
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isOnPic(int x, int y) {
        if (dstRect.contains(x, y)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int evX = (int) event.getX();
        int evY = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e("QUADRANT", "" + getQuadrant(evX, evY));
                if (isInControlScale(evX, evY)) {
                    MODE = CONTROL_MODE;
                    downPoint.x = evX;
                    downPoint.y = evY;
                } else {
                    MODE = TRANSALTE_MODE;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isOnPic(evX, evY) && MODE == TRANSALTE_MODE) {
                    translate(evX, evY);
                } else {
                    if (getQuadrant(evX, evY) == FIRST_QUADRANT
                            || getQuadrant(evX, evY) == FOURTH_QUADRANT) {
                        MODE = SCALE_MODE;
                    }
                    if (getQuadrant(evX, evY) == SECOND_QUADRANT
                            || getQuadrant(evX, evY) == THRID_QUADRANT) {
                        MODE = ROTATE_MODE;
                    }
                }
                if (MODE == SCALE_MODE) {
                    float tmp2 = getPointDistance(evX, evY);
                    scaleValue = tmp2 / tmp1;
                    scale();
                    if (getQuadrant(evX, evY) == SECOND_QUADRANT
                            || getQuadrant(evX, evY) == THRID_QUADRANT) {
                        MODE = ROTATE_MODE;
                    }
                }
                if (MODE == ROTATE_MODE) {
                    //  rotate();
                    if (getQuadrant(evX, evY) == FIRST_QUADRANT
                            || getQuadrant(evX, evY) == FOURTH_QUADRANT) {
                        MODE = SCALE_MODE;
                    }
                }
                invalidate();
                break;
            default:
                break;
        }
        matrix.mapRect(dstRect, srcRect);
        lastPoint.x = evX;
        lastPoint.y = evY;
        return true;
    }

    private void rotate() {
        matrix.postRotate(getDegrees(), deltaX, deltaY);
    }

    private float getDegrees() {

        return 0;
    }

    /**
     * 获得触摸点在第几象限
     *
     * @param x
     * @param y
     * @return FIRST_QUADRANT 第一象限,SECOND_QUADRANT 第二象限,THRID_QUADRANT
     * 第三象限,FOURTH_QUADRANT 第四象限
     */
    private int getQuadrant(int x, int y) {
        float originX = lastPivot.x + martixBitmap.getWidth() * scaleValue / 2
                + controlRadius;
        float originY = lastPivot.y + martixBitmap.getHeight() * scaleValue / 2
                + controlRadius;
        if (x < originX && y < originY)
            return FIRST_QUADRANT;
        if (x > originX && y < originY)
            return SECOND_QUADRANT;
        if (x < originX && y > originY)
            return THRID_QUADRANT;
        if (x > originX && y > originY)
            return FOURTH_QUADRANT;
        return 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        width = measureWidth(widthMeasureSpec);
        height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * 判断触目点是否在控制点上
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isInControlScale(int x, int y) {

        if (controlRect.contains(x, y)) {
            return true;
        }

        return false;
    }

    private float getPointDistance(int x, int y) {
        float distance = (float) Math.sqrt(Math.abs(lastPivot.x - x)
                * Math.abs(lastPivot.x - x) + Math.abs(lastPivot.y - y)
                * Math.abs(lastPivot.y - y));
        return distance;
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text

            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text

            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        // canvas.drawBitmap(bgBitmap, 0, 0, mPaint);
        canvas.saveLayerAlpha(0, 0, width, height, 0x66, LAYER_FLAGS);
        canvas.drawRect(new Rect(0, 0, width, height), mPaint);
        canvas.drawBitmap(martixBitmap, matrix, mPaint);
        drawFrame(canvas);
        controlRect = new RectF(lastPivot.x + martixBitmap.getWidth()
                * scaleValue / 2 - controlRadius, lastPivot.y
                + martixBitmap.getHeight() * scaleValue / 2 - controlRadius,
                lastPivot.x + martixBitmap.getWidth() * scaleValue / 2
                        + controlRadius, lastPivot.y + martixBitmap.getHeight()
                * scaleValue / 2 + controlRadius);
        canvas.saveLayer(controlRect, mPaint, LAYER_FLAGS);
        drawControlPoint(canvas);
        canvas.restore();
        canvas.restore();
    }

    /**
     * 获得截取图片的BITMAP
     *
     * @return
     */
    public Bitmap getClipBitmap() {

        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        // 取两层绘制交集,显示上层
        canvas.clipRect(lastPivot.x - martixBitmap.getWidth() * scaleValue / 2,
                lastPivot.y - martixBitmap.getHeight() * scaleValue / 2,
                lastPivot.x + martixBitmap.getWidth() * scaleValue / 2,
                lastPivot.y + martixBitmap.getHeight() * scaleValue / 2);
        canvas.drawBitmap(martixBitmap, matrix, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bgBitmap, width, height,
                true);
        canvas.drawBitmap(scaleBitmap, 0, 0, paint);

        // 将图片画上去
        // 返回Bitmap对象
        // Log.e("output", output.getWidth() + " " + output.getHeight());
        Bitmap clipBitmap = Bitmap.createBitmap(output, lastPivot.x
                        - martixBitmap.getWidth() / 2,
                lastPivot.y - martixBitmap.getHeight() / 2,
                martixBitmap.getWidth(), martixBitmap.getHeight());
        return clipBitmap;
    }

    /**
     * 判断模板是否超出边界
     *
     * @return
     */
    public boolean isInEndge() {
        if ((lastPivot.x - (martixBitmap.getWidth() * scaleValue / 2)) < 0
                || (lastPivot.y - (martixBitmap.getHeight() * scaleValue / 2)) < 0
                || (lastPivot.x + (martixBitmap.getWidth() * scaleValue / 2) + controlRadius) > width
                || (lastPivot.y + (martixBitmap.getHeight() * scaleValue / 2) + controlRadius) > height) {
            return true;
        }

        return false;
    }

    /**
     * 绘制控制框
     *
     * @param canvas
     */
    private void drawFrame(Canvas canvas) {
        // framePoints = new float[] { lastPivot.x - martixBitmap.getWidth() /
        // 2,
        // lastPivot.y - martixBitmap.getHeight() / 2,
        // lastPivot.x + martixBitmap.getWidth(),
        // lastPivot.y + martixBitmap.getHeight() };
        framePaint.setAntiAlias(true);
        framePaint.setColor(Color.GRAY);
        framePaint.setStyle(Style.STROKE);
        framePaint.setStrokeWidth(5);
        canvas.drawRect(lastPivot.x
                - (martixBitmap.getWidth() * scaleValue / 2), lastPivot.y
                - (martixBitmap.getHeight() * scaleValue / 2), lastPivot.x
                + (martixBitmap.getWidth() * scaleValue / 2), lastPivot.y
                + (martixBitmap.getHeight() * scaleValue / 2), framePaint);
    }

    /**
     * 绘制控制点
     *
     * @param canvas
     */
    private void drawControlPoint(Canvas canvas) {
        controlPaint.setAntiAlias(true);
        controlPaint.setColor(Color.RED);
        controlPaint.setStyle(Style.FILL);
        canvas.drawCircle(lastPivot.x
                        + (martixBitmap.getWidth() * scaleValue / 2), lastPivot.y
                        + (martixBitmap.getHeight() * scaleValue / 2), controlRadius,
                controlPaint);
    }
}

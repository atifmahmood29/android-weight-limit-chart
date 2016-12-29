package wisdomitsol.com.limitareachart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class LimitLineChart extends View {

    public float upperLimit = 90;
    public float lowerLimit = 20;
    public float currentValue = 0;

    private static final int CHART_COLOR = 0xFF0099CC;
    private static final int CIRCLE_SIZE = 8;
    private static final int STROKE_SIZE = 2;
    private static final float SMOOTHNESS = 0.3f; // the higher the smoother, but don't go over 0.5

    private Paint mPaint;
    private Path mPath;
    private final float mCircleSize;
    private final float mStrokeSize;
    private final float mBorder;

    private float mMinY;
    private float mMaxY;

    public LimitLineChart(Context context) {
        this(context, null, 0);
    }

    public LimitLineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LimitLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        float scale = context.getResources().getDisplayMetrics().density;

        mCircleSize = scale * CIRCLE_SIZE;
        mStrokeSize = scale * STROKE_SIZE;
        mBorder = 0;//mCircleSize;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mStrokeSize);

        mPath = new Path();
    }

    public void drawGraph() {
        mMinY = 0;
        mMaxY = upperLimit + 10;

        invalidate();
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        final float height = getMeasuredHeight() - 2 * mBorder;
        final float width = getMeasuredWidth() - 2 * mBorder;
        float maxY = mMaxY/height*100;

        PointF[] values = new PointF[]{
                new PointF(0, maxY),
                new PointF(width, maxY)
        };
        List<PointF> upperLimitPoints = getPointCoordinates(values, maxY);

        values = new PointF[]{
                new PointF(0, upperLimit/height*100),
                new PointF(width, upperLimit/height*100)
        };
        List<PointF> normalLimitPoints  = getPointCoordinates(values, maxY);


        values = new PointF[]{
                new PointF(0, lowerLimit/height*100),
                new PointF(width, lowerLimit/height*100)
        };
        List<PointF> lowerLimitPoints  = getPointCoordinates(values, maxY);

        values = new PointF[]{
                new PointF(0, currentValue/height*100),
                new PointF(width, currentValue/height*100)
        };
        List<PointF> currentValuePoints  = getPointCoordinates(values, maxY);

        float difference = (upperLimitPoints.get(0).y-normalLimitPoints.get(0).y)/2;
        drawArea(canvas, upperLimitPoints, Color.parseColor("#ff0000"), "OVER", upperLimitPoints.get(0).y-difference,upperLimit+" upper limit",normalLimitPoints.get(0).y-getScaledSize(1, height),"OK condition",normalLimitPoints.get(0).y-getScaledSize(8, height));

        difference = (normalLimitPoints.get(0).y-lowerLimitPoints.get(0).y)/2;
        drawArea(canvas, normalLimitPoints, Color.parseColor("#00ff00"), "OK", normalLimitPoints.get(0).y-difference,"",normalLimitPoints.get(0).y,"",normalLimitPoints.get(0).y);

        difference = (lowerLimitPoints.get(0).y)/2;
        drawArea(canvas, lowerLimitPoints, Color.parseColor("#FFF400"), "UNDER", lowerLimitPoints.get(0).y+difference,lowerLimit+" lower limit",lowerLimitPoints.get(0).y,"OK condition",lowerLimitPoints.get(0).y+getScaledSize(8, height));

        drawArea(canvas, currentValuePoints, Color.parseColor("#00000000"), "", currentValuePoints.get(0).y,"",currentValuePoints.get(0).y,"",currentValuePoints.get(0).y);
        printText(canvas, "Current", getScaledSize(30, width), currentValuePoints.get(0).y, getScaledSize(7, height), Typeface.NORMAL);
        printText(canvas, "weight "+currentValue, getScaledSize(30, width), currentValuePoints.get(0).y+getScaledSize(8, height), getScaledSize(7, height), Typeface.NORMAL);
    }

    private void drawArea(Canvas canvas, List<PointF> points, int areaColor, String statusText, float statusTextYposition
            , String limitText, float limitTextYposition , String limitText2, float limitTextYposition2) {
        mPath.reset();
        int size = points.size();
        final float height = getMeasuredHeight() - 2 * mBorder;
        final float width = getMeasuredWidth() - 2 * mBorder;

        // calculate smooth path
        float lX = 0, lY = 0;
        mPath.moveTo(points.get(0).x, points.get(0).y);
        for (int i = 1; i < size; i++) {
            PointF p = points.get(i);    // current point

            // first control point
            PointF p0 = points.get(i - 1);    // previous point
            float d0 = (float) Math.sqrt(Math.pow(p.x - p0.x, 2) + Math.pow(p.y - p0.y, 2));    // distance between p and p0
            float x1 = Math.min(p0.x + lX * d0, (p0.x + p.x) / 2);    // min is used to avoid going too much right
            float y1 = p0.y + lY * d0;

            // second control point
            PointF p1 = points.get(i + 1 < size ? i + 1 : i);    // next point
            float d1 = (float) Math.sqrt(Math.pow(p1.x - p0.x, 2) + Math.pow(p1.y - p0.y, 2));    // distance between p1 and p0 (length of reference line)
            lX = (p1.x - p0.x) / d1 * SMOOTHNESS;        // (lX,lY) is the slope of the reference line
            lY = (p1.y - p0.y) / d1 * SMOOTHNESS;
            float x2 = Math.max(p.x - lX * d0, (p0.x + p.x) / 2);    // max is used to avoid going too much left
            float y2 = p.y - lY * d0;

            // add line
            mPath.cubicTo(x1, y1, x2, y2, p.x, p.y);
        }


        // draw path
        mPaint.setColor(CHART_COLOR);
        mPaint.setStyle(Style.STROKE);
        canvas.drawPath(mPath, mPaint);


        // draw area
        if (size > 0) {
            mPaint.setStyle(Style.FILL);
            mPaint.setColor(areaColor);//((CHART_COLOR & 0xFFFFFF) | 0x10000000);
            mPath.lineTo(points.get(size - 1).x, height + mBorder);
            mPath.lineTo(points.get(0).x, height + mBorder);
            mPath.close();
            canvas.drawPath(mPath, mPaint);
        }

       /* // draw circles
        mPaint.setColor(CHART_COLOR);
        mPaint.setStyle(Style.FILL_AND_STROKE);
        for (PointF point : points) {
            canvas.drawCircle(point.x, point.y, mCircleSize / 2, mPaint);
        }
        mPaint.setStyle(Style.FILL);
        mPaint.setColor(Color.WHITE);
        for (PointF point : points) {
            canvas.drawCircle(point.x, point.y, (mCircleSize - mStrokeSize) / 2, mPaint);
        }*/

        printText(canvas, statusText, getScaledSize(1, width), statusTextYposition, getScaledSize(8, height), Typeface.BOLD);
        printText(canvas, limitText,  getScaledSize(60, width), limitTextYposition, getScaledSize(7, height), Typeface.NORMAL);
        //printText(canvas, limitText2,  getScaledSize(60, width), limitTextYposition2, getScaledSize(7, height), Typeface.NORMAL);

    }

    private void printText(Canvas canvas, String text, float xPos, float yPos, float textSize, int typeFace){
        mPaint.setColor(Color.parseColor("#0000ff"));
        mPaint.setTextSize(textSize);
        mPaint.setShadowLayer(4, 2, 2, 0x80000000);
        mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, typeFace));
        canvas.drawText(text, xPos, yPos, mPaint);
    }

    private List<PointF> getPointCoordinates(PointF[] values, float maxY) {
        int size = values.length;

        final float height = getMeasuredHeight() - 2 * mBorder;
        final float width = getMeasuredWidth() - 2 * mBorder;

        final float left = values[0].x;
        final float right = values[values.length - 1].x;
        final float dX = (right - left) > 0 ? (right - left) : (2);
        final float dY = (maxY - mMinY) > 0 ? (maxY - mMinY) : (2);

        // calculate point coordinates
        List<PointF> points = new ArrayList<PointF>(size);
        for (PointF point : values) {
            float x = mBorder + (point.x - left) * width / dX;
            float y = mBorder + height - (point.y - mMinY) * height / dY;
            points.add(new PointF(x, y));
        }


        float updatedVal = points.get(0).y;
        if(updatedVal <= 10){
            points.get(0).y = 25;
            points.get(1).y = 25;
        }else if(updatedVal >= height-10){
            points.get(0).y = height - 14;
            points.get(1).y = height - 14;
        }

        return points;
    }

    private float getScaledSize(float size, float height){
        return  (size/(float) 100)*height;
    }

}

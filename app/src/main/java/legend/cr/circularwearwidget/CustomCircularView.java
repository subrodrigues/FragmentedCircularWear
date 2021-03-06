package legend.cr.circularwearwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by filiperodrigues on 10/11/17.
 */

public class CustomCircularView extends View {
    private static final int STROKE_WIDTH = 20;
    private static final int STARTING_SPACING_ANGLE_THRESHOLD = 4;
    private static final int ENDING_SPACING_ANGLE_THRESHOLD = STARTING_SPACING_ANGLE_THRESHOLD * 2;
    private static final int ARC_FULL_ANGLE_VALUE = 360;
    private static final int ARC_HALF_ANGLE_VALUE = 180;
    private static final int ARC_QUARTER_ANGLE_VALUE = 90;

    private Paint mBasePaint, mDegreesPaint, mCenterPaint, mRectPaint;
    private RectF mRect;
    private int centerX, centerY, radius;
    private MetricModel[] mMetrics;

    public CustomCircularView(Context context) {
        super(context);
        init();
    }

    public CustomCircularView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomCircularView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        mRectPaint.setStyle(Paint.Style.FILL);

        mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        mCenterPaint.setStyle(Paint.Style.FILL);

        mBasePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBasePaint.setAntiAlias(true);
        mBasePaint.setStyle(Paint.Style.STROKE);
        mBasePaint.setStrokeWidth(STROKE_WIDTH);
        mBasePaint.setStrokeCap(Paint.Cap.ROUND);
        mBasePaint.setColor(ContextCompat.getColor(getContext(), R.color.blue));

        mDegreesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDegreesPaint.setAntiAlias(true);
        mDegreesPaint.setStyle(Paint.Style.STROKE);
        mDegreesPaint.setStrokeWidth(STROKE_WIDTH);
        mDegreesPaint.setStrokeCap(Paint.Cap.ROUND);
        mDegreesPaint.setColor(ContextCompat.getColor(getContext(), R.color.green));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mMetrics == null || mMetrics.length <= 0)
            return;

        // Configure drawing space if not set. mRect will also be null after a configuration change.
        if (mRect == null) {
            initRect();
        }

        /** Draw Background Bounds **/
//        canvas.drawRect(mRect, mRectPaint);

        /** Draw Circle **/
//        canvas.drawCircle(centerX, centerY, radius - STROKE_WIDTH / 2, mBasePaint);

        final int numFields = mMetrics.length;
        int angleValueToIncrement = getInitialAngle(numFields);
        int currentAngleTotal = 0;

        Log.e("", " ");
        Log.e("TOTAL", "ANGLES: " + numFields);

        for (int i = 0; i < numFields; i++) {
            /* drawing the background arc */
            canvas.drawArc(mRect, currentAngleTotal + (numFields == 1 ? 0 : STARTING_SPACING_ANGLE_THRESHOLD), angleValueToIncrement - (numFields == 1 ? 0 : ENDING_SPACING_ANGLE_THRESHOLD), false, mBasePaint);

            Log.e(" ANGLES", "START: " + i + " inc: " + angleValueToIncrement);

            /* drawing the filled arc bar (percentage value) */
            float percentFill = mMetrics[i].getCurrentPercentageValue() * angleValueToIncrement;
            canvas.drawArc(mRect, currentAngleTotal + STARTING_SPACING_ANGLE_THRESHOLD, percentFill - ENDING_SPACING_ANGLE_THRESHOLD, false, mDegreesPaint);

            currentAngleTotal += angleValueToIncrement;
            /** With an odd number of fields we need to change angleValueToIncrement on-the-fly for the last item **/
            if (numFields == 3 && (i + 1) == (numFields - 1) && currentAngleTotal != ARC_FULL_ANGLE_VALUE) {
                angleValueToIncrement += angleValueToIncrement;
            }
        }
    }

    /**
     * Method that returns the initial angle increment value, based on number of items.
     *
     * @param numItems
     * @return initial angle
     */
    private int getInitialAngle(int numItems) {
        switch (numItems) {
            case 1: // Full circle
                return ARC_FULL_ANGLE_VALUE;
            case 2: // Two parts
                return ARC_HALF_ANGLE_VALUE;
            default: // In case we have 3 parts or 4, both starts with 90º
                return ARC_QUARTER_ANGLE_VALUE;
        }
    }

    /**
     * RectF will define the drawing space
     */
    private void initRect() {
        // take the minimum of width and height here to be on he safe side:
        centerX = getMeasuredWidth() / 2;
        centerY = getMeasuredHeight() / 2;
        radius = Math.min(centerX, centerY);

        // We have to take into account the STROKE_WIDTH with drawArc() as well as drawCircle():
        // circles as well as arcs are drawn 50% outside of the bounds defined by the radius (radius for arcs is calculated from the rectangle mRect).
        // So if mRect is too large, the lines will not fit into the View
        final int startTopAndLeft = STROKE_WIDTH / 2;
        final int endBottomAndRight = 2 * radius - startTopAndLeft;

        mRect = new RectF(startTopAndLeft, startTopAndLeft, endBottomAndRight, endBottomAndRight);
    }

    public void setupFields(MetricModel[] metrics) {
        this.mMetrics = metrics;
        this.invalidate();
    }

}

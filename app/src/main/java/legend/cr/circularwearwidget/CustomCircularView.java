package legend.cr.circularwearwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.support.annotation.NonNull;
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

    private static final float ANIMATED_FILL_INCREMENT_THRESHOLD = 0.05f;
    private static final int ANIMATION_FILL_DELAY = 50;

    private MetricModel[] mMetrics;

    private Paint mBasePaint, mDegreesPaint;
    private RectF mRect;

    private final Handler mFillHandler = new Handler();
    private Runnable mFillRunnable;
    private boolean isInitialRun = true;
    private float[] mTempPercentageToAnimate;

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
        isInitialRun = true;

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
    protected void onDetachedFromWindow() {
        if(mFillHandler != null && mFillRunnable != null)
            mFillHandler.removeCallbacks(mFillRunnable);

        super.onDetachedFromWindow();
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

        // If isInitialRun it means that a new view setup is required
        if (isInitialRun) {
            isInitialRun = false;

            // init tempAngleToAnimate in order to animate filling bars
            mTempPercentageToAnimate = new float[mMetrics.length];
            startAnimatingArc();
        }

        final int numFields = mMetrics.length;
        int angleValueToIncrement = getInitialAngle(numFields);
        int currentAngleTotal = 0;

        Log.e("", " ");
        Log.e("TOTAL", "ANGLES: " + numFields);

        for (int i = 0; i < numFields; i++) {
            /* drawing the background arc */
            canvas.drawArc(mRect, currentAngleTotal + (numFields == 1 ? 0 : STARTING_SPACING_ANGLE_THRESHOLD), angleValueToIncrement - (numFields == 1 ? 0 : ENDING_SPACING_ANGLE_THRESHOLD), false, mBasePaint);

            /** drawing the filled arc bar (percentage value) **/
            float percentAngleToFill = mTempPercentageToAnimate[i] * angleValueToIncrement;
            Log.e(" FILLING", "Max Percentage: " + mMetrics[i].getCurrentPercentageValue() + "\n Percentage inc:  " + mTempPercentageToAnimate[i] + "\n Percent Angle: " + percentAngleToFill);

            if (angleValueToIncrement == ARC_FULL_ANGLE_VALUE) {
                canvas.drawArc(mRect, currentAngleTotal, percentAngleToFill, false, mDegreesPaint);
            } else {
                final float percentAngleWithThreshold = percentAngleToFill - ENDING_SPACING_ANGLE_THRESHOLD;
                canvas.drawArc(mRect, currentAngleTotal + STARTING_SPACING_ANGLE_THRESHOLD, percentAngleWithThreshold > 0.0f ? percentAngleWithThreshold : 0.0f, false, mDegreesPaint);
            }

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
        int centerX = getMeasuredWidth() / 2;
        int centerY = getMeasuredHeight() / 2;
        int radius = Math.min(centerX, centerY);

        // We have to take into account the STROKE_WIDTH with drawArc() as well as drawCircle():
        // circles as well as arcs are drawn 50% outside of the bounds defined by the radius (radius for arcs is calculated from the rectangle mRect).
        // So if mRect is too large, the lines will not fit into the View
        final int startTopAndLeft = STROKE_WIDTH / 2;
        final int endBottomAndRight = 2 * radius - startTopAndLeft;

        mRect = new RectF(startTopAndLeft, startTopAndLeft, endBottomAndRight, endBottomAndRight);
    }

    /**
     * Aux method to getFillRunnable().
     * Update temporary percentage values and notifies Runnable in case we have remaining arcs to animate.
     *
     * @return boolean notifying the Runnable if some arcs are remaining to be animated
     */
    private boolean updateTempPercentageToAnimate() {
        boolean areArcsRemaining = false;
        for (int i = 0; i < mMetrics.length; i++) {
            if (mTempPercentageToAnimate[i] < mMetrics[i].getCurrentPercentageValue()) {
                mTempPercentageToAnimate[i] += ANIMATED_FILL_INCREMENT_THRESHOLD;
                areArcsRemaining = true;
            }
        }
        return areArcsRemaining;
    }


    /**
     * Method that returns the Runnable that will deal with arc(s) filling animation
     * @return the Runnable
     */
    @NonNull
    private Runnable getFillRunnable() {
        return new Runnable() {
            public void run() {
                if (updateTempPercentageToAnimate()) {
                    invalidate();

                    mFillHandler.postDelayed(this, ANIMATION_FILL_DELAY);
                } else {
                    mFillHandler.removeCallbacks(this);
                }
            }
        };
    }

    /**
     * Method that starts the animation fill on arc(s) when a net view setup is set
     */
    public void startAnimatingArc() {
        if (mFillRunnable == null) {
            mFillRunnable = getFillRunnable();
        } else {
            mFillHandler.removeCallbacks(mFillRunnable);
        }

        mFillHandler.postDelayed(mFillRunnable, ANIMATION_FILL_DELAY);
    }

    /**
     * Method that setup new view and init it.
     * @param metrics the array of models expected to fill the arcs
     */
    public void setupFields(MetricModel[] metrics) {
        this.mMetrics = metrics;
        this.isInitialRun = true;
        this.invalidate();
    }

}

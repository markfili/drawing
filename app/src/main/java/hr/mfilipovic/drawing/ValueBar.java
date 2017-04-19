package hr.mfilipovic.drawing;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Class to test drawing custom views.
 * <p>
 * https://www.intertech.com/Blog/android-custom-view-tutorial-part-2-custom-attributes-drawing-and-measuring/
 * <p>
 * Created by marko on 11/04/17.
 */

public class ValueBar extends View {

    private int maxValue = 100;
    private int currentValue = 0;

    private int barHeight;
    private int circleRadius;
    private int spaceAfterBar;
    private int circleTextSize;
    private int maxValueTextSize;
    private int labelTextSize;
    private int labelTextColor;
    private int currentValueTextColor;
    private int circleTextColor;
    private int baseColor;
    private int fillColor;
    private String labelText;

    private Paint labelPaint;
    private Paint maxValuePaint;
    private Paint barBasePaint;
    private Paint barFillPaint;
    private Paint circlePaint;
    private Paint currentValuePaint;

    private ValueAnimator animation;

    private boolean animated;

    private long animationDuration = 4000L;

    private float previousValue;
    private float valueToDraw;

    public ValueBar(Context context) {
        super(context);
    }

    public ValueBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ValueBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawLabel(canvas);
        drawBar(canvas);
        drawMaxValue(canvas);
    }

    private int measureHeight(int measureSpec) {
        // determine height
        int size = getPaddingTop() + getPaddingBottom();
        size += labelPaint.getFontSpacing();
        float maxValueTextSpacing = maxValuePaint.getFontSpacing();
        size += Math.max(maxValueTextSpacing, Math.max(barHeight, circleRadius * 2));
        return resolveSizeAndState(size, measureSpec, 0);
    }

    private int measureWidth(int measureSpec) {
        // determine width
        int size = getPaddingStart() + getPaddingEnd();
        Rect bounds = new Rect();
        labelPaint.getTextBounds(labelText, 0, labelText.length(), bounds);
        size += bounds.width();

        bounds = new Rect();
        String maxValueString = String.valueOf(maxValue);
        maxValuePaint.getTextBounds(maxValueString, 0, maxValueString.length(), bounds);
        return resolveSizeAndState(size, measureSpec, 0);
    }

    private void drawLabel(Canvas canvas) {
        float x = getPaddingLeft();
        // the y coordinate marks the bottom of the text, so we need to factor in the height
        Rect bounds = new Rect();
        labelPaint.getTextBounds(labelText, 0, labelText.length(), bounds);
        float y = getPaddingTop() + bounds.height();

        canvas.drawText(labelText, x, y, labelPaint);
    }

    /**
     * Measures maxValueText text height
     *
     * @param canvas
     */
    private void drawMaxValue(Canvas canvas) {
        // convert value to string
        String maxValueString = String.valueOf(maxValue);

        // calculate maxValue text bounds
        Rect maxValueRect = new Rect();
        maxValuePaint.getTextBounds(maxValueString, 0, maxValueString.length(), maxValueRect);

        // get x position
        // subtract right padding from max width - text is being drawn from right to left
        float xPos = getWidth() - getPaddingEnd();
        // get y position
        // add half the size of maxValue text to center the text on the barCenter
        float yPos = getBarCenter() + maxValueRect.height() / 2;

        canvas.drawText(maxValueString, xPos, yPos, maxValuePaint);
    }

    private void drawBar(Canvas canvas) {
        String maxValueString = String.valueOf(maxValue);
        Rect maxValueRect = new Rect();
        maxValuePaint.getTextBounds(maxValueString, 0, maxValueString.length(), maxValueRect);

        float barLength = getWidth() - getPaddingRight() - getPaddingLeft() - maxValueRect.width() - spaceAfterBar - circleRadius;

        float barCenter = getBarCenter();
        float halfBarHeight = barHeight / 2;
        float top = barCenter - halfBarHeight;
        float bottom = barCenter + halfBarHeight;
        float left = getPaddingLeft();
        float right = getPaddingLeft() + barLength;

        RectF rect = new RectF(left, top, right, bottom);
        canvas.drawRoundRect(rect, halfBarHeight, halfBarHeight, barBasePaint);

        float percentageFilled = (float) valueToDraw / maxValue;
        float fillLength = percentageFilled * barLength;
        float fillPosition = left + fillLength;
        RectF fillRect = new RectF(left, top, fillPosition, bottom);
        canvas.drawRoundRect(fillRect, halfBarHeight, halfBarHeight, barFillPaint);

        canvas.drawCircle(fillPosition, barCenter, circleRadius, circlePaint);

        String currentValueString = String.valueOf(valueToDraw);
        Rect currentValueRect = new Rect();
        currentValuePaint.getTextBounds(currentValueString, 0, currentValueString.length(), currentValueRect);
        float y = halfBarHeight + currentValueRect.height() / 2;
        canvas.drawText(currentValueString, fillPosition, y, currentValuePaint);
    }

    private void init(Context context, AttributeSet attrs) {
        // enable state saving
        setSaveEnabled(true);

        // extract data from XML attributes
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ValueBar, 0, 0);
        barHeight = pixelSize(ta, R.styleable.ValueBar_barHeight);
        circleRadius = pixelSize(ta, R.styleable.ValueBar_circleRadius);
        circleTextSize = pixelSize(ta, R.styleable.ValueBar_circleTextSize);
        maxValueTextSize = pixelSize(ta, R.styleable.ValueBar_maxValueTextSize);
        labelTextSize = pixelSize(ta, R.styleable.ValueBar_labelTextSize);

        labelTextColor = colorOrBlack(ta, R.styleable.ValueBar_labelTextColor);
        currentValueTextColor = colorOrBlack(ta, R.styleable.ValueBar_maxValueTextColor);

        circleTextColor = colorOrBlack(ta, R.styleable.ValueBar_circleTextColor);
        baseColor = colorOrBlack(ta, R.styleable.ValueBar_baseColor);
        fillColor = colorOrBlack(ta, R.styleable.ValueBar_fillColor);

        labelText = ta.getString(R.styleable.ValueBar_labelText);

        ta.recycle();

        initPaint();
    }

    private void initPaint() {
        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setTextSize(labelTextSize);
        labelPaint.setColor(labelTextColor);
        /*
            set the text alignment to left.
            This means that when we call drawText on the Canvas,
            the text will start at the specified x position and
            be drawn from left to right. Thus, to determine the
            x position for the label text, we can simply call
            getPaddingLeft().
         */
        labelPaint.setTextAlign(Paint.Align.LEFT);
        labelPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        maxValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maxValuePaint.setTextSize(maxValueTextSize);
        maxValuePaint.setColor(currentValueTextColor);
        maxValuePaint.setTextAlign(Paint.Align.RIGHT);
        maxValuePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        barBasePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barBasePaint.setColor(baseColor);

        barFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barFillPaint.setColor(fillColor);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(fillColor);

        currentValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        currentValuePaint.setColor(circleTextColor);
        currentValuePaint.setTextSize(circleTextSize);
        currentValuePaint.setTextAlign(Paint.Align.CENTER);
    }

    private float getBarCenter() {
        // position the bar slightly below the middle of the drawable area (60%)
        float barCenter = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2; // center vert
        // move it down a bit
        barCenter += getPaddingTop() + .1f * getHeight();
        return barCenter;
    }


    private int colorOrBlack(TypedArray ta, int colorResId) {
        return ta.getColor(colorResId, Color.BLACK);
    }

    private int pixelSize(TypedArray ta, int pixelResId) {
        return ta.getDimensionPixelSize(pixelResId, 0);
    }


    /*
        Both setter methods call invalidate(), and setMaxValue also calls requestLayout().
        Calling invalidate() tells Android that the state of the view has changed and needs to be
        redrawn. requestLayout means that the size of the view may have changed and needs to be remeasured,
        which could impact the entire layout. When we measure the view, the size of the max value text
        will be used to help determine the view’s dimensions, that’s why we need to call requestLayout
        here.
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        invalidate();
        requestLayout();
    }

    public void setValue(int newValue) {
        previousValue = currentValue;
        if (newValue < 0) {
            this.currentValue = 0;
        } else if (newValue > this.maxValue) {
            this.currentValue = this.maxValue;
        } else {
            this.currentValue = newValue;
        }

        if (animation != null) {
            animation.cancel();
        }
        if (animated) {
            animation = ValueAnimator.ofFloat(previousValue, currentValue);

            int changeInValue = (int) Math.abs(currentValue - previousValue);
            long durationToUse = (long) (animationDuration * ((float) changeInValue / (float) maxValue));
            animation.setDuration(durationToUse);
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    valueToDraw = (float) valueAnimator.getAnimatedValue();
                    ValueBar.this.invalidate();
                }
            });
            animation.start();
        } else {
            valueToDraw = currentValue;
        }
        invalidate();
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public void setAnimationDuration(long animationDuration) {
        this.animationDuration = animationDuration;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.value = currentValue;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(((SavedState) state).getSuperState());
        currentValue = ss.value;
        valueToDraw = currentValue;
    }

    private static class SavedState extends BaseSavedState {

        int value; // stores current value from ValueBar

        // used to save the state
        SavedState(Parcelable superState) {
            super(superState);
        }

        // used to extract data from the saved state
        private SavedState(Parcel in) {
            super(in);
            value = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(value);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel source) {
                        return new SavedState(source);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}

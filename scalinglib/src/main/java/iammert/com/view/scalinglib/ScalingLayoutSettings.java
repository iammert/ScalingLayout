package iammert.com.view.scalinglib;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

/**
 * Values read from the {@code ScalingLayout} XML attributes and calculated on
 * the first layout.
 * <p>
 * Created by mertsimsek on 30/09/2017.
 */

public class ScalingLayoutSettings {

    private static final float DEFAULT_RADIUS_FACTOR = 1.0f;
    private static final float MIN_RADIUS_FACTOR = 0.0f;
    private static final float MAX_RADIUS_FACTOR = 1.0f;
    private static final int DEFAULT_ANIMATION_DURATION = 200;

    private float radiusFactor;
    private long animationDuration;
    private int initialWidth;
    private int maxWidth;
    private int maxExpandedWidth;
    private float maxRadius;
    private float elevation;
    private boolean isInitialized = false;

    public ScalingLayoutSettings(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ScalingLayout);
        radiusFactor = typedArray.getFloat(R.styleable.ScalingLayout_radiusFactor, DEFAULT_RADIUS_FACTOR);
        animationDuration = Math.max(0, typedArray.getInt(R.styleable.ScalingLayout_animationDuration, DEFAULT_ANIMATION_DURATION));
        maxWidth = context.getResources().getDisplayMetrics().widthPixels;
        maxExpandedWidth = Math.max(0, typedArray.getDimensionPixelSize(R.styleable.ScalingLayout_maxExpandedWidth, 0));
        typedArray.recycle();

        if (Float.isNaN(radiusFactor)) {
            radiusFactor = DEFAULT_RADIUS_FACTOR;
        }
        radiusFactor = Math.max(MIN_RADIUS_FACTOR, Math.min(MAX_RADIUS_FACTOR, radiusFactor));
    }

    public void initialize(int width, int height) {
        if (!isInitialized) {
            isInitialized = true;
            initialWidth = width;
            setHeight(height);
        }
    }

    /**
     * Recalculates the max radius for the given layout height.
     *
     * @param height layout height in pixels
     */
    void setHeight(int height) {
        maxRadius = height / 2f * radiusFactor;
    }

    public int getInitialWidth() {
        return initialWidth;
    }

    /**
     * Width the layout expands to. This is the available width, limited by
     * {@code app:maxExpandedWidth} when it is set. It is never smaller than
     * the initial width.
     *
     * @return expanded width in pixels
     */
    public int getMaxWidth() {
        if (maxExpandedWidth > 0) {
            return Math.min(maxWidth, Math.max(maxExpandedWidth, initialWidth));
        }
        return maxWidth;
    }

    /**
     * @return width available inside the parent, ignoring {@code app:maxExpandedWidth}
     */
    int getAvailableWidth() {
        return maxWidth;
    }

    /**
     * @return max expanded width in pixels, or 0 if there is no limit
     */
    public int getMaxExpandedWidth() {
        return maxExpandedWidth;
    }

    void setMaxExpandedWidth(int maxExpandedWidth) {
        this.maxExpandedWidth = Math.max(0, maxExpandedWidth);
    }

    /**
     * Updates the width available for the layout to expand to. Non positive values are ignored.
     *
     * @param maxWidth width in pixels
     */
    void setMaxWidth(int maxWidth) {
        if (maxWidth > 0) {
            this.maxWidth = maxWidth;
        }
    }

    public float getMaxRadius() {
        return maxRadius;
    }

    public long getAnimationDuration() {
        return animationDuration;
    }

    void setAnimationDuration(long animationDuration) {
        this.animationDuration = Math.max(0, animationDuration);
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * @deprecated Elevation is no longer managed by ScalingLayout.
     * Use {@link android.view.View#setElevation(float)} on the layout instead.
     */
    @Deprecated
    public void setElevation(float elevation) {
        this.elevation = elevation;
    }

    /**
     * @deprecated Use {@link android.view.View#getElevation()} on the layout instead.
     */
    @Deprecated
    public float getElevation() {
        return elevation;
    }
}

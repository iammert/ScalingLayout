package iammert.com.view.scalinglib;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

/**
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
    private float maxRadius;
    private float elevation;
    private boolean isInitialized = false;

    public ScalingLayoutSettings(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ScalingLayout);
        radiusFactor = typedArray.getFloat(R.styleable.ScalingLayout_radiusFactor, DEFAULT_RADIUS_FACTOR);
        animationDuration = Math.max(0, typedArray.getInt(R.styleable.ScalingLayout_animationDuration, DEFAULT_ANIMATION_DURATION));
        maxWidth = context.getResources().getDisplayMetrics().widthPixels;
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
            float radiusLimit = height / 2f;
            maxRadius = radiusLimit * radiusFactor;
        }
    }

    public int getInitialWidth() {
        return initialWidth;
    }

    public int getMaxWidth() {
        return maxWidth;
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

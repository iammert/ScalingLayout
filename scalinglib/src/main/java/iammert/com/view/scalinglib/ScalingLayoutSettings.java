package iammert.com.view.scalinglib;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

/**
 * Created by mertsimsek on 30/09/2017.
 */

public class ScalingLayoutSettings {

    private static final float DEFAULT_RADIUS_FACTOR = 1.0f;

    private float radiusFactor;
    private int initialWidth;
    private int maxWidth;
    private float maxRadius;
    private float elevation;
    private boolean isInitialized = false;
    private boolean hasToolbar ;

    public ScalingLayoutSettings(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ScalingLayout);
        radiusFactor = typedArray.getFloat(R.styleable.ScalingLayout_radiusFactor, DEFAULT_RADIUS_FACTOR);
        hasToolbar = typedArray.getBoolean(R.styleable.ScalingLayout_hasToolbar,false);
        maxWidth = context.getResources().getDisplayMetrics().widthPixels;
        typedArray.recycle();

        if (radiusFactor > DEFAULT_RADIUS_FACTOR) {
            radiusFactor = DEFAULT_RADIUS_FACTOR;
        }
    }

    public void initialize(int width, int height) {
        if (!isInitialized) {
            isInitialized = true;
            initialWidth = width;
            float radiusLimit = height / 2;
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

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setElevation(float elevation) {
        this.elevation = elevation;
    }

    public float getElevation() {
        return elevation;
    }

    public boolean hasToolbar() {
        return hasToolbar;
    }
}

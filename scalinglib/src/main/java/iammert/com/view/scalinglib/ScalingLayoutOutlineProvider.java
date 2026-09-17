package iammert.com.view.scalinglib;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * Rounded rectangle outline of a {@link ScalingLayout}. It draws the elevation
 * shadow and clips the rounded corners.
 * <p>
 * Created by mertsimsek on 08/01/2018.
 */
public class ScalingLayoutOutlineProvider extends ViewOutlineProvider {

    private int width;
    private int height;
    private float radius;

    ScalingLayoutOutlineProvider(int width, int height, float radius) {
        this.width = width;
        this.height = height;
        this.radius = radius;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        // A radius larger than half of the shorter side cannot be clipped on every API level.
        float maxRadius = Math.min(width, height) / 2f;
        outline.setRoundRect(0, 0, width, height, Math.min(radius, maxRadius));
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}

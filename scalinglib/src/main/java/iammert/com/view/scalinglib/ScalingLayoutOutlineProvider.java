package iammert.com.view.scalinglib;

import android.graphics.Outline;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * Created by mertsimsek on 08/01/2018.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScalingLayoutOutlineProvider extends ViewOutlineProvider  {

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
        outline.setRoundRect(0, 0, width, height, radius);
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

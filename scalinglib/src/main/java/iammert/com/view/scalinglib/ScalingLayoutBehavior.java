package iammert.com.view.scalinglib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

/**
 * Created by mertsimsek on 30/09/2017.
 */

public class ScalingLayoutBehavior extends CoordinatorLayout.Behavior<ScalingLayout> {

    private final float toolbarHeightInPixel;

    public ScalingLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        toolbarHeightInPixel = context.getResources().getDimensionPixelSize(R.dimen.sl_toolbar_size);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull ScalingLayout child, @NonNull View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull ScalingLayout child, @NonNull View dependency) {
        int totalScrollRange = ((AppBarLayout) dependency).getTotalScrollRange();
        if (totalScrollRange <= 0) {
            // AppBarLayout is not scrollable (yet). Avoid dividing by zero.
            return false;
        }

        float progress = -dependency.getY() / totalScrollRange;
        child.setProgress(Math.max(0f, Math.min(1f, progress)));

        float halfChildHeight = child.getMeasuredHeight() / 2f;
        if (totalScrollRange + dependency.getY() > halfChildHeight) {
            child.setTranslationY(totalScrollRange + dependency.getY() + toolbarHeightInPixel - halfChildHeight);
        } else {
            child.setTranslationY(toolbarHeightInPixel);
        }
        return true;
    }
}

package iammert.com.view.scalinglib;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

/**
 * Expands a {@link ScalingLayout} while an {@link AppBarLayout} is scrolled
 * away, and keeps it below the toolbar once the app bar is collapsed.
 * Set it with {@code app:layout_behavior="iammert.com.view.scalinglib.ScalingLayoutBehavior"}.
 * <p>
 * Created by mertsimsek on 30/09/2017.
 */

public class ScalingLayoutBehavior extends CoordinatorLayout.Behavior<ScalingLayout> {

    private final float toolbarHeightInPixel;

    public ScalingLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        toolbarHeightInPixel = resolveToolbarHeight(context);
    }

    /**
     * Uses the action bar size of the current theme, which differs on tablets
     * and in custom themes. Falls back to 56dp when the theme does not define it.
     */
    static float resolveToolbarHeight(Context context) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)
                && typedValue.type == TypedValue.TYPE_DIMENSION) {
            return TypedValue.complexToDimensionPixelSize(typedValue.data, context.getResources().getDisplayMetrics());
        }
        return context.getResources().getDimensionPixelSize(R.dimen.sl_toolbar_size);
    }

    float getToolbarHeight() {
        return toolbarHeightInPixel;
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

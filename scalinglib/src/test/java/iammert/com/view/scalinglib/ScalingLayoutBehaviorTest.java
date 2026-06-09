package iammert.com.view.scalinglib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
public class ScalingLayoutBehaviorTest {

    private static final int SCROLL_RANGE = 200;

    @Test
    public void toolbarHeight_isReadFromTheme() {
        Context context = new ContextThemeWrapper(RuntimeEnvironment.getApplication(),
                com.google.android.material.R.style.Theme_MaterialComponents_Light_NoActionBar);

        ScalingLayoutBehavior behavior = new ScalingLayoutBehavior(context, null);

        assertEquals(expectedActionBarSize(context), behavior.getToolbarHeight(), 0f);
    }

    @Test
    @Config(qualifiers = "w800dp-h1280dp")
    public void toolbarHeight_followsTabletActionBarSize() {
        Context context = new ContextThemeWrapper(RuntimeEnvironment.getApplication(),
                com.google.android.material.R.style.Theme_MaterialComponents_Light_NoActionBar);

        ScalingLayoutBehavior behavior = new ScalingLayoutBehavior(context, null);

        float density = context.getResources().getDisplayMetrics().density;
        assertEquals(64 * density, behavior.getToolbarHeight(), 1f);
    }

    @Test
    public void appBarWithoutScrollRange_isIgnored() {
        Context context = materialContext();
        ScalingLayoutBehavior behavior = new ScalingLayoutBehavior(context, null);
        ScalingLayout child = new ScalingLayout(context);
        AppBarLayout appBar = new AppBarLayout(context);

        assertFalse(behavior.onDependentViewChanged(new CoordinatorLayout(context), child, appBar));
        assertEquals(0f, child.getProgress(), 0f);
    }

    @Test
    public void halfScrolledAppBar_halfExpandsLayout() {
        Context context = materialContext();
        ScalingLayoutBehavior behavior = new ScalingLayoutBehavior(context, null);
        ScalingLayout child = new ScalingLayout(context);
        AppBarLayout appBar = scrollableAppBar(context);

        appBar.setY(-SCROLL_RANGE / 2f);
        assertTrue(behavior.onDependentViewChanged(new CoordinatorLayout(context), child, appBar));

        assertEquals(0.5f, child.getProgress(), 0.001f);
        assertEquals(SCROLL_RANGE / 2f + behavior.getToolbarHeight(), child.getTranslationY(), 0.001f);
    }

    @Test
    public void collapsedAppBar_fullyExpandsLayoutBelowToolbar() {
        Context context = materialContext();
        ScalingLayoutBehavior behavior = new ScalingLayoutBehavior(context, null);
        ScalingLayout child = new ScalingLayout(context);
        AppBarLayout appBar = scrollableAppBar(context);

        appBar.setY(-SCROLL_RANGE);
        behavior.onDependentViewChanged(new CoordinatorLayout(context), child, appBar);

        assertEquals(State.EXPANDED, child.getState());
        assertEquals(behavior.getToolbarHeight(), child.getTranslationY(), 0.001f);
    }

    @Test
    public void layoutDependsOnAppBarOnly() {
        Context context = materialContext();
        ScalingLayoutBehavior behavior = new ScalingLayoutBehavior(context, null);
        CoordinatorLayout parent = new CoordinatorLayout(context);
        ScalingLayout child = new ScalingLayout(context);

        assertTrue(behavior.layoutDependsOn(parent, child, new AppBarLayout(context)));
        assertFalse(behavior.layoutDependsOn(parent, child, new View(context)));
    }

    private static Context materialContext() {
        return new ContextThemeWrapper(RuntimeEnvironment.getApplication(),
                com.google.android.material.R.style.Theme_MaterialComponents_Light_NoActionBar);
    }

    private static AppBarLayout scrollableAppBar(Context context) {
        AppBarLayout appBar = new AppBarLayout(context);
        AppBarLayout.LayoutParams params = new AppBarLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, SCROLL_RANGE);
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
        appBar.addView(new View(context), params);
        appBar.measure(View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        appBar.layout(0, 0, 400, appBar.getMeasuredHeight());
        return appBar;
    }

    private static float expectedActionBarSize(Context context) {
        android.content.res.TypedArray a = context.obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        float size = a.getDimensionPixelSize(0, -1);
        a.recycle();
        return size;
    }
}

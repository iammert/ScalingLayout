package iammert.com.view.scalinglib;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.ContextThemeWrapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
public class ScalingLayoutBehaviorTest {

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

    private static float expectedActionBarSize(Context context) {
        android.content.res.TypedArray a = context.obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        float size = a.getDimensionPixelSize(0, -1);
        a.recycle();
        return size;
    }
}

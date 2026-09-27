package iammert.com.view.scalinglayout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.time.Duration;

import iammert.com.view.scalinglib.ScalingLayout;
import iammert.com.view.scalinglib.State;

@RunWith(RobolectricTestRunner.class)
public class DemoNavigationTest {

    @Test
    public void fabDemo_backCollapsesExpandedLayout() {
        assertBackCollapsesThenCloses(Robolectric.buildActivity(FABDemo.class).setup().get());
    }

    @Test
    public void searchDemo_backCollapsesExpandedLayout() {
        assertBackCollapsesThenCloses(Robolectric.buildActivity(SearchBarDemoActivity.class).setup().get());
    }

    @Test
    public void searchDemo_arrowBackCollapses() {
        SearchBarDemoActivity activity = Robolectric.buildActivity(SearchBarDemoActivity.class).setup().get();
        ScalingLayout scalingLayout = activity.findViewById(R.id.scalingLayout);
        idle();
        scalingLayout.expand(false);

        activity.findViewById(R.id.arrowBack).performClick();
        finishAnimation();

        assertEquals(State.COLLAPSED, scalingLayout.getState());
    }

    @Test
    public void collapsingDemo_upNavigationClosesScreen() {
        CollapsingDemoActivity activity = Robolectric.buildActivity(CollapsingDemoActivity.class).setup().get();

        assertTrue(activity.onSupportNavigateUp());

        assertTrue(activity.isFinishing());
    }

    private static void assertBackCollapsesThenCloses(AppCompatActivity activity) {
        ScalingLayout scalingLayout = activity.findViewById(R.id.scalingLayout);
        idle();
        scalingLayout.expand(false);

        activity.getOnBackPressedDispatcher().onBackPressed();
        finishAnimation();

        assertEquals(State.COLLAPSED, scalingLayout.getState());
        assertFalse(activity.isFinishing());

        activity.getOnBackPressedDispatcher().onBackPressed();
        assertTrue(activity.isFinishing());
    }

    private static void idle() {
        shadowOf(Looper.getMainLooper()).idle();
    }

    private static void finishAnimation() {
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(1));
    }
}

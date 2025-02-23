package iammert.com.view.scalinglib;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.time.Duration;

@RunWith(RobolectricTestRunner.class)
public class ScalingLayoutTest {

    private static final int PARENT_WIDTH = 1080;
    private static final int PARENT_HEIGHT = 1920;
    private static final int INITIAL_WIDTH = 300;
    private static final int HEIGHT = 100;

    private Context context;
    private FrameLayout parent;
    private ScalingLayout scalingLayout;

    @Before
    public void setUp() {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        context = activity;
        parent = new FrameLayout(context);
        scalingLayout = new ScalingLayout(context);
        parent.addView(scalingLayout, new FrameLayout.LayoutParams(INITIAL_WIDTH, HEIGHT));
        activity.setContentView(parent);
    }

    @Test
    public void startsCollapsed() {
        layout();
        assertEquals(State.COLLAPSED, scalingLayout.getState());
        assertEquals(INITIAL_WIDTH, scalingLayout.getWidth());
    }

    @Test
    public void expand_beforeFirstLayout_isAppliedAfterLayout() {
        scalingLayout.expand();

        layout();

        assertEquals(State.EXPANDED, scalingLayout.getState());
        assertEquals(1f, scalingLayout.getProgress(), 0f);
    }

    @Test
    public void setProgress_beforeFirstLayout_isAppliedAfterLayout() {
        scalingLayout.setProgress(0.5f);

        layout();

        assertEquals(State.PROGRESSING, scalingLayout.getState());
        assertEquals(0.5f, scalingLayout.getProgress(), 0.001f);
    }

    @Test
    public void expandAndCollapse_animateBetweenStates() {
        layout();

        scalingLayout.expand();
        finishAnimation();
        assertEquals(State.EXPANDED, scalingLayout.getState());

        scalingLayout.collapse();
        finishAnimation();
        assertEquals(State.COLLAPSED, scalingLayout.getState());
        assertEquals(INITIAL_WIDTH, scalingLayout.getWidth());
    }

    @Test
    public void setProgress_cancelsRunningAnimation() {
        layout();

        scalingLayout.expand();
        scalingLayout.setProgress(0.25f);
        finishAnimation();

        assertEquals(State.PROGRESSING, scalingLayout.getState());
        assertEquals(0.25f, scalingLayout.getProgress(), 0.001f);
    }

    /**
     * Measures and lays out the hierarchy, then runs posted runnables
     * and lays out again so that pending changes are reflected.
     */
    void layout() {
        measureAndLayout();
        shadowOf(Looper.getMainLooper()).idle();
        measureAndLayout();
    }

    void finishAnimation() {
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(1));
        measureAndLayout();
    }

    private void measureAndLayout() {
        parent.measure(
                View.MeasureSpec.makeMeasureSpec(PARENT_WIDTH, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(PARENT_HEIGHT, View.MeasureSpec.EXACTLY));
        parent.layout(0, 0, PARENT_WIDTH, PARENT_HEIGHT);
    }
}

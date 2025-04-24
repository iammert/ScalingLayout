package iammert.com.view.scalinglib;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.widget.FrameLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.time.Duration;

@RunWith(RobolectricTestRunner.class)
public class ScalingLayoutTest {

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
    public void expand_fillsParentWidth() {
        layout();

        scalingLayout.expand();
        finishAnimation();

        assertEquals(parent.getWidth(), scalingLayout.getWidth());
    }

    @Test
    public void expand_respectsParentPadding() {
        parent.setPadding(40, 0, 40, 0);
        layout();

        scalingLayout.expand();
        finishAnimation();

        assertEquals(parent.getWidth() - 80, scalingLayout.getWidth());
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
     * Runs pending layout passes and posted runnables.
     */
    void layout() {
        shadowOf(Looper.getMainLooper()).idle();
    }

    void finishAnimation() {
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(1));
    }
}

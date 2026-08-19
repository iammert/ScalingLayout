package iammert.com.view.scalinglib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Looper;
import android.os.Parcel;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;

import androidx.core.view.ViewCompat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

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
        // Layout direction is only resolved to RTL when the app declares RTL support.
        RuntimeEnvironment.getApplication().getApplicationInfo().flags |= ApplicationInfo.FLAG_SUPPORTS_RTL;
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
    public void expand_beforeFirstLayout_isReportedImmediately() {
        scalingLayout.expand();

        assertEquals(State.EXPANDED, scalingLayout.getState());
        assertEquals(1f, scalingLayout.getProgress(), 0f);
    }

    @Test
    public void expand_afterPendingProgress_winsOverPendingValue() {
        scalingLayout.setProgress(0.5f);
        parent.measure(View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY));
        parent.layout(0, 0, 400, 400);

        // Layout is initialized but the pending progress is not applied yet.
        scalingLayout.collapse();
        finishAnimation();

        assertEquals(State.COLLAPSED, scalingLayout.getState());
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
    public void expandAndCollapse_withoutAnimation_applyImmediately() {
        RecordingListener listener = new RecordingListener();
        scalingLayout.addListener(listener);
        layout();

        scalingLayout.expand(false);
        assertEquals(State.EXPANDED, scalingLayout.getState());
        assertEquals(1, listener.expanded);

        scalingLayout.collapse(false);
        assertEquals(State.COLLAPSED, scalingLayout.getState());
        assertEquals(1, listener.collapsed);
    }

    @Test
    public void expandWithoutAnimation_cancelsRunningCollapse() {
        layout();
        scalingLayout.expand(false);

        scalingLayout.collapse();
        scalingLayout.expand(false);
        finishAnimation();

        assertEquals(State.EXPANDED, scalingLayout.getState());
    }

    @Test
    public void toggle_switchesBetweenStates() {
        layout();

        scalingLayout.toggle();
        finishAnimation();
        assertTrue(scalingLayout.isExpanded());

        scalingLayout.toggle();
        finishAnimation();
        assertFalse(scalingLayout.isExpanded());
        assertEquals(State.COLLAPSED, scalingLayout.getState());
    }

    @Test
    public void toggle_reversesRunningExpandAnimation() {
        layout();

        scalingLayout.expand();
        scalingLayout.toggle();
        finishAnimation();

        assertEquals(State.COLLAPSED, scalingLayout.getState());
    }

    @Test
    public void toggle_beforeFirstLayout_expandsAfterLayout() {
        scalingLayout.toggle();
        assertTrue(scalingLayout.isExpanded());

        layout();

        assertEquals(State.EXPANDED, scalingLayout.getState());
    }

    @Test
    public void expand_fillsParentWidth() {
        layout();

        scalingLayout.expand();
        finishAnimation();

        assertEquals(parent.getWidth(), scalingLayout.getWidth());
    }

    @Test
    public void expand_isLimitedByMaxExpandedWidth() {
        scalingLayout.setMaxExpandedWidth(INITIAL_WIDTH + 10);
        layout();

        scalingLayout.expand();
        finishAnimation();

        assertEquals(INITIAL_WIDTH + 10, scalingLayout.getWidth());
    }

    @Test
    public void setMaxExpandedWidth_updatesExpandedLayout() {
        layout();
        scalingLayout.expand(false);
        layout();

        scalingLayout.setMaxExpandedWidth(INITIAL_WIDTH + 10);
        layout();

        assertEquals(INITIAL_WIDTH + 10, scalingLayout.getWidth());
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
    public void expanded_followsParentWidthChanges() {
        layout();
        scalingLayout.expand();
        finishAnimation();

        parent.getLayoutParams().width = 250;
        parent.requestLayout();
        layout();

        assertEquals(250, parent.getWidth());
        assertEquals(250, scalingLayout.getWidth());
        assertEquals(State.EXPANDED, scalingLayout.getState());
    }

    @Test
    public void collapsed_keepsInitialWidthWhenParentChanges() {
        layout();

        parent.getLayoutParams().width = 250;
        parent.requestLayout();
        layout();

        assertEquals(INITIAL_WIDTH, scalingLayout.getWidth());
        assertEquals(State.COLLAPSED, scalingLayout.getState());
    }

    @Test
    public void radius_followsHeightChanges() {
        layout();
        assertEquals(HEIGHT / 2f, outlineRadius(), 0.001f);

        scalingLayout.getLayoutParams().height = 60;
        scalingLayout.requestLayout();
        layout();

        assertEquals(30f, scalingLayout.getSettings().getMaxRadius(), 0.001f);
        assertEquals(30f, outlineRadius(), 0.001f);
    }

    @Test
    public void detachDuringAnimation_jumpsToTargetState() {
        layout();
        scalingLayout.expand();

        parent.removeView(scalingLayout);

        assertEquals(State.EXPANDED, scalingLayout.getState());
        assertEquals(1f, scalingLayout.getProgress(), 0f);
    }

    @Test
    public void margins_shrinkWithProgress() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(INITIAL_WIDTH, HEIGHT);
        params.setMargins(20, 10, 40, 30);
        scalingLayout.setLayoutParams(params);
        layout();

        scalingLayout.setProgress(0.5f);
        layout();

        assertEquals(10, params.leftMargin);
        assertEquals(5, params.topMargin);
        assertEquals(20, params.rightMargin);
        assertEquals(15, params.bottomMargin);
    }

    @Test
    public void relativeMargins_shrinkInRtlLayout() {
        parent.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(INITIAL_WIDTH, HEIGHT);
        params.setMarginStart(40);
        params.setMarginEnd(20);
        scalingLayout.setLayoutParams(params);
        layout();

        scalingLayout.setProgress(0.5f);
        layout();

        assertEquals(20, params.getMarginStart());
        assertEquals(10, params.getMarginEnd());
        assertEquals(20, params.rightMargin);
        assertEquals(10, params.leftMargin);
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

    @Test
    public void setProgress_clampsOutOfRangeValues() {
        layout();

        scalingLayout.setProgress(1.0000001f);
        assertEquals(State.EXPANDED, scalingLayout.getState());

        scalingLayout.setProgress(-0.5f);
        assertEquals(State.COLLAPSED, scalingLayout.getState());

        scalingLayout.setProgress(Float.NaN);
        assertEquals(0f, scalingLayout.getProgress(), 0f);
    }

    @Test
    public void savedState_survivesParcelRoundTrip() {
        layout();
        scalingLayout.setProgress(0.4f);

        Parcel parcel = Parcel.obtain();
        scalingLayout.onSaveInstanceState().writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        ScalingLayout.SavedState restored = ScalingLayout.SavedState.CREATOR.createFromParcel(parcel);
        parcel.recycle();

        ScalingLayout newLayout = new ScalingLayout(context);
        newLayout.onRestoreInstanceState(restored);
        assertEquals(0.4f, newLayout.getProgress(), 0.001f);
    }

    @Test
    public void savedState_storesTargetOfRunningAnimation() {
        layout();
        scalingLayout.expand();

        ScalingLayout.SavedState state = (ScalingLayout.SavedState) scalingLayout.onSaveInstanceState();

        assertEquals(1f, state.progress, 0f);
    }

    @Test
    public void listeners_areNotifiedOncePerStateChange() {
        RecordingListener first = new RecordingListener();
        RecordingListener second = new RecordingListener();
        scalingLayout.addListener(first);
        scalingLayout.addListener(second);
        layout();

        scalingLayout.expand();
        finishAnimation();
        scalingLayout.setProgress(1f);

        assertEquals(1, first.expanded);
        assertEquals(1, second.expanded);
        assertEquals(0, first.collapsed);
    }

    @Test
    public void listener_canOverrideOnlyOneCallback() {
        final int[] expanded = new int[1];
        scalingLayout.addListener(new ScalingLayoutListener() {
            @Override
            public void onExpanded() {
                expanded[0]++;
            }
        });
        layout();

        scalingLayout.setProgress(0.5f);
        scalingLayout.setProgress(1f);
        scalingLayout.setProgress(0f);

        assertEquals(1, expanded[0]);
    }

    @Test
    public void removedListener_isNotNotified() {
        RecordingListener listener = new RecordingListener();
        scalingLayout.addListener(listener);
        scalingLayout.removeListener(listener);
        layout();

        scalingLayout.expand();
        finishAnimation();

        assertEquals(0, listener.expanded);
    }

    @Test
    public void setListener_replacesRegisteredListeners() {
        RecordingListener first = new RecordingListener();
        RecordingListener second = new RecordingListener();
        scalingLayout.addListener(first);
        scalingLayout.setListener(second);
        layout();

        scalingLayout.setProgress(1f);

        assertEquals(0, first.expanded);
        assertEquals(1, second.expanded);
    }

    @Test
    public void accessibility_exposesExpandActionWhenCollapsed() {
        layout();

        AccessibilityNodeInfo info = scalingLayout.createAccessibilityNodeInfo();

        assertTrue(info.getActionList().contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_EXPAND));
        assertFalse(info.getActionList().contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_COLLAPSE));
        assertEquals("Collapsed", String.valueOf(ViewCompat.getStateDescription(scalingLayout)));
    }

    @Test
    public void accessibility_expandAndCollapseActions() {
        layout();

        assertTrue(scalingLayout.performAccessibilityAction(AccessibilityNodeInfo.ACTION_EXPAND, null));
        finishAnimation();
        assertEquals(State.EXPANDED, scalingLayout.getState());
        assertEquals("Expanded", String.valueOf(ViewCompat.getStateDescription(scalingLayout)));

        AccessibilityNodeInfo info = scalingLayout.createAccessibilityNodeInfo();
        assertTrue(info.getActionList().contains(AccessibilityNodeInfo.AccessibilityAction.ACTION_COLLAPSE));

        assertTrue(scalingLayout.performAccessibilityAction(AccessibilityNodeInfo.ACTION_COLLAPSE, null));
        finishAnimation();
        assertEquals(State.COLLAPSED, scalingLayout.getState());
    }

    private static class RecordingListener implements ScalingLayoutListener {
        int collapsed;
        int expanded;

        @Override
        public void onCollapsed() {
            collapsed++;
        }

        @Override
        public void onExpanded() {
            expanded++;
        }

        @Override
        public void onProgress(float progress) {
        }
    }

    private float outlineRadius() {
        return ((ScalingLayoutOutlineProvider) scalingLayout.getOutlineProvider()).getRadius();
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

package iammert.com.view.scalinglayout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import iammert.com.view.scalinglib.ScalingLayout;
import iammert.com.view.scalinglib.State;

@RunWith(AndroidJUnit4.class)
public class FABDemoTest {

    private static final long TIMEOUT_MS = 3000;

    @Test
    public void clickingFab_expandsAndClickingOutside_collapses() {
        try (ActivityScenario<FABDemo> scenario = ActivityScenario.launch(FABDemo.class)) {
            scenario.onActivity(activity -> activity.findViewById(R.id.scalingLayout).performClick());
            assertEquals(State.EXPANDED, waitForState(scenario, State.EXPANDED));

            scenario.onActivity(activity -> activity.findViewById(R.id.rootLayout).performClick());
            assertEquals(State.COLLAPSED, waitForState(scenario, State.COLLAPSED));
        }
    }

    @Test
    public void expandedState_survivesRecreation() {
        try (ActivityScenario<FABDemo> scenario = ActivityScenario.launch(FABDemo.class)) {
            scenario.onActivity(activity -> activity.<ScalingLayout>findViewById(R.id.scalingLayout).expand());
            waitForState(scenario, State.EXPANDED);

            scenario.recreate();

            assertEquals(State.EXPANDED, waitForState(scenario, State.EXPANDED));
        }
    }

    @Test
    public void back_collapsesExpandedLayoutInsteadOfClosing() {
        try (ActivityScenario<FABDemo> scenario = ActivityScenario.launch(FABDemo.class)) {
            scenario.onActivity(activity -> activity.<ScalingLayout>findViewById(R.id.scalingLayout).expand());
            waitForState(scenario, State.EXPANDED);

            Espresso.pressBack();

            assertEquals(State.COLLAPSED, waitForState(scenario, State.COLLAPSED));
            scenario.onActivity(activity -> assertFalse(activity.isFinishing()));
        }
    }

    private static State waitForState(ActivityScenario<FABDemo> scenario, State expected) {
        AtomicReference<State> state = new AtomicReference<>();
        long deadline = SystemClock.uptimeMillis() + TIMEOUT_MS;
        do {
            scenario.onActivity(activity ->
                    state.set(activity.<ScalingLayout>findViewById(R.id.scalingLayout).getState()));
            if (state.get() == expected) {
                break;
            }
            SystemClock.sleep(50);
        } while (SystemClock.uptimeMillis() < deadline);
        return state.get();
    }
}

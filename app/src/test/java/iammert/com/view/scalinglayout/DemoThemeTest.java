package iammert.com.view.scalinglayout;

import static org.junit.Assert.assertNotNull;

import android.app.Activity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Inflates every demo with the Material 3 theme in light and dark mode.
 */
@RunWith(RobolectricTestRunner.class)
public class DemoThemeTest {

    @Test
    public void demosInflateInLightMode() {
        launchAll();
    }

    @Test
    @Config(qualifiers = "night")
    public void demosInflateInDarkMode() {
        launchAll();
    }

    private static void launchAll() {
        launch(MainActivity.class);
        launch(CollapsingDemoActivity.class);
        launch(FABDemo.class);
        launch(SearchBarDemoActivity.class);
    }

    private static void launch(Class<? extends Activity> activityClass) {
        Activity activity = Robolectric.buildActivity(activityClass).setup().get();
        assertNotNull(activity.findViewById(android.R.id.content));
    }
}

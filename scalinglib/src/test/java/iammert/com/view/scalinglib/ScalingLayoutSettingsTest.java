package iammert.com.view.scalinglib;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.AttributeSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class ScalingLayoutSettingsTest {

    private final Context context = RuntimeEnvironment.getApplication();

    @Test
    public void maxRadius_defaultsToHalfOfHeight() {
        ScalingLayoutSettings settings = new ScalingLayoutSettings(context, null);
        settings.initialize(300, 101);
        assertEquals(50.5f, settings.getMaxRadius(), 0.001f);
    }

    @Test
    public void radiusFactor_isClampedToOne() {
        ScalingLayoutSettings settings = settingsWithRadiusFactor("2.5");
        settings.initialize(300, 100);
        assertEquals(50f, settings.getMaxRadius(), 0.001f);
    }

    @Test
    public void negativeRadiusFactor_isClampedToZero() {
        ScalingLayoutSettings settings = settingsWithRadiusFactor("-0.5");
        settings.initialize(300, 100);
        assertEquals(0f, settings.getMaxRadius(), 0.001f);
    }

    @Test
    public void maxExpandedWidth_limitsExpandedWidth() {
        AttributeSet attrs = Robolectric.buildAttributeSet()
                .addAttribute(R.attr.maxExpandedWidth, "200px")
                .build();
        ScalingLayoutSettings settings = new ScalingLayoutSettings(context, attrs);
        settings.initialize(100, 50);
        settings.setMaxWidth(500);

        assertEquals(200, settings.getMaxExpandedWidth());
        assertEquals(200, settings.getMaxWidth());
        assertEquals(500, settings.getAvailableWidth());
    }

    @Test
    public void maxExpandedWidth_isNeverSmallerThanInitialWidth() {
        ScalingLayoutSettings settings = new ScalingLayoutSettings(context, null);
        settings.setMaxExpandedWidth(80);
        settings.initialize(100, 50);
        settings.setMaxWidth(500);

        assertEquals(100, settings.getMaxWidth());
    }

    private ScalingLayoutSettings settingsWithRadiusFactor(String value) {
        AttributeSet attrs = Robolectric.buildAttributeSet()
                .addAttribute(R.attr.radiusFactor, value)
                .build();
        return new ScalingLayoutSettings(context, attrs);
    }
}

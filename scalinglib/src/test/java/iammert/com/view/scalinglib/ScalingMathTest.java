package iammert.com.view.scalinglib;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ScalingMathTest {

    private static final float DELTA = 0.0001f;

    @Test
    public void clampProgress_keepsValuesInRange() {
        assertEquals(0f, ScalingMath.clampProgress(-0.5f), DELTA);
        assertEquals(0.3f, ScalingMath.clampProgress(0.3f), DELTA);
        assertEquals(1f, ScalingMath.clampProgress(1.7f), DELTA);
        assertEquals(0f, ScalingMath.clampProgress(Float.NaN), DELTA);
    }

    @Test
    public void radiusFor_isMaxWhenCollapsedAndZeroWhenExpanded() {
        assertEquals(24f, ScalingMath.radiusFor(24f, 0f), DELTA);
        assertEquals(12f, ScalingMath.radiusFor(24f, 0.5f), DELTA);
        assertEquals(0f, ScalingMath.radiusFor(24f, 1f), DELTA);
    }

    @Test
    public void widthFor_interpolatesBetweenInitialAndMaxWidth() {
        assertEquals(300, ScalingMath.widthFor(300, 1080, 0f));
        assertEquals(690, ScalingMath.widthFor(300, 1080, 0.5f));
        assertEquals(1080, ScalingMath.widthFor(300, 1080, 1f));
    }

    @Test
    public void widthFor_worksWithoutRoundedCorners() {
        // radiusFactor = 0 used to divide by a zero max radius and collapse the width to 0.
        assertEquals(1080, ScalingMath.widthFor(300, 1080, 1f));
    }

    @Test
    public void widthFor_handlesLayoutWiderThanMaxWidth() {
        assertEquals(1200, ScalingMath.widthFor(1200, 1080, 0f));
        assertEquals(1080, ScalingMath.widthFor(1200, 1080, 1f));
    }

    @Test
    public void marginFor_shrinksToZeroWhenExpanded() {
        assertEquals(16f, ScalingMath.marginFor(16f, 0f), DELTA);
        assertEquals(4f, ScalingMath.marginFor(16f, 0.75f), DELTA);
        assertEquals(0f, ScalingMath.marginFor(16f, 1f), DELTA);
    }

    @Test
    public void stateFor_mapsProgressToState() {
        assertEquals(State.COLLAPSED, ScalingMath.stateFor(0f));
        assertEquals(State.PROGRESSING, ScalingMath.stateFor(0.4f));
        assertEquals(State.EXPANDED, ScalingMath.stateFor(1f));
    }
}

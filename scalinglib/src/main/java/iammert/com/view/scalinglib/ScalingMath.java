package iammert.com.view.scalinglib;

/**
 * Pure calculations used by {@link ScalingLayout}.
 * <p>
 * Progress is always in range [0, 1] where 0 means collapsed
 * and 1 means fully expanded.
 */
final class ScalingMath {

    private ScalingMath() {
    }

    static float clampProgress(float progress) {
        if (Float.isNaN(progress)) {
            return 0f;
        }
        return Math.max(0f, Math.min(1f, progress));
    }

    static float radiusFor(float maxRadius, float progress) {
        return maxRadius * (1f - clampProgress(progress));
    }

    static int widthFor(int initialWidth, int maxWidth, float progress) {
        float width = initialWidth + (maxWidth - initialWidth) * clampProgress(progress);
        int lower = Math.min(initialWidth, maxWidth);
        int upper = Math.max(initialWidth, maxWidth);
        return Math.max(lower, Math.min(upper, Math.round(width)));
    }

    static float marginFor(float initialMargin, float progress) {
        return initialMargin * (1f - clampProgress(progress));
    }

    static State stateFor(float progress) {
        float clamped = clampProgress(progress);
        if (clamped >= 1f) {
            return State.EXPANDED;
        } else if (clamped <= 0f) {
            return State.COLLAPSED;
        }
        return State.PROGRESSING;
    }
}

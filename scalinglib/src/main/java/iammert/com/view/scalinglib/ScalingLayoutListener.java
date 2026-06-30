package iammert.com.view.scalinglib;

/**
 * Created by mertsimsek on 01/10/2017.
 * <p>
 * All methods have empty default implementations, override only the ones you need.
 */

public interface ScalingLayoutListener {

    /**
     * Called once when the layout reaches its initial (collapsed) state.
     */
    default void onCollapsed() {
    }

    /**
     * Called once when the layout is fully expanded.
     */
    default void onExpanded() {
    }

    /**
     * Called while the layout is between collapsed and expanded states.
     *
     * @param progress value between 0.0f (collapsed) and 1.0f (expanded),
     *                 same scale as {@link ScalingLayout#setProgress(float)}
     */
    default void onProgress(float progress) {
    }
}

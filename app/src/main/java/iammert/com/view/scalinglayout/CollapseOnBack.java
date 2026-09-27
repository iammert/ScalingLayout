package iammert.com.view.scalinglayout;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;

import iammert.com.view.scalinglib.ScalingLayout;
import iammert.com.view.scalinglib.ScalingLayoutListener;
import iammert.com.view.scalinglib.State;

/**
 * Collapses an expanded ScalingLayout on back instead of leaving the screen.
 */
final class CollapseOnBack {

    private CollapseOnBack() {
    }

    static void attach(ComponentActivity activity, ScalingLayout scalingLayout) {
        OnBackPressedCallback callback = new OnBackPressedCallback(scalingLayout.getState() != State.COLLAPSED) {
            @Override
            public void handleOnBackPressed() {
                scalingLayout.collapse();
            }
        };
        activity.getOnBackPressedDispatcher().addCallback(activity, callback);

        scalingLayout.addListener(new ScalingLayoutListener() {
            @Override
            public void onCollapsed() {
                callback.setEnabled(false);
            }

            @Override
            public void onExpanded() {
                callback.setEnabled(true);
            }

            @Override
            public void onProgress(float progress) {
                callback.setEnabled(true);
            }
        });
    }
}

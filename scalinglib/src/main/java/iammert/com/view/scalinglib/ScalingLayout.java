package iammert.com.view.scalinglib;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

/**
 * Created by mertsimsek on 30/09/2017.
 */

public class ScalingLayout extends FrameLayout {

    /**
     * Settings
     */
    private ScalingLayoutSettings settings;

    /**
     * Current progress. 0 is collapsed, 1 is fully expanded.
     * Radius, width and margins are derived from this value.
     */
    private float currentProgress;

    /**
     * Current radius
     */
    private float currentRadius;

    /**
     * Width is dependent value. It depends on
     * progress. If progress gets updated,
     * layout width will be updated according to this change.
     */
    private int currentWidth;

    /**
     * If layout has margins, margin has to be change
     * according to progress. Margins are kept in
     * start, top, end, bottom order to support RTL layouts.
     */
    private float[] maxMargins;
    private float[] currentMargins;

    /**
     * State for layout.
     */
    private State state;

    /**
     * Values to draw rounded on layout
     */
    private Path path;
    private RectF rectF;
    private Paint maskPaint;

    /**
     * Animator to expand and collapse
     */
    private ValueAnimator valueAnimator;

    /**
     * Listeners to notify observers about
     * progress and collapse/expand
     */
    private final List<ScalingLayoutListener> listeners = new ArrayList<>();

    /**
     * Progress requested before the first layout, either restored from
     * saved instance state or set through expand/collapse/setProgress.
     * It is applied once the layout is measured for the first time.
     */
    private float pendingProgress = -1f;

    /**
     * Target progress of the running expand/collapse animation.
     */
    private float animationTargetProgress;

    /**
     * CustomOutline for elevation shadows
     */
    @Nullable
    private ScalingLayoutOutlineProvider viewOutline;

    /**
     * Follows the parent size so an expanded layout keeps filling the parent
     * when it is resized, for example in multi window mode.
     */
    private final View.OnLayoutChangeListener parentLayoutChangeListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                   int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (right - left != oldRight - oldLeft) {
                onParentWidthChanged();
            }
        }
    };


    public ScalingLayout(@NonNull Context context) {
        super(context);
        setup(context, null);
    }

    public ScalingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    public ScalingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs);
    }

    public ScalingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context, attrs);
    }

    /**
     * Initialize layout. Called only from constructors.
     *
     * @param context
     * @param attributeSet
     */
    private void setup(Context context, @Nullable AttributeSet attributeSet) {
        settings = new ScalingLayoutSettings(context, attributeSet);
        state = State.COLLAPSED;
        updateStateDescription();

        path = new Path();
        rectF = new RectF(0, 0, 0, 0);

        maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        setLayerType(LAYER_TYPE_HARDWARE, null);

        valueAnimator = ValueAnimator.ofFloat(0, 0);
        valueAnimator.setDuration(settings.getAnimationDuration());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                applyProgress((Float) valueAnimator.getAnimatedValue());
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (maxMargins == null) {
            maxMargins = new float[4];
            currentMargins = new float[4];
            if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = ((ViewGroup.MarginLayoutParams) getLayoutParams());
                currentMargins[0] = maxMargins[0] = marginLayoutParams.getMarginStart();
                currentMargins[1] = maxMargins[1] = marginLayoutParams.topMargin;
                currentMargins[2] = maxMargins[2] = marginLayoutParams.getMarginEnd();
                currentMargins[3] = maxMargins[3] = marginLayoutParams.bottomMargin;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        settings.setMaxWidth(getParentContentWidth());
        if (!settings.isInitialized()) {
            settings.initialize(w, h);
            currentWidth = w;
            currentProgress = 0f;
            currentRadius = settings.getMaxRadius();
            viewOutline = new ScalingLayoutOutlineProvider(w, h, currentRadius);
            setOutlineProvider(viewOutline);

            if (pendingProgress >= 0f) {
                final float restoredProgress = pendingProgress;
                post(new Runnable() {
                    @Override
                    public void run() {
                        applyProgress(restoredProgress);
                    }
                });
            }
            pendingProgress = -1f;
        } else if (h != oldh) {
            // Height changed, for example by wrap_content or a new font scale.
            settings.setHeight(h);
            currentRadius = ScalingMath.radiusFor(settings.getMaxRadius(), currentProgress);
        }

        rectF.set(0, 0, w, h);
        updateViewOutline(h, currentWidth, currentRadius);
        invalidate();
    }

    /**
     * Width available inside the parent. The layout expands to this width
     * instead of the screen width, so padded parents, split screen and
     * multi window modes are respected.
     *
     * @return parent width without horizontal padding, or 0 if unknown
     */
    private int getParentContentWidth() {
        ViewParent parent = getParent();
        if (!(parent instanceof ViewGroup)) {
            return 0;
        }
        ViewGroup parentGroup = (ViewGroup) parent;
        return parentGroup.getWidth() - parentGroup.getPaddingLeft() - parentGroup.getPaddingRight();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int save = canvas.save();
        super.dispatchDraw(canvas);
        canvas.restoreToCount(save);

        path.reset();
        path.addRoundRect(rectF, currentRadius, currentRadius, Path.Direction.CCW);
        canvas.drawPath(path, maskPaint);
    }

    /**
     * Provides current {@link ScalingLayoutSettings}
     *
     * @return
     */
    public ScalingLayoutSettings getSettings() {
        return settings;
    }

    /**
     * get current state of layout
     *
     * @return
     */
    public State getState() {
        return state;
    }

    /**
     * Expand layout to screen.
     * If the layout is not measured yet, it is expanded right after the first layout.
     */
    public void expand() {
        animateProgressTo(1f);
    }

    /**
     * Collapse layout to initial position.
     * If the layout is not measured yet, it stays collapsed after the first layout.
     */
    public void collapse() {
        animateProgressTo(0f);
    }

    /**
     * Animates from the current progress to the target progress so that
     * calling expand/collapse in the middle of an animation does not jump.
     *
     * @param targetProgress
     */
    private void animateProgressTo(float targetProgress) {
        if (!settings.isInitialized()) {
            pendingProgress = targetProgress;
            return;
        }
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        if (currentProgress == targetProgress) {
            return;
        }
        animationTargetProgress = targetProgress;
        valueAnimator.setFloatValues(currentProgress, targetProgress);
        valueAnimator.start();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        // If an animation is running, save where it is heading to.
        savedState.progress = valueAnimator.isRunning() ? animationTargetProgress : currentProgress;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (settings.isInitialized()) {
            applyProgress(savedState.progress);
        } else {
            pendingProgress = savedState.progress;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getParent() instanceof View) {
            ((View) getParent()).addOnLayoutChangeListener(parentLayoutChangeListener);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (getParent() instanceof View) {
            ((View) getParent()).removeOnLayoutChangeListener(parentLayoutChangeListener);
        }
        valueAnimator.cancel();
        super.onDetachedFromWindow();
    }

    private void onParentWidthChanged() {
        int parentWidth = getParentContentWidth();
        if (parentWidth <= 0 || parentWidth == settings.getMaxWidth()) {
            return;
        }
        settings.setMaxWidth(parentWidth);
        if (settings.isInitialized() && currentProgress > 0f && !valueAnimator.isRunning()) {
            // Parent is in the middle of a layout pass, apply on the next frame.
            post(new Runnable() {
                @Override
                public void run() {
                    applyProgress(currentProgress);
                }
            });
        }
    }

    /**
     * This method takes a progress parameter value
     * between 0.0f and 1.0f. And apply this
     * progress value to layout. A running expand/collapse
     * animation is cancelled.
     *
     * @param progress
     */
    public void setProgress(float progress) {
        if (Float.isNaN(progress) || progress > 1.0f || progress < 0.0f) {
            return;
        }
        if (!settings.isInitialized()) {
            pendingProgress = progress;
            return;
        }
        valueAnimator.cancel();
        applyProgress(progress);
    }

    /**
     * Current progress of the layout.
     *
     * @return value between 0.0f (collapsed) and 1.0f (expanded)
     */
    public float getProgress() {
        return currentProgress;
    }

    /**
     * Sets expand/collapse animation duration in milliseconds.
     * Can also be set from XML with app:animationDuration.
     *
     * @param durationMillis
     */
    public void setAnimationDuration(long durationMillis) {
        settings.setAnimationDuration(durationMillis);
        valueAnimator.setDuration(settings.getAnimationDuration());
    }

    /**
     * Sets interpolator used by expand/collapse animation.
     *
     * @param interpolator
     */
    public void setInterpolator(@Nullable TimeInterpolator interpolator) {
        valueAnimator.setInterpolator(interpolator);
    }

    /**
     * Replaces all registered listeners with the given listener.
     *
     * @param scalingLayoutListener listener to set, or null to remove all listeners
     */
    public void setListener(@Nullable ScalingLayoutListener scalingLayoutListener) {
        listeners.clear();
        if (scalingLayoutListener != null) {
            listeners.add(scalingLayoutListener);
        }
    }

    /**
     * Registers a listener in addition to the ones already registered.
     *
     * @param scalingLayoutListener
     */
    public void addListener(@NonNull ScalingLayoutListener scalingLayoutListener) {
        if (!listeners.contains(scalingLayoutListener)) {
            listeners.add(scalingLayoutListener);
        }
    }

    /**
     * Removes a previously registered listener.
     *
     * @param scalingLayoutListener
     */
    public void removeListener(@NonNull ScalingLayoutListener scalingLayoutListener) {
        listeners.remove(scalingLayoutListener);
    }

    /**
     * Updates view outline borders and radius
     *
     * @param height
     * @param width
     * @param radius
     */
    private void updateViewOutline(int height, int width, float radius) {
        if (viewOutline != null) {
            viewOutline.setHeight(height);
            viewOutline.setWidth(width);
            viewOutline.setRadius(radius);
            invalidateOutline();
        }
    }

    /**
     * Applies given progress to the layout. Radius, width and
     * margins of the layout are calculated from progress.
     *
     * @param progress
     */
    private void applyProgress(float progress) {
        if (Float.isNaN(progress) || !settings.isInitialized()) {
            return;
        }

        currentProgress = ScalingMath.clampProgress(progress);
        currentRadius = ScalingMath.radiusFor(settings.getMaxRadius(), currentProgress);
        currentWidth = ScalingMath.widthFor(settings.getInitialWidth(), settings.getMaxWidth(), currentProgress);
        for (int i = 0; i < currentMargins.length; i++) {
            currentMargins[i] = ScalingMath.marginFor(maxMargins[i], currentProgress);
        }
        updateState();
        updateViewOutline(getHeight(), currentWidth, currentRadius);

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = currentWidth;
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            int start = (int) currentMargins[0];
            int end = (int) currentMargins[2];
            boolean isRtl = getLayoutDirection() == LAYOUT_DIRECTION_RTL;
            // Absolute margins are what parents use while laying out children,
            // relative ones are kept in sync so a later RTL resolution does not
            // restore the initial values.
            marginLayoutParams.setMargins(
                    isRtl ? end : start,
                    (int) currentMargins[1],
                    isRtl ? start : end,
                    (int) currentMargins[3]);
            marginLayoutParams.setMarginStart(start);
            marginLayoutParams.setMarginEnd(end);
        }
        requestLayout();
        invalidate();
    }

    /**
     * Updates layout state
     */
    private void updateState() {
        State previousState = state;
        state = ScalingMath.stateFor(currentProgress);

        if (state != previousState) {
            updateStateDescription();
        }
        if (state != previousState || state == State.PROGRESSING) {
            notifyListener();
        }
    }

    /**
     * Lets accessibility services announce whether the layout is expanded.
     */
    private void updateStateDescription() {
        if (state == State.EXPANDED) {
            ViewCompat.setStateDescription(this, getContext().getString(R.string.sl_state_expanded));
        } else if (state == State.COLLAPSED) {
            ViewCompat.setStateDescription(this, getContext().getString(R.string.sl_state_collapsed));
        }
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return ScalingLayout.class.getName();
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (state != State.EXPANDED) {
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_EXPAND);
        }
        if (state != State.COLLAPSED) {
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_COLLAPSE);
        }
    }

    @Override
    public boolean performAccessibilityAction(int action, @Nullable Bundle arguments) {
        if (action == AccessibilityNodeInfo.ACTION_EXPAND && state != State.EXPANDED) {
            expand();
            return true;
        }
        if (action == AccessibilityNodeInfo.ACTION_COLLAPSE && state != State.COLLAPSED) {
            collapse();
            return true;
        }
        return super.performAccessibilityAction(action, arguments);
    }

    /**
     * Notify observers about change
     */
    private void notifyListener() {
        // Copy so listeners can remove themselves while being notified.
        for (ScalingLayoutListener listener : new ArrayList<>(listeners)) {
            if (state == State.COLLAPSED) {
                listener.onCollapsed();
            } else if (state == State.EXPANDED) {
                listener.onExpanded();
            } else {
                listener.onProgress(currentProgress);
            }
        }
    }

    static class SavedState extends BaseSavedState {

        float progress;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            progress = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(progress);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}

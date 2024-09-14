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
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by mertsimsek on 30/09/2017.
 */

public class ScalingLayout extends FrameLayout {

    /**
     * Settings
     */
    ScalingLayoutSettings settings;

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
     * according to progress.
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
     * Listener to notify observer about
     * progress and collapse/expand
     */
    private ScalingLayoutListener scalingLayoutListener;

    /**
     * Progress restored from saved instance state. It is applied
     * once the layout is measured for the first time.
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


    public ScalingLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public ScalingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScalingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ScalingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /**
     * Initialize layout
     *
     * @param context
     * @param attributeSet
     */
    public void init(Context context, AttributeSet attributeSet) {
        settings = new ScalingLayoutSettings(context, attributeSet);
        state = State.COLLAPSED;

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
                currentMargins[0] = maxMargins[0] = marginLayoutParams.leftMargin;
                currentMargins[1] = maxMargins[1] = marginLayoutParams.topMargin;
                currentMargins[2] = maxMargins[2] = marginLayoutParams.rightMargin;
                currentMargins[3] = maxMargins[3] = marginLayoutParams.bottomMargin;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!settings.isInitialized()) {
            settings.initialize(w, h);
            currentWidth = w;
            currentProgress = 0f;
            currentRadius = settings.getMaxRadius();
            viewOutline = new ScalingLayoutOutlineProvider(w, h, currentRadius);
            setOutlineProvider(viewOutline);

            if (pendingProgress > 0f) {
                final float restoredProgress = pendingProgress;
                post(new Runnable() {
                    @Override
                    public void run() {
                        applyProgress(restoredProgress);
                    }
                });
            }
            pendingProgress = -1f;
        }

        rectF.set(0, 0, w, h);
        updateViewOutline(h, currentWidth, currentRadius);
        invalidate();
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
     * Expand layout to screen
     */
    public void expand() {
        animateProgressTo(1f);
    }

    /**
     * Collapse layout to initial position
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
    protected void onDetachedFromWindow() {
        valueAnimator.cancel();
        super.onDetachedFromWindow();
    }

    /**
     * This method takes a progress parameter value
     * between 0.0f and 1.0f. And apply this
     * progress value to layout.
     *
     * @param progress
     */
    public void setProgress(float progress) {
        if (Float.isNaN(progress) || progress > 1.0f || progress < 0.0f) {
            return;
        }
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

    public void setListener(ScalingLayoutListener scalingLayoutListener) {
        this.scalingLayoutListener = scalingLayoutListener;
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
            ((ViewGroup.MarginLayoutParams) layoutParams)
                    .setMargins((int) currentMargins[0],
                            (int) currentMargins[1],
                            (int) currentMargins[2],
                            (int) currentMargins[3]);
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

        if (state != previousState || state == State.PROGRESSING) {
            notifyListener();
        }
    }

    /**
     * Notify observers about change
     */
    private void notifyListener() {
        if (scalingLayoutListener != null) {
            if (state == State.COLLAPSED) {
                scalingLayoutListener.onCollapsed();
            } else if (state == State.EXPANDED) {
                scalingLayoutListener.onExpanded();
            } else {
                scalingLayoutListener.onProgress(currentProgress);
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

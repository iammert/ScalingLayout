package iammert.com.view.scalinglib;

/**
 * State of a {@link ScalingLayout}. {@link #PROGRESSING} means the layout is
 * between collapsed and expanded, for example while it is animating.
 * <p>
 * Created by mertsimsek on 01/10/2017.
 */

public enum State {
    EXPANDED, COLLAPSED, PROGRESSING
}

# ScalingLayoutBehavior is instantiated by CoordinatorLayout via reflection
# when referenced from XML with app:layout_behavior.
-keep public class iammert.com.view.scalinglib.ScalingLayoutBehavior {
    public <init>(android.content.Context, android.util.AttributeSet);
}

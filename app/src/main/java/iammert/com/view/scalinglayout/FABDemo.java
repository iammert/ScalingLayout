package iammert.com.view.scalinglayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import iammert.com.view.scalinglib.ScalingLayout;
import iammert.com.view.scalinglib.ScalingLayoutListener;
import iammert.com.view.scalinglib.State;

/**
 * Created by mertsimsek on 01/10/2017.
 */

public class FABDemo extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemBars.apply(this);
        setContentView(R.layout.activity_fab);

        final ImageView fabIcon = findViewById(R.id.fabIcon);
        final LinearLayout filterLayout = findViewById(R.id.filterLayout);
        final ScalingLayout scalingLayout = findViewById(R.id.scalingLayout);

        scalingLayout.setListener(new ScalingLayoutListener() {
            @Override
            public void onCollapsed() {
                fabIcon.animate().alpha(1).setDuration(150).start();
                filterLayout.animate().alpha(0).setDuration(150)
                        .withStartAction(() -> fabIcon.setVisibility(View.VISIBLE))
                        .withEndAction(() -> filterLayout.setVisibility(View.INVISIBLE))
                        .start();
            }

            @Override
            public void onExpanded() {
                fabIcon.animate().alpha(0).setDuration(200).start();
                filterLayout.animate().alpha(1).setDuration(200)
                        .withStartAction(() -> filterLayout.setVisibility(View.VISIBLE))
                        .withEndAction(() -> fabIcon.setVisibility(View.INVISIBLE))
                        .start();
            }

            @Override
            public void onProgress(float progress) {
                fabIcon.setVisibility(View.INVISIBLE);
                filterLayout.setVisibility(View.INVISIBLE);
            }
        });

        scalingLayout.setOnClickListener(view -> {
            if (scalingLayout.getState() == State.COLLAPSED) {
                scalingLayout.expand();
            }
        });

        findViewById(R.id.rootLayout).setOnClickListener(view -> {
            if (scalingLayout.getState() == State.EXPANDED) {
                scalingLayout.collapse();
            }
        });

        CollapseOnBack.attach(this, scalingLayout);
    }
}

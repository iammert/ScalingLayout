package iammert.com.view.scalinglayout;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import iammert.com.view.scalinglib.ScalingLayout;
import iammert.com.view.scalinglib.ScalingLayoutListener;
import iammert.com.view.scalinglib.State;

/**
 * Created by mertsimsek on 01/10/2017.
 */

public class SearchBarDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemBars.apply(this);
        setContentView(R.layout.activity_search);

        final TextView textViewSearch = findViewById(R.id.textViewSearch);
        final RelativeLayout searchLayout = findViewById(R.id.searchLayout);
        final ScalingLayout scalingLayout = findViewById(R.id.scalingLayout);

        scalingLayout.setListener(new ScalingLayoutListener() {
            @Override
            public void onCollapsed() {
                textViewSearch.animate().alpha(1).setDuration(150).start();
                searchLayout.animate().alpha(0).setDuration(150)
                        .withStartAction(() -> textViewSearch.setVisibility(View.VISIBLE))
                        .withEndAction(() -> searchLayout.setVisibility(View.INVISIBLE))
                        .start();
            }

            @Override
            public void onExpanded() {
                textViewSearch.animate().alpha(0).setDuration(200).start();
                searchLayout.animate().alpha(1).setDuration(200)
                        .withStartAction(() -> searchLayout.setVisibility(View.VISIBLE))
                        .withEndAction(() -> textViewSearch.setVisibility(View.INVISIBLE))
                        .start();
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
    }
}

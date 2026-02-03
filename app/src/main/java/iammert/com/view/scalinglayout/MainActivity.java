package iammert.com.view.scalinglayout;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemBars.apply(this);
        setContentView(R.layout.activity_main);

        findViewById(R.id.demo1).setOnClickListener(view ->
                startActivity(new Intent(this, CollapsingDemoActivity.class)));

        findViewById(R.id.demo2).setOnClickListener(view ->
                startActivity(new Intent(this, FABDemo.class)));

        findViewById(R.id.demo3).setOnClickListener(view ->
                startActivity(new Intent(this, SearchBarDemoActivity.class)));
    }
}

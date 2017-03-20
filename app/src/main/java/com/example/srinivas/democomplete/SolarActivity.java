package com.example.srinivas.democomplete;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SolarActivity extends AppCompatActivity {

    private TextView obtainedareatv;
    private Double area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            area = bundle.getDouble("computedarea");

        }

        obtainedareatv =(TextView)findViewById(R.id.getareatextview);

        obtainedareatv.setText(area.toString());
    }
}

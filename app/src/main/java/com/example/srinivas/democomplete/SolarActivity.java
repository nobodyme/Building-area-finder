package com.example.srinivas.democomplete;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.SuperscriptSpan;
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

        obtainedareatv.setText(area.toString()+" ");


        SpannableStringBuilder cs = new SpannableStringBuilder("ft2");
        cs.setSpan(new SuperscriptSpan(), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        cs.setSpan(new RelativeSizeSpan(0.75f), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        obtainedareatv.append(cs);
    }
}

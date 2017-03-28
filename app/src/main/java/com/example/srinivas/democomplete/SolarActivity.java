package com.example.srinivas.democomplete;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.SuperscriptSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

public class SolarActivity extends AppCompatActivity {

    private TextView obtainedareatv;
    private TextView defaulttv;
    private Double area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            area = bundle.getDouble("computedarea");

        }

        obtainedareatv = (TextView) findViewById(R.id.getareatextview);

        obtainedareatv.setText(area.toString() + " ");


        SpannableStringBuilder cs = new SpannableStringBuilder("ft2");
        cs.setSpan(new SuperscriptSpan(), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        cs.setSpan(new RelativeSizeSpan(0.75f), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        obtainedareatv.append(cs);

        defaulttv = (TextView) findViewById(R.id.defaulttextview);

        defaulttv.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_solar, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.solarSettings:

                startActivity(new Intent(SolarActivity.this,Settings.class));

                return true;


        }
        return super.onOptionsItemSelected(item);
    }
}

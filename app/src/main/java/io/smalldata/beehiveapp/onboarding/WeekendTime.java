package io.smalldata.beehiveapp.onboarding;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.main.AppInfo;
import io.smalldata.beehiveapp.main.MainActivity;

public class WeekendTime extends AppCompatActivity {


    private Spinner spinner;
    private static final String[]paths = {"item 1", "item 2", "item 3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekend_time);

        setTitle("Step 2 of 3");
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ImageButton btnBack = (ImageButton) findViewById(R.id.img_btn_back_weekend_screen);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), WeekdayTime.class));
            }
        });

        Button btnNext = (Button) findViewById(R.id.img_btn_next_weekend_screen);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OnboardingCompleted.class));
            }
        });


        spinner = (Spinner)findViewById(R.id.spinner_weekend_time);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


}

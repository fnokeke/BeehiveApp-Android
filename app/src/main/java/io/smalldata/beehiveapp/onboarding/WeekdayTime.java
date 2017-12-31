package io.smalldata.beehiveapp.onboarding;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.login.LoginActivity;

public class WeekdayTime extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekday_time);

        setTitle("Step 1 of 3");
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button btnNext = (Button) findViewById(R.id.img_btn_next_weekday_screen);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), WeekendTime.class));
            }
        });

    }
}

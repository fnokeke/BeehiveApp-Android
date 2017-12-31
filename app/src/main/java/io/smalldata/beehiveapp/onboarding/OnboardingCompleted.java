package io.smalldata.beehiveapp.onboarding;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toolbar;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.main.MainActivity;

public class OnboardingCompleted extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_completed);

        setTitle("Step 3 of 3");
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ImageButton btnBack = (ImageButton) findViewById(R.id.img_btn_back_congrats_screen);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), WeekendTime.class));
            }
        });

        Button btnNext = (Button) findViewById(R.id.img_btn_close_congrats);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}

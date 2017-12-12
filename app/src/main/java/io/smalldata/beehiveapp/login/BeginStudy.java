package io.smalldata.beehiveapp.login;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import io.smalldata.beehiveapp.R;

public class BeginStudy extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin_study);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String username = extras.getString("username");
            String title = "Welcome " + username;
            TextView tvBeginStudyTitle = (TextView) findViewById(R.id.tv_begin_study_title);
            tvBeginStudyTitle.setText(title);
        }
    }
}

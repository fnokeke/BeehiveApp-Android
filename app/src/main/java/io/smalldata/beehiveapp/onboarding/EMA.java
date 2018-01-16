package io.smalldata.beehiveapp.onboarding;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.main.AppInfo;
import io.smalldata.beehiveapp.utils.Store;

public class EMA extends AppCompatActivity {
    private Context mContext;
    private EditText etEmaContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        prepareEMAContent();
        activateSaveButton();
        setContentView(R.layout.activity_ema);
    }

    private void prepareEMAContent() {
        Intent intent = getIntent();
        TextView tvEmaTitle = (TextView) findViewById(R.id.tv_ema_title);
        tvEmaTitle.setText(intent.getStringExtra("title"));
        etEmaContent = (EditText) findViewById(R.id.et_ema_content);
    }

    private void activateSaveButton() {
        Button btnSave = (Button) findViewById(R.id.btn_ema_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValidEntry()) {
                    Toast.makeText(mContext, "Response cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveEMAResponse();
                startActivity(new Intent(mContext, AppInfo.class));
            }
        });
    }

    private void saveEMAResponse() {
        String response = etEmaContent.getText().toString();
        Store.setString(mContext, Constants.EMA_RESPONSE, response);
    }

    private boolean isValidEntry() {
        return !etEmaContent.getText().toString().equals("");
    }
}

package com.example.carshering;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carshering.utils.NetworkUtils;

public class RegisterSuccessActivity extends AppCompatActivity {

    private Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!NetworkUtils.isNetworkAvailable(this)) {
            startActivity(new Intent(this, NoConnectionActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_register_success);

        btnContinue = findViewById(R.id.btnFinish);

        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

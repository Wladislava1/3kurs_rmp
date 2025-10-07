package com.example.carshering.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carshering.MainActivity;
import com.example.carshering.ui.no_connection.NoConnectionActivity;
import com.example.carshering.R;
import com.example.carshering.utils.NetworkUtils;

public class RegisterSuccessActivity extends AppCompatActivity {

    private Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!NetworkUtils.isNetworkAvailable(this)) {
            startActivity(new Intent(this, NoConnectionActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_register_success);

        btnFinish = findViewById(R.id.btnFinish);

        btnFinish.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

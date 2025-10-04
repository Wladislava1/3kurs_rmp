package com.example.carshering;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carshering.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!NetworkUtils.isNetworkAvailable(this)) {
            startActivity(new Intent(this, NoConnectionActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
    }
}
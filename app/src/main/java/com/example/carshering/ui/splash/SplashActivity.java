package com.example.carshering.ui.splash;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carshering.MainActivity;
import com.example.carshering.ui.no_connection.NoConnectionActivity;
import com.example.carshering.databinding.ActivitySplashBinding;
import com.example.carshering.ui.onboarding.OnboardingActivity;
import com.example.carshering.utils.NetworkUtils;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Handler().postDelayed(() -> {
            boolean firstLaunch = true; // впервые открыл
            boolean hasToken = false;

            if (!NetworkUtils.isNetworkAvailable(this)) {
                startActivity(new Intent(this, NoConnectionActivity.class));
            } else if (firstLaunch || !hasToken) {
                startActivity(new Intent(this, OnboardingActivity.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
        }, 2500);
    }
}


package com.example.carshering.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carshering.MainActivity;
import com.example.carshering.ui.no_connection.NoConnectionActivity;
import com.example.carshering.databinding.ActivitySplashBinding;
import com.example.carshering.ui.onboarding.OnboardingActivity;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Handler().postDelayed(() -> {
            boolean firstLaunch = true;
            boolean hasToken = false;

            if (!isNetworkAvailable()) {
                startActivity(new Intent(this, NoConnectionActivity.class));
            } else if (firstLaunch || !hasToken) {
                startActivity(new Intent(this, OnboardingActivity.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
        }, 2500);
    }

    private boolean isNetworkAvailable() {
        return true;
    }
}


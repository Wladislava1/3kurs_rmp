package com.example.carshering;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carshering.databinding.ActivityNoConnectionBinding;

public class NoConnectionActivity extends AppCompatActivity {

    private ActivityNoConnectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoConnectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.retryButton.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                binding.errorText.setText("Все еще нет сети. Попробуйте позже.");
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo active = cm.getActiveNetworkInfo();
        return active != null && active.isConnected();
    }
}

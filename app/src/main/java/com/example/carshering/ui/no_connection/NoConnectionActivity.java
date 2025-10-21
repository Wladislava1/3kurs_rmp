package com.example.carshering.ui.no_connection;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carshering.MainActivity;
import com.example.carshering.R;
import com.example.carshering.databinding.ActivityNoConnectionBinding;
import com.example.carshering.ui.login.LoginActivity;
import com.example.carshering.utils.SessionManager;

public class NoConnectionActivity extends AppCompatActivity {

    private ActivityNoConnectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoConnectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.retryButton.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                if (SessionManager.isLoggedIn(this)) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra(getString(R.string.openHomeFragment), true);
                    startActivity(intent);
                    finish();
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                }
            } else {
                binding.errorText.setText(R.string.no_retry_one);
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo active = cm.getActiveNetworkInfo();
        return active != null && active.isConnected();
    }
}

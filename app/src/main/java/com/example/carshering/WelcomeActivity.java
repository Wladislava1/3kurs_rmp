package com.example.carshering;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carshering.utils.NetworkUtils;

import com.example.carshering.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {

    private ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!NetworkUtils.isNetworkAvailable(this)) {
            startActivity(new Intent(this, NoConnectionActivity.class));
            finish();
            return;
        }

        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLoginScreen.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );

        binding.btnRegisterScreen.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterStep1Activity.class))
        );
    }
}

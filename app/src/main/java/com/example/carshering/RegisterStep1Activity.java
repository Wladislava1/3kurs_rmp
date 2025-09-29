package com.example.carshering;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carshering.databinding.ActivityRegisterStep1Binding;

public class RegisterStep1Activity extends AppCompatActivity {

    private ActivityRegisterStep1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterStep1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnNextStep1.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterStep2Activity.class));
        });

        binding.ivBack.setOnClickListener(v -> finish());
    }
}

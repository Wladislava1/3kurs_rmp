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

        binding.etPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getRawX() >= (binding.etPassword.getRight() - binding.etPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                if (binding.etPassword.getInputType() == 129) { // скрытый пароль
                    binding.etPassword.setInputType(145); // показать
                } else {
                    binding.etPassword.setInputType(129); // скрыть
                }
                binding.etPassword.setSelection(binding.etPassword.getText().length());
                return true;
            }
            return false;
        });

        binding.etRepeatPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getRawX() >= (binding.etRepeatPassword.getRight() - binding.etRepeatPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                if (binding.etRepeatPassword.getInputType() == 129) {
                    binding.etRepeatPassword.setInputType(145);
                } else {
                    binding.etRepeatPassword.setInputType(129);
                }
                binding.etRepeatPassword.setSelection(binding.etRepeatPassword.getText().length());
                return true;
            }
            return false;
        });
    }
}

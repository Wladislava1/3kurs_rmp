package com.example.carshering;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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
            // Получаем email и пароль из полей ввода
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, введите email и пароль", Toast.LENGTH_SHORT).show();
                return;
            }

            // Создаём Intent и передаём данные на Step2
            Intent intent = new Intent(this, RegisterStep2Activity.class);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            startActivity(intent);
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

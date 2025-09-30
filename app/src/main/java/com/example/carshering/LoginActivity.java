package com.example.carshering;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carshering.databinding.ActivityLoginBinding;
import com.example.carshering.model.User;
import com.example.carshering.api.ApiClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setEnabled(false); // по умолчанию неактивна

        // Проверка полей для активации кнопки "Войти"
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = binding.etLoginEmail.getText().toString().trim();
                String pass = binding.etLoginPassword.getText().toString().trim();
                binding.btnLogin.setEnabled(!email.isEmpty() && !pass.isEmpty());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.etLoginEmail.addTextChangedListener(watcher);
        binding.etLoginPassword.addTextChangedListener(watcher);

        // Показ/скрытие пароля
        binding.etLoginPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_eye,0);
        binding.etLoginPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if(event.getRawX() >= (binding.etLoginPassword.getRight() - binding.etLoginPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                if (binding.etLoginPassword.getInputType() == (129)) { // текст пароля
                    binding.etLoginPassword.setInputType(145); // показать
                } else {
                    binding.etLoginPassword.setInputType(129); // скрыть
                }
                binding.etLoginPassword.setSelection(binding.etLoginPassword.getText().length());
                return true;
            }
            return false;
        });

        // Кнопка "Войти"
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etLoginEmail.getText().toString().trim();
            String password = binding.etLoginPassword.getText().toString().trim();

            User user = new User(email, password);

            ApiClient.getApiService().login(user).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    if(response.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Авторизация успешна", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Неверная почта или пароль", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                }
            });
        });


        // Кнопка "Войти через Google"
        binding.btnGoogleLogin.setOnClickListener(v -> {
            // TODO: Google OAuth
            Toast.makeText(this, "Google авторизация пока заглушка", Toast.LENGTH_SHORT).show();
        });

        // Кнопка "Зарегистрироваться"
        binding.btnGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterStep1Activity.class))
        );

        // Забыли пароль
        binding.tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(this, "Восстановление пароля пока заглушка", Toast.LENGTH_SHORT).show()
        );
    }
}

package com.example.carshering;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.InputType;
import android.util.Patterns;
import android.view.MotionEvent;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carshering.databinding.ActivityRegisterStep1Binding;
import com.example.carshering.utils.NetworkUtils;

public class RegisterStep1Activity extends AppCompatActivity {

    private ActivityRegisterStep1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!NetworkUtils.isNetworkAvailable(this)) {
            startActivity(new Intent(this, NoConnectionActivity.class));
            finish();
            return;
        }

        binding = ActivityRegisterStep1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Изначально кнопка заблокирована
        binding.btnNextStep1.setEnabled(false);
        binding.btnNextStep1.setAlpha(0.5f);

        // Навешиваем обработчики для валидации
        binding.etEmail.addTextChangedListener(simpleWatcher);
        binding.etPassword.addTextChangedListener(simpleWatcher);
        binding.etRepeatPassword.addTextChangedListener(simpleWatcher);
        binding.cbAgreement.setOnCheckedChangeListener((buttonView, isChecked) -> validateForm());

        // Кнопка "Далее"
        binding.btnNextStep1.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            Intent intent = new Intent(this, RegisterStep2Activity.class);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            startActivity(intent);
        });

        // Стрелка "Назад"
        binding.ivBack.setOnClickListener(v -> finish());

        // Переключение показа/скрытия пароля
        binding.etPassword.setOnTouchListener((v, event) -> togglePasswordVisibility(binding.etPassword, event));
        binding.etRepeatPassword.setOnTouchListener((v, event) -> togglePasswordVisibility(binding.etRepeatPassword, event));
    }

    // Проверка условий для активации кнопки и отображения ошибок
    private void validateForm() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String repeatPassword = binding.etRepeatPassword.getText().toString().trim();
        boolean isChecked = binding.cbAgreement.isChecked();

        boolean isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches();
        boolean isPasswordValid = !password.isEmpty() && password.equals(repeatPassword);
        boolean isAgreementValid = isChecked;

        // Проверка email
        if (!email.isEmpty() && !isEmailValid) {
            binding.etEmail.setError("Введите корректный адрес электронной почты.");
        } else {
            binding.etEmail.setError(null);
        }

        // Проверка совпадения паролей
        if (!password.isEmpty() && !repeatPassword.isEmpty() && !password.equals(repeatPassword)) {
            binding.etRepeatPassword.setError("Пароли не совпадают.");
        } else {
            binding.etRepeatPassword.setError(null);
        }

        // Проверка чекбокса
        if (!isAgreementValid) {
            binding.cbAgreement.setError("Необходимо согласиться с условиями обслуживания и политикой конфиденциальности.");
        } else {
            binding.cbAgreement.setError(null);
        }

        // Кнопка активна только если все условия выполнены
        boolean isValid = isEmailValid && isPasswordValid && isAgreementValid;
        binding.btnNextStep1.setEnabled(isValid);
        binding.btnNextStep1.setAlpha(isValid ? 1f : 0.5f);
    }

    // Слушатель изменений текста
    private final TextWatcher simpleWatcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { validateForm(); }
        @Override public void afterTextChanged(Editable s) {}
    };

    // Метод для переключения видимости пароля
    private boolean togglePasswordVisibility(android.widget.EditText editText, MotionEvent event) {
        final int DRAWABLE_END = 2;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                editText.setSelection(editText.getText().length());
                return true;
            }
        }
        return false;
    }
}

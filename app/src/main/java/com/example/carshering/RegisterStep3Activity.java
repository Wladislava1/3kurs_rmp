package com.example.carshering;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.carshering.model.User;
import com.example.carshering.api.ApiClient;

import okhttp3.ResponseBody;
import retrofit2.Call;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterStep3Activity extends AppCompatActivity {

    private EditText etLicenseNumber, etIssueDate;
    private ImageView btnUploadLicense, btnUploadPassport, btnBack;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step3);

        etLicenseNumber = findViewById(R.id.etDriverLicenseNumber);
        etIssueDate = findViewById(R.id.etDriverLicenseDate);

        btnUploadLicense = findViewById(R.id.ivUploadLicense);
        btnUploadPassport = findViewById(R.id.ivUploadPassport);;
        btnNext = findViewById(R.id.btnNextStep3);
        btnBack = findViewById(R.id.ivBack);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        String middleName = intent.getStringExtra("middleName");
        String birthDate = intent.getStringExtra("birthDate");
        int genderId = intent.getIntExtra("genderId", -1);
        String gender = (genderId == R.id.rbMale) ? "Male" : "Female";

        btnUploadLicense.setOnClickListener(v ->
                Toast.makeText(this, "Загрузка фото ВУ (пока заглушка)", Toast.LENGTH_SHORT).show());

        btnUploadPassport.setOnClickListener(v ->
                Toast.makeText(this, "Загрузка фото паспорта (пока заглушка)", Toast.LENGTH_SHORT).show());

        btnNext.setOnClickListener(v -> {
            String license = etLicenseNumber.getText().toString().trim();
            String issueDate = etIssueDate.getText().toString().trim();

            if (license.isEmpty() || issueDate.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните все обязательные поля", Toast.LENGTH_SHORT).show();
                return;
            }

            // Собираем данные из предыдущих шагов (можно через Intent extras)
            User user = new User(email, password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setMiddleName(middleName);
            user.setBirthDate(birthDate);
            user.setGender(gender);
            user.setDriverLicenseNumber(license);
            user.setDriverLicenseIssueDate(issueDate);

            ApiClient.getApiService().register(user).enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    if(response.isSuccessful()) {
                        Toast.makeText(RegisterStep3Activity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterStep3Activity.this, RegisterSuccessActivity.class));
                    } else {
                        Toast.makeText(RegisterStep3Activity.this, "Пользователь уже существует", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace(); // выводим полную ошибку в Logcat
                    Toast.makeText(RegisterStep3Activity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });


        btnBack.setOnClickListener(v -> finish());
    }
}

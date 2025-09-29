package com.example.carshering;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterStep3Activity extends AppCompatActivity {

    private EditText etLicenseNumber, etIssueDate;
    private Button btnUploadLicense, btnUploadPassport, btnNext, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step3);

        etLicenseNumber = findViewById(R.id.etLicenseNumber);
        etIssueDate = findViewById(R.id.etIssueDate);

        btnUploadLicense = findViewById(R.id.btnUploadLicense);
        btnUploadPassport = findViewById(R.id.btnUploadPassport);
        btnNext = findViewById(R.id.btnNextStep3);
        btnBack = findViewById(R.id.btnBackStep3);

        btnUploadLicense.setOnClickListener(v ->
                Toast.makeText(this, "Загрузка фото ВУ (пока заглушка)", Toast.LENGTH_SHORT).show());

        btnUploadPassport.setOnClickListener(v ->
                Toast.makeText(this, "Загрузка фото паспорта (пока заглушка)", Toast.LENGTH_SHORT).show());

        btnNext.setOnClickListener(v -> {
            String license = etLicenseNumber.getText().toString().trim();
            String issueDate = etIssueDate.getText().toString().trim();

            if (license.isEmpty() || issueDate.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните все обязательные поля", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, RegisterSuccessActivity.class);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}

package com.example.carshering;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

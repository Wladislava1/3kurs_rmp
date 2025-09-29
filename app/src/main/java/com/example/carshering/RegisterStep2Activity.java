package com.example.carshering;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterStep2Activity extends AppCompatActivity {

    private EditText etLastName, etFirstName, etMiddleName, etBirthDate;
    private RadioGroup rgGender;
    private Button btnNext;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step2);

        etLastName = findViewById(R.id.etSurname);
        etFirstName = findViewById(R.id.etName);
        etMiddleName = findViewById(R.id.etPatronymic);
        etBirthDate = findViewById(R.id.etBirthDate);
        rgGender = findViewById(R.id.rgGender);

        btnNext = findViewById(R.id.btnNextStep2);
        btnBack = findViewById(R.id.ivBack);

        btnNext.setOnClickListener(v -> {
            String last = etLastName.getText().toString().trim();
            String first = etFirstName.getText().toString().trim();
            String birth = etBirthDate.getText().toString().trim();
            int genderId = rgGender.getCheckedRadioButtonId();

            if (last.isEmpty() || first.isEmpty() || birth.isEmpty() || genderId == -1) {
                Toast.makeText(this, "Пожалуйста, заполните все обязательные поля", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, RegisterStep3Activity.class);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}

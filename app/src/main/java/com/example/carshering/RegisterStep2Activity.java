package com.example.carshering;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carshering.utils.NetworkUtils;

import java.util.Calendar;

public class RegisterStep2Activity extends AppCompatActivity {

    private EditText etLastName, etFirstName, etMiddleName, etBirthDate;
    private RadioGroup rgGender;
    private Button btnNext;
    private ImageView btnBack;
    private Calendar cal = Calendar.getInstance();

    private String current = "";
    private String ddmmyyyy = getString(R.string.ddmmyyyy);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!NetworkUtils.isNetworkAvailable(this)) {
            startActivity(new Intent(this, NoConnectionActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_register_step2);

        etLastName = findViewById(R.id.etSurname);
        etFirstName = findViewById(R.id.etName);
        etMiddleName = findViewById(R.id.etPatronymic);
        etBirthDate = findViewById(R.id.etBirthDate);
        rgGender = findViewById(R.id.rgGender);

        btnNext = findViewById(R.id.btnNextStep2);
        btnBack = findViewById(R.id.ivBack);

        btnNext.setEnabled(false);
        btnNext.setAlpha(0.5f);

        String email = getIntent().getStringExtra(getString(R.string.email));
        String password = getIntent().getStringExtra(getString(R.string.password));

        etBirthDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d]", "");
                    String cleanC = current.replaceAll("[^\\d]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) sel++;
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = Math.max(1, Math.min(mon, 12));
                        cal.set(Calendar.MONTH, mon - 1);
                        year = Math.max(1900, Math.min(year, 2007));
                        cal.set(Calendar.YEAR, year);

                        day = Math.min(day, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                        clean = String.format(getString(R.string.mask), day, mon, year);
                    }

                    clean = String.format(getString(R.string.format),
                            clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    current = clean;
                    etBirthDate.setText(current);
                    etBirthDate.setSelection(Math.min(sel, current.length()));

                    validateForm();
                }
            }
        });

        etLastName.addTextChangedListener(formWatcher);
        etFirstName.addTextChangedListener(formWatcher);
        etMiddleName.addTextChangedListener(formWatcher);
        rgGender.setOnCheckedChangeListener((group, checkedId) -> validateForm());

        btnNext.setOnClickListener(v -> {
            String last = etLastName.getText().toString().trim();
            String first = etFirstName.getText().toString().trim();
            String middle = etMiddleName.getText().toString().trim();
            String birth = etBirthDate.getText().toString().trim();
            int genderId = rgGender.getCheckedRadioButtonId();

            if (last.isEmpty() || first.isEmpty() || birth.isEmpty() || genderId == -1) {
                if (last.isEmpty()) etLastName.setError(getString(R.string.all_input));
                if (first.isEmpty()) etFirstName.setError(getString(R.string.all_input));
                if (birth.isEmpty()) etBirthDate.setError(getString(R.string.all_input));
                return;
            }

            if (!isValidDate(birth)) {
                etBirthDate.setError(getString(R.string.correct_birthDay));
                return;
            }

            Intent intent = new Intent(this, RegisterStep3Activity.class);
            intent.putExtra(getString(R.string.email), email);
            intent.putExtra(getString(R.string.password), password);
            intent.putExtra(getString(R.string.firstname), first);
            intent.putExtra(getString(R.string.lastname), last);
            intent.putExtra(getString(R.string.middlename), middle);
            intent.putExtra(getString(R.string.birthDate), birth);
            intent.putExtra(getString(R.string.genderId), genderId);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private boolean isValidDate(String date) {
        if (date == null || !date.matches("\\d{2}/\\d{2}/\\d{4}")) return false;
        String[] parts = date.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        if (month < 1 || month > 12) return false;
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.YEAR, year);

        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return day >= 1 && day <= maxDay;
    }

    private void validateForm() {
        String last = etLastName.getText().toString().trim();
        String first = etFirstName.getText().toString().trim();
        String birth = etBirthDate.getText().toString().trim();
        int genderId = rgGender.getCheckedRadioButtonId();

        boolean isValid = !last.isEmpty() && !first.isEmpty() && !birth.isEmpty()
                && isValidDate(birth) && genderId != -1;

        if (last.isEmpty()) etLastName.setError(getString(R.string.all_input));
        else etLastName.setError(null);
        if (first.isEmpty()) etFirstName.setError(getString(R.string.all_input));
        else etFirstName.setError(null);
        if (birth.isEmpty()) etBirthDate.setError(getString(R.string.all_input));
        else if (!isValidDate(birth)) etBirthDate.setError(getString(R.string.all_input));
        else etBirthDate.setError(null);

        btnNext.setEnabled(isValid);
        btnNext.setAlpha(isValid ? 1f : 0.5f);
    }

    private final TextWatcher formWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            validateForm();
        }
    };
}

package com.example.carshering.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carshering.ui.no_connection.NoConnectionActivity;
import com.example.carshering.R;
import com.example.carshering.utils.NetworkUtils;
import com.example.carshering.utils.DateUtils;

import java.util.Calendar;

public class RegisterStep2Activity extends AppCompatActivity {

    private EditText etLastName, etFirstName, etMiddleName, etBirthDate;
    private RadioGroup rgGender;
    private Button btnNextStep2;
    private ImageView btnBackStep2;

    private final Calendar calendar = Calendar.getInstance();

    private String currentBirthInput = "";
    private String birthDateMask;
    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = 2007;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!NetworkUtils.isNetworkAvailable(this)) {
            startActivity(new Intent(this, NoConnectionActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_register_step2);
        birthDateMask = getString(R.string.ddmmyyyy);

        etLastName = findViewById(R.id.etSurname);
        etFirstName = findViewById(R.id.etName);
        etMiddleName = findViewById(R.id.etPatronymic);
        etBirthDate = findViewById(R.id.etBirthDate);
        rgGender = findViewById(R.id.rgGender);

        btnBackStep2 = findViewById(R.id.ivBack);

        btnNextStep2 = findViewById(R.id.btnNextStep2);
        btnNextStep2.setEnabled(false);
        btnNextStep2.setAlpha(0.5f);

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
                if (!s.toString().equals(currentBirthInput)) {
                    String clean = s.toString().replaceAll("[^\\d]", "");
                    String cleanC = currentBirthInput.replaceAll("[^\\d]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) sel++;
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + birthDateMask.substring(clean.length());
                    } else {
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = Math.max(1, Math.min(mon, 12));
                        calendar.set(Calendar.MONTH, mon - 1);
                        year = Math.max(MIN_YEAR, Math.min(year, MAX_YEAR));
                        calendar.set(Calendar.YEAR, year);

                        day = Math.min(day, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                        clean = String.format(getString(R.string.mask), day, mon, year);
                    }

                    clean = String.format(getString(R.string.format),
                            clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    currentBirthInput = clean;
                    etBirthDate.setText(currentBirthInput);
                    etBirthDate.setSelection(Math.min(sel, currentBirthInput.length()));

                    validateForm();
                }
            }
        });

        etLastName.addTextChangedListener(formWatcher);
        etFirstName.addTextChangedListener(formWatcher);
        etMiddleName.addTextChangedListener(formWatcher);
        rgGender.setOnCheckedChangeListener((group, checkedId) -> validateForm());

        btnNextStep2.setOnClickListener(v -> {
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

            if (!DateUtils.isValidDate(birth)) {
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

        btnBackStep2.setOnClickListener(v -> finish());
    }
    private void setErrorIfEmpty(EditText editText, String text) {
        if (text.isEmpty()) {
            editText.setError(getString(R.string.all_input));
        } else {
            editText.setError(null);
        }
    }

    private void validateForm() {
        String lastName = etLastName.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String birthDate = etBirthDate.getText().toString().trim();
        int genderId = rgGender.getCheckedRadioButtonId();

        boolean isValid = !lastName.isEmpty() && !firstName.isEmpty()
                && !birthDate.isEmpty() && DateUtils.isValidDate(birthDate)
                && genderId != -1;

        setErrorIfEmpty(etLastName, lastName);
        setErrorIfEmpty(etFirstName, firstName);
        setErrorIfEmpty(etBirthDate, birthDate);

        btnNextStep2.setEnabled(isValid);
        btnNextStep2.setAlpha(isValid ? 1f : 0.5f);
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

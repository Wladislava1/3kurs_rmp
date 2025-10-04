package com.example.carshering;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.view.View;

import com.example.carshering.model.User;
import com.example.carshering.api.ApiClient;
import com.example.carshering.utils.NetworkUtils;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class RegisterStep3Activity extends AppCompatActivity {

    private EditText etLicenseNumber, etIssueDate;
    private ImageView btnUploadLicense, btnUploadPassport, btnBack;
    private Button btnNext;
    private Calendar cal = Calendar.getInstance();
    private String currentDate = "";
    private final String ddmmyyyy = "DDMMYYYY";
    private Drawable licensePlaceholder, passportPlaceholder;

    private boolean isLicensePhotoUploaded = false;

    private boolean isPassportPhotoUploaded = false;
    private ImageView ivProfilePhoto, ivAddPhotoIcon;

    private static final int PICK_LICENSE_PHOTO = 101;
    private static final int PICK_PASSPORT_PHOTO = 102;
    private static final int PICK_PROFILE_PHOTO = 201;
    private static final int CAPTURE_PROFILE_PHOTO = 202;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!NetworkUtils.isNetworkAvailable(this)) {
            startActivity(new Intent(this, NoConnectionActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_register_step3);

        btnUploadLicense = findViewById(R.id.ivUploadLicense);
        btnUploadPassport = findViewById(R.id.ivUploadPassport);
        // Сохраняем плейсхолдеры
        licensePlaceholder = btnUploadLicense.getDrawable();
        passportPlaceholder = btnUploadPassport.getDrawable();
        // Поля ввода
        etLicenseNumber = findViewById(R.id.etDriverLicenseNumber);
        etIssueDate = findViewById(R.id.etDriverLicenseDate);

        // Кнопки загрузки документов
        btnUploadLicense = findViewById(R.id.ivUploadLicense);
        btnUploadPassport = findViewById(R.id.ivUploadPassport);
        btnBack = findViewById(R.id.ivBack);
        btnNext = findViewById(R.id.btnNextStep3);

        // Фото профиля
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        ivAddPhotoIcon = findViewById(R.id.ivAddPhotoIcon);

        // Изначально кнопка "Далее" заблокирована
        btnNext.setEnabled(false);
        btnNext.setAlpha(0.5f);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        String middleName = intent.getStringExtra("middleName");
        String birthDate = intent.getStringExtra("birthDate");
        int genderId = intent.getIntExtra("genderId", -1);
        String gender = (genderId == R.id.rbMale) ? "Male" : "Female";

        // Маска даты выдачи ВУ
        etIssueDate.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(currentDate)) {
                    String clean = s.toString().replaceAll("[^\\d]", "");
                    String cleanC = currentDate.replaceAll("[^\\d]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) sel++;

                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        int day = Integer.parseInt(clean.substring(0,2));
                        int month = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        month = Math.max(1, Math.min(month, 12));
                        cal.set(Calendar.MONTH, month - 1);
                        year = Math.max(1900, Math.min(year, 2025));
                        cal.set(Calendar.YEAR, year);

                        day = Math.min(day, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                        clean = String.format("%02d%02d%04d", day, month, year);
                    }

                    clean = String.format("%s/%s/%s",
                            clean.substring(0,2),
                            clean.substring(2,4),
                            clean.substring(4,8));

                    sel = Math.max(sel,0);
                    currentDate = clean;
                    etIssueDate.setText(currentDate);
                    etIssueDate.setSelection(Math.min(sel, currentDate.length()));

                    validateForm();
                }
            }
        });

        // Слушатели для проверки формы
        etLicenseNumber.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { validateForm(); }
            @Override public void afterTextChanged(Editable s) {}
        });
        etLicenseNumber.setFilters(new android.text.InputFilter[]{
                (source, start, end, dest, dstart, dend) -> {
                    for (int i = start; i < end; i++) {
                        if (!Character.isDigit(source.charAt(i))) {
                            return "";
                        }
                    }
                    return null;
                }
        });
        etIssueDate.addTextChangedListener(textWatcher);

        // Загрузка фото ВУ
        btnUploadLicense.setOnClickListener(v -> openGallery(PICK_LICENSE_PHOTO));

        // Загрузка фото паспорта
        btnUploadPassport.setOnClickListener(v -> openGallery(PICK_PASSPORT_PHOTO));

        // Загрузка аватарки
        ivAddPhotoIcon.setOnClickListener(v -> showPhotoOptions());

        // Кнопка Далее
        btnNext.setOnClickListener(v -> {
            String license = etLicenseNumber.getText().toString().trim();
            String issueDate = etIssueDate.getText().toString().trim();

            boolean isLicensePhotoUploaded = btnUploadLicense.getDrawable() != licensePlaceholder;
            boolean isPassportPhotoUploaded = btnUploadPassport.getDrawable() != passportPlaceholder;

            if (!isLicensePhotoUploaded || !isPassportPhotoUploaded) {
                Toast.makeText(this, "Пожалуйста, загрузите фото паспорта и водительского удостоверения", Toast.LENGTH_LONG).show();
                return;
            }

            if (license.isEmpty() || !isValidDate(issueDate)) {
                Toast.makeText(this, "Пожалуйста, заполните все обязательные поля корректно", Toast.LENGTH_SHORT).show();
                return;
            }

            // Создание объекта пользователя
            User user = new User(email, password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setMiddleName(middleName);
            user.setBirthDate(birthDate);
            user.setGender(gender);
            user.setDriverLicenseNumber(license);
            user.setDriverLicenseIssueDate(issueDate);

            // Отправка на сервер
            ApiClient.getApiService().register(user).enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterStep3Activity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterStep3Activity.this, RegisterSuccessActivity.class));
                    } else {
                        Toast.makeText(RegisterStep3Activity.this, "Пользователь уже существует", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(RegisterStep3Activity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        btnBack.setOnClickListener(v -> finish());
    }

    // Выбор фото профиля: галерея или камера
    private void showPhotoOptions() {
        String[] options = {"Выбрать из галереи", "Сделать фото"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить фото профиля")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) openGallery(PICK_PROFILE_PHOTO);
                    else openCamera();
                })
                .show();
    }

    private void openGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_PROFILE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == PICK_LICENSE_PHOTO) {
                Uri selectedImage = data.getData();
                btnUploadLicense.setImageURI(selectedImage);
                isLicensePhotoUploaded = true;
                validateForm();
            } else if (requestCode == PICK_PASSPORT_PHOTO) {
                Uri selectedImage = data.getData();
                btnUploadPassport.setImageURI(selectedImage);
                isPassportPhotoUploaded = true;
                validateForm();
            } else if (requestCode == PICK_PROFILE_PHOTO) {
                Uri selectedImage = data.getData();
                try {
                    ivProfilePhoto.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage));
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CAPTURE_PROFILE_PHOTO) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                ivProfilePhoto.setImageBitmap(photo);
            }
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { validateForm(); }
        @Override public void afterTextChanged(Editable s) {}
    };

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
        String license = etLicenseNumber.getText().toString().trim();
        String date = etIssueDate.getText().toString().trim();

        TextView tvLicenseError = findViewById(R.id.tvLicenseError);
        TextView tvPassportError = findViewById(R.id.tvPassportError);

        boolean isLicenseFilled = !license.isEmpty();
        boolean isDateFilled = !date.isEmpty();
        boolean isLicenseValid = license.matches("\\d{10}");
        boolean isDateValid = isValidDate(date);

        // Проверка загруженных фото
        boolean isLicensePhotoUploaded = btnUploadLicense.getDrawable() != licensePlaceholder;
        boolean isPassportPhotoUploaded = btnUploadPassport.getDrawable() != passportPlaceholder;

        boolean allPhotosUploaded = isLicensePhotoUploaded && isPassportPhotoUploaded;
        // Общая валидность формы
        boolean isValid = isLicenseFilled && isDateFilled && isLicenseValid && isDateValid && allPhotosUploaded;
        btnNext.setEnabled(isValid);
        btnNext.setAlpha(isValid ? 1f : 0.5f);

        if (!isLicensePhotoUploaded) {
            tvLicenseError.setText("Пожалуйста, загрузите фото водительского удостоверения");
            tvLicenseError.setVisibility(View.VISIBLE);
        } else {
            tvLicenseError.setVisibility(View.GONE);
        }

        if (!isPassportPhotoUploaded) {
            tvPassportError.setText("Пожалуйста, загрузите фото паспорта");
            tvPassportError.setVisibility(View.VISIBLE);
        } else {
            tvPassportError.setVisibility(View.GONE);
        }
        // Сообщения об ошибках
        if (!isLicenseFilled || !isDateFilled) {
            if (!isLicenseFilled) etLicenseNumber.setError("Пожалуйста, заполните все обязательные поля.");
            if (!isDateFilled) etIssueDate.setError("Пожалуйста, заполните все обязательные поля.");
        } else {
            etLicenseNumber.setError(null);
            etIssueDate.setError(null);
        }

        if (isDateFilled && !isDateValid) {
            etIssueDate.setError("Введите корректную дату выдачи.");
        }

        if (!isLicenseValid && license.length() > 0) {
            etLicenseNumber.setError("Номер должен содержать ровно 10 цифр");
        } else {
            etLicenseNumber.setError(null);
        }
    }
}

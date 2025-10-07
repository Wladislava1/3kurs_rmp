package com.example.carshering.ui.register;

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

import com.example.carshering.ui.no_connection.NoConnectionActivity;
import com.example.carshering.utils.DateUtils;
import com.example.carshering.R;
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

        final String ddmmyyyy = getString(R.string.ddmmyyyy);

        if (!NetworkUtils.isNetworkAvailable(this)) {
            startActivity(new Intent(this, NoConnectionActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_register_step3);

        btnUploadLicense = findViewById(R.id.ivUploadLicense);
        btnUploadPassport = findViewById(R.id.ivUploadPassport);

        licensePlaceholder = btnUploadLicense.getDrawable();
        passportPlaceholder = btnUploadPassport.getDrawable();

        etLicenseNumber = findViewById(R.id.etDriverLicenseNumber);
        etIssueDate = findViewById(R.id.etDriverLicenseDate);

        btnUploadLicense = findViewById(R.id.ivUploadLicense);
        btnUploadPassport = findViewById(R.id.ivUploadPassport);
        btnBack = findViewById(R.id.ivBack);
        btnNext = findViewById(R.id.btnNextStep3);


        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        ivAddPhotoIcon = findViewById(R.id.ivAddPhotoIcon);

        btnNext.setEnabled(false);
        btnNext.setAlpha(0.5f);

        Intent intent = getIntent();
        String email = intent.getStringExtra(getString(R.string.email));
        String password = intent.getStringExtra(getString(R.string.password));
        String firstName = intent.getStringExtra(getString(R.string.firstname));
        String lastName = intent.getStringExtra(getString(R.string.lastname));
        String middleName = intent.getStringExtra(getString(R.string.middlename));
        String birthDate = intent.getStringExtra(getString(R.string.birthDate));
        int genderId = intent.getIntExtra(getString(R.string.genderId), -1);
        String gender =
                (genderId == R.id.rbMale) ? getString(R.string.Male) : getString(R.string.Female);

        etIssueDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start, int before, int count) {
            }

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
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int month = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        month = Math.max(1, Math.min(month, 12));
                        cal.set(Calendar.MONTH, month - 1);
                        year = Math.max(1900, Math.min(year, 2025));
                        cal.set(Calendar.YEAR, year);

                        day = Math.min(day, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                        clean = String.format(getString(R.string.mask), day, month, year);
                    }

                    clean = String.format(getString(R.string.format),
                            clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = Math.max(sel, 0);
                    currentDate = clean;
                    etIssueDate.setText(currentDate);
                    etIssueDate.setSelection(Math.min(sel, currentDate.length()));

                    validateForm();
                }
            }
        });

        etLicenseNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start, int before, int count) {
                validateForm();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
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


        btnUploadLicense.setOnClickListener(v -> openGallery(PICK_LICENSE_PHOTO));

        btnUploadPassport.setOnClickListener(v -> openGallery(PICK_PASSPORT_PHOTO));

        ivAddPhotoIcon.setOnClickListener(v -> showPhotoOptions());

        btnNext.setOnClickListener(v -> {
            String license = etLicenseNumber.getText().toString().trim();
            String issueDate = etIssueDate.getText().toString().trim();

            boolean isLicensePhotoUploaded = btnUploadLicense.getDrawable() != licensePlaceholder;
            boolean isPassportPhotoUploaded = btnUploadPassport.getDrawable() != passportPlaceholder;

            if (!isLicensePhotoUploaded || !isPassportPhotoUploaded) {
                Toast.makeText(this, getString(R.string.upload_photo_passport_licence),
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (license.isEmpty() || !DateUtils.isValidDate(issueDate)) {
                Toast.makeText(this, getString(R.string.all_input),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(email, password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setMiddleName(middleName);
            user.setBirthDate(birthDate);
            user.setGender(gender);
            user.setDriverLicenseNumber(license);
            user.setDriverLicenseIssueDate(issueDate);

            ApiClient.getApiService().register(user).enqueue(
                    new retrofit2.Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call,
                                               retrofit2.Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(RegisterStep3Activity.this,
                                        getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterStep3Activity.this,
                                        RegisterSuccessActivity.class));
                            } else {
                                Toast.makeText(RegisterStep3Activity.this,
                                        getString(R.string.user_alreade_exist), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                            Toast.makeText(RegisterStep3Activity.this,
                                    getString(R.string.error_internet) + t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void showPhotoOptions() {
        String[] options = {getString(R.string.galerry), getString(R.string.make_photo)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_photo_profil))
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
                    Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CAPTURE_PROFILE_PHOTO) {
                Bitmap photo = (Bitmap) data.getExtras().get(getString(R.string.data));
                ivProfilePhoto.setImageBitmap(photo);
            }
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validateForm();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    private void validateForm() {
        String license = etLicenseNumber.getText().toString().trim();
        String date = etIssueDate.getText().toString().trim();

        TextView tvLicenseError = findViewById(R.id.tvLicenseError);
        TextView tvPassportError = findViewById(R.id.tvPassportError);

        boolean isLicenseFilled = !license.isEmpty();
        boolean isDateFilled = !date.isEmpty();
        boolean isLicenseValid = license.matches("\\d{10}");
        boolean isDateValid = DateUtils.isValidDate(date);

        boolean isLicensePhotoUploaded = btnUploadLicense.getDrawable() != licensePlaceholder;
        boolean isPassportPhotoUploaded = btnUploadPassport.getDrawable() != passportPlaceholder;

        boolean allPhotosUploaded = isLicensePhotoUploaded && isPassportPhotoUploaded;
        boolean isValid = isLicenseFilled && isDateFilled && isLicenseValid && isDateValid && allPhotosUploaded;
        btnNext.setEnabled(isValid);
        btnNext.setAlpha(isValid ? 1f : 0.5f);

        if (!isLicensePhotoUploaded) {
            tvLicenseError.setText(getString(R.string.upload_licence_photo));
            tvLicenseError.setVisibility(View.VISIBLE);
        } else {
            tvLicenseError.setVisibility(View.GONE);
        }

        if (!isPassportPhotoUploaded) {
            tvPassportError.setText(getString(R.string.upload_passport_photo));
            tvPassportError.setVisibility(View.VISIBLE);
        } else {
            tvPassportError.setVisibility(View.GONE);
        }

        if (!isLicenseFilled || !isDateFilled) {
            if (!isLicenseFilled) etLicenseNumber.setError(getString(R.string.all_input));
            if (!isDateFilled) etIssueDate.setError(getString(R.string.all_input));
        } else {
            etLicenseNumber.setError(null);
            etIssueDate.setError(null);
        }

        if (isDateFilled && !isDateValid) {
            etIssueDate.setError(getString(R.string.correctly_data_get));
        }

        if (!isLicenseValid && license.length() > 0) {
            etLicenseNumber.setError(getString(R.string.then_correctly));
        } else {
            etLicenseNumber.setError(null);
        }
    }
}

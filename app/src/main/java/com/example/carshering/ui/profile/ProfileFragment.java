package com.example.carshering.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carshering.databinding.FragmentProfileBinding;
import com.example.carshering.model.User;
import com.example.carshering.R;

import com.example.carshering.api.ApiClient;
import com.example.carshering.api.ApiService;
import com.example.carshering.ui.login.LoginActivity;
import com.example.carshering.utils.CircleTransform;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private Uri selectedImageUri;
    private ApiService apiService;
    private String userEmail;
    private static final String[] RUSSIAN_MONTHS_DATIVE = {
            "январе", "феврале", "марте", "апреле", "мае", "июне",
            "июле", "августе", "сентябре", "октябре", "ноябре", "декабре"
    };

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            selectedImageUri = result.getData().getData();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        requireActivity().getContentResolver(),
                                        selectedImageUri
                                );
                                binding.ivProfilePhoto.setImageBitmap(bitmap);
                                uploadImageToServer();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(requireContext(), "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        apiService = ApiClient.getApiService();

        userEmail = requireContext()
                .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .getString("user_email", null);

        loadUserData();

        binding.ivAddPhotoIcon.setOnClickListener(v -> openImagePicker());
        binding.tvLoginOut.setOnClickListener(v -> logoutUser());

        return binding.getRoot();
    }

    private void loadUserData() {
        if (userEmail == null) {
            Toast.makeText(requireContext(), "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getUser(userEmail).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    String fullName = ((user.getFirstName() != null ? user.getFirstName() : "") + " " +
                            (user.getLastName() != null ? user.getLastName() : "")).trim();

                    String registrationText = "Дата не указана";
                    if (user.getRegistrationDate() != null) {
                        try {
                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            Date date = inputFormat.parse(user.getRegistrationDate().substring(0, 10));
                            SimpleDateFormat monthFormat = new SimpleDateFormat("M", Locale.getDefault());
                            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
                            int monthIndex = Integer.parseInt(monthFormat.format(date)) - 1;
                            String monthName = RUSSIAN_MONTHS_DATIVE[monthIndex];
                            String year = yearFormat.format(date);
                            String genderPrefix = "Male".equalsIgnoreCase(user.getGender()) ? "Присоединился" : "Присоединилась";
                            registrationText = String.format("%s в %s %s года", genderPrefix, monthName, year);
                        } catch (ParseException e) {
                            Log.e("ProfileFragment", "Ошибка парсинга даты: " + e.getMessage());
                        }
                    }
                    binding.tvLogRegister.setText(registrationText);
                    binding.tvUserName.setText(!fullName.isEmpty() ? fullName : "Имя пользователя");
                    binding.tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "Email");

                    String genderText = "Male".equalsIgnoreCase(user.getGender()) ? "Мужской" : "Женский";
                    binding.tvUserGender.setText(genderText);

                    binding.ivProfilePhoto.setImageDrawable(null);
                    if (user.getProfilePhotoUrl() != null && !user.getProfilePhotoUrl().isEmpty()) {
                        String imageUrl = "http://10.0.2.2:8080" + user.getProfilePhotoUrl();
                        Log.d("ProfileFragment", "Попытка загрузки изображения: " + imageUrl);
                        Picasso.get()
                                .load(imageUrl)
                                .transform(new CircleTransform())
                                .into(binding.ivProfilePhoto, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d("ProfileFragment", "Изображение успешно загружено");
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Log.e("ProfileFragment", "Ошибка загрузки изображения: " + e.getMessage());
                                        Toast.makeText(requireContext(), "Ошибка загрузки изображения: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(requireContext(), "Ошибка загрузки данных пользователя: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void uploadImageToServer() {
        if (selectedImageUri == null || userEmail == null) {
            Toast.makeText(requireContext(), "Ошибка: изображение или пользователь не выбраны", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream inputStream = requireActivity().getContentResolver().openInputStream(selectedImageUri);
            byte[] fileBytes = new byte[inputStream.available()];
            inputStream.read(fileBytes);
            inputStream.close();

            binding.ivProfilePhoto.setImageDrawable(null);

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    requireActivity().getContentResolver(),
                    selectedImageUri
            );
            binding.ivProfilePhoto.setImageBitmap(bitmap);

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), fileBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", "profile_photo.jpg", requestFile);

            apiService.uploadProfilePhoto(userEmail, body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Фото профиля успешно загружено", Toast.LENGTH_SHORT).show();
                        loadUserData();
                    } else {
                        Toast.makeText(requireContext(), "Ошибка загрузки фото: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(requireContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Ошибка обработки изображения", Toast.LENGTH_SHORT).show();
        }
    }

    private void logoutUser() {
        requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .edit()
                .remove("user_email")
                .apply();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
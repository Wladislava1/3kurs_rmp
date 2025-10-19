package com.example.carshering.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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


import com.example.carshering.api.ApiClient;
import com.example.carshering.api.ApiService;
import com.example.carshering.ui.login.LoginActivity;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private Uri selectedImageUri;
    private ApiService apiService;

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

        loadUserData();

        binding.ivAddPhotoIcon.setOnClickListener(v -> openImagePicker());

        binding.tvLoginOut.setOnClickListener(v -> logoutUser());

        return binding.getRoot();
    }

    private void loadUserData() {
        String email = requireContext()
                .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .getString("user_email", null);

        if (email == null) {
            Toast.makeText(requireContext(), "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getUser(email).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    String fullName = ((user.getFirstName() != null ? user.getFirstName() : "") + " " +
                            (user.getLastName() != null ? user.getLastName() : "")).trim();
                    binding.tvLogRegister.setText(
                            user.getRegistrationDate() != null ? "Присоединился в " + user.getRegistrationDate().substring(0, 10) : "Дата не указана"
                    );
                    binding.tvUserName.setText(!fullName.isEmpty() ? fullName : "Имя пользователя");
                    binding.tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "Email");
                    binding.tvUserGender.setText(user.getGender() != null ? user.getGender() : "Пол не указан");
                } else {
                    Toast.makeText(requireContext(), "Ошибка загрузки данных пользователя", Toast.LENGTH_SHORT).show();
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

    private void logoutUser() {
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
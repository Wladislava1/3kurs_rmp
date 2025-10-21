package com.example.carshering.ui.settings;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carshering.R;
import com.example.carshering.api.ApiClient;
import com.example.carshering.api.ApiService;
import com.example.carshering.databinding.FragmentSettingsBinding;
import com.example.carshering.model.User;
import com.example.carshering.utils.CircleTransform;
import com.example.carshering.ui.profile.ProfileFragment;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private ApiService apiService;
    private String userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        apiService = ApiClient.getApiService();
        Picasso.get().setLoggingEnabled(true);

        userEmail = requireContext()
                .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .getString("user_email", null);

        loadUserData();

        binding.ivProfileArrow.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

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

                    binding.tvUserName.setText(!fullName.isEmpty() ? fullName : "Имя пользователя");
                    binding.tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "Email");
                    binding.ivAvatar.setImageDrawable(null);

                    if (user.getProfilePhotoUrl() != null && !user.getProfilePhotoUrl().isEmpty()) {
                        String imageUrl = "http://10.0.2.2:8080" + user.getProfilePhotoUrl();
                        Log.d("SettingsFragment", "Попытка загрузки изображения: " + imageUrl);
                        Picasso.get()
                                .load(imageUrl)
                                .transform(new CircleTransform())
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .error(R.drawable.ic_profile_placeholder)
                                .into(binding.ivAvatar, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d("SettingsFragment", "Изображение успешно загружено");
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Log.e("SettingsFragment", "Ошибка загрузки изображения: " + e.getMessage());
                                    }
                                });
                    } else {
                        binding.ivAvatar.setImageResource(R.drawable.ic_profile_placeholder);
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

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
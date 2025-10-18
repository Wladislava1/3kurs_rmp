package com.example.carshering;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.carshering.databinding.ActivityMainBinding;
import com.example.carshering.ui.home.HomeFragment;
import com.example.carshering.ui.settings.SettingsFragment;
import com.example.carshering.ui.no_connection.NoConnectionActivity;
import com.example.carshering.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Log.d(TAG, "onCreate: No network available, starting NoConnectionActivity");
            startActivity(new Intent(this, NoConnectionActivity.class));
            finish();
            return;
        }

        Log.d(TAG, "onCreate: Initializing view binding");
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Инициализация начального фрагмента и состояния
        loadFragment(new HomeFragment());
        updateButtonIcons(R.id.nav_home_container);

        // Обработчики кликов для контейнеров
        binding.navHomeContainer.setOnClickListener(v -> {
            Log.d(TAG, "Clicked nav_home_container");
            updateButtonIcons(R.id.nav_home_container);
            loadFragment(new HomeFragment());
        });

        binding.navReservationContainer.setOnClickListener(v -> {
            Log.d(TAG, "Clicked nav_reservation_container");
            updateButtonIcons(R.id.nav_reservation_container);
            loadFragment(new HomeFragment()); // Замените на ReservationFragment
        });

        binding.navSettingsContainer.setOnClickListener(v -> {
            Log.d(TAG, "Clicked nav_settings_container");
            updateButtonIcons(R.id.nav_settings_container);
            loadFragment(new SettingsFragment());
        });
    }

    private void updateButtonIcons(int activeButtonId) {
        Log.d(TAG, "Updating icons for active button: " + activeButtonId);

        // Сбрасываем все иконки на неактивное состояние
        binding.navHomeActive.setVisibility(View.GONE);
        binding.navHomeInactive.setVisibility(View.VISIBLE);
        binding.navReservationActive.setVisibility(View.GONE);
        binding.navReservationInactive.setVisibility(View.VISIBLE);
        binding.navSettingsActive.setVisibility(View.GONE);
        binding.navSettingsInactive.setVisibility(View.VISIBLE);

        // Устанавливаем активное состояние для выбранной кнопки
        if (activeButtonId == R.id.nav_home_container) {
            binding.navHomeActive.setVisibility(View.VISIBLE);
            binding.navHomeInactive.setVisibility(View.GONE);
            Log.d(TAG, "Set nav_home_active VISIBLE, nav_home_inactive GONE");
        } else if (activeButtonId == R.id.nav_reservation_container) {
            binding.navReservationActive.setVisibility(View.VISIBLE);
            binding.navReservationInactive.setVisibility(View.GONE);
            Log.d(TAG, "Set nav_reservation_active VISIBLE, nav_reservation_inactive GONE");
        } else if (activeButtonId == R.id.nav_settings_container) {
            binding.navSettingsActive.setVisibility(View.VISIBLE);
            binding.navSettingsInactive.setVisibility(View.GONE);
            Log.d(TAG, "Set nav_settings_active VISIBLE, nav_settings_inactive GONE");
        }


        binding.navigationBar.invalidate();
        binding.navigationBar.requestLayout();
        Log.d(TAG, "Invalidated navigation bar");
    }

    private void loadFragment(@NonNull Fragment fragment) {
        Log.d(TAG, "Loading fragment: " + fragment.getClass().getSimpleName());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
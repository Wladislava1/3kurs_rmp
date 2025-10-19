package com.example.carshering;

import android.content.Intent;
import android.os.Bundle;
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
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!NetworkUtils.isNetworkAvailable(this)) {
            startActivity(new Intent(this, NoConnectionActivity.class));
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadFragment(new HomeFragment());
        updateButtonIcons(R.id.nav_home_container);

        binding.navHomeContainer.setOnClickListener(v -> {
            goToHome();
        });

        binding.navReservationContainer.setOnClickListener(v -> {
            updateButtonIcons(R.id.nav_reservation_container);
            loadFragment(new HomeFragment());
        });

        binding.navSettingsContainer.setOnClickListener(v -> {
            updateButtonIcons(R.id.nav_settings_container);
            loadFragment(new SettingsFragment());
        });
    }

    @Override
    public void onBackPressed() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (current instanceof HomeFragment || current instanceof SettingsFragment) {
            super.onBackPressed();
        } else {
            goToHome();
        }
    }

    private void updateButtonIcons(int activeButtonId) {

        binding.navHomeActive.setVisibility(View.GONE);
        binding.navHomeInactive.setVisibility(View.VISIBLE);
        binding.navReservationActive.setVisibility(View.GONE);
        binding.navReservationInactive.setVisibility(View.VISIBLE);
        binding.navSettingsActive.setVisibility(View.GONE);
        binding.navSettingsInactive.setVisibility(View.VISIBLE);

        if (activeButtonId == R.id.nav_home_container) {
            binding.navHomeActive.setVisibility(View.VISIBLE);
            binding.navHomeInactive.setVisibility(View.GONE);
        } else if (activeButtonId == R.id.nav_reservation_container) {
            binding.navReservationActive.setVisibility(View.VISIBLE);
            binding.navReservationInactive.setVisibility(View.GONE);

        } else if (activeButtonId == R.id.nav_settings_container) {
            binding.navSettingsActive.setVisibility(View.VISIBLE);
            binding.navSettingsInactive.setVisibility(View.GONE);

        }

        binding.navigationBar.invalidate();
        binding.navigationBar.requestLayout();
    }

    public void goToHome() {
        updateButtonIcons(R.id.nav_home_container);
        loadFragment(new HomeFragment());
    }

    private void loadFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
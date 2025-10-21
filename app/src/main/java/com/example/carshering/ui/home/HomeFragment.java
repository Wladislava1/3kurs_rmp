package com.example.carshering.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carshering.R;
import com.example.carshering.api.ApiClient;
import com.example.carshering.api.ApiService;
import com.example.carshering.databinding.FragmentHomeBinding;
import com.example.carshering.model.Car;
import com.example.carshering.ui.no_connection.NoConnectionActivity;
import com.example.carshering.utils.NetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private CarAdapter carAdapter;
    private ApiService apiService;
    private View errorLayout;
    private Button btnRetry;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        apiService = ApiClient.getApiService();
        errorLayout = binding.getRoot().findViewById(R.id.errorLayout);
        btnRetry = binding.getRoot().findViewById(R.id.btnRetry);

        btnRetry.setOnClickListener(v -> {
            errorLayout.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
            loadCars();
        });

        carAdapter = new CarAdapter(new CarAdapter.OnCarActionListener() {
            @Override
            public void onBookClick(Car car) {
                // TODO: переход на экран бронирования
            }

            @Override
            public void onDetailsClick(Car car) {
                // TODO: переход на экран деталей
            }

            @Override
            public void onRetryClick() {
                loadCars();
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(carAdapter);
        loadCars();

        EditText searchField = binding.searchField;
        ImageButton searchButton = binding.searchButton;

        searchButton.setOnClickListener(v -> {
            String query = searchField.getText().toString().trim();
            if (TextUtils.isEmpty(query)) {
                Toast.makeText(requireContext(), "Введите марку автомобиля", Toast.LENGTH_SHORT).show();
            } else {
                if (!NetworkUtils.isNetworkAvailable(requireContext())) {
                    startActivity(new Intent(requireContext(), NoConnectionActivity.class));
                    requireActivity().finish();
                } else {
                    startSearch(query);
                }
            }
        });

        return binding.getRoot();
    }

    private void startSearch(String query) {
        LoadingFragment loadingFragment = LoadingFragment.newInstance(query, true);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, loadingFragment)
                .commit();
    }

    private void loadCars() {
        errorLayout.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        apiService.getCars().enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(@NonNull Call<List<Car>> call, @NonNull Response<List<Car>> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    carAdapter.setCars(response.body());
                    binding.recyclerView.setVisibility(View.VISIBLE);
                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Car>> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}

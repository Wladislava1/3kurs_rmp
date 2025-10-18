package com.example.carshering.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carshering.api.ApiClient;
import com.example.carshering.api.ApiService;
import com.example.carshering.databinding.FragmentHomeBinding;
import com.example.carshering.model.Car;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private CarAdapter carAdapter;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        apiService = ApiClient.getApiService();

        carAdapter = new CarAdapter(new CarAdapter.OnCarActionListener() {
            @Override
            public void onBookClick(Car car) {
                // TODO: переход на экран бронирования
            }

            @Override
            public void onDetailsClick(Car car) {
                // TODO: переход на экран деталей
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(carAdapter);
        loadCars();

        return binding.getRoot();
    }

    private void loadCars() {
        binding.progressBar.setVisibility(View.VISIBLE);

        apiService.getCars().enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(@NonNull Call<List<Car>> call, @NonNull Response<List<Car>> response) {
                binding.progressBar.setVisibility(View.GONE);
                Log.d("API", "Response code: " + response.code());
                Log.d("API", "Body: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    carAdapter.setCars(response.body());
                } else {
                    showError("Не удалось загрузить данные. Попробуйте снова.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Car>> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                showError("Ошибка загрузки данных. Проверьте подключение к интернету.");
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}

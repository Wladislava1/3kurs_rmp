package com.example.carshering.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.carshering.api.ApiClient;
import com.example.carshering.api.ApiService;
import com.example.carshering.model.Car;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.carshering.R;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class SearchResultsFragment extends Fragment {

    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private RecyclerView recyclerCars;
    private List<Car> carList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        errorLayout = view.findViewById(R.id.errorLayout);
        recyclerCars = view.findViewById(R.id.recyclerCars);
        Button btnRetry = view.findViewById(R.id.btnRetry);

        recyclerCars.setLayoutManager(new LinearLayoutManager(getContext()));

        btnRetry.setOnClickListener(v -> loadCars("Kia"));

        loadCars("Kia");

        return view;
    }

    private void loadCars(String query) {
        progressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        recyclerCars.setVisibility(View.GONE);

        ApiService apiService = ApiClient.getApiService();
        apiService.searchCars(query).enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(@NonNull Call<List<Car>> call, @NonNull Response<List<Car>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    carList.clear();
                    carList.addAll(response.body());

                    if (carList.isEmpty()) {
                        errorLayout.setVisibility(View.VISIBLE);
                    } else {
                        recyclerCars.setVisibility(View.VISIBLE);
                        if (recyclerCars.getAdapter() == null) {
                            recyclerCars.setAdapter(new CarAdapter(new CarAdapter.OnCarActionListener() {
                                @Override
                                public void onBookClick(Car car) {
                                    // TODO: переход на экран аренды
                                }

                                @Override
                                public void onDetailsClick(Car car) {
                                    // TODO: переход на экран деталей
                                }
                            }));
                        }
                        ((CarAdapter) recyclerCars.getAdapter()).setCars(carList);
                    }
                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Car>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}

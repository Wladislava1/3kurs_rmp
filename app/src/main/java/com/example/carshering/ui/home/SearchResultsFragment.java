package com.example.carshering.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carshering.MainActivity;
import com.example.carshering.R;
import com.example.carshering.api.ApiClient;
import com.example.carshering.api.ApiService;
import com.example.carshering.model.Car;
import com.example.carshering.ui.no_connection.NoConnectionActivity;
import com.example.carshering.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsFragment extends Fragment {
    private View errorLayout;
    private ProgressBar progressBar;
    private RecyclerView recyclerCars;
    private List<Car> carList = new ArrayList<>();

    public static SearchResultsFragment newInstance(String query, List<Car> cars) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        if (cars != null) {
            args.putSerializable("cars", new ArrayList<>(cars));
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        errorLayout = view.findViewById(R.id.errorLayout);
        recyclerCars = view.findViewById(R.id.recyclerCars);
        Button btnRetry = view.findViewById(R.id.btnRetry);
        ImageButton btnBack = view.findViewById(R.id.btnBack);

        recyclerCars.setLayoutManager(new LinearLayoutManager(getContext()));

        String query = getArguments() != null ?
                getArguments().getString(getString(R.string.query), getString(R.string.kia))
                :  getString(R.string.kia);
        ArrayList<Car> cars = getArguments() != null ?
                (ArrayList<Car>) getArguments().getSerializable(getString(R.string.cars)) : null;

        if (cars != null) {
            progressBar.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
            carList.addAll(cars);
            if (carList.isEmpty()) {
                errorLayout.setVisibility(View.VISIBLE);
            } else {
                recyclerCars.setVisibility(View.VISIBLE);
                recyclerCars.setAdapter(new CarAdapter(new CarAdapter.OnCarActionListener() {
                    @Override
                    public void onBookClick(Car car) {
                        // TODO: переход на экран аренды
                    }

                    @Override
                    public void onDetailsClick(Car car) {
                        // TODO: переход на экран деталей
                    }
                    @Override
                    public void onRetryClick() {
                        loadCars(query);
                    }
                }));
                ((CarAdapter) recyclerCars.getAdapter()).setCars(carList);
            }
        } else {
            loadCars(query);
        }

        btnRetry.setOnClickListener(v -> loadCars(query));

        btnBack.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).goToHome();
        });

        return view;
    }

    private void loadCars(String query) {
        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            startActivity(new Intent(requireContext(), NoConnectionActivity.class));
            requireActivity().finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        recyclerCars.setVisibility(View.GONE);

        ApiService apiService = ApiClient.getApiService();
        apiService.searchCars(query).enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(@NonNull Call<List<Car>> call,
                                   @NonNull Response<List<Car>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    carList.clear();
                    carList.addAll(response.body());

                    if (carList.isEmpty()) {
                        errorLayout.setVisibility(View.VISIBLE);
                    } else {
                        recyclerCars.setVisibility(View.VISIBLE);
                        if (recyclerCars.getAdapter() == null) {
                            recyclerCars.setAdapter(new CarAdapter(
                                    new CarAdapter.OnCarActionListener() {
                                @Override
                                public void onBookClick(Car car) {
                                    // TODO: переход на экран аренды
                                }

                                @Override
                                public void onDetailsClick(Car car) {
                                    // TODO: переход на экран деталей
                                }
                                @Override
                                public void onRetryClick() {
                                    loadCars(query);
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
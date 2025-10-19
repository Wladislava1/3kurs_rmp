package com.example.carshering.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carshering.MainActivity;
import com.example.carshering.R;
import com.example.carshering.api.ApiClient;
import com.example.carshering.api.ApiService;
import com.example.carshering.model.Car;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingFragment extends Fragment {
    private View dot1, dot2, dot3;
    private final Handler handler = new Handler();
    private int step = 0;
    private String query;
    private boolean fromHome = false;

    public static LoadingFragment newInstance(String query, boolean fromHome) {
        LoadingFragment fragment = new LoadingFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        args.putBoolean("fromHome", fromHome);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            query = getArguments().getString("query", "");
            fromHome = getArguments().getBoolean("fromHome", false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);

        dot1 = view.findViewById(R.id.dot1);
        dot2 = view.findViewById(R.id.dot2);
        dot3 = view.findViewById(R.id.dot3);

        if (fromHome) {
            startDotsAnimation();
            startSearchRequest();
        } else {
            ((MainActivity) requireActivity()).goToHome();
        }

        return view;
    }

    private void startDotsAnimation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                step = (step + 1) % 4;
                dot1.setAlpha(step == 1 ? 1f : 0.2f);
                dot2.setAlpha(step == 2 ? 1f : 0.2f);
                dot3.setAlpha(step == 3 ? 1f : 0.2f);
                handler.postDelayed(this, 400);
            }
        }, 300);
    }

    private void startSearchRequest() {

        long startTime = System.currentTimeMillis();

        ApiService apiService = ApiClient.getApiService();
        apiService.searchCars(query).enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(@NonNull Call<List<Car>> call, @NonNull Response<List<Car>> response) {
                long elapsed = System.currentTimeMillis() - startTime;
                long delay = Math.max(0, 1500 - elapsed);

                handler.postDelayed(() -> {
                    handler.removeCallbacksAndMessages(null);

                    if (response.isSuccessful() && response.body() != null) {
                        SearchResultsFragment resultsFragment = SearchResultsFragment.newInstance(query, response.body());
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, resultsFragment)
                                .commit();
                    } else {
                        Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                        ((MainActivity) requireActivity()).goToHome();
                    }
                }, delay);
            }

            @Override
            public void onFailure(@NonNull Call<List<Car>> call, @NonNull Throwable t) {
                handler.removeCallbacksAndMessages(null);
                Toast.makeText(requireContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                ((MainActivity) requireActivity()).goToHome();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}
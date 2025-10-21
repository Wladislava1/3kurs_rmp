package com.example.carshering.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carshering.R;
import com.example.carshering.model.Car;

import java.util.ArrayList;
import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private final List<Car> cars = new ArrayList<>();
    private final OnCarActionListener listener;
    private boolean loadError = false;
    private static final String BASE_URL = "http://10.0.2.2:8080";

    public interface OnCarActionListener {
        void onBookClick(Car car);
        void onDetailsClick(Car car);
        void onRetryClick();
    }

    public CarAdapter(OnCarActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        if (loadError) {
            holder.errorLayout.setVisibility(View.VISIBLE);
            holder.carLayout.setVisibility(View.GONE);

            holder.btnRetry.setOnClickListener(v -> listener.onRetryClick());
            return;
        }

        holder.errorLayout.setVisibility(View.GONE);
        holder.carLayout.setVisibility(View.VISIBLE);

        Car car = cars.get(position);
        holder.tvBrandModel.setText(car.getBrand());
        holder.tvMark.setText(car.getModel());
        holder.tvPrice.setText(String.format("%.0f / час", car.getPricePerHour()));
        holder.tvTransmission.setText(car.getTransmission());
        holder.tvFuel.setText(car.getFuelType());

        if (car.getImageUrl() != null && !car.getImageUrl().isEmpty()) {
            String imageUrl = car.getImageUrl().startsWith("http") ? car.getImageUrl() : BASE_URL + car.getImageUrl();
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.car_placeholder)
                    .error(R.drawable.car_placeholder)
                    .into(holder.ivCarImage);
        } else {
            holder.ivCarImage.setImageResource(R.drawable.car_placeholder);
        }

        holder.btnBook.setOnClickListener(v -> listener.onBookClick(car));
        holder.btnDetails.setOnClickListener(v -> listener.onDetailsClick(car));
    }

    @Override
    public int getItemCount() {
        return loadError ? 1 : cars.size();
    }

    public void setCars(List<Car> newCars) {
        loadError = false;
        cars.clear();
        if (newCars != null) {
            cars.addAll(newCars);
        }
        notifyDataSetChanged();
    }

    public void showError() {
        loadError = true;
        notifyDataSetChanged();
    }

    static class CarViewHolder extends RecyclerView.ViewHolder {
        View carLayout;
        ImageView ivCarImage;
        TextView tvBrandModel, tvMark, tvPrice, tvTransmission, tvFuel;
        Button btnBook, btnDetails;

        // Ошибка
        View errorLayout;
        TextView tvErrorMessage;
        Button btnRetry;

        CarViewHolder(@NonNull View itemView) {
            super(itemView);

            carLayout = itemView.findViewById(R.id.carLayout);
            ivCarImage = itemView.findViewById(R.id.ivCarImage);
            tvBrandModel = itemView.findViewById(R.id.tvBrandModel);
            tvMark = itemView.findViewById(R.id.tvMark);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTransmission = itemView.findViewById(R.id.tvTransmission);
            tvFuel = itemView.findViewById(R.id.tvFuel);
            btnBook = itemView.findViewById(R.id.btnBook);
            btnDetails = itemView.findViewById(R.id.btnDetails);

            errorLayout = itemView.findViewById(R.id.errorLayout);
            tvErrorMessage = itemView.findViewById(R.id.tvErrorMessage);
            btnRetry = itemView.findViewById(R.id.btnRetry);
        }
    }
}

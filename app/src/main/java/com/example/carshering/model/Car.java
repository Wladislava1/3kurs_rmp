package com.example.carshering.model;

public class Car {
    private int id;
    private String brand;
    private String model;
    private String transmission;
    private String fuelType;
    private double pricePerHour;
    private String imageUrl;
    public Car(int id, String brand, String model, String transmission, String fuelType, double pricePerHour, String imageUrl) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.transmission = transmission;
        this.fuelType = fuelType;
        this.pricePerHour = pricePerHour;
        this.imageUrl = imageUrl;
    }
    public int getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getTransmission() {
        return transmission;
    }

    public String getFuelType() {
        return fuelType;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

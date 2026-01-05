package me.pieralini.com.objects;

import java.math.BigDecimal;

public class Car {

    private int id;
    private int carModelId;
    private CarModel carModel;
    private int year;
    private int colorId;
    private Color color;
    private int fuelTypeId;
    private FuelType fuelType;
    private int transmissionId;
    private Transmission transmission;
    private BigDecimal price;
    private String licensePlate;
    private int mileage;
    private boolean available;

    public Car() {
        this.available = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCarModelId() {
        return carModelId;
    }

    public void setCarModelId(int carModelId) {
        this.carModelId = carModelId;
    }

    public CarModel getCarModel() {
        return carModel;
    }

    public void setCarModel(CarModel carModel) {
        this.carModel = carModel;
        if (carModel != null) {
            this.carModelId = carModel.getId();
        }
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        if (color != null) {
            this.colorId = color.getId();
        }
    }

    public int getFuelTypeId() {
        return fuelTypeId;
    }

    public void setFuelTypeId(int fuelTypeId) {
        this.fuelTypeId = fuelTypeId;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
        if (fuelType != null) {
            this.fuelTypeId = fuelType.getId();
        }
    }

    public int getTransmissionId() {
        return transmissionId;
    }

    public void setTransmissionId(int transmissionId) {
        this.transmissionId = transmissionId;
    }

    public Transmission getTransmission() {
        return transmission;
    }

    public void setTransmission(Transmission transmission) {
        this.transmission = transmission;
        if (transmission != null) {
            this.transmissionId = transmission.getId();
        }
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getFullName() {
        if (carModel != null) {
            return carModel.getFullName() + " " + year;
        }
        return "Car #" + id;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", model=" + (carModel != null ? carModel.getFullName() : carModelId) +
                ", year=" + year +
                ", color=" + (color != null ? color.getName() : colorId) +
                ", fuelType=" + (fuelType != null ? fuelType.getName() : fuelTypeId) +
                ", transmission=" + (transmission != null ? transmission.getName() : transmissionId) +
                ", price=" + price +
                ", licensePlate='" + licensePlate + '\'' +
                ", mileage=" + mileage +
                ", available=" + available +
                '}';
    }
}

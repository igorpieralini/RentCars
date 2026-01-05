package me.pieralini.com.objects;

public class CarModel {

    private int id;
    private String name;
    private int brandId;
    private Brand brand;

    public CarModel() {
    }

    public CarModel(int id, String name, int brandId) {
        this.id = id;
        this.name = name;
        this.brandId = brandId;
    }

    public CarModel(int id, String name, Brand brand) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.brandId = brand != null ? brand.getId() : 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
        if (brand != null) {
            this.brandId = brand.getId();
        }
    }

    public String getFullName() {
        return brand != null ? brand.getName() + " " + name : name;
    }

    @Override
    public String toString() {
        return "CarModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brand=" + (brand != null ? brand.getName() : brandId) +
                '}';
    }
}


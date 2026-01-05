package me.pieralini.com.objects;

public class Brand {

    private int id;
    private String name;
    private String country;
    private int foundedYear;

    public Brand() {
    }

    public Brand(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Brand(int id, String name, String country, int foundedYear) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.foundedYear = foundedYear;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getFoundedYear() {
        return foundedYear;
    }

    public void setFoundedYear(int foundedYear) {
        this.foundedYear = foundedYear;
    }

    @Override
    public String toString() {
        return "Brand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", foundedYear=" + foundedYear +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Brand brand = (Brand) o;
        return id == brand.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}

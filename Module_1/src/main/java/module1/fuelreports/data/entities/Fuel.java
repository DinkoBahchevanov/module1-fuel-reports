package module1.fuelreports.data.entities;

public class Fuel extends BaseEntity {

    private String type;
    private double price;
    private PetrolStation station;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public PetrolStation getStation() {
        return station;
    }

    public void setStation(PetrolStation petrolStation) {
        this.station = petrolStation;
    }
}

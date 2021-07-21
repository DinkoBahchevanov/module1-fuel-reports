package module1.fuelreports.data.entities;

public abstract class BaseEntity {

    private int id;

    public BaseEntity() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

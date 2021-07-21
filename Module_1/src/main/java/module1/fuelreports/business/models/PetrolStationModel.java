package module1.fuelreports.business.models;

import javax.xml.bind.annotation.*;
import java.time.LocalDate;

@XmlRootElement(name = "petrolStation")
@XmlAccessorType(XmlAccessType.FIELD)
public class PetrolStationModel {

    @XmlAttribute
    private String name;
    @XmlAttribute
    private String address;
    @XmlAttribute
    private String city;
    @XmlElement
    private FuelRootModel fuels;
    private LocalDate date;

    public PetrolStationModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public FuelRootModel getFuels() {
        return fuels;
    }

    public void setFuels(FuelRootModel fuels) {
        this.fuels = fuels;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

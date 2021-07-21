package module1.fuelreports.business.models;

import module1.fuelreports.config.LocalDateAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.List;

@XmlRootElement(name = "petrolStations")
@XmlAccessorType(XmlAccessType.FIELD)
public class PetrolStationRootModel {

    @XmlElement(name = "petrolStation")
    private List<PetrolStationModel> petrolStations;

    @XmlAttribute(name = "date")
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    private LocalDate date;

    public PetrolStationRootModel() {
    }

    public List<PetrolStationModel> getPetrolStations() {
        return petrolStations;
    }

    public void setPetrolStations(List<PetrolStationModel> petrolStations) {
        this.petrolStations = petrolStations;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

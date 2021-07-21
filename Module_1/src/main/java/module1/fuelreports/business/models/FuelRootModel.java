package module1.fuelreports.business.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "fuels")
@XmlAccessorType(XmlAccessType.FIELD)
public class FuelRootModel {

    @XmlElement(name = "fuel")
    private List<FuelModel> fuels;

    public List<FuelModel> getFuels() {
        return fuels;
    }

    public void setFuels(List<FuelModel> fuels) {
        this.fuels = fuels;
    }
}

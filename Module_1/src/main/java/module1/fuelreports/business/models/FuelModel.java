package module1.fuelreports.business.models;


import javax.xml.bind.annotation.*;

@XmlRootElement(name = "fuel")
@XmlAccessorType(XmlAccessType.FIELD)
public class FuelModel {

    @XmlAttribute(name = "type")
    private String type;
    @XmlElement(name = "price")
    private double price;

    public FuelModel() {
    }

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
}

package module1.fuelreports.business;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;

public class ReportCommandHandler {

    @Parameter(
            names = "--period",
            description = "date period for query",
            required = true
    )
    private String period;

    @Parameter(
            names = "--fuel-type",
            description = "type of fuel",
            required = false,
            variableArity = true
    )
    private ArrayList<String> fuelType;

    @Parameter(
            names = "--petrol-station",
            description = "name of petrol station",
            required = false,
            variableArity = true
    )
    private ArrayList<String> petrolStationName;


    @Parameter(
            names = "--city",
            description = "name of petrol station city",
            required = false,
            variableArity = true
    )
    private ArrayList<String> petrolStationCityName;

    public ArrayList<String> getFuelType() {
        return fuelType;
    }

    public String getPeriod() {
        return period;
    }

    public ArrayList<String> getPetrolStationCityName() {
        return petrolStationCityName;
    }

    public ArrayList<String> getPetrolStationName() {
        return petrolStationName;
    }
}

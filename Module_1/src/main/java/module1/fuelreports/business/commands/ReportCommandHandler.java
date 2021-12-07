package module1.fuelreports.business.commands;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;

public class ReportCommandHandler {

    @Parameter(
            names = "--period",
            description = "date period for query",
            required = false
    )
    private String period;

    @Parameter(
            names = "--day",
            description = "date period for query",
            required = false
    )
    private String day;

    @Parameter(
            names = "--month",
            description = "date period for query",
            required = false
    )
    private String month;

    @Parameter(
            names = "--year",
            description = "date period for query",
            required = false
    )
    private String year;

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

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public void setPeriod() {
        if (period == null) {
            if (year != null) {
                period = year;
            }else if (month != null) {
                period = month;
            }else if (day != null) {
                period = day;
            }
        }
    }
}

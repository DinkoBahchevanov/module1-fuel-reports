package module1.fuelreports.data.repositories;

import com.beust.jcommander.JCommander;
import module1.fuelreports.business.commands.ReportCommandHandler;
import module1.fuelreports.business.services.DatabaseConnectionService;
import module1.fuelreports.business.services.DatabaseManagerService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class FuelRepository {
    private static final Logger LOGGER = Logger.getLogger(DatabaseManagerService.class.getSimpleName());

    public String getAvgFuelPrice(String[] args) {

        //2021-02-15.xml

        //config --data-dir src/main/resources/data/

        //report --period 2021 --city Ar Ru’ays
        //report --period 2021-02 --petrol-station Wintheiser Inc
        //report --day 2021-02 --petrol-station Wintheiser Inc
        //report --month 2021-02 --petrol-station Wintheiser Inc
        //report --period 2021-02-15 --fuel-type Premium Petrol
        //report --period 2021 --petrol-station Wintheiser Inc --city Ar Ru’ays

        ReportCommandHandler reportCommandHandler = new ReportCommandHandler();

        String[] argumentValues = new String[args.length - 1];

        LinkedList<String> parameterList = new LinkedList<>(List.of(args));
        parameterList.removeFirst();
        for ( int i = 0; i < argumentValues.length; i++ ) {
            argumentValues[i] = parameterList.get(i);
        }

        JCommander reportFuelCmd = JCommander.newBuilder()
                .addObject(reportCommandHandler)
                .build();

        reportFuelCmd.parse(argumentValues);

        if (!validatePeriodInsertion(reportCommandHandler)) {
            return "Wrong period parameters!";
        }

        reportCommandHandler.setPeriod();

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT f.type, AVG(price) FROM petrol_stations_fuels_prices as fp\n" +
                "INNER JOIN fuels as f\n" +
                "ON fp.fuel_id = f.id\n" +
                "INNER JOIN petrol_stations as ps\n" +
                "ON fp.petrol_station_id = ps.id\n" +
                "WHERE ");

        String[] date = reportCommandHandler.getPeriod().split("-");

        if (date.length == 1) sb.append("YEAR(fp.`date`) = '" + date[0] + "'");
        if (date.length == 2) sb.append("YEAR(fp.`date`) = '" + date[0] + "' " + "AND MONTH(fp.`date`) = '" + date[1] + "' ");
        if (date.length == 3) sb.append("DATE(fp.`date`) = '" + reportCommandHandler.getPeriod() + "' ");

        if (reportCommandHandler.getFuelType() != null && reportCommandHandler.getFuelType().size() != 0) {
            sb.append("AND f.type = '");
            for ( int i = 0; i < reportCommandHandler.getFuelType().size(); i++ ) {
                String partOfName = reportCommandHandler.getFuelType().get(i);
                sb.append("" + partOfName + "");
            }
            sb.append("' ");
        }
        if (reportCommandHandler.getPetrolStationName() != null && reportCommandHandler.getPetrolStationName().size() != 0) {
            sb.append("AND ps.name = '");
            for ( int i = 0; i < reportCommandHandler.getPetrolStationName().size(); i++ ) {
                String partOfName = reportCommandHandler.getPetrolStationName().get(i);
                sb.append("" + partOfName + " ");
            }
            String query = sb.toString().trim();
            sb = new StringBuilder();
            sb.append(query);
            sb.append("' ");
        }

        if (reportCommandHandler.getPetrolStationCityName() != null && reportCommandHandler.getPetrolStationCityName().size() != 0) {
            sb.append("AND ps.city = '");
            for ( int i = 0; i < reportCommandHandler.getPetrolStationCityName().size(); i++ ) {
                String partOfName = reportCommandHandler.getPetrolStationCityName().get(i);
                sb.append("" + partOfName + " ");
            }
            String query = sb.toString().trim();
            sb = new StringBuilder();
            sb.append(query);
            sb.append("' ");
        }

        if (reportCommandHandler.getFuelType() == null) {
            sb.append("GROUP BY f.id");
        }
        sb.append(";");
        DatabaseConnectionService dbs = new DatabaseConnectionService();
        dbs.connectMainUrl();

        try {
            ResultSet resultSet = dbs.getStmt().executeQuery(sb.toString());

            if (reportCommandHandler.getFuelType() == null) {
                sb = new StringBuilder();
                while (resultSet.next()) {
                    sb.append(resultSet.getString("type")).append(": ").append(resultSet.getDouble(2))
                            .append(System.lineSeparator());
                }
                return sb.toString();
            }

            if (resultSet.next()) {
                double price = resultSet.getDouble(2);
                if (price != 0) {
                    return resultSet.getString("type") + ": " + price + "";
                }
            }

        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }
        return "There are no fuel reports corresponding to these parameters!";
    }

    private boolean validatePeriodInsertion(ReportCommandHandler reportCommandHandler) {

        if (reportCommandHandler.getYear() != null && reportCommandHandler.getYear().length() != 4) {
            return false;
        }

        if (reportCommandHandler.getMonth() != null && reportCommandHandler.getMonth().length() != 7) {
            return false;
        }

        if (reportCommandHandler.getDay() != null && reportCommandHandler.getDay().length() != 10) {
            return false;
        }

        return true;
    }
}

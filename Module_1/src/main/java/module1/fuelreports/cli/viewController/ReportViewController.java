package module1.fuelreports.cli.viewController;

import com.beust.jcommander.JCommander;

import module1.fuelreports.business.ReportCommandHandler;
import module1.fuelreports.business.services.DatabaseConnectionService;
import module1.fuelreports.business.services.DatabaseManagerService;
import module1.fuelreports.data.entities.Fuel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReportViewController {

    public void viewFuelReport(String[] args) {
        ReportCommandHandler reportCommandHandler = new ReportCommandHandler();

        String[] argumentValues = new String[args.length-1];

        for ( int i = 1; i < args.length; i++ ) {
            argumentValues[i-1] = args[i];
        }

        JCommander reportFuelCmd = JCommander.newBuilder()
                .addObject(reportCommandHandler)
                .build();

        reportFuelCmd.parse(argumentValues);

        Fuel fuel = new Fuel();

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT AVG(price) FROM petrol_stations_fuels_prices as fp\n" +
                "INNER JOIN fuels as f\n" +
                "ON fp.fuel_id = f.fuel_id\n" +
                "INNER JOIN petrol_stations as ps\n" +
                "ON fp.petrol_station_id = ps.petrol_station_id\n" +
                "WHERE ");
        String[] date = reportCommandHandler.getPeriod().split("-");
//        if (reportCommandHandler.getPeriod().split("-").length == 1) {
//
//        }
        if (date.length == 1) sb.append("YEAR(fp.`date`) = '" + date[0] + "'");
        if (date.length == 2) sb.append("YEAR(fp.`date`) = '" + date[0] + "' " + "AND MONTH(fp.`date`) = '" + date[1] + "' ");
        if (date.length == 3) sb.append("DATE(fp.`date`) = '"+ reportCommandHandler.getPeriod() +"' ");

        if (reportCommandHandler.getFuelType() != null && reportCommandHandler.getFuelType().size() != 0) {
            sb.append("AND f.type = '");
            for ( int i = 0; i < reportCommandHandler.getFuelType().size(); i++ ) {
                String partOfName = reportCommandHandler.getFuelType().get(i);
                sb.append("" + partOfName + "");
            }
            sb.append("' ");
        }
        if (reportCommandHandler.getPetrolStationName()!= null && reportCommandHandler.getPetrolStationName().size() != 0) {
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

        if ( reportCommandHandler.getPetrolStationCityName()!= null && reportCommandHandler.getPetrolStationCityName().size() != 0) {
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
        sb.append(";");
        System.out.println(sb.toString());
        DatabaseConnectionService dbs = new DatabaseConnectionService();
        dbs.connectMain();

        try {
            ResultSet resultSet = dbs.getStmt().executeQuery(sb.toString());

            while (resultSet.next()) {
                System.out.println(resultSet.getDouble(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

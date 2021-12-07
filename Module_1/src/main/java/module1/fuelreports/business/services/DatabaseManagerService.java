package module1.fuelreports.business.services;

import module1.fuelreports.data.entities.Fuel;
import module1.fuelreports.data.entities.PetrolStation;

import java.sql.*;
import java.util.*;

import java.util.logging.Logger;

public class DatabaseManagerService {

    private static final Logger LOGGER = Logger.getLogger(DatabaseManagerService.class.getSimpleName());

    private final DatabaseConnectionService dbs;

    public DatabaseManagerService(DatabaseConnectionService dbs) {
        this.dbs = dbs;
    }

    public void createDatabase() {
        try {
            dbs.connectStartingUrl();
            //drop DB if it exists
            String dropDB = "DROP SCHEMA IF EXISTS fuel_reports;";
            dbs.getStmt().executeUpdate(dropDB);

            //create DB if it doesn't exists
            String sql = "CREATE DATABASE IF NOT EXISTS fuel_reports;";
            dbs.getStmt().executeUpdate(sql);

            LOGGER.info("Database created successfully...");
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public void createTables() {

        try {
            dbs.connectMainUrl();
            //query for USING the created DB
            String useDB = "USE fuel_reports;";
            dbs.getStmt().executeUpdate(useDB);

            //create fuels table
            String fuel =
                    "CREATE TABLE IF NOT EXISTS `fuels` (\n" +
                            "\t `id` INT AUTO_INCREMENT,\n" +
                            "\t`type` VARCHAR (18) NOT NULL UNIQUE,\n" +
                            "    PRIMARY KEY (`id`)\n" +
                            ");";
            dbs.getStmt().executeUpdate(fuel);

            //create petrolStations table
            String petrolStationInsert =
                    "CREATE TABLE IF NOT EXISTS `petrol_stations` (\n" +
                            "\t`id` INT AUTO_INCREMENT,\n" +
                            "    `name` VARCHAR(20) NOT NULL,\n" +
                            "    `address` VARCHAR(40) NOT NULL,\n" +
                            "    `city` VARCHAR(30) NOT NULL,\n" +
                            "    PRIMARY KEY (`id`),\n" +
                            "    UNIQUE UQ_name_address_city(`name`, city, address)" +
                            ");";
            dbs.getStmt().executeUpdate(petrolStationInsert);

            //create mapping table
            String petrolStationsFuelsPrices =
                    "CREATE TABLE IF NOT EXISTS `petrol_stations_fuels_prices` (\n" +
                            "\t`id` INT AUTO_INCREMENT,\n" +
                            "\t`fuel_id` INT NOT NULL,\n" +
                            "\t`petrol_station_id` INT NOT NULL ,\n" +
                            "    `price` DECIMAL(10,2) NOT NULL,\n" +
                            "    `date` DATE NOT NULL,\n" +
                            "    PRIMARY KEY (id),\n" +
                            "    FOREIGN KEY(`fuel_id`) REFERENCES `fuels`(id),\n" +
                            "    FOREIGN KEY(`petrol_station_id`) REFERENCES `petrol_stations`(id)" +
                            ");";

            String configTable = "CREATE TABLE IF NOT EXISTS `config` (\n" +
                    "\t`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                    "    `config_type` VARCHAR(20) NOT NULL,\n" +
                    "    `info` VARCHAR(30) NOT NULL\n" +
                    ");";

            dbs.getStmt().executeUpdate(configTable);

            dbs.getStmt().executeUpdate(petrolStationsFuelsPrices);
            LOGGER.info("Tables created successfully...");
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public void seedData(ArrayList<PetrolStation> petrolStations) {
        dbs.connectMainUrl();

        long start = System.nanoTime();

        LOGGER.info("Insertion starts here...");

        Map<String, Integer> fuelMap = new HashMap<>();

        Map<String,Integer> petrolStationsIdsMap = new HashMap<>();

        for ( int i = 0; i < petrolStations.size(); i++ ) {

            PetrolStation petrolStation = petrolStations.get(i);

            String petrolStationInsert = "INSERT IGNORE INTO petrol_stations (`name`, address, city) VALUES (?,?,?)";
            try (PreparedStatement petrolStationInsertStmt = dbs.getConnection().prepareStatement(petrolStationInsert, Statement.RETURN_GENERATED_KEYS)) {
                petrolStationInsertStmt.setString(1, petrolStation.getName());
                petrolStationInsertStmt.setString(2, petrolStation.getAddress());
                petrolStationInsertStmt.setString(3, petrolStation.getCity());

                petrolStationInsertStmt.executeUpdate();

                ResultSet petrolStationResultSet = petrolStationInsertStmt.getGeneratedKeys();

                if (petrolStationResultSet.next()) {
                    petrolStation.setId(petrolStationResultSet.getInt(1));
                }

                for ( int j = 0; j < petrolStation.getFuels().size(); j++ ) {
                    Fuel fuel = petrolStation.getFuels().get(j);

                    String fuelInsert = "INSERT IGNORE INTO fuels (`type`) VALUES (?)";
                    PreparedStatement fuelInsertStmt = dbs.getConnection().prepareStatement(fuelInsert, Statement.RETURN_GENERATED_KEYS);
                    fuelInsertStmt.setString(1, fuel.getType());

                    if (!fuelMap.containsKey(fuel.getType())) {
                        try {
                            fuelInsertStmt.executeUpdate();
                        } catch (SQLException ex) {
                            LOGGER.severe(ex.getMessage());
                        }
                        ResultSet fuelResultSet = fuelInsertStmt.getGeneratedKeys();
                        if (fuelResultSet.next()) {
                            fuelMap.put(fuel.getType(), fuelResultSet.getInt(1));
                            fuel.setId(fuelResultSet.getInt(1));
                        }
                    } else {
                        fuel.setId(fuelMap.get(fuel.getType()));
                    }

                    if (petrolStationsIdsMap.containsKey(petrolStation.getName()+petrolStation.getAddress()+petrolStation.getCity())) {
                        String petrolStationsFuelsPricesInsert = "INSERT IGNORE INTO " +
                                "petrol_stations_fuels_prices(`fuel_id`,`petrol_station_id`,`price`,`date`)" +
                                "VALUES" +
                                " (" + fuelMap.get(fuel.getType()) + ", " + petrolStationsIdsMap.get(petrolStation.getName()+petrolStation.getAddress()+petrolStation.getCity()) + ", "
                                + fuel.getPrice() + ", " + "'" + petrolStation.getDate() + "');";
                        dbs.getStmt().executeUpdate(petrolStationsFuelsPricesInsert);
                        continue;
                    }
                    petrolStationsIdsMap.put(petrolStation.getName()+petrolStation.getAddress()+petrolStation.getCity(),petrolStation.getId());

                    //inserting data into petrol_stations_fuels_prices table
                    String petrolStationsFuelsPricesInsert = "INSERT IGNORE INTO " +
                            "petrol_stations_fuels_prices(`fuel_id`,`petrol_station_id`,`price`,`date`)" +
                            "VALUES" +
                            " (" + fuel.getId() + ", " + petrolStation.getId() + ", "
                            + fuel.getPrice() + ", " + "'" + petrolStation.getDate() + "');";

                    dbs.getStmt().executeUpdate(petrolStationsFuelsPricesInsert);
                }
            } catch (SQLException e) {
                LOGGER.severe(e.getMessage());
            }
        }
        LOGGER.info("Records are inserted into the database...");
        long end = System.nanoTime();
        LOGGER.info(String.format("Needed time to seed the Data: %d seconds.%n", ((end - start) / 1_000_000_000)));
    }
}

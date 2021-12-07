package module1.fuelreports.business;

import com.beust.jcommander.JCommander;
import module1.fuelreports.business.commands.ConfigCommandHandler;
import module1.fuelreports.business.services.DatabaseConnectionService;
import module1.fuelreports.business.services.DatabaseManagerService;
import module1.fuelreports.business.services.DeserializationService;
import module1.fuelreports.business.services.SFTPDownloaderService;
import module1.fuelreports.business.services.FuelReportService;
import module1.fuelreports.data.entities.PetrolStation;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;


public class FuelReportEngine {

    private final DeserializationService deserializationService;
    private final SFTPDownloaderService sftpDownloaderService;
    private final DatabaseManagerService databaseManagerService;
    private final FuelReportService reportViewController;
    private final String[] args;

    private final String CONFIG = "config";
    private final String PROCESS = "process";
    private final String REPORT = "report";

    public FuelReportEngine(DeserializationService deserializationService, SFTPDownloaderService sftpDownloaderService
            , DatabaseManagerService databaseManagerService, FuelReportService reportViewController, String[] args) {
        this.deserializationService = deserializationService;
        this.sftpDownloaderService = sftpDownloaderService;
        this.databaseManagerService = databaseManagerService;
        this.reportViewController = reportViewController;
        this.args = args;
    }

    public void start() throws JAXBException {
        switch (args[0]) {
            case CONFIG:
                databaseManagerService.createDatabase();
                databaseManagerService.createTables();
                config();
                break;
            case PROCESS:
                sftpDownloaderService.downloadData();
                deserializationService.parseXml();
                databaseManagerService.seedData((ArrayList<PetrolStation>) deserializationService.getPetrolStationList());
                break;
            case REPORT:
                reportViewController.viewFuelReport(args);
                break;
        }
    }


    private void config() {
        ConfigCommandHandler configCommandHandler = new ConfigCommandHandler();

        String[] argumentValues = new String[args.length-1];
        for ( int i = 1; i < args.length; i++ ) {
            argumentValues[i-1] = args[i];
        }

        JCommander configCmd = JCommander.newBuilder()
                .addObject(configCommandHandler)
                .build();

        configCmd.parse(argumentValues);

        //inserting the path into the config table
        DatabaseConnectionService dbs = new DatabaseConnectionService();

        String query = "INSERT INTO `config`(config_type, info) VALUES ('path', '" + configCommandHandler.getPath() + "');";
//        a correct dir: config --data-dir src/main/resources/data/
        try {
            dbs.connectMainUrl();
            dbs.getStmt().executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //creating the directory
        File theDir = new File(configCommandHandler.getPath());
        if (!theDir.exists()){
            boolean b = theDir.mkdirs();
        }
    }
}

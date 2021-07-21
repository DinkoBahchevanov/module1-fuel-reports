package module1.fuelreports.business;

import com.beust.jcommander.JCommander;
import module1.fuelreports.business.services.DatabaseConnectionService;
import module1.fuelreports.business.services.DatabaseManagerService;
import module1.fuelreports.business.services.DeserializationService;
import module1.fuelreports.business.services.SFTPDownloaderService;
import module1.fuelreports.cli.viewController.ReportViewController;
import module1.fuelreports.data.entities.PetrolStation;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;


public class FuelReportEngine {

    private DeserializationService deserializationService;
    private SFTPDownloaderService sftpDownloaderService;
    private DatabaseManagerService databaseManagerService;
    private ReportViewController reportViewController;

    private String[] args;

    public FuelReportEngine(DeserializationService deserializationService, SFTPDownloaderService sftpDownloaderService
            , DatabaseManagerService databaseManagerService, String[] args, ReportViewController reportViewController) {
        this.deserializationService = deserializationService;
        this.sftpDownloaderService = sftpDownloaderService;
        this.databaseManagerService = databaseManagerService;
        this.reportViewController = reportViewController;
        this.args = args;
    }

    public void start(String[] args) throws JAXBException {
        switch (args[0]) {
            case "config":
                databaseManagerService.createDatabase();
                databaseManagerService.createTables();
                config();
                break;
            case "process":
                sftpDownloaderService.downloadData();
                deserializationService.parseXml();
                databaseManagerService.seedData((ArrayList<PetrolStation>) deserializationService.getPetrolStationList());
                break;
            case "report":
                reportViewController.viewFuelReport(args);
                break;
        }
//        deserializationService.parseXml();
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
        try {
            dbs.connectMain();
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

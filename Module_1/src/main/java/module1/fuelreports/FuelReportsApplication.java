package module1.fuelreports;

import module1.fuelreports.business.FuelReportEngine;
import module1.fuelreports.business.services.*;

import module1.fuelreports.business.services.FuelReportService;
import module1.fuelreports.data.repositories.FuelRepository;
import org.modelmapper.ModelMapper;

import javax.xml.bind.JAXBException;

public class FuelReportsApplication {

    public static void main(String[] args) throws JAXBException {
        FuelReportEngine engine =
                new FuelReportEngine(new DeserializationService(new ModelMapper(), new XmlParseServiceImpl()),
                new SFTPDownloaderService(), new DatabaseManagerService(new DatabaseConnectionService()),
                        new FuelReportService(new FuelRepository()), args);
        engine.start();
    }
}
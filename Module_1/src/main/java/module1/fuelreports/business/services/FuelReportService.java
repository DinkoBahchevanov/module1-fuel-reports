package module1.fuelreports.business.services;

import module1.fuelreports.business.services.DatabaseManagerService;
import module1.fuelreports.data.repositories.FuelRepository;

import java.util.logging.Logger;

public class FuelReportService {

    private final FuelRepository fuelRepository;

    private static final Logger LOGGER = Logger.getLogger(DatabaseManagerService.class.getSimpleName());

    public FuelReportService(FuelRepository fuelRepository) {
        this.fuelRepository = fuelRepository;
    }

    public void viewFuelReport(String[] args) {
        LOGGER.info(fuelRepository.getAvgFuelPrice(args));
    }
}

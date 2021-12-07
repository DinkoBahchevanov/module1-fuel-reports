package module1.fuelreports.business.services;

import module1.fuelreports.business.models.PetrolStationRootModel;
import module1.fuelreports.data.entities.Fuel;
import module1.fuelreports.data.entities.PetrolStation;
import org.modelmapper.ModelMapper;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DeserializationService {

    private static final Logger LOGGER = Logger.getLogger(DeserializationService.class.getSimpleName());

    private final ModelMapper modelMapper;
    private final XmlParserService xmlParserService;
    private String DATA_FOLDER_PATH = "";

    private List<PetrolStation> petrolStationList;

    public DeserializationService(ModelMapper mapper, XmlParserService xmlParserService) {
        this.modelMapper = mapper;
        this.xmlParserService = xmlParserService;
        petrolStationList = new ArrayList<>();
    }

    public void parseXml() throws JAXBException {
        String[] pathNames;
        DATA_FOLDER_PATH = getPath();
        if (DATA_FOLDER_PATH.charAt(0) == '/') {
            DATA_FOLDER_PATH = DATA_FOLDER_PATH.substring(1);
        }
        File f = new File(DATA_FOLDER_PATH);
        pathNames = f.list();

        ArrayList<PetrolStationRootModel> petrolStationRootModelList = new ArrayList<>();

        for ( int i = 0; i < pathNames.length; i++ ) {
            Path path = Paths.get(DATA_FOLDER_PATH + pathNames[i]);
            Charset charset = StandardCharsets.UTF_8;
            String content = "";
            try {
                content = Files.readString(path, charset);
                content = content.replaceAll("\\$", "");
                Files.write(path, content.getBytes(charset));
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }


            PetrolStationRootModel petrolStationRootModel = this.xmlParserService.parseXml(PetrolStationRootModel.class, DATA_FOLDER_PATH + pathNames[i]);
            petrolStationRootModelList.add(petrolStationRootModel);

            for ( int k = 0; k < petrolStationRootModel.getPetrolStations().size(); k++ ) {
                petrolStationRootModel.getPetrolStations().get(k).setDate(petrolStationRootModel.getDate());
                PetrolStation petrolStation = this.modelMapper.map(petrolStationRootModel.getPetrolStations().get(k), PetrolStation.class);

                List<Fuel> fuels = new ArrayList<>();
                for ( int j = 0; j < petrolStationRootModel.getPetrolStations().get(k).getFuels().getFuels().size(); j++ ) {
                    fuels.add(modelMapper.map(petrolStationRootModel.getPetrolStations().get(k).getFuels().getFuels().get(j), Fuel.class));
                    fuels.get(fuels.size()-1).setStation(petrolStation);
                }
                petrolStation.setFuels(fuels);
                petrolStationList.add(petrolStation);
            }

        }
    }

    private String getPath() {
        DatabaseConnectionService dbs = new DatabaseConnectionService();

        dbs.connectMainUrl();
        String query = "SELECT `info` FROM config";
        String path = "";
        try {
            ResultSet rs = dbs.getStmt().executeQuery(query);
            rs.next();
            path = rs.getString("info");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "/" + path;
    }

    public List<PetrolStation> getPetrolStationList() {
        return petrolStationList;
    }
}

package module1.fuelreports.business.services;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class XmlParseServiceImpl implements XmlParserService {

    @Override
    @SuppressWarnings("unchecked")
    public  <O> O parseXml(Class<O> object, String filePath) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(object);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (O) unmarshaller.unmarshal(new File(filePath));
    }
}

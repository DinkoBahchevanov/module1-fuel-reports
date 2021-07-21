package module1.fuelreports.business.services;

import javax.xml.bind.JAXBException;

public interface XmlParserService {

    <O> O parseXml(Class<O> object, String filePath) throws JAXBException;
}

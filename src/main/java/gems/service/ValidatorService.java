package gems.service;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

public class ValidatorService {

    /**
     * Перевіряє XML-документ на відповідність XSD-схемі.
     * @param xmlPath шлях до XML-файлу (наприклад, "src/main/resources/sample.xml")
     * @param xsdPath шлях до XSD-схеми (наприклад, "src/main/resources/gems.xsd")
     * @return true, якщо XML валідний; false, якщо є помилки
     */
    public boolean validateXMLSchema(String xmlPath, String xsdPath) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlPath)));
            System.out.println("XML is valid against XSD.");
            return true;
        } catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
        } catch (SAXException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }
        return false;
    }
}

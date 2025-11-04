package gems.service;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

public class TransformationService {

    public void transformXML(String xmlPath, String xsltPath, String outputPath) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Source xslt = new StreamSource(new File(xsltPath));
            Transformer transformer = factory.newTransformer(xslt);

            Source xml = new StreamSource(new File(xmlPath));
            transformer.transform(xml, new StreamResult(new File(outputPath)));

            System.out.println("Transformation complete → " + outputPath);
        } catch (Exception e) {
            System.err.println("Transformation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

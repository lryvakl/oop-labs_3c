package gems.parser;

import lab2.gems.ColorType;
import lab2.gems.GemType;
import lab2.gems.PreciousnessType;
import lab2.gems.VisualParametersType;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StaxGemsParser implements GemsParser {

    @Override
    public List<GemType> parse(String xmlPath) {
        List<GemType> gems = new ArrayList<>();
        GemType currentGem = null;
        VisualParametersType currentVisual = null;
        String currentElement = "";

        try (FileInputStream fis = new FileInputStream(xmlPath)) {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(fis);

            while (reader.hasNext()) {
                int event = reader.next();

                switch (event) {
                    case XMLStreamConstants.START_ELEMENT -> {
                        currentElement = reader.getLocalName();

                        if ("gem".equals(currentElement)) {
                            currentGem = new GemType();
                            currentGem.setId(reader.getAttributeValue(null, "id"));
                        } else if ("visualParameters".equals(currentElement)) {
                            currentVisual = new VisualParametersType();
                        }
                    }

                    case XMLStreamConstants.CHARACTERS -> {
                        String text = reader.getText().trim();
                        if (text.isEmpty() || currentGem == null) continue;

                        switch (currentElement) {
                            case "name" -> currentGem.setName(text);
                            case "preciousness" -> currentGem.setPreciousness(PreciousnessType.fromValue(text));
                            case "origin" -> currentGem.setOrigin(text);
                            case "value" -> currentGem.setValue(new BigDecimal(text));
                            case "color" -> currentVisual.setColor(ColorType.fromValue(text));
                            case "transparency" -> currentVisual.setTransparency(Integer.parseInt(text));
                            case "cutting" -> currentVisual.setCutting(Integer.parseInt(text));
                        }
                    }

                    case XMLStreamConstants.END_ELEMENT -> {
                        String end = reader.getLocalName();

                        if ("visualParameters".equals(end) && currentGem != null) {
                            currentGem.setVisualParameters(currentVisual);
                        } else if ("gem".equals(end) && currentGem != null) {
                            gems.add(currentGem);
                            currentGem = null;
                        }
                        currentElement = "";
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("StAX parsing error: " + e.getMessage());
            e.printStackTrace();
        }

        return gems;
    }
}

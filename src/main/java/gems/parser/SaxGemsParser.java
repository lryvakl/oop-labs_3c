package gems.parser;

import lab2.gems.ColorType;
import lab2.gems.GemType;
import lab2.gems.PreciousnessType;
import lab2.gems.VisualParametersType;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SaxGemsParser extends DefaultHandler implements GemsParser {

    private final List<GemType> gems = new ArrayList<>();
    private GemType currentGem;
    private VisualParametersType currentVisual;
    private StringBuilder currentText = new StringBuilder();

    @Override
    public List<GemType> parse(String xmlPath) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(new File(xmlPath), this);
        } catch (Exception e) {
            System.err.println("SAX parsing error: " + e.getMessage());
            e.printStackTrace();
        }
        return gems;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentText.setLength(0);

        if ("gem".equals(localName) || "gem".equals(qName)) {
            currentGem = new GemType();
            String id = attributes.getValue("id");
            currentGem.setId(id);
        } else if ("visualParameters".equals(localName) || "visualParameters".equals(qName)) {
            currentVisual = new VisualParametersType();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        currentText.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String text = currentText.toString().trim();

        if (currentGem != null) {
            switch (localName) {
                case "name" -> currentGem.setName(text);
                case "preciousness" -> currentGem.setPreciousness(PreciousnessType.fromValue(text));
                case "origin" -> currentGem.setOrigin(text);
                case "value" -> currentGem.setValue(new BigDecimal(text));
                case "color" -> currentVisual.setColor(ColorType.fromValue(text));
                case "transparency" -> currentVisual.setTransparency(Integer.parseInt(text));
                case "cutting" -> currentVisual.setCutting(Integer.parseInt(text));
                case "visualParameters" -> currentGem.setVisualParameters(currentVisual);
                case "gem" -> gems.add(currentGem);
            }
        }
    }
}

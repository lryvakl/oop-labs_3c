package gems.parser;

import lab2.gems.ColorType;
import lab2.gems.GemType;
import lab2.gems.PreciousnessType;
import lab2.gems.VisualParametersType;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DomGemsParser implements GemsParser {

    @Override
    public List<GemType> parse(String xmlPath) {
        List<GemType> gems = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(xmlPath));

            NodeList gemNodes = document.getElementsByTagNameNS("*", "Stone");

            for (int i = 0; i < gemNodes.getLength(); i++) {
                Element gemElement = (Element) gemNodes.item(i);

                GemType gem = new GemType();
                gem.setId(gemElement.getAttribute("id"));
                gem.setName(getText(gemElement, "name"));

                // enums з тексту
                String preciousnessText = getText(gemElement, "preciousness");
                gem.setPreciousness(PreciousnessType.fromValue(preciousnessText));

                gem.setOrigin(getText(gemElement, "origin"));

                // BigDecimal
                String valueText = getText(gemElement, "value");
                gem.setValue(new BigDecimal(valueText));

                // visual parameters
                Element visualEl = (Element) gemElement.getElementsByTagNameNS("*", "visualParameters").item(0);
                if (visualEl != null) {
                    VisualParametersType vp = new VisualParametersType();

                    String colorText = getText(visualEl, "color");
                    vp.setColor(ColorType.fromValue(colorText));

                    vp.setTransparency(Integer.parseInt(getText(visualEl, "transparency")));
                    vp.setCutting(Integer.parseInt(getText(visualEl, "cutting")));

                    gem.setVisualParameters(vp);
                }

                gems.add(gem);
            }

        } catch (Exception e) {
            System.err.println("Error parsing XML with DOM: " + e.getMessage());
            e.printStackTrace();
        }

        return gems;
    }

    private String getText(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagNameNS("*", tagName);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent().trim();
        }
        return "";
    }
}

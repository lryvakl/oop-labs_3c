package gems;

import gems.parser.DomGemsParser;
import gems.parser.SaxGemsParser;
import gems.parser.StaxGemsParser;
import gems.service.GemComparatorService;
import gems.service.TransformationService;
import lab2.gems.GemType;

import java.util.List;

public class App {
    public static void main(String[] args) {
        String xmlPath = "src/main/resources/sample.xml";

        System.out.println("=== DOM Parser ===");
        DomGemsParser domParser = new DomGemsParser();
        List<GemType> domGems = domParser.parse(xmlPath);
        printGems(domGems);

        System.out.println("=== SAX Parser ===");
        SaxGemsParser saxParser = new SaxGemsParser();
        List<GemType> saxGems = saxParser.parse(xmlPath);
        printGems(saxGems);

        System.out.println("=== StAX Parser ===");
        StaxGemsParser staxParser = new StaxGemsParser();
        List<GemType> staxGems = staxParser.parse(xmlPath);
        printGems(staxGems);

        System.out.println("=== Sorted by VALUE ===");
        GemComparatorService sorter = new GemComparatorService();
        List<GemType> gemsForSort = domGems;
        sorter.sort(gemsForSort, GemComparatorService.SortField.VALUE);
        printGems(gemsForSort);

        System.out.println("=== Sorted by TRANSPARENCY ===");
        sorter.sort(gemsForSort, GemComparatorService.SortField.TRANSPARENCY);
        printGems(gemsForSort);

        System.out.println("=== XSLT Transformation ===");
        TransformationService transformer = new TransformationService();
        transformer.transformXML(
                "src/main/resources/sample.xml",
                "src/main/resources/to-html.xslt",
                "target/output/gems.html"
        );

    }

    private static void printGems(List<GemType> gems) {
        gems.forEach(gem -> {
            System.out.println("  Gem: " + gem.getName());
            System.out.println("  Type: " + gem.getPreciousness());
            System.out.println("  Origin: " + gem.getOrigin());
            System.out.println("  Color: " + gem.getVisualParameters().getColor());
            System.out.println("  Transparency: " + gem.getVisualParameters().getTransparency());
            System.out.println("  Cutting: " + gem.getVisualParameters().getCutting());
            System.out.println("  Value: " + gem.getValue());
            System.out.println("----------------------");
        });
    }
}

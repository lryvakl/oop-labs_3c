package gems.parser;

import gems.parser.DomGemsParser;
import lab2.gems.GemType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DomGemsParserTest {

    private final DomGemsParser parser = new DomGemsParser();

    @Test
    void testParseReturnsThreeGems() {
        List<GemType> gems = parser.parse("src/main/resources/sample.xml");
        assertEquals(3, gems.size(), "Expected 3 gems in sample.xml");
    }

    @Test
    void testFirstGemHasExpectedName() {
        List<GemType> gems = parser.parse("src/main/resources/sample.xml");
        assertEquals("Emerald", gems.get(0).getName());
        assertEquals("Colombia", gems.get(0).getOrigin());
    }
}

package gems.parser;

import lab2.gems.GemType;
import java.util.List;

public interface GemsParser {
    List<GemType> parse(String xmlPath);
}

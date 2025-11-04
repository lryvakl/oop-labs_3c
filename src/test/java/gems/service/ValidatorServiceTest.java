package gems.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidatorServiceTest {

    private final ValidatorService validator = new ValidatorService();

    @Test
    void testValidXmlPassesValidation() {
        boolean isValid = validator.validateXMLSchema(
                "src/main/resources/sample.xml",
                "src/main/resources/gems.xsd"
        );
        assertTrue(isValid, "Expected XML to be valid against XSD");
    }

    @Test
    void testInvalidXmlFailsValidation() {
        boolean isValid = validator.validateXMLSchema(
                "src/test/resources/invalid-samples/invalid.xml",
                "src/main/resources/gems.xsd"
        );
        assertFalse(isValid, "Expected invalid XML to fail validation");
    }
}

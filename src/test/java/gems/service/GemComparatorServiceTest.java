package gems.service;

import lab2.gems.GemType;
import lab2.gems.VisualParametersType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GemComparatorServiceTest {

    private final GemComparatorService sorter = new GemComparatorService();
    private List<GemType> gems;

    @BeforeEach
    void setUp() {
        gems = new ArrayList<>();

        GemType g1 = new GemType();
        g1.setName("Emerald");
        g1.setValue(new BigDecimal("45.5"));
        VisualParametersType vp1 = new VisualParametersType();
        vp1.setTransparency(85);
        g1.setVisualParameters(vp1);

        GemType g2 = new GemType();
        g2.setName("Amethyst");
        g2.setValue(new BigDecimal("15.25"));
        VisualParametersType vp2 = new VisualParametersType();
        vp2.setTransparency(70);
        g2.setVisualParameters(vp2);

        gems.add(g1);
        gems.add(g2);
    }

    @Test
    void testSortByValueAscending() {
        sorter.sort(gems, GemComparatorService.SortField.VALUE);
        assertEquals("Amethyst", gems.get(0).getName());
    }

    @Test
    void testSortByTransparencyAscending() {
        sorter.sort(gems, GemComparatorService.SortField.TRANSPARENCY);
        assertEquals(70, gems.get(0).getVisualParameters().getTransparency());
    }
}

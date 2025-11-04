package gems.service;

import lab2.gems.GemType;

import java.util.Comparator;
import java.util.List;

public class GemComparatorService {

    public enum SortField {
        NAME,
        VALUE,
        PRECIOUSNESS,
        COLOR,
        TRANSPARENCY
    }

    /**
     * Sorts the list of gems by the specified field
     */
    public void sort(List<GemType> gems, SortField field) {
        Comparator<GemType> comparator;

        switch (field) {
            case NAME -> comparator = Comparator.comparing(GemType::getName, String.CASE_INSENSITIVE_ORDER);
            case VALUE -> comparator = Comparator.comparing(g -> g.getValue().doubleValue());
            case PRECIOUSNESS -> comparator = Comparator.comparing(g -> g.getPreciousness().value());
            case COLOR -> comparator = Comparator.comparing(g -> g.getVisualParameters().getColor().value());
            case TRANSPARENCY -> comparator = Comparator.comparingInt(g -> g.getVisualParameters().getTransparency());
            default -> throw new IllegalArgumentException("Unsupported sort field: " + field);
        }

        gems.sort(comparator);
    }
}

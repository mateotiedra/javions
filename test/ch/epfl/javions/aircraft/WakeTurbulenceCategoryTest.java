package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WakeTurbulenceCategoryTest {
    @Test
    void wakeTurbulenceCategoryOfWorksWithL() {
        assertEquals(WakeTurbulenceCategory.LIGHT, WakeTurbulenceCategory.of("L"));
    }

    @Test
    void wakeTurbulenceCategoryOfWorksWithM() {
        assertEquals(WakeTurbulenceCategory.MEDIUM, WakeTurbulenceCategory.of("M"));
    }

    @Test
    void wakeTurbulenceCategoryOfWorksWithH() {
        assertEquals(WakeTurbulenceCategory.HEAVY, WakeTurbulenceCategory.of("H"));
    }

    @Test
    void wakeTurbulenceCategoryOfWorksWithEmptyString() {
        assertEquals(WakeTurbulenceCategory.UNKNOWN, WakeTurbulenceCategory.of(""));
    }

    @Test
    void wakeTurbulenceCategoryOfWorksWithOther() {
        assertEquals(WakeTurbulenceCategory.UNKNOWN, WakeTurbulenceCategory.of("DDF"));
    }
}

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BandSizeCalculatorTest {

    @Test
    void approximateR() {
        assertTrue(BandSizeCalculator.approximateR(200,0.9)==21);
        assertTrue(BandSizeCalculator.bestFactorR(200,0.9)==20);
    }
}
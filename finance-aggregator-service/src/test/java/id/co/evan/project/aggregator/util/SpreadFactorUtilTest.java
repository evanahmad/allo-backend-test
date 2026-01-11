package id.co.evan.project.aggregator.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SpreadFactorUtilTest {

    private final SpreadFactorUtil util = new SpreadFactorUtil();

    @Test
    @DisplayName("Success - Calculate Spread Factor based on username")
    void shouldCalculateCorrectSpreadFactor() {
        String username = "evanahmad";
        double expected = 0.00933;

        double actual = util.calculateSpreadFactor(username);

        assertEquals(expected, actual, 0.000001);
    }

    @Test
    @DisplayName("Success - Calculate Buy Spread")
    void shouldCalculateCorrectBuySpread() {
        double rateUsd = 0.000063;
        double spreadFactor = 0.00933;
        double expected = (1.0 / rateUsd) * (1.0 + spreadFactor);

        double actual = util.calculateBuySpread(rateUsd, spreadFactor);

        assertEquals(expected, actual, 0.000001);
    }
}
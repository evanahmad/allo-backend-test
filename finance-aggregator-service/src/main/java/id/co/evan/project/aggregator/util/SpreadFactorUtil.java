package id.co.evan.project.aggregator.util;

import org.springframework.stereotype.Component;

@Component
public class SpreadFactorUtil {

    public double calculateSpreadFactor(String username) {
        if (username == null || username.isBlank()) return 0.0;

        return (username.toLowerCase().chars().sum() % 1000) / 100000.0;
    }

    public double calculateBuySpread(double rateUsd, double spreadFactor) {
        return (1.0 / rateUsd) * (1.0 + spreadFactor);
    }
}
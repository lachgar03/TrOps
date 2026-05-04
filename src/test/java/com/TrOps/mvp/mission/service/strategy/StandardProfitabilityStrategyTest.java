package com.TrOps.mvp.mission.service.strategy;

import com.TrOps.mvp.mission.model.ProfitabilityScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class StandardProfitabilityStrategyTest {

    private StandardProfitabilityStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new StandardProfitabilityStrategy();
    }

    @Test
    void shouldCalculateHighlyProfitableMission() {
        BigDecimal revenues = new BigDecimal("1000.00");
        BigDecimal costs = new BigDecimal("500.00");

        ProfitabilityResult result = strategy.calculate(revenues, costs);

        assertThat(result.profit()).isEqualByComparingTo("500.00");
        assertThat(result.margin()).isEqualByComparingTo("0.50"); // 50%
        assertThat(result.score()).isEqualTo(ProfitabilityScore.PROFITABLE);
    }

    @Test
    void shouldCalculateMediumProfitableMission() {
        BigDecimal revenues = new BigDecimal("1000.00");
        BigDecimal costs = new BigDecimal("960.00");

        ProfitabilityResult result = strategy.calculate(revenues, costs);

        assertThat(result.profit()).isEqualByComparingTo("40.00");
        assertThat(result.margin()).isEqualByComparingTo("0.04"); // 4% (less than 5%)
        assertThat(result.score()).isEqualTo(ProfitabilityScore.MEDIUM);
    }

    @Test
    void shouldCalculateLossMission() {
        BigDecimal revenues = new BigDecimal("1000.00");
        BigDecimal costs = new BigDecimal("1200.00");

        ProfitabilityResult result = strategy.calculate(revenues, costs);

        assertThat(result.profit()).isEqualByComparingTo("-200.00");
        assertThat(result.margin()).isEqualByComparingTo("-0.20"); // -20%
        assertThat(result.score()).isEqualTo(ProfitabilityScore.LOSS);
    }

    @Test
    void shouldHandleZeroRevenuesSafely() {
        BigDecimal revenues = BigDecimal.ZERO;
        BigDecimal costs = new BigDecimal("100.00");

        ProfitabilityResult result = strategy.calculate(revenues, costs);

        assertThat(result.profit()).isEqualByComparingTo("-100.00");
        assertThat(result.margin()).isEqualByComparingTo("-1.00"); // Technically -100%
        assertThat(result.score()).isEqualTo(ProfitabilityScore.LOSS);
    }
}

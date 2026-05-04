package com.TrOps.mvp.mission.service.strategy;

import com.TrOps.mvp.mission.model.ProfitabilityScore;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class StandardProfitabilityStrategy implements ProfitabilityStrategy {

    @Override
    public ProfitabilityResult calculate(BigDecimal revenues, BigDecimal costs) {
        if (revenues == null) {
            revenues = BigDecimal.ZERO;
        }
        if (costs == null) {
            costs = BigDecimal.ZERO;
        }

        BigDecimal profit = revenues.subtract(costs);
        
        BigDecimal margin = BigDecimal.ZERO;
        if (revenues.compareTo(BigDecimal.ZERO) > 0) {
            margin = profit.divide(revenues, 4, RoundingMode.HALF_UP);
        } else if (revenues.compareTo(BigDecimal.ZERO) == 0 && profit.compareTo(BigDecimal.ZERO) < 0) {
            // S'il n'y a pas de revenus mais qu'il y a des coûts, la marge est techniquement -100% ou on la laisse à 0
            // Pour plus de sens, on peut la mettre à -1.0 (-100%)
            margin = BigDecimal.valueOf(-1.0);
        }

        ProfitabilityScore score;
        if (profit.compareTo(BigDecimal.ZERO) > 0) {
            // Bonus : Utilisation de la marge pour affiner le "MEDIUM"
            // Par exemple, si le profit est positif mais la marge est inférieure à 5% (0.05), c'est MEDIUM
            if (margin.compareTo(new BigDecimal("0.05")) < 0) {
                score = ProfitabilityScore.MEDIUM;
            } else {
                score = ProfitabilityScore.PROFITABLE;
            }
        } else if (profit.compareTo(BigDecimal.ZERO) == 0) {
            score = ProfitabilityScore.MEDIUM;
        } else {
            score = ProfitabilityScore.LOSS;
        }

        return new ProfitabilityResult(profit, margin, score);
    }
}

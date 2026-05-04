package com.TrOps.mvp.mission.service.strategy;

import com.TrOps.mvp.mission.model.ProfitabilityScore;
import java.math.BigDecimal;

public record ProfitabilityResult(
        BigDecimal profit,
        BigDecimal margin,
        ProfitabilityScore score
) {}

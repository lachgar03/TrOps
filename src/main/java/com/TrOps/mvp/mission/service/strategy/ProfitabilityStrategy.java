package com.TrOps.mvp.mission.service.strategy;

import java.math.BigDecimal;

public interface ProfitabilityStrategy {
    ProfitabilityResult calculate(BigDecimal revenues, BigDecimal costs);
}

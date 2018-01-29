package at.ac.tuwien.sepm.fridget.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalHelpers {

    private BigDecimalHelpers() {
        // Avoid instantiation of BigDecimalHelpers
    }

    public static BigDecimal divideMoney(BigDecimal a, int b) {
        return divideMoney(a, new BigDecimal(b));
    }

    public static BigDecimal divideMoney(BigDecimal a, BigDecimal b) {
        return divideMoney(a, b, RoundingMode.FLOOR);
    }

    public static BigDecimal divideMoney(BigDecimal a, BigDecimal b, RoundingMode roundingMode) {
        return a.divide(b, 2, roundingMode);
    }

}

package at.ac.tuwien.sepm.fridget.common.services;

import java.math.BigDecimal;

public interface OCRService {

    BigDecimal extractSum(byte[] imageData);

}

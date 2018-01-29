package at.ac.tuwien.sepm.fridget.client.services;

import at.ac.tuwien.sepm.fridget.client.util.RestClient;
import at.ac.tuwien.sepm.fridget.common.services.OCRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ClientOCRService implements OCRService {

    @Autowired
    RestClient restClient;

    @Override
    public BigDecimal extractSum(byte[] imageData) {
        return restClient.postForObject("/ocr/extract-sum", imageData, BigDecimal.class);
    }

}

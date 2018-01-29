package at.ac.tuwien.sepm.fridget.server.controllers;

import at.ac.tuwien.sepm.fridget.common.services.OCRService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;

@RestController
@RequestMapping("/ocr")
public class OCRController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private OCRService ocrService;

    @PostMapping(value = "/extract-sum")
    public BigDecimal extractSum(@RequestBody byte[] imageData) {
        LOG.info("called inviteUserToGroup");
        return ocrService.extractSum(imageData);
    }

}

package at.ac.tuwien.sepm.fridget.server.services;

import at.ac.tuwien.sepm.fridget.common.services.OCRService;
import at.ac.tuwien.sepm.fridget.server.util.ResourceGoogleCredentialsProvider;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service("ocrService")
public class ServerOCRService implements OCRService {

    private static final String[] CURRENCY_PATTERNS = new String[] {
        "usd", "\\$",
        "eur", "€",
        "gbp", "£",
    };

    private static final String[] SUM_LABEL_PATTERNS = new String[] {
        "summe",
        "sum",
        "total",
        "gesamt",
        "gesamtsumme",
    };

    @Override
    public BigDecimal extractSum(byte[] imageData) {
        // Instantiates a client
        ImageAnnotatorSettings.Builder imageAnnotatorSettingsBuilder = ImageAnnotatorSettings.newBuilder();
        imageAnnotatorSettingsBuilder.setCredentialsProvider(new ResourceGoogleCredentialsProvider("Fridget-79a1c5d62033.json"));
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create(imageAnnotatorSettingsBuilder.build())) {
            // Reads the image file into memory
            ByteString imgBytes = ByteString.copyFrom(imageData);

            // Builds the image annotation request
            List<AnnotateImageRequest> requests = new ArrayList<>();
            Image img = Image.newBuilder().setContent(imgBytes).build();

            Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();
            requests.add(request);

            // Performs label detection on the image file
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            AnnotateImageResponse res = response.getResponses(0);
            if (res.hasError()) {
                throw new Exception("Error: " + res.getError().getMessage());
            } else {
                String text = res.getTextAnnotations(0).getDescription().replace("\n", "\t");

                String keywordPattern = Arrays.stream(SUM_LABEL_PATTERNS).map(s -> {
                    StringBuilder newString = new StringBuilder();
                    for (int i = 0; i < s.length(); i++) {
                        if (i > 0) newString.append("\\s*");
                        newString.append(s.charAt(i));
                    }
                    return newString.toString();
                }).collect(Collectors.joining("|"));
                String currencyPattern = Arrays.stream(CURRENCY_PATTERNS).collect(Collectors.joining("|"));
                String numberPattern = "(?:" + currencyPattern + ")?\\s*(\\d+([.,]\\d+)?)\\s*(?:" + currencyPattern + ")?";
                Pattern p1 = Pattern.compile("\\b(?:" + keywordPattern + ")\\s+" + numberPattern + "\\b", Pattern.CASE_INSENSITIVE);
                Pattern p2 = Pattern.compile("\\b" + numberPattern + "\\s+(?:" + keywordPattern + ")\\b", Pattern.CASE_INSENSITIVE);
                Matcher m;
                if ((m = p1.matcher(text)).find()) {
                    return new BigDecimal(m.group(1).replace(",", "."));
                } else if ((m = p2.matcher(text)).find()) {
                    return new BigDecimal(m.group(1).replace(",", "."));
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

}

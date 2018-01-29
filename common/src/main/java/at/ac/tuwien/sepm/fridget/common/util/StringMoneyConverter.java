package at.ac.tuwien.sepm.fridget.common.util;

import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

/**
 * Used to convert text input to big decimals in an elegant fashion
 * Currently locale is set to english, maybe make it accept more
 */
public class StringMoneyConverter extends StringConverter<BigDecimal> {

    DecimalFormat formatter;

    public StringMoneyConverter() {
        formatter = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
        formatter.setParseBigDecimal(true);
    }

    @Override
    public String toString(BigDecimal value) {
        if (value == null)
            return "0";
        return formatter.format(value);
    }

    @Override
    public BigDecimal fromString(String text) {
        if (text == null || text.isEmpty() || text.equals("null"))
            return new BigDecimal(0);

        try {
            return (BigDecimal) formatter.parse(text);
        } catch (ParseException e) {
            return null;
        }
    }
}

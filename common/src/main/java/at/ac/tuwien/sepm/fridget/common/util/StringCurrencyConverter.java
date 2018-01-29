package at.ac.tuwien.sepm.fridget.common.util;

import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

public class StringCurrencyConverter extends StringConverter<Double> {

    DecimalFormat formatter;

    public StringCurrencyConverter() {
        formatter = new DecimalFormat("0.0000", new DecimalFormatSymbols(Locale.ENGLISH));
    }

    @Override
    public String toString(Double value) {
        if (value == null)
            return "0";
        return formatter.format(value);
    }

    @Override
    public Double fromString(String text) {
        if (text == null || text.isEmpty() || text.equals("null"))
            return 0d;

        try {
            return formatter.parse(text).doubleValue();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

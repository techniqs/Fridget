package at.ac.tuwien.sepm.fridget.common.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum Currency {
    // Euro is used as base for all exchange rate calculations
    EUR(1, "€", "Euro", 1.0d, NumberFormat.getCurrencyInstance(Locale.GERMANY)),
    USD(2, "$", "US Dollar", 0.831241376d, NumberFormat.getCurrencyInstance(Locale.US)),
    GBP(3, "£", "Pound", 1.12794467d, NumberFormat.getCurrencyInstance(Locale.ENGLISH)),
    BTC(4, "Ƀ", "Bitcoin", 13680.05d, NumberFormat.getCurrencyInstance(Locale.ENGLISH));

    private static final Map<Integer, Currency> map = new HashMap<>();

    static {
        for (Currency currency : Currency.values()) {
            map.put(currency.getId(), currency);
        }
    }

    private final int id;
    private final String symbol;
    private final String name;
    private final NumberFormat numberFormat;
    private final double exchangeRate;

    Currency(int id, String symbol, String name, double exchangeRate, NumberFormat numberFormat) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.exchangeRate = exchangeRate;
        this.numberFormat = numberFormat;
    }

    public static Currency fromId(int id) {
        return map.get(id);
    }

    public int getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public String format(BigDecimal value) {
        return this.numberFormat.format(value);
    }

    /**
     * Calculates an exchange rate for any given other currency
     * The base currency used for calculations is EURO, see enum definitions of Currency
     *
     * @param other the other currency
     * @return the exchange rate
     */
    public double calculateExchangeRate(Currency other) {
        double euroInCurrent = 1 / getExchangeRate();
        double euroInOther = 1 / other.getExchangeRate();
        return euroInCurrent / euroInOther;
    }

    /**
     * Helper for formatting currency for display
     *
     * @return String in format "Euro (€)" or "US Dollar ($)"
     */
    public String displayFormat() {
        return getName() + " (" + getSymbol() + ")";
    }

}

package at.ac.tuwien.sepm.fridget.common.util;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

/**
 * Source: https://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
 */
public class RandomString {

    public static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWERCASE_LETTERS = UPPERCASE_LETTERS.toLowerCase(Locale.ROOT);
    public static final String DIGITS = "0123456789";
    public static final String ALPHANUMERIC = UPPERCASE_LETTERS + LOWERCASE_LETTERS + DIGITS;

    private final Random random;
    private final char[] symbols;
    private final char[] buf;

    public RandomString(int length, Random random, String symbols) {
        if (length < 1) throw new IllegalArgumentException();
        if (symbols.length() < 2) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    /**
     * Create an alphanumeric string generator.
     */
    public RandomString(int length, Random random) {
        this(length, random, ALPHANUMERIC);
    }

    /**
     * Create an alphanumeric strings from a secure generator.
     */
    public RandomString(int length) {
        this(length, new SecureRandom());
    }

    /**
     * Create session identifiers.
     */
    public RandomString() {
        this(21);
    }

    /**
     * Generate a random string.
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

}

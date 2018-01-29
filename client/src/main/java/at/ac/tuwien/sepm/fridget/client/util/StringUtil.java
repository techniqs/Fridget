package at.ac.tuwien.sepm.fridget.client.util;

public class StringUtil {

    // Based on https://stackoverflow.com/questions/3597550/ideal-method-to-truncate-a-string-with-ellipsis
    private final static String NON_THIN = "[^iIl1\\.,']";

    private static int textWidth(String str) {
        return (str.length() - str.replaceAll(NON_THIN, "").length() / 2);
    }

    public static String truncateWithEllipse(String text, int max) {

        if (textWidth(text) <= max)
            return text;

        // Start by chopping off at the word before max
        int end = text.lastIndexOf(' ', max - 3);

        // Just one long word. Chop it off.
        if (end == -1)
            return text.substring(0, max - 3) + "...";

        // Step
        int newEnd = end;
        do {
            end = newEnd;
            newEnd = text.indexOf(' ', end + 1);

            // No more spaces.
            if (newEnd == -1)
                newEnd = text.length();

        } while (textWidth(text.substring(0, newEnd) + "...") < max);

        return text.substring(0, end) + "...";
    }
}

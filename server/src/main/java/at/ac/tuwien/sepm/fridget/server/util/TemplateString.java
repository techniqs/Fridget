package at.ac.tuwien.sepm.fridget.server.util;

import javafx.util.Pair;

import java.util.Map;

public class TemplateString {
    @SafeVarargs
    public static String create(String base, Pair<String, String>... replacements) {
        for (Pair<String, String> replacement : replacements) {
            base = TemplateString.replace(base, replacement.getKey(), replacement.getValue());
        }
        return base;
    }

    public static String create(String base, Map<String, String> replacements) {
        for (Map.Entry<String, String> replacement : replacements.entrySet()) {
            base = TemplateString.replace(base, replacement.getKey(), replacement.getValue());
        }
        return base;
    }

    private static String replace(String base, String key, String value) {
        return base.replace("{{" + key + "}}", value);
    }
}


package at.ac.tuwien.sepm.fridget.server.util;

import at.ac.tuwien.sepm.fridget.common.entities.User;

import java.util.HashMap;
import java.util.Map;

public class EmailUtils {
    public static Map<String, String> getUserMap(User user) {
        Map<String, String> replacementMap = new HashMap<>();
        replacementMap.put("name", user.getName());
        replacementMap.put("email", user.getEmail());
        return replacementMap;
    }
}

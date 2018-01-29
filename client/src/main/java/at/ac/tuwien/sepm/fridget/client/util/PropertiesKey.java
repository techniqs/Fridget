package at.ac.tuwien.sepm.fridget.client.util;

public enum PropertiesKey {
    CREDENTIALS("credentials");

    private final String name;

    PropertiesKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

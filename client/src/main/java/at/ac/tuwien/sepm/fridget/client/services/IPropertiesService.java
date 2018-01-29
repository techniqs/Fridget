package at.ac.tuwien.sepm.fridget.client.services;

import at.ac.tuwien.sepm.fridget.client.util.PropertiesKey;

public interface IPropertiesService {

    void setProperty(PropertiesKey key, String value);

    String getProperty(PropertiesKey key);

    void removeProperty(PropertiesKey key);

}

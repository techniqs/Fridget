package at.ac.tuwien.sepm.fridget.client.services;

import at.ac.tuwien.sepm.fridget.client.util.PropertiesKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Properties;

@Service("propertiesService")
public class PropertiesService implements IPropertiesService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String FILENAME = "fridget.properties";
    private Properties properties = null;

    @Override
    public void setProperty(PropertiesKey key, String value) {
        this.ensureProperties();
        properties.setProperty(key.getName(), value);
        this.writeToFile();
    }

    @Override
    public String getProperty(PropertiesKey key) {
        this.ensureProperties();
        return properties.getProperty(key.getName());
    }

    @Override
    public void removeProperty(PropertiesKey key) {
        this.ensureProperties();
        properties.remove(key.getName());
        this.writeToFile();
    }


    private File getFile() {
        return new File(System.getProperty("user.home"), FILENAME);
    }

    private void ensureProperties() {
        if (properties == null) {
            this.readFromFile();
        }
    }

    private void writeToFile() {
        try (FileOutputStream fos = new FileOutputStream(getFile())) {
            properties.store(fos, null);
        } catch (IOException e) {
            LOG.error("Error while trying to save the properties file", e);
        }
    }

    private void readFromFile() {
        properties = new Properties();
        if (getFile().exists()) {
            try (FileInputStream fis = new FileInputStream(getFile())) {
                properties.load(fis);
            } catch (IOException e) {
                LOG.error("Error while trying to read the properties file", e);
            }
        }
    }

}

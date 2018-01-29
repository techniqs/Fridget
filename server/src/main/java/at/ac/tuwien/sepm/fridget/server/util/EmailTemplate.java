package at.ac.tuwien.sepm.fridget.server.util;

import at.ac.tuwien.sepm.fridget.common.exception.EmailException;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class EmailTemplate {

    // Singleton Instance
    private static EmailTemplate instance;

    // Constants
    private static final String TEMPLATE_PATH = "email-templates/base.html";

    // Internal Instance Variables
    private String content;

    // Internal Constructor
    private EmailTemplate() {
        try {
            this.content = loadTemplate(TEMPLATE_PATH);
        } catch (EmailException e) {
            this.content = "ERROR LOADING EMAIL TEMPLATE";
            System.err.println("ERROR LOADING EMAIL TEMPLATE - Please check the existence of the specified template");
        }
    }

    // Instance Retrieval
    public static EmailTemplate getInstance() {
        if (EmailTemplate.instance == null) {
            EmailTemplate.instance = new EmailTemplate();
        }
        return EmailTemplate.instance;
    }

    /**
     * Get filled template with the main field being {{title}} and {{content}}
     * @param title Title of the email
     * @param content Strings to use for the replacement
     * @return Filled Email Template
     */
    public String fillTemplate(String title, String content) {
        return TemplateString.create(this.content, new Pair<>("title", title), new Pair<>("content", content));
    }

    /**
     * Load template from specified path in resources
     * @param templatePath Path to template
     * @return The content of the template
     * @throws EmailException If template could not be found or loaded
     */
    private String loadTemplate(String templatePath) throws EmailException {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(templatePath).getFile());
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new EmailException("Error on loading email template", e);
        } catch (NullPointerException e) {
            throw new EmailException("Error on resolving template path", e);
        }
    }

    @Override
    public String toString() {
        return this.content;
    }
}

package at.ac.tuwien.sepm.fridget.client.application;

import at.ac.tuwien.sepm.fridget.client.configuration.ClientApplicationConfiguration;
import at.ac.tuwien.sepm.fridget.client.services.IAuthenticationService;
import at.ac.tuwien.sepm.fridget.client.services.UIService;
import at.ac.tuwien.sepm.fridget.client.springfx.SpringFxApplication;
import at.ac.tuwien.sepm.fridget.client.util.FridgetScene;
import at.ac.tuwien.sepm.fridget.client.util.SceneManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.invoke.MethodHandles;

@SpringBootApplication
@ComponentScan(basePackages = {
    "at.ac.tuwien.sepm.fridget.client.services",
    "at.ac.tuwien.sepm.fridget.client.controllers",
    "at.ac.tuwien.sepm.fridget.client.springfx",
    "at.ac.tuwien.sepm.fridget.client.util",
})
@Import(ClientApplicationConfiguration.class)
public class FridgetClient extends SpringFxApplication implements SceneManager {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Autowired
    IAuthenticationService authenticationService;

    @Autowired
    UIService uiService;


    private Stage primaryStage = null;

    public static void main(String[] args) {
        LOG.debug("Application starting with arguments={}", (Object) args);
        Application.launch(FridgetClient.class, args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        uiService.setPrimaryStage(primaryStage);
        uiService.setSceneManager(this);

        // setup application
        primaryStage.setTitle("Fridget");
        primaryStage.setOnCloseRequest(event -> {
            LOG.debug("Application shutdown");
        });

        if (authenticationService.getStoredCredentials() != null && authenticationService.getUser() != null) {
            this.loadScene(FridgetScene.APPLICATION);
        } else {
            this.loadScene(FridgetScene.LOGIN);
        }

        // show application
        primaryStage.show();
        primaryStage.toFront();
        LOG.debug("Application startup complete");
    }

    @Override
    public void loadScene(FridgetScene scene) {
        switch (scene) {
            case LOGIN:
                primaryStage.hide();
                primaryStage.setMinWidth(300);
                primaryStage.setMinHeight(400);
                primaryStage.setWidth(300);
                primaryStage.setHeight(400);
                primaryStage.centerOnScreen();
                primaryStage.setScene(new Scene(loadParent("/fxml/login.fxml")));
                primaryStage.show();
                break;
            case REGISTER:
                primaryStage.hide();
                primaryStage.setMinWidth(300);
                primaryStage.setMinHeight(400);
                primaryStage.setWidth(300);
                primaryStage.setHeight(400);
                primaryStage.centerOnScreen();
                primaryStage.setScene(new Scene(loadParent("/fxml/register.fxml")));
                primaryStage.show();
                break;
            case FORGOT_PASSWORD:
                primaryStage.hide();
                primaryStage.setMinWidth(300);
                primaryStage.setMinHeight(500);
                primaryStage.setWidth(300);
                primaryStage.setHeight(500);
                primaryStage.centerOnScreen();
                primaryStage.setScene(new Scene(loadParent("/fxml/forgotPassword.fxml")));
                primaryStage.show();
                break;
            case APPLICATION:
                primaryStage.hide();
                primaryStage.setMinWidth(800);
                primaryStage.setMinHeight(600);
                primaryStage.setWidth(1366);
                primaryStage.setHeight(768);
                primaryStage.centerOnScreen();
                primaryStage.setScene(new Scene(loadParent("/fxml/application.fxml")));
                primaryStage.show();
                break;
        }
    }

}

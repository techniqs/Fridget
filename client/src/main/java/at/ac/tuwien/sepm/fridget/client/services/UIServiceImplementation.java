package at.ac.tuwien.sepm.fridget.client.services;

import at.ac.tuwien.sepm.fridget.client.controllers.ApplicationController;
import at.ac.tuwien.sepm.fridget.client.controllers.LoginController;
import at.ac.tuwien.sepm.fridget.client.util.FridgetScene;
import at.ac.tuwien.sepm.fridget.client.util.SceneManager;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("uiService")
public class UIServiceImplementation implements UIService {

    private SceneManager sceneManager;
    private Stage primaryStage;

    @Autowired
    private ApplicationController applicationController;

    @Autowired
    private LoginController loginController;


    @Override
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    @Override
    public void handleLoginCompleted() {
        sceneManager.loadScene(FridgetScene.APPLICATION);
    }

    @Override
    public void handleLogout() {
        sceneManager.loadScene(FridgetScene.LOGIN);
    }

    @Override
    public void goToScene(FridgetScene scene) {
        sceneManager.loadScene(scene);
    }

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public void handleUserUpdated(User user) {
        this.applicationController.handleUserUpdated(user);
    }

}

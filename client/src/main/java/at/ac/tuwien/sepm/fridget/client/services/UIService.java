package at.ac.tuwien.sepm.fridget.client.services;

import at.ac.tuwien.sepm.fridget.client.util.FridgetScene;
import at.ac.tuwien.sepm.fridget.client.util.SceneManager;
import at.ac.tuwien.sepm.fridget.common.entities.User;
import javafx.stage.Stage;

public interface UIService {

    Stage getPrimaryStage();

    void setPrimaryStage(Stage stage);

    void handleLoginCompleted();

    void handleLogout();

    void goToScene(FridgetScene scene);

    void setSceneManager(SceneManager sceneManager);

    void handleUserUpdated(User user);

}

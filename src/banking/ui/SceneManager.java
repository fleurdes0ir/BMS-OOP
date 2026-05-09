package banking.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * SceneManager — Singleton.
 * Mengelola navigasi antar screen tanpa membuat Stage baru.
 * Semua perpindahan screen cukup panggil SceneManager.getInstance().navigate(...)
 */
public class SceneManager {

    private static SceneManager instance;
    private Stage primaryStage;
    private Scene mainScene;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) instance = new SceneManager();
        return instance;
    }

    public void init(Stage stage) {
        this.primaryStage = stage;
    }

    public Stage getStage() { return primaryStage; }

    public void setMainScene(Scene scene) {
        this.mainScene = scene;
    }

    public Scene getMainScene() { return mainScene; }
}

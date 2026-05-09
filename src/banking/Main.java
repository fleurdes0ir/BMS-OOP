package banking;

import banking.ui.SceneManager;
import banking.ui.ThemeManager;
import banking.ui.screens.LoginScreen;
import banking.util.DataSeeder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // 1. Seed data CSV jika belum ada
        DataSeeder.seed();

        // 2. Init SceneManager
        SceneManager.getInstance().init(stage);

        // 3. Buat LoginScreen sebagai scene pertama
        LoginScreen loginScreen = new LoginScreen();
        Scene scene = new Scene(loginScreen.getView(), 1100, 680);

        // 4. Load CSS
        String css = getClass().getResource("/banking/ui/styles/theme.css").toExternalForm();
        scene.getStylesheets().add(css);

        // 5. Apply tema awal (light)
        ThemeManager.getInstance().setScene(scene);
        SceneManager.getInstance().setMainScene(scene);

        // 6. Konfigurasi stage
        stage.setTitle("NusaBank — Sistem Manajemen Perbankan");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

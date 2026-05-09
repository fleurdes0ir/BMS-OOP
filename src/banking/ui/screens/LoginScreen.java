package banking.ui.screens;

import banking.model.User;
import banking.service.AuthService;
import banking.ui.SceneManager;
import banking.ui.ThemeManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class LoginScreen {

    private VBox view;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorLabel;

    public LoginScreen() {
        buildUI();
    }

    private void buildUI() {
        // view adalah root yang langsung diset ke scene
        // ThemeManager.applyTheme() memanggil scene.getRoot().getStyleClass()
        // maka theme class harus ada di sini
        view = new VBox();
        view.getStyleClass().addAll("root-pane",
            ThemeManager.getInstance().isDark() ? "theme-dark" : "theme-light");

        // StackPane sebagai full-screen layer
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(root, Priority.ALWAYS);

        // Theme toggle pojok kanan atas
        Button themeToggle = new Button(
            ThemeManager.getInstance().isDark() ? "\u2600\uFE0F Light Mode" : "\uD83C\uDF19 Dark Mode");
        themeToggle.getStyleClass().add("theme-toggle");
        themeToggle.setOnAction(e -> {
            ThemeManager.getInstance().toggle();
            themeToggle.setText(ThemeManager.getInstance().isDark()
                ? "\u2600\uFE0F Light Mode" : "\uD83C\uDF19 Dark Mode");
        });
        StackPane.setAlignment(themeToggle, Pos.TOP_RIGHT);
        StackPane.setMargin(themeToggle, new Insets(16));

        // Login card
        VBox card = new VBox(16);
        card.getStyleClass().add("login-card");
        card.setMaxWidth(400);
        card.setAlignment(Pos.TOP_LEFT);

        // Header
        Label appName = new Label("NusaBank");
        appName.getStyleClass().add("login-title");
        Label subtitle = new Label("Sistem Manajemen Perbankan");
        subtitle.getStyleClass().add("login-subtitle");
        VBox header = new VBox(4, appName, subtitle);
        header.setPadding(new Insets(0, 0, 8, 0));

        Separator sep = new Separator();

        // Username
        Label usernameLabel = new Label("Username");
        usernameLabel.getStyleClass().add("field-label");
        usernameField = new TextField();
        usernameField.getStyleClass().add("text-field-nb");
        usernameField.setPromptText("Masukkan username");
        usernameField.setPrefWidth(304);
        VBox usernameBox = new VBox(4, usernameLabel, usernameField);

        // Password
        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("field-label");
        passwordField = new PasswordField();
        passwordField.getStyleClass().add("password-field-nb");
        passwordField.setPromptText("Masukkan password");
        VBox passwordBox = new VBox(4, passwordLabel, passwordField);

        // Error label
        errorLabel = new Label();
        errorLabel.getStyleClass().add("badge-danger");
        errorLabel.setVisible(false);
        errorLabel.setWrapText(true);

        // Tombol login
        Button loginBtn = new Button("Masuk");
        loginBtn.getStyleClass().add("btn-primary");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setOnAction(e -> handleLogin());
        passwordField.setOnAction(e -> handleLogin());
        usernameField.setOnAction(e -> passwordField.requestFocus());

        // Hint
        Label hint = new Label("Demo: admin / admin123   atau   budi.santoso / nasabah123");
        hint.getStyleClass().add("field-label");
        hint.setWrapText(true);

        card.getChildren().addAll(header, sep, usernameBox, passwordBox,
                                   errorLabel, loginBtn, hint);

        root.getChildren().addAll(card, themeToggle);
        StackPane.setAlignment(card, Pos.CENTER);

        view.getChildren().add(root);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username dan password tidak boleh kosong.");
            return;
        }

        try {
            User user = AuthService.getInstance().login(username, password);
            navigateToDashboard(user);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            passwordField.clear();
        } catch (Exception e) {
            showError("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private void navigateToDashboard(User user) {
        try {
            MainScreen mainScreen = new MainScreen(user);
            SceneManager.getInstance().getMainScene()
                .setRoot(mainScreen.getView());
        } catch (Exception e) {
            showError("Gagal memuat dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }

    public VBox getView() { return view; }
}

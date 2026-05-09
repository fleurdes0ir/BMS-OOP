package banking.ui;

import javafx.scene.Scene;

/**
 * ThemeManager — Singleton.
 * Mengelola state tema (dark/light) dan meng-apply ke scene aktif.
 *
 * Cara kerja:
 * - Theme class ("theme-dark" / "theme-light") ditambahkan ke scene.getRoot()
 * - CSS selector .theme-dark dan .theme-light mendefinisikan semua warna
 * - Toggle hanya ganti class, tidak reload CSS
 */
public class ThemeManager {

    private static ThemeManager instance;
    private boolean isDark = false;
    private Scene currentScene;

    private ThemeManager() {}

    public static ThemeManager getInstance() {
        if (instance == null) instance = new ThemeManager();
        return instance;
    }

    /** Dipanggil setiap kali scene atau root berubah */
    public void setScene(Scene scene) {
        this.currentScene = scene;
        applyTheme();
    }

    /** Dipanggil setiap kali root node diganti (navigasi login → dashboard) */
    public void applyToCurrentScene() {
        applyTheme();
    }

    public void toggle() {
        isDark = !isDark;
        applyTheme();
    }

    public boolean isDark() { return isDark; }

    private void applyTheme() {
        if (currentScene == null) return;
        var root = currentScene.getRoot();
        if (root == null) return;
        root.getStyleClass().removeAll("theme-light", "theme-dark");
        root.getStyleClass().add(isDark ? "theme-dark" : "theme-light");
    }
}

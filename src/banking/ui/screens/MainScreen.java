package banking.ui.screens;

import banking.model.User;
import banking.service.AuthService;
import banking.ui.SceneManager;
import banking.ui.ThemeManager;
import banking.ui.panels.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;

/**
 * MainScreen — layout utama setelah login.
 * Terdiri dari: Topbar + Sidebar + Content Area.
 * Navigasi antar panel dilakukan dengan mengganti konten di contentArea.
 */
public class MainScreen {

    private BorderPane view;
    private StackPane contentArea;
    private Button activeNavButton;

    private final User currentUser;

    // Panel-panel konten (lazy init)
    private DashboardPanel   dashboardPanel;
    private CustomerPanel    customerPanel;
    private AccountPanel     accountPanel;
    private TransactionPanel transactionPanel;
    private LoanPanel        loanPanel;

    public MainScreen(User user) throws Exception {
        this.currentUser = user;
        buildUI();
    }

    private void buildUI() throws Exception {
        view = new BorderPane();
        view.getStyleClass().addAll("root-pane",
            ThemeManager.getInstance().isDark() ? "theme-dark" : "theme-light");

        // ── Topbar ──
        view.setTop(buildTopbar());

        // ── Sidebar ──
        view.setLeft(buildSidebar());

        // ── Content area (default: dashboard) ──
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        view.setCenter(contentArea);

        // Muat dashboard sebagai panel pertama
        showDashboard();
    }

    // ── TOPBAR ──────────────────────────────────────────────────────────────

    private HBox buildTopbar() {
        HBox topbar = new HBox();
        topbar.getStyleClass().add("topbar");
        topbar.setAlignment(Pos.CENTER_LEFT);

        // Judul kiri
        VBox titleBox = new VBox(2);
        Label title = new Label("NusaBank");
        title.getStyleClass().add("topbar-title");
        Label subtitle = new Label("Sistem Manajemen Perbankan");
        subtitle.getStyleClass().add("topbar-subtitle");
        titleBox.getChildren().addAll(title, subtitle);

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Info user
        String roleText = currentUser.isAdmin() ? "Administrator" : "Nasabah";
        Label userLabel = new Label(currentUser.getUsername() + " · " + roleText);
        userLabel.getStyleClass().add("topbar-subtitle");
        userLabel.setPadding(new Insets(0, 16, 0, 0));

        // Theme toggle
        Button themeBtn = new Button(ThemeManager.getInstance().isDark() ? "☀️" : "🌙");
        themeBtn.getStyleClass().add("btn-icon");
        themeBtn.setOnAction(e -> {
            ThemeManager.getInstance().toggle();
            themeBtn.setText(ThemeManager.getInstance().isDark() ? "☀️" : "🌙");
        });

        // Logout
        Button logoutBtn = new Button("Keluar");
        logoutBtn.getStyleClass().add("btn-secondary");
        logoutBtn.setOnAction(e -> handleLogout());

        topbar.getChildren().addAll(titleBox, spacer, userLabel, themeBtn,
                                     new Label("  "), logoutBtn);
        topbar.setPadding(new Insets(0, 20, 0, 20));
        return topbar;
    }

    // ── SIDEBAR ─────────────────────────────────────────────────────────────

    private VBox buildSidebar() {
        VBox sidebar = new VBox(4);
        sidebar.getStyleClass().add("sidebar");

        Label menuLabel = new Label("MENU");
        menuLabel.getStyleClass().add("sidebar-section-label");

        Button btnDashboard    = navBtn("🏠  Dashboard",    e -> showDashboard());
        Button btnCustomer     = navBtn("👥  Nasabah",      e -> showCustomer());
        Button btnAccount      = navBtn("💳  Rekening",     e -> showAccount());
        Button btnTransaction  = navBtn("↔️  Transaksi",    e -> showTransaction());
        Button btnLoan         = navBtn("📋  Pinjaman",     e -> showLoan());

        // Admin-only
        if (!currentUser.isAdmin()) {
            btnCustomer.setDisable(true);
            btnCustomer.setVisible(false);
        }

        Separator sep = new Separator();
        sep.setPadding(new Insets(8, 0, 8, 0));

        sidebar.getChildren().addAll(
            menuLabel,
            btnDashboard,
            btnCustomer,
            btnAccount,
            btnTransaction,
            btnLoan
        );

        // Set dashboard sebagai aktif default
        setActive(btnDashboard);

        return sidebar;
    }

    private Button navBtn(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            setActive(btn);
            handler.handle(e);
        });
        return btn;
    }

    private void setActive(Button btn) {
        if (activeNavButton != null) {
            activeNavButton.getStyleClass().remove("nav-button-active");
        }
        btn.getStyleClass().add("nav-button-active");
        activeNavButton = btn;
    }

    // ── NAVIGASI PANEL ──────────────────────────────────────────────────────

    private void showPanel(Node panel) {
        contentArea.getChildren().setAll(panel);
    }

    private void showDashboard() {
        try {
            if (dashboardPanel == null)
                dashboardPanel = new DashboardPanel(currentUser);
            else
                dashboardPanel.refresh();
            showPanel(dashboardPanel.getView());
        } catch (Exception e) { showError(e); }
    }

    private void showCustomer() {
        try {
            if (customerPanel == null)
                customerPanel = new CustomerPanel();
            else
                customerPanel.refresh();
            showPanel(customerPanel.getView());
        } catch (Exception e) { showError(e); }
    }

    private void showAccount() {
        try {
            if (accountPanel == null)
                accountPanel = new AccountPanel(currentUser);
            else
                accountPanel.refresh();
            showPanel(accountPanel.getView());
        } catch (Exception e) { showError(e); }
    }

    private void showTransaction() {
        try {
            if (transactionPanel == null)
                transactionPanel = new TransactionPanel(currentUser);
            else
                transactionPanel.refresh();
            showPanel(transactionPanel.getView());
        } catch (Exception e) { showError(e); }
    }

    private void showLoan() {
        try {
            if (loanPanel == null)
                loanPanel = new LoanPanel(currentUser);
            else
                loanPanel.refresh();
            showPanel(loanPanel.getView());
        } catch (Exception e) { showError(e); }
    }

    private void handleLogout() {
        AuthService.getInstance().logout();
        LoginScreen loginScreen = new LoginScreen();
        SceneManager.getInstance().getMainScene()
            .setRoot(loginScreen.getView());
    }

    private void showError(Exception e) {
        Label err = new Label("Gagal memuat panel: " + e.getMessage());
        err.getStyleClass().add("badge-danger");
        contentArea.getChildren().setAll(err);
        e.printStackTrace();
    }

    public BorderPane getView() { return view; }
}

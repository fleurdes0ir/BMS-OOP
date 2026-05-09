package banking.ui.panels;

import banking.model.*;
import banking.service.AuthService;
import banking.service.BankService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.List;

public class DashboardPanel {

    private VBox view;
    private final User currentUser;
    private final BankService bankService = new BankService();

    public DashboardPanel(User user) throws Exception {
        this.currentUser = user;
        buildUI();
    }

    private void buildUI() throws Exception {
        view = new VBox(20);
        view.setPadding(new Insets(8));

        // Judul
        Label title = new Label("Dashboard");
        title.getStyleClass().add("page-title");
        String roleText = currentUser.isAdmin() ? "Selamat datang, Administrator" :
                          "Selamat datang, " + currentUser.getUsername();
        Label subtitle = new Label(roleText);
        subtitle.getStyleClass().add("page-subtitle");

        // Metric cards
        HBox metrics = buildMetrics();

        // Transaksi terbaru
        VBox recentTx = buildRecentTransactions();

        view.getChildren().addAll(title, subtitle, metrics, recentTx);
    }

    private HBox buildMetrics() throws Exception {
        List<Customer>    customers    = bankService.getAllCustomers();
        List<Account>     accounts     = bankService.getAllAccounts();
        List<Transaction> transactions = bankService.getAllTransactions();

        double totalBalance = accounts.stream().mapToDouble(Account::getBalance).sum();

        HBox row = new HBox(12);
        if (currentUser.isAdmin()) {
            row.getChildren().addAll(
                metricCard("Total Nasabah",    String.valueOf(customers.size()),    false),
                metricCard("Total Rekening",   String.valueOf(accounts.size()),     false),
                metricCard("Total Transaksi",  String.valueOf(transactions.size()), false),
                metricCard("Total Dana (Rp)",  formatRp(totalBalance),             true)
            );
        } else {
            // Nasabah hanya lihat rekening miliknya
            List<Account> myAccounts = bankService.getAccountsByCustomer(currentUser.getCustomerId());
            double myBalance = myAccounts.stream().mapToDouble(Account::getBalance).sum();
            row.getChildren().addAll(
                metricCard("Jumlah Rekening", String.valueOf(myAccounts.size()), false),
                metricCard("Total Saldo (Rp)", formatRp(myBalance), true)
            );
        }
        return row;
    }

    private VBox metricCard(String label, String value, boolean accent) {
        VBox card = new VBox(6);
        card.getStyleClass().add("metric-card");
        card.setPrefWidth(200);

        Label lbl = new Label(label);
        lbl.getStyleClass().add("metric-label");

        Label val = new Label(value);
        val.getStyleClass().add(accent ? "metric-value-accent" : "metric-value");

        card.getChildren().addAll(lbl, val);
        return card;
    }

    private VBox buildRecentTransactions() throws Exception {
        VBox box = new VBox(8);
        box.getStyleClass().add("card");

        Label title = new Label("Transaksi Terbaru");
        title.getStyleClass().add("card-title");

        List<Transaction> txList = currentUser.isAdmin()
            ? bankService.getAllTransactions()
            : bankService.getTransactionHistory(getFirstAccountId());

        // Ambil 5 terbaru
        int from = Math.max(0, txList.size() - 5);
        List<Transaction> recent = txList.subList(from, txList.size());

        box.getChildren().add(title);

        if (recent.isEmpty()) {
            Label empty = new Label("Belum ada transaksi.");
            empty.getStyleClass().add("page-subtitle");
            box.getChildren().add(empty);
        } else {
            for (Transaction tx : recent) {
                box.getChildren().add(txRow(tx));
            }
        }
        return box;
    }

    private HBox txRow(Transaction tx) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 0, 6, 0));
        row.setStyle("-fx-border-color: -nb-border; -fx-border-width: 0 0 0.5 0;");

        VBox info = new VBox(2);
        Label type = new Label(tx.getType().name());
        type.getStyleClass().add("card-title");
        Label detail = new Label(tx.getFormattedTimestamp() + " · " +
                                 tx.getSourceAccountId() + " · " + tx.getNote());
        detail.getStyleClass().add("page-subtitle");
        info.getChildren().addAll(type, detail);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label amount = new Label(tx.getFormattedAmount());
        String badgeStyle = switch (tx.getType()) {
            case DEPOSIT, TRANSFER_IN -> "badge-success";
            case WITHDRAWAL           -> "badge-danger";
            case TRANSFER_OUT         -> "badge-warning";
        };
        amount.getStyleClass().add(badgeStyle);

        row.getChildren().addAll(info, amount);
        return row;
    }

    private String getFirstAccountId() throws Exception {
        List<Account> accounts = bankService.getAccountsByCustomer(currentUser.getCustomerId());
        return accounts.isEmpty() ? "" : accounts.get(0).getAccountId();
    }

    public void refresh() throws Exception { buildUI(); }

    public VBox getView() { return view; }

    private String formatRp(double val) {
        return String.format("Rp%,.0f", val);
    }
}

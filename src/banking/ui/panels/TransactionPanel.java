package banking.ui.panels;

import banking.model.Account;
import banking.model.Transaction;
import banking.model.User;
import banking.service.BankService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TransactionPanel {

    private VBox view;
    private TableView<Transaction> table;
    private final User currentUser;
    private final BankService bankService = new BankService();

    public TransactionPanel(User user) throws Exception {
        this.currentUser = user;
        buildUI();
    }

    private void buildUI() throws Exception {
        view = new VBox(16);
        view.setPadding(new Insets(8));

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Transaksi");
        title.getStyleClass().add("page-title");
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button depositBtn  = new Button("+ Deposit");
        Button withdrawBtn = new Button("- Tarik");
        Button transferBtn = new Button("\u2194 Transfer");
        depositBtn.getStyleClass().add("btn-primary");
        withdrawBtn.getStyleClass().add("btn-secondary");
        transferBtn.getStyleClass().add("btn-secondary");

        depositBtn.setOnAction(e  -> showTransactionDialog("DEPOSIT"));
        withdrawBtn.setOnAction(e -> showTransactionDialog("WITHDRAWAL"));
        transferBtn.setOnAction(e -> showTransactionDialog("TRANSFER"));

        header.getChildren().addAll(title, spacer, depositBtn, withdrawBtn, transferBtn);

        TextField filterField = new TextField();
        filterField.getStyleClass().add("text-field-nb");
        filterField.setPromptText("Filter ID rekening...");
        filterField.setPrefWidth(240);

        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);
        filterField.textProperty().addListener((o, oldV, newV) -> filterTable(newV));

        view.getChildren().addAll(header, filterField, table);
        loadData(null);
    }

    @SuppressWarnings("unchecked")
    private TableView<Transaction> buildTable() {
        TableView<Transaction> tv = new TableView<>();
        tv.getStyleClass().add("table-view");
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Transaction, String> colId   = col("ID",      t -> t.getTxId(),                80);
        TableColumn<Transaction, String> colType = col("Tipe",    t -> t.getType().name(),         110);
        TableColumn<Transaction, String> colAmt  = col("Nominal", t -> t.getFormattedAmount(),     130);
        TableColumn<Transaction, String> colSrc  = col("Dari",    t -> t.getSourceAccountId(),     100);
        TableColumn<Transaction, String> colTgt  = col("Ke",      t -> {
            String tgt = t.getTargetAccountId();
            return (tgt != null && !tgt.isEmpty()) ? tgt : "-";
        },                                                                                          100);
        TableColumn<Transaction, String> colTime = col("Waktu",   t -> t.getFormattedTimestamp(),  150);
        TableColumn<Transaction, String> colNote = col("Catatan", t -> t.getNote(),                160);

        colType.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                getStyleClass().removeAll("badge-success","badge-danger","badge-warning","badge-info");
                if (!empty && item != null) {
                    String style = switch (item) {
                        case "DEPOSIT"      -> "badge-success";
                        case "WITHDRAWAL"   -> "badge-danger";
                        case "TRANSFER_OUT" -> "badge-warning";
                        case "TRANSFER_IN"  -> "badge-info";
                        default -> "";
                    };
                    if (!style.isEmpty()) getStyleClass().add(style);
                }
            }
        });

        tv.getColumns().addAll(colId, colType, colAmt, colSrc, colTgt, colTime, colNote);
        return tv;
    }

    private TableColumn<Transaction, String> col(String title, Function<Transaction, String> fn, int width) {
        TableColumn<Transaction, String> c = new TableColumn<>(title);
        c.setCellValueFactory(data -> new SimpleStringProperty(fn.apply(data.getValue())));
        c.setPrefWidth(width);
        return c;
    }

    private void loadData(String accountFilter) throws Exception {
        List<Transaction> txList;
        if (currentUser.isAdmin()) {
            txList = bankService.getAllTransactions();
        } else {
            List<Account> myAccounts = bankService.getAccountsByCustomer(currentUser.getCustomerId());
            txList = new ArrayList<>();
            for (Account a : myAccounts) {
                txList.addAll(bankService.getTransactionHistory(a.getAccountId()));
            }
        }

        if (accountFilter != null && !accountFilter.isEmpty()) {
            txList = txList.stream()
                .filter(t -> t.getSourceAccountId().contains(accountFilter) ||
                            (t.getTargetAccountId() != null &&
                             t.getTargetAccountId().contains(accountFilter)))
                .toList();
        }
        table.setItems(FXCollections.observableArrayList(txList));
    }

    private void filterTable(String filter) {
        try { loadData(filter); } catch (Exception e) { e.printStackTrace(); }
    }

    private void showTransactionDialog(String type) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(switch (type) {
            case "DEPOSIT"    -> "Setoran Dana";
            case "WITHDRAWAL" -> "Penarikan Dana";
            case "TRANSFER"   -> "Transfer Dana";
            default           -> "Transaksi";
        });
        dialog.setHeaderText(null);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10); grid.setPadding(new Insets(16));

        Label lblSrc = new Label("ID Rekening Sumber"); lblSrc.getStyleClass().add("field-label");
        TextField srcField = new TextField(); srcField.getStyleClass().add("text-field-nb");
        srcField.setPrefWidth(260); srcField.setPromptText("contoh: A001");
        grid.add(lblSrc, 0, 0); grid.add(srcField, 1, 0);

        TextField tgtField = null;
        if ("TRANSFER".equals(type)) {
            Label lblTgt = new Label("ID Rekening Tujuan"); lblTgt.getStyleClass().add("field-label");
            tgtField = new TextField(); tgtField.getStyleClass().add("text-field-nb");
            tgtField.setPrefWidth(260); tgtField.setPromptText("contoh: A002");
            grid.add(lblTgt, 0, 1); grid.add(tgtField, 1, 1);
        }

        int row = "TRANSFER".equals(type) ? 2 : 1;
        Label lblAmt = new Label("Nominal (Rp)"); lblAmt.getStyleClass().add("field-label");
        TextField amtField = new TextField(); amtField.getStyleClass().add("text-field-nb");
        amtField.setPrefWidth(260); amtField.setPromptText("contoh: 500000");

        Label lblNote = new Label("Catatan"); lblNote.getStyleClass().add("field-label");
        TextField noteField = new TextField(); noteField.getStyleClass().add("text-field-nb");
        noteField.setPrefWidth(260);

        grid.add(lblAmt,  0, row);     grid.add(amtField,  1, row);
        grid.add(lblNote, 0, row + 1); grid.add(noteField, 1, row + 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().getStylesheets().addAll(table.getScene().getStylesheets());

        final TextField finalTgtField = tgtField;
        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    String src    = srcField.getText().trim();
                    double amount = Double.parseDouble(amtField.getText().trim());
                    String note   = noteField.getText().trim();

                    switch (type) {
                        case "DEPOSIT"    -> bankService.deposit(src, amount, note);
                        case "WITHDRAWAL" -> bankService.withdraw(src, amount, note);
                        case "TRANSFER"   -> {
                            String tgt = finalTgtField.getText().trim();
                            bankService.transfer(src, tgt, amount, note);
                        }
                    }

                    // Ambil saldo terbaru setelah transaksi untuk ditampilkan di notifikasi
                    Account updated = bankService.findAccountById(src).orElse(null);
                    String balanceInfo = "";
                    if (updated != null) {
                        balanceInfo = String.format(
                            "\n\nSaldo rekening %s sekarang: Rp%,.0f",
                            src, updated.getBalance());
                        // Tandai jika saldo negatif (overdraft)
                        if (updated.getBalance() < 0) {
                            balanceInfo += "\n\u26A0\uFE0F Rekening dalam kondisi overdraft.";
                        }
                    }

                    refresh();
                    Alert ok = new Alert(Alert.AlertType.INFORMATION,
                        "Transaksi berhasil diproses." + balanceInfo, ButtonType.OK);
                    ok.setTitle("Transaksi Berhasil");
                    ok.setHeaderText(null);
                    ok.showAndWait();

                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                    alert.setTitle("Transaksi Gagal");
                    alert.setHeaderText(null);
                    alert.showAndWait();
                }
            }
        });
    }

    public void refresh() throws Exception { buildUI(); }
    public VBox getView() { return view; }
}

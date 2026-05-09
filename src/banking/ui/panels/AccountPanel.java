package banking.ui.panels;

import banking.model.*;
import banking.model.enums.AccountType;
import banking.service.BankService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;

public class AccountPanel {

    private VBox view;
    private TableView<Account> table;
    private final User currentUser;
    private final BankService bankService = new BankService();

    public AccountPanel(User user) throws Exception {
        this.currentUser = user;
        buildUI();
    }

    private void buildUI() throws Exception {
        view = new VBox(16);
        view.setPadding(new Insets(8));

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Manajemen Rekening");
        title.getStyleClass().add("page-title");
        HBox spacer = new HBox(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Buka Rekening");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> showOpenAccountDialog());
        if (!currentUser.isAdmin()) addBtn.setDisable(true);

        header.getChildren().addAll(title, spacer, addBtn);

        // Table
        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        view.getChildren().addAll(header, table);
        loadData();
    }

    @SuppressWarnings("unchecked")
    private TableView<Account> buildTable() {
        TableView<Account> tv = new TableView<>();
        tv.getStyleClass().add("table-view");
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Account, String> colId      = new TableColumn<>("ID Rekening");
        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
            d.getValue().getAccountId()));
        colId.setPrefWidth(100);

        TableColumn<Account, String> colOwner   = new TableColumn<>("ID Nasabah");
        colOwner.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
            d.getValue().getCustomerId()));
        colOwner.setPrefWidth(100);

        TableColumn<Account, String> colType    = new TableColumn<>("Jenis");
        colType.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
            d.getValue().getAccountType().name()));
        colType.setPrefWidth(100);

        TableColumn<Account, String> colBalance = new TableColumn<>("Saldo");
        colBalance.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
            String.format("Rp%,.0f", d.getValue().getBalance())));
        colBalance.setPrefWidth(150);

        TableColumn<Account, String> colInterest = new TableColumn<>("Bunga/Info");
        colInterest.setCellValueFactory(d -> {
            Account a = d.getValue();
            String info = switch (a.getAccountType()) {
                case SAVINGS -> String.format("%.1f%% p.a", ((SavingsAccount) a).getInterestRate());
                case CURRENT -> String.format("Overdraft Rp%,.0f", ((CurrentAccount) a).getOverdraftLimit());
                case DEPOSIT -> String.format("%.1f%% / %d bln", ((DepositAccount) a).getInterestRate(),
                                              ((DepositAccount) a).getTenorMonths());
            };
            return new javafx.beans.property.SimpleStringProperty(info);
        });
        colInterest.setPrefWidth(160);

        TableColumn<Account, String> colDate = new TableColumn<>("Dibuka");
        colDate.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
            d.getValue().getCreatedAt().toString()));
        colDate.setPrefWidth(100);

        tv.getColumns().addAll(colId, colOwner, colType, colBalance, colInterest, colDate);
        return tv;
    }

    private void loadData() throws Exception {
        List<Account> accounts = currentUser.isAdmin()
            ? bankService.getAllAccounts()
            : bankService.getAccountsByCustomer(currentUser.getCustomerId());
        table.setItems(FXCollections.observableArrayList(accounts));
    }

    private void showOpenAccountDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Buka Rekening Baru");
        dialog.setHeaderText(null);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(16));

        // ID Nasabah
        Label lblCust = new Label("ID Nasabah"); lblCust.getStyleClass().add("field-label");
        TextField custField = new TextField();
        custField.setId("custField"); custField.getStyleClass().add("text-field-nb");
        custField.setPrefWidth(240); custField.setPromptText("contoh: C001");

        // Jenis rekening
        Label lblType = new Label("Jenis Rekening"); lblType.getStyleClass().add("field-label");
        ComboBox<AccountType> typeBox = new ComboBox<>(
            FXCollections.observableArrayList(AccountType.values()));
        typeBox.getStyleClass().add("combo-box-nb");
        typeBox.setPrefWidth(240);
        typeBox.setConverter(new StringConverter<>() {
            public String toString(AccountType t) {
                return t == null ? "" : switch (t) {
                    case SAVINGS -> "Tabungan"; case CURRENT -> "Giro"; case DEPOSIT -> "Deposito";
                };
            }
            public AccountType fromString(String s) { return null; }
        });
        typeBox.setValue(AccountType.SAVINGS);

        // Saldo awal
        Label lblBal = new Label("Saldo Awal (Rp)"); lblBal.getStyleClass().add("field-label");
        TextField balField = new TextField("1000000");
        balField.getStyleClass().add("text-field-nb"); balField.setPrefWidth(240);

        // Bunga / Info
        Label lblRate = new Label("Bunga (% p.a)"); lblRate.getStyleClass().add("field-label");
        TextField rateField = new TextField("2.5");
        rateField.getStyleClass().add("text-field-nb"); rateField.setPrefWidth(240);

        grid.add(lblCust, 0, 0); grid.add(custField, 1, 0);
        grid.add(lblType, 0, 1); grid.add(typeBox,   1, 1);
        grid.add(lblBal,  0, 2); grid.add(balField,  1, 2);
        grid.add(lblRate, 0, 3); grid.add(rateField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().getStylesheets().addAll(table.getScene().getStylesheets());

        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    String customerId = custField.getText().trim();
                    AccountType type  = typeBox.getValue();
                    double balance    = Double.parseDouble(balField.getText().trim());
                    double rate       = Double.parseDouble(rateField.getText().trim());
                    String accId      = bankService.generateNextAccountId();

                    Account account = switch (type) {
                        case SAVINGS -> new SavingsAccount(accId, customerId, balance, rate, LocalDate.now());
                        case CURRENT -> new CurrentAccount(accId, customerId, balance, rate * 100000, LocalDate.now());
                        case DEPOSIT -> new DepositAccount(accId, customerId, balance, rate, 12, LocalDate.now());
                    };
                    bankService.openAccount(account);
                    refresh();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                    alert.setHeaderText(null); alert.showAndWait();
                }
            }
        });
    }

    public void refresh() throws Exception { buildUI(); }
    public VBox getView() { return view; }
}

package banking.ui.panels;

import banking.model.Loan;
import banking.model.User;
import banking.model.enums.LoanStatus;
import banking.service.BankService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

public class LoanPanel {

    private VBox view;
    private TableView<Loan> table;
    private final User currentUser;
    private final BankService bankService = new BankService();

    public LoanPanel(User user) throws Exception {
        this.currentUser = user;
        buildUI();
    }

    private void buildUI() throws Exception {
        view = new VBox(16);
        view.setPadding(new Insets(8));

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Manajemen Pinjaman");
        title.getStyleClass().add("page-title");
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button applyBtn = new Button("+ Ajukan Pinjaman");
        applyBtn.getStyleClass().add("btn-primary");
        applyBtn.setOnAction(e -> showApplyDialog());
        header.getChildren().addAll(title, spacer, applyBtn);

        HBox summaryRow = buildSummary();
        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);
        view.getChildren().addAll(header, summaryRow, table);
        loadData();
    }

    private HBox buildSummary() throws Exception {
        HBox row = new HBox(12);
        if (!currentUser.isAdmin()) return row;
        List<Loan> loans = bankService.getAllLoans();
        long active   = loans.stream().filter(l -> l.getStatus() == LoanStatus.ACTIVE).count();
        double total  = loans.stream().mapToDouble(Loan::getPrincipal).sum();
        double income = loans.stream().mapToDouble(Loan::getTotalInterest).sum();
        row.getChildren().addAll(
            metricCard("Pinjaman Aktif",     String.valueOf(active)),
            metricCard("Total Pokok",        String.format("Rp%,.0f", total)),
            metricCard("Total Bunga (Est.)", String.format("Rp%,.0f", income))
        );
        return row;
    }

    private VBox metricCard(String label, String value) {
        VBox card = new VBox(6);
        card.getStyleClass().add("metric-card");
        card.setPrefWidth(200);
        Label lbl = new Label(label); lbl.getStyleClass().add("metric-label");
        Label val = new Label(value); val.getStyleClass().add("metric-value");
        card.getChildren().addAll(lbl, val);
        return card;
    }

    @SuppressWarnings("unchecked")
    private TableView<Loan> buildTable() {
        TableView<Loan> tv = new TableView<>();
        tv.getStyleClass().add("table-view");
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Loan, String> colId     = col("ID",          l -> l.getLoanId(),                                        80);
        TableColumn<Loan, String> colCust   = col("ID Nasabah",  l -> l.getCustomerId(),                                    100);
        TableColumn<Loan, String> colPrinc  = col("Pokok",       l -> String.format("Rp%,.0f", l.getPrincipal()),           130);
        TableColumn<Loan, String> colRate   = col("Bunga",       l -> l.getInterestRate() + "% p.a",                         90);
        TableColumn<Loan, String> colTenor  = col("Tenor",       l -> l.getTenorMonths() + " bulan",                         90);
        TableColumn<Loan, String> colInst   = col("Cicilan/bln", l -> String.format("Rp%,.0f", l.getMonthlyInstallment()),  130);
        TableColumn<Loan, String> colTotal  = col("Total Bayar", l -> String.format("Rp%,.0f", l.getTotalPayment()),         130);
        TableColumn<Loan, String> colStart  = col("Mulai",       l -> l.getStartDate().toString(),                           100);
        TableColumn<Loan, String> colStatus = col("Status",      l -> l.getStatus().name(),                                   90);

        colStatus.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                getStyleClass().removeAll("badge-success","badge-danger","badge-warning","badge-info");
                if (!empty && item != null) {
                    String style = switch (item) {
                        case "ACTIVE"    -> "badge-info";
                        case "PAID_OFF"  -> "badge-success";
                        case "DEFAULTED" -> "badge-danger";
                        default -> "";
                    };
                    if (!style.isEmpty()) getStyleClass().add(style);
                }
            }
        });

        tv.getColumns().addAll(colId, colCust, colPrinc, colRate, colTenor, colInst, colTotal, colStart, colStatus);
        return tv;
    }

    private TableColumn<Loan, String> col(String title, Function<Loan, String> fn, int width) {
        TableColumn<Loan, String> c = new TableColumn<>(title);
        c.setCellValueFactory(data -> new SimpleStringProperty(fn.apply(data.getValue())));
        c.setPrefWidth(width);
        return c;
    }

    private void loadData() throws Exception {
        List<Loan> loans = currentUser.isAdmin()
            ? bankService.getAllLoans()
            : bankService.getLoansByCustomer(currentUser.getCustomerId());
        table.setItems(FXCollections.observableArrayList(loans));
    }

    private void showApplyDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajukan Pinjaman Baru");
        dialog.setHeaderText(null);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10); grid.setPadding(new Insets(16));

        Label lblCust = new Label("ID Nasabah"); lblCust.getStyleClass().add("field-label");
        TextField custF = new TextField(currentUser.isCustomer() ? currentUser.getCustomerId() : "");
        custF.getStyleClass().add("text-field-nb"); custF.setPrefWidth(260);
        if (currentUser.isCustomer()) custF.setEditable(false);

        Label lblAmt = new Label("Jumlah Pinjaman (Rp)"); lblAmt.getStyleClass().add("field-label");
        TextField amtF = new TextField("10000000"); amtF.getStyleClass().add("text-field-nb"); amtF.setPrefWidth(260);

        Label lblRate = new Label("Bunga (% p.a)"); lblRate.getStyleClass().add("field-label");
        TextField rateF = new TextField("12.0"); rateF.getStyleClass().add("text-field-nb"); rateF.setPrefWidth(260);

        Label lblTenor = new Label("Tenor (bulan)"); lblTenor.getStyleClass().add("field-label");
        TextField tenorF = new TextField("12"); tenorF.getStyleClass().add("text-field-nb"); tenorF.setPrefWidth(260);

        Label lblPreview = new Label("Estimasi cicilan: -");
        lblPreview.getStyleClass().add("badge-info");

        Runnable updatePreview = () -> {
            try {
                double p = Double.parseDouble(amtF.getText().trim());
                double r = Double.parseDouble(rateF.getText().trim());
                int    t = Integer.parseInt(tenorF.getText().trim());
                Loan preview = new Loan("","",p,r,t,LocalDate.now(),LoanStatus.ACTIVE);
                lblPreview.setText(String.format("Estimasi cicilan: Rp%,.0f / bulan  |  Total: Rp%,.0f",
                    preview.getMonthlyInstallment(), preview.getTotalPayment()));
            } catch (Exception ignored) { lblPreview.setText("Estimasi cicilan: -"); }
        };

        amtF.textProperty().addListener((o,v,n)  -> updatePreview.run());
        rateF.textProperty().addListener((o,v,n) -> updatePreview.run());
        tenorF.textProperty().addListener((o,v,n)-> updatePreview.run());
        updatePreview.run();

        grid.add(lblCust, 0, 0);    grid.add(custF, 1, 0);
        grid.add(lblAmt, 0, 1);     grid.add(amtF, 1, 1);
        grid.add(lblRate, 0, 2);    grid.add(rateF, 1, 2);
        grid.add(lblTenor, 0, 3);   grid.add(tenorF, 1, 3);
        grid.add(lblPreview, 0, 4); GridPane.setColumnSpan(lblPreview, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().getStylesheets().addAll(table.getScene().getStylesheets());

        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    bankService.applyLoan(custF.getText().trim(),
                        Double.parseDouble(amtF.getText().trim()),
                        Double.parseDouble(rateF.getText().trim()),
                        Integer.parseInt(tenorF.getText().trim()));
                    refresh();
                    Alert ok = new Alert(Alert.AlertType.INFORMATION, "Pinjaman berhasil diajukan.", ButtonType.OK);
                    ok.setHeaderText(null); ok.showAndWait();
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

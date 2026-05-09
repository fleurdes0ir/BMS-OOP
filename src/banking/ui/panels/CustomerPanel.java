package banking.ui.panels;

import banking.model.Customer;
import banking.service.BankService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;

public class CustomerPanel {

    private VBox view;
    private TableView<Customer> table;
    private TextField searchField;
    private ObservableList<Customer> data;
    private final BankService bankService = new BankService();

    public CustomerPanel() throws Exception {
        buildUI();
    }

    private void buildUI() throws Exception {
        view = new VBox(16);
        view.setPadding(new Insets(8));

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Manajemen Nasabah");
        title.getStyleClass().add("page-title");
        HBox spacer = new HBox(); HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField = new TextField();
        searchField.getStyleClass().add("text-field-nb");
        searchField.setPromptText("🔍 Cari nama nasabah...");
        searchField.setPrefWidth(240);
        searchField.textProperty().addListener((o, oldVal, newVal) -> filterTable(newVal));

        Button addBtn = new Button("+ Tambah Nasabah");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setOnAction(e -> showAddDialog());

        header.getChildren().addAll(title, spacer, searchField, addBtn);

        // Table
        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        view.getChildren().addAll(header, table);
        loadData();
    }

    @SuppressWarnings("unchecked")
    private TableView<Customer> buildTable() {
        TableView<Customer> tv = new TableView<>();
        tv.getStyleClass().add("table-view");
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Customer, String> colId    = col("ID",       "customerId", 80);
        TableColumn<Customer, String> colName  = col("Nama",     "name",       180);
        TableColumn<Customer, String> colEmail = col("Email",    "email",      200);
        TableColumn<Customer, String> colPhone = col("Telepon",  "phone",      140);
        TableColumn<Customer, String> colDate  = col("Bergabung","createdAt",  120);

        // Kolom aksi
        TableColumn<Customer, Void> colAksi = new TableColumn<>("Aksi");
        colAksi.setPrefWidth(120);
        colAksi.setCellFactory(c -> new TableCell<>() {
            final Button editBtn   = new Button("✏️");
            final Button deleteBtn = new Button("🗑️");
            {
                editBtn.getStyleClass().add("btn-icon");
                deleteBtn.getStyleClass().add("btn-icon");
                editBtn.setOnAction(e -> {
                    Customer cust = getTableView().getItems().get(getIndex());
                    showEditDialog(cust);
                });
                deleteBtn.setOnAction(e -> {
                    Customer cust = getTableView().getItems().get(getIndex());
                    handleDelete(cust);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else { HBox box = new HBox(4, editBtn, deleteBtn); setGraphic(box); }
            }
        });

        tv.getColumns().addAll(colId, colName, colEmail, colPhone, colDate, colAksi);
        return tv;
    }

    private <T> TableColumn<Customer, T> col(String title, String prop, int width) {
        TableColumn<Customer, T> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        col.setPrefWidth(width);
        return col;
    }

    private void loadData() throws Exception {
        List<Customer> customers = bankService.getAllCustomers();
        data = FXCollections.observableArrayList(customers);
        table.setItems(data);
    }

    private void filterTable(String query) {
        if (query == null || query.isEmpty()) {
            table.setItems(data);
            return;
        }
        ObservableList<Customer> filtered = FXCollections.observableArrayList();
        for (Customer c : data) {
            if (c.getName().toLowerCase().contains(query.toLowerCase()) ||
                c.getCustomerId().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(c);
            }
        }
        table.setItems(filtered);
    }

    private void showAddDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Tambah Nasabah Baru");
        dialog.setHeaderText(null);

        GridPane grid = buildCustomerForm(null);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().getStyleClass().add("dialog-pane");
        dialog.getDialogPane().getStylesheets().addAll(
            table.getScene().getStylesheets());

        TextField nameF     = (TextField) grid.lookup("#nameField");
        TextField emailF    = (TextField) grid.lookup("#emailField");
        TextField phoneF    = (TextField) grid.lookup("#phoneField");
        TextField usernameF = (TextField) grid.lookup("#usernameField");
        PasswordField passF = (PasswordField) grid.lookup("#passField");

        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    bankService.addCustomer(
                        nameF.getText(), emailF.getText(), phoneF.getText(),
                        usernameF.getText(), passF.getText()
                    );
                    refresh();
                } catch (Exception e) {
                    showAlert("Gagal", e.getMessage());
                }
            }
        });
    }

    private void showEditDialog(Customer customer) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Nasabah");
        dialog.setHeaderText(null);

        GridPane grid = buildCustomerForm(customer);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().getStylesheets().addAll(table.getScene().getStylesheets());

        TextField nameF  = (TextField) grid.lookup("#nameField");
        TextField emailF = (TextField) grid.lookup("#emailField");
        TextField phoneF = (TextField) grid.lookup("#phoneField");

        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    customer.setName(nameF.getText());
                    customer.setEmail(emailF.getText());
                    customer.setPhone(phoneF.getText());
                    bankService.updateCustomer(customer);
                    refresh();
                } catch (Exception e) {
                    showAlert("Gagal", e.getMessage());
                }
            }
        });
    }

    private void handleDelete(Customer customer) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Hapus nasabah " + customer.getName() + "?",
            ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    bankService.deleteCustomer(customer.getCustomerId());
                    refresh();
                } catch (Exception e) {
                    showAlert("Gagal", e.getMessage());
                }
            }
        });
    }

    private GridPane buildCustomerForm(Customer existing) {
        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(16));

        boolean isEdit = existing != null;

        addRow(grid, 0, "Nama Lengkap", "nameField", TextField.class,
               isEdit ? existing.getName() : "");
        addRow(grid, 1, "Email", "emailField", TextField.class,
               isEdit ? existing.getEmail() : "");
        addRow(grid, 2, "Telepon", "phoneField", TextField.class,
               isEdit ? existing.getPhone() : "");

        if (!isEdit) {
            addRow(grid, 3, "Username Login", "usernameField", TextField.class, "");
            addRowPassword(grid, 4, "Password", "passField");
        }
        return grid;
    }

    private void addRow(GridPane grid, int row, String label, String id,
                        Class<?> cls, String val) {
        Label lbl = new Label(label); lbl.getStyleClass().add("field-label");
        TextField field = new TextField(val);
        field.setId(id); field.getStyleClass().add("text-field-nb");
        field.setPrefWidth(280);
        grid.add(lbl, 0, row); grid.add(field, 1, row);
    }

    private void addRowPassword(GridPane grid, int row, String label, String id) {
        Label lbl = new Label(label); lbl.getStyleClass().add("field-label");
        PasswordField field = new PasswordField();
        field.setId(id); field.getStyleClass().add("password-field-nb");
        field.setPrefWidth(280);
        grid.add(lbl, 0, row); grid.add(field, 1, row);
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setTitle(title); alert.setHeaderText(null); alert.showAndWait();
    }

    public void refresh() throws Exception { buildUI(); }
    public VBox getView() { return view; }
}

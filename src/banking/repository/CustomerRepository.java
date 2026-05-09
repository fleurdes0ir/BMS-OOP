package banking.repository;

import banking.model.Customer;
import banking.util.AppConfig;
import banking.util.CsvUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CustomerRepository — mengelola baca/tulis customers.csv.
 *
 * Format baris: customerId,name,email,phone,createdAt
 * Index kolom :      0        1     2      3       4
 */
public class CustomerRepository {

    // -----------------------------------------------------------------------
    // READ
    // -----------------------------------------------------------------------

    public List<Customer> findAll() throws IOException {
        List<String[]> rows = CsvUtil.readAll(AppConfig.CUSTOMERS_CSV);
        List<Customer> customers = new ArrayList<>();
        for (String[] row : rows) {
            customers.add(mapToCustomer(row));
        }
        return customers;
    }

    public Optional<Customer> findById(String customerId) throws IOException {
        return findAll().stream()
            .filter(c -> c.getCustomerId().equals(customerId))
            .findFirst();
    }

    public Optional<Customer> findByName(String name) throws IOException {
        return findAll().stream()
            .filter(c -> c.getName().toLowerCase().contains(name.toLowerCase()))
            .findFirst();
    }

    // -----------------------------------------------------------------------
    // WRITE
    // -----------------------------------------------------------------------

    /** Simpan nasabah baru (INSERT). */
    public void save(Customer customer) throws IOException {
        CsvUtil.appendRow(
            AppConfig.CUSTOMERS_CSV,
            AppConfig.HEADER_CUSTOMERS,
            mapToRow(customer)
        );
    }

    /** Update data nasabah (tulis ulang seluruh file). */
    public void update(Customer updated) throws IOException {
        List<Customer> all = findAll();
        List<String[]> rows = new ArrayList<>();
        for (Customer c : all) {
            if (c.getCustomerId().equals(updated.getCustomerId())) {
                rows.add(mapToRow(updated));
            } else {
                rows.add(mapToRow(c));
            }
        }
        CsvUtil.writeAll(AppConfig.CUSTOMERS_CSV, AppConfig.HEADER_CUSTOMERS, rows);
    }

    /** Hapus nasabah berdasarkan ID. */
    public void delete(String customerId) throws IOException {
        List<Customer> all = findAll();
        List<String[]> rows = new ArrayList<>();
        for (Customer c : all) {
            if (!c.getCustomerId().equals(customerId)) {
                rows.add(mapToRow(c));
            }
        }
        CsvUtil.writeAll(AppConfig.CUSTOMERS_CSV, AppConfig.HEADER_CUSTOMERS, rows);
    }

    /** Generate ID nasabah berikutnya. */
    public String generateNextId() throws IOException {
        return CsvUtil.generateId("C", findAll().size());
    }

    // -----------------------------------------------------------------------
    // Mapping helpers
    // -----------------------------------------------------------------------

    private Customer mapToCustomer(String[] row) {
        return new Customer(
            row[0],                           // customerId
            row[1],                           // name
            row[2],                           // email
            row[3],                           // phone
            LocalDate.parse(row[4])           // createdAt
        );
    }

    private String[] mapToRow(Customer c) {
        return new String[]{
            c.getCustomerId(),
            c.getName(),
            c.getEmail(),
            c.getPhone(),
            c.getCreatedAt().toString()
        };
    }
}

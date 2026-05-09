package banking.repository;

import banking.model.*;
import banking.model.enums.AccountType;
import banking.util.AppConfig;
import banking.util.CsvUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * AccountRepository — mengelola baca/tulis accounts.csv.
 *
 * Format baris: accountId,customerId,type,balance,interestRate,overdraftLimit,tenorMonths,createdAt
 * Index kolom :      0         1       2      3          4              5           6          7
 *
 * POIN OOP PENTING:
 * Method mapToAccount() mendemonstrasikan rekonstruksi polymorphism —
 * kolom 'type' menentukan subclass mana yang di-instantiate saat load dari CSV.
 */
public class AccountRepository {

    // -----------------------------------------------------------------------
    // READ
    // -----------------------------------------------------------------------

    public List<Account> findAll() throws IOException {
        List<String[]> rows = CsvUtil.readAll(AppConfig.ACCOUNTS_CSV);
        List<Account> accounts = new ArrayList<>();
        for (String[] row : rows) {
            accounts.add(mapToAccount(row));
        }
        return accounts;
    }

    public List<Account> findByCustomerId(String customerId) throws IOException {
        List<Account> result = new ArrayList<>();
        for (Account a : findAll()) {
            if (a.getCustomerId().equals(customerId)) result.add(a);
        }
        return result;
    }

    public Optional<Account> findById(String accountId) throws IOException {
        return findAll().stream()
            .filter(a -> a.getAccountId().equals(accountId))
            .findFirst();
    }

    // -----------------------------------------------------------------------
    // WRITE
    // -----------------------------------------------------------------------

    /** Simpan rekening baru. */
    public void save(Account account) throws IOException {
        CsvUtil.appendRow(
            AppConfig.ACCOUNTS_CSV,
            AppConfig.HEADER_ACCOUNTS,
            mapToRow(account)
        );
    }

    /**
     * Update saldo (dan field lain) setelah transaksi.
     * Tulis ulang seluruh file — diperlukan karena CSV tidak bisa in-place edit.
     */
    public void update(Account updated) throws IOException {
        List<Account> all = findAll();
        List<String[]> rows = new ArrayList<>();
        for (Account a : all) {
            if (a.getAccountId().equals(updated.getAccountId())) {
                rows.add(mapToRow(updated));
            } else {
                rows.add(mapToRow(a));
            }
        }
        CsvUtil.writeAll(AppConfig.ACCOUNTS_CSV, AppConfig.HEADER_ACCOUNTS, rows);
    }

    /** Hapus rekening. */
    public void delete(String accountId) throws IOException {
        List<Account> all = findAll();
        List<String[]> rows = new ArrayList<>();
        for (Account a : all) {
            if (!a.getAccountId().equals(accountId)) rows.add(mapToRow(a));
        }
        CsvUtil.writeAll(AppConfig.ACCOUNTS_CSV, AppConfig.HEADER_ACCOUNTS, rows);
    }

    public String generateNextId() throws IOException {
        return CsvUtil.generateId("A", findAll().size());
    }

    // -----------------------------------------------------------------------
    // Mapping helpers — INI ADALAH DEMONSTRASI POLYMORPHISM
    // -----------------------------------------------------------------------

    /**
     * Rekonstruksi objek Account yang tepat berdasarkan kolom 'type'.
     * Tanpa ini, kita tidak bisa membedakan SavingsAccount dari DepositAccount
     * setelah data disimpan ke CSV.
     */
    private Account mapToAccount(String[] row) {
        String      accountId  = row[0];
        String      customerId = row[1];
        AccountType type       = AccountType.valueOf(row[2]);
        double      balance    = Double.parseDouble(row[3]);
        double      rate       = Double.parseDouble(CsvUtil.getOrDefault(row, 4, "0"));
        double      overdraft  = Double.parseDouble(CsvUtil.getOrDefault(row, 5, "0"));
        int         tenor      = Integer.parseInt(CsvUtil.getOrDefault(row, 6, "0"));
        LocalDate   createdAt  = LocalDate.parse(row[7]);

        return switch (type) {
            case SAVINGS -> new SavingsAccount(accountId, customerId, balance, rate, createdAt);
            case CURRENT -> new CurrentAccount(accountId, customerId, balance, overdraft, createdAt);
            case DEPOSIT -> new DepositAccount(accountId, customerId, balance, rate, tenor, createdAt);
        };
    }

    private String[] mapToRow(Account a) {
        double rate     = 0, overdraft = 0;
        int    tenor    = 0;

        if (a instanceof SavingsAccount s) {
            rate = s.getInterestRate();
        } else if (a instanceof CurrentAccount c) {
            overdraft = c.getOverdraftLimit();
        } else if (a instanceof DepositAccount d) {
            rate  = d.getInterestRate();
            tenor = d.getTenorMonths();
        }

        return new String[]{
            a.getAccountId(),
            a.getCustomerId(),
            a.getAccountType().name(),
            String.valueOf(a.getBalance()),
            String.valueOf(rate),
            String.valueOf(overdraft),
            String.valueOf(tenor),
            a.getCreatedAt().toString()
        };
    }
}

package banking.util;

import java.io.IOException;
import java.nio.file.*;

/**
 * DataSeeder — membuat file CSV awal dengan data dummy.
 * Panggil DataSeeder.seed() di main() sebelum launch UI.
 * Jika file sudah ada, seed tidak dijalankan ulang.
 */
public class DataSeeder {

    private DataSeeder() {}

    public static void seed() throws IOException {
        Files.createDirectories(Paths.get(AppConfig.DATA_DIR));

        seedUsers();
        seedCustomers();
        seedAccounts();
        seedTransactions();
        seedLoans();

        System.out.println("[DataSeeder] Data awal berhasil dibuat di folder: " + AppConfig.DATA_DIR);
    }

    private static void seedUsers() throws IOException {
        Path p = Paths.get(AppConfig.USERS_CSV);
        if (Files.exists(p)) return;

        // password untuk semua akun demo: "admin123" dan "nasabah123"
        String adminHash   = CsvUtil.hashPassword("admin123");
        String customerHash = CsvUtil.hashPassword("nasabah123");

        CsvUtil.writeAll(AppConfig.USERS_CSV, AppConfig.HEADER_USERS,
            java.util.List.of(
                new String[]{"U001", "admin",       adminHash,    "ADMIN",    ""},
                new String[]{"U002", "budi.santoso", customerHash, "CUSTOMER", "C001"},
                new String[]{"U003", "sari.dewi",    customerHash, "CUSTOMER", "C002"}
            ));
    }

    private static void seedCustomers() throws IOException {
        Path p = Paths.get(AppConfig.CUSTOMERS_CSV);
        if (Files.exists(p)) return;

        CsvUtil.writeAll(AppConfig.CUSTOMERS_CSV, AppConfig.HEADER_CUSTOMERS,
            java.util.List.of(
                new String[]{"C001", "Budi Santoso",  "budi@email.com",  "08123456789", "2024-01-15"},
                new String[]{"C002", "Sari Dewi",     "sari@email.com",  "08234567890", "2024-02-20"},
                new String[]{"C003", "Agus Setiawan", "agus@email.com",  "08345678901", "2024-03-10"}
            ));
    }

    private static void seedAccounts() throws IOException {
        Path p = Paths.get(AppConfig.ACCOUNTS_CSV);
        if (Files.exists(p)) return;

        // format: accountId,customerId,type,balance,interestRate,overdraftLimit,tenorMonths,createdAt
        CsvUtil.writeAll(AppConfig.ACCOUNTS_CSV, AppConfig.HEADER_ACCOUNTS,
            java.util.List.of(
                new String[]{"A001", "C001", "SAVINGS", "15000000", "2.5", "0",       "0",  "2024-01-15"},
                new String[]{"A002", "C001", "DEPOSIT", "50000000", "5.0", "0",       "12", "2024-01-15"},
                new String[]{"A003", "C002", "SAVINGS", "8000000",  "2.5", "0",       "0",  "2024-02-20"},
                new String[]{"A004", "C002", "CURRENT", "25000000", "0",   "5000000", "0",  "2024-02-20"},
                new String[]{"A005", "C003", "SAVINGS", "3000000",  "2.5", "0",       "0",  "2024-03-10"}
            ));
    }

    private static void seedTransactions() throws IOException {
        Path p = Paths.get(AppConfig.TRANSACTIONS_CSV);
        if (Files.exists(p)) return;

        CsvUtil.writeAll(AppConfig.TRANSACTIONS_CSV, AppConfig.HEADER_TRANSACTIONS,
            java.util.List.of(
                new String[]{"T001", "DEPOSIT",      "15000000", "A001", "",     "2024-01-15 09:00:00", "Setoran awal"},
                new String[]{"T002", "DEPOSIT",      "50000000", "A002", "",     "2024-01-15 09:05:00", "Buka deposito"},
                new String[]{"T003", "WITHDRAWAL",   "500000",   "A001", "",     "2024-02-01 10:30:00", "Tarik tunai"},
                new String[]{"T004", "TRANSFER_OUT", "2000000",  "A001", "A003", "2024-02-15 14:00:00", "Transfer ke Sari"},
                new String[]{"T005", "TRANSFER_IN",  "2000000",  "A003", "A001", "2024-02-15 14:00:00", "Transfer dari Budi"},
                new String[]{"T006", "DEPOSIT",      "8000000",  "A003", "",     "2024-02-20 09:00:00", "Setoran awal"}
            ));
    }

    private static void seedLoans() throws IOException {
        Path p = Paths.get(AppConfig.LOANS_CSV);
        if (Files.exists(p)) return;

        CsvUtil.writeAll(AppConfig.LOANS_CSV, AppConfig.HEADER_LOANS,
            java.util.List.of(
                new String[]{"L001", "C001", "50000000", "12.0", "24", "2024-03-01", "ACTIVE"},
                new String[]{"L002", "C003", "20000000", "10.0", "12", "2024-04-01", "ACTIVE"}
            ));
    }
}

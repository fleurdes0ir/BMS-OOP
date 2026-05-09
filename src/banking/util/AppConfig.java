package banking.util;

/**
 * AppConfig — satu tempat untuk semua path file dan konstanta aplikasi.
 * Ubah DATA_DIR jika lokasi folder data berbeda di mesin lain.
 */
public class AppConfig {

    private AppConfig() {}

    // Folder data relatif terhadap working directory project
    public static final String DATA_DIR = "data/";

    // Path file CSV
    public static final String CUSTOMERS_CSV    = DATA_DIR + "customers.csv";
    public static final String ACCOUNTS_CSV     = DATA_DIR + "accounts.csv";
    public static final String TRANSACTIONS_CSV = DATA_DIR + "transactions.csv";
    public static final String USERS_CSV        = DATA_DIR + "users.csv";
    public static final String LOANS_CSV        = DATA_DIR + "loans.csv";

    // Header CSV (urutan kolom harus konsisten dengan Repository masing-masing)
    public static final String HEADER_CUSTOMERS =
        "customerId,name,email,phone,createdAt";
    public static final String HEADER_ACCOUNTS  =
        "accountId,customerId,type,balance,interestRate,overdraftLimit,tenorMonths,createdAt";
    public static final String HEADER_TRANSACTIONS =
        "txId,type,amount,sourceAccountId,targetAccountId,timestamp,note";
    public static final String HEADER_USERS =
        "userId,username,passwordHash,role,customerId";
    public static final String HEADER_LOANS =
        "loanId,customerId,principal,interestRate,tenorMonths,startDate,status";

    // Nama aplikasi
    public static final String APP_NAME    = "NusaBank";
    public static final String APP_VERSION = "1.0.0";
}

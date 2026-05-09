package banking.service;

import banking.model.*;
import banking.model.enums.TransactionType;
import banking.repository.*;
import banking.util.CsvUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * BankService — pusat semua operasi perbankan.
 *
 * Kelas ini mengkoordinasikan model, repository, dan aturan bisnis.
 * UI tidak boleh langsung memanggil repository — selalu lewat service ini.
 */
public class BankService {

    private final CustomerRepository    customerRepo;
    private final AccountRepository     accountRepo;
    private final TransactionRepository txRepo;
    private final LoanRepository        loanRepo;
    private final AuthService           auth;

    public BankService() {
        this.customerRepo = new CustomerRepository();
        this.accountRepo  = new AccountRepository();
        this.txRepo       = new TransactionRepository();
        this.loanRepo     = new LoanRepository();
        this.auth         = AuthService.getInstance();
    }

    // =======================================================================
    // CUSTOMER MANAGEMENT
    // =======================================================================

    public List<Customer> getAllCustomers() throws IOException {
        auth.requireLogin();
        return customerRepo.findAll();
    }

    public Optional<Customer> findCustomerById(String id) throws IOException {
        auth.requireLogin();
        return customerRepo.findById(id);
    }

    /**
     * Tambah nasabah baru beserta akun user-nya.
     * Admin only.
     */
    public Customer addCustomer(String name, String email, String phone,
                                String username, String plainPassword) throws IOException {
        auth.requireAdmin();

        String customerId = customerRepo.generateNextId();
        Customer customer = new Customer(customerId, name, email, phone, LocalDate.now());
        customerRepo.save(customer);

        // Buat User untuk nasabah ini
        String userId   = auth.generateNextUserId();
        String passHash = CsvUtil.hashPassword(plainPassword);
        User   user     = new User(userId, username, passHash,
                                   banking.model.enums.UserRole.CUSTOMER, customerId);
        new UserRepository().save(user);

        return customer;
    }

    public void updateCustomer(Customer customer) throws IOException {
        auth.requireAdmin();
        customerRepo.update(customer);
    }

    public void deleteCustomer(String customerId) throws IOException {
        auth.requireAdmin();
        customerRepo.delete(customerId);
    }

    // =======================================================================
    // ACCOUNT MANAGEMENT
    // =======================================================================

    public List<Account> getAccountsByCustomer(String customerId) throws IOException {
        auth.requireLogin();
        return accountRepo.findByCustomerId(customerId);
    }

    public Optional<Account> findAccountById(String accountId) throws IOException {
        auth.requireLogin();
        return accountRepo.findById(accountId);
    }

    public List<Account> getAllAccounts() throws IOException {
        auth.requireAdmin();
        return accountRepo.findAll();
    }

    public Account openAccount(Account account) throws IOException {
        auth.requireAdmin();
        accountRepo.save(account);
        return account;
    }

    public String generateNextAccountId() throws IOException {
        return accountRepo.generateNextId();
    }

    // =======================================================================
    // TRANSACTION OPERATIONS
    // =======================================================================

    /**
     * Setoran tunai ke rekening.
     */
    public Transaction deposit(String accountId, double amount,
                               String note) throws IOException {
        auth.requireLogin();

        Account account = accountRepo.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Rekening tidak ditemukan: " + accountId));

        account.deposit(amount); // method dari interface Transactable
        accountRepo.update(account);

        Transaction tx = buildTransaction(TransactionType.DEPOSIT, amount,
                                          accountId, null, note);
        txRepo.save(tx);
        return tx;
    }

    /**
     * Penarikan tunai dari rekening.
     * Masing-masing subclass punya aturan withdraw berbeda (Polymorphism).
     */
    public Transaction withdraw(String accountId, double amount,
                                String note) throws IOException {
        auth.requireLogin();

        Account account = accountRepo.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Rekening tidak ditemukan: " + accountId));

        account.withdraw(amount); // polymorphism: setiap subclass validasi berbeda
        accountRepo.update(account);

        Transaction tx = buildTransaction(TransactionType.WITHDRAWAL, amount,
                                          accountId, null, note);
        txRepo.save(tx);
        return tx;
    }

    /**
     * Transfer antar rekening.
     * Membuat dua record transaksi: TRANSFER_OUT untuk sumber, TRANSFER_IN untuk target.
     */
    public void transfer(String sourceId, String targetId,
                         double amount, String note) throws IOException {
        auth.requireLogin();

        Account source = accountRepo.findById(sourceId)
            .orElseThrow(() -> new IllegalArgumentException("Rekening sumber tidak ditemukan."));
        Account target = accountRepo.findById(targetId)
            .orElseThrow(() -> new IllegalArgumentException("Rekening tujuan tidak ditemukan."));

        source.transfer(amount, target); // method dari abstract Account

        accountRepo.update(source);
        accountRepo.update(target);

        // Catat dua sisi transaksi
        txRepo.save(buildTransaction(TransactionType.TRANSFER_OUT, amount, sourceId, targetId, note));
        txRepo.save(buildTransaction(TransactionType.TRANSFER_IN,  amount, targetId, sourceId, note));
    }

    // =======================================================================
    // TRANSACTION HISTORY
    // =======================================================================

    public List<Transaction> getTransactionHistory(String accountId) throws IOException {
        auth.requireLogin();
        return txRepo.findByAccountId(accountId);
    }

    public List<Transaction> getAllTransactions() throws IOException {
        auth.requireAdmin();
        return txRepo.findAll();
    }

    // =======================================================================
    // LOAN MANAGEMENT
    // =======================================================================

    public Loan applyLoan(String customerId, double principal,
                          double interestRate, int tenorMonths) throws IOException {
        auth.requireLogin();

        String loanId = loanRepo.generateNextId();
        Loan loan = new Loan(loanId, customerId, principal, interestRate,
                             tenorMonths, LocalDate.now(),
                             banking.model.enums.LoanStatus.ACTIVE);
        loanRepo.save(loan);
        return loan;
    }

    public List<Loan> getLoansByCustomer(String customerId) throws IOException {
        auth.requireLogin();
        return loanRepo.findByCustomerId(customerId);
    }

    public List<Loan> getAllLoans() throws IOException {
        auth.requireAdmin();
        return loanRepo.findAll();
    }

    // =======================================================================
    // Private helpers
    // =======================================================================

    private Transaction buildTransaction(TransactionType type, double amount,
                                         String sourceId, String targetId,
                                         String note) throws IOException {
        String txId = txRepo.generateNextId();
        return new Transaction(txId, type, amount, sourceId, targetId,
                               LocalDateTime.now(), note);
    }
}

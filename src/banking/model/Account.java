package banking.model;

import banking.model.enums.AccountType;
import java.time.LocalDate;

/**
 * Abstract class Account — Inheritance & Encapsulation pilar OOP.
 * Tidak bisa di-instantiate langsung; harus melalui subclass spesifik.
 */
public abstract class Account implements Transactable {

    // Encapsulation: semua field private, akses via getter/setter
    private String accountId;
    private String customerId;
    private double balance;
    private AccountType accountType;
    private LocalDate createdAt;

    public Account(String accountId, String customerId, double initialBalance,
                   AccountType accountType, LocalDate createdAt) {
        this.accountId   = accountId;
        this.customerId  = customerId;
        this.balance     = initialBalance;
        this.accountType = accountType;
        this.createdAt   = createdAt;
    }

    // --- Transactable: deposit (sama untuk semua jenis rekening) ---
    @Override
    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Nominal deposit harus lebih dari 0.");
        this.balance += amount;
    }

    // --- Transactable: transfer (template — withdraw dari sumber, deposit ke target) ---
    @Override
    public void transfer(double amount, Account target) throws IllegalArgumentException {
        this.withdraw(amount);      // delegasi ke implementasi withdraw masing-masing subclass
        target.deposit(amount);
    }

    // --- Abstract method: Polymorphism — setiap subclass hitung bunga berbeda ---
    public abstract double calculateInterest();

    // --- Getters ---
    public String getAccountId()    { return accountId; }
    public String getCustomerId()   { return customerId; }
    public double getBalance()      { return balance; }
    public AccountType getAccountType() { return accountType; }
    public LocalDate getCreatedAt() { return createdAt; }

    // --- Protected setter balance: hanya subclass yang boleh ubah langsung ---
    protected void setBalance(double balance) { this.balance = balance; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Saldo: Rp%,.0f", accountType, accountId, balance);
    }
}

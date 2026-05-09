package banking.model;

import banking.model.enums.AccountType;
import java.time.LocalDate;

/**
 * SavingsAccount (Rekening Tabungan).
 * Aturan: saldo minimum Rp 50.000 harus tetap ada setelah penarikan.
 * Bunga: dihitung flat dari saldo berjalan.
 */
public class SavingsAccount extends Account {

    private static final double MIN_BALANCE = 50_000.0;

    private double interestRate; // persen per tahun, misal 2.5

    public SavingsAccount(String accountId, String customerId, double initialBalance,
                          double interestRate, LocalDate createdAt) {
        super(accountId, customerId, initialBalance, AccountType.SAVINGS, createdAt);
        this.interestRate = interestRate;
    }

    /**
     * Polymorphism: implementasi withdraw khusus Tabungan.
     * Tidak boleh menarik dana jika saldo tersisa < MIN_BALANCE.
     */
    @Override
    public void withdraw(double amount) throws IllegalArgumentException {
        if (amount <= 0)
            throw new IllegalArgumentException("Nominal penarikan harus lebih dari 0.");
        if (getBalance() - amount < MIN_BALANCE)
            throw new IllegalArgumentException(
                String.format("Saldo tidak mencukupi. Saldo minimum tabungan: Rp%,.0f", MIN_BALANCE));
        setBalance(getBalance() - amount);
    }

    /**
     * Polymorphism: bunga tabungan = saldo × rate / 12 (bunga bulanan).
     */
    @Override
    public double calculateInterest() {
        return getBalance() * (interestRate / 100.0) / 12.0;
    }

    // Getter & Setter
    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
}

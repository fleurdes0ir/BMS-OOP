package banking.model;

import banking.model.enums.AccountType;
import java.time.LocalDate;

/**
 * DepositAccount (Rekening Deposito).
 * Aturan: penarikan sebelum tenor dikenakan penalti 20% dari bunga yang sudah berjalan.
 * Bunga: dihitung berdasarkan rate × tenor.
 */
public class DepositAccount extends Account {

    private static final double PENALTY_RATE = 0.20; // 20% dari bunga berjalan

    private double interestRate; // persen per tahun, misal 5.0
    private int    tenorMonths;  // tenor dalam bulan, misal 12
    private LocalDate maturityDate;

    public DepositAccount(String accountId, String customerId, double initialBalance,
                          double interestRate, int tenorMonths, LocalDate createdAt) {
        super(accountId, customerId, initialBalance, AccountType.DEPOSIT, createdAt);
        this.interestRate  = interestRate;
        this.tenorMonths   = tenorMonths;
        this.maturityDate  = createdAt.plusMonths(tenorMonths);
    }

    /**
     * Polymorphism: penarikan deposito — cek apakah sudah jatuh tempo.
     * Jika belum, potong penalti dari saldo.
     */
    @Override
    public void withdraw(double amount) throws IllegalArgumentException {
        if (amount <= 0)
            throw new IllegalArgumentException("Nominal penarikan harus lebih dari 0.");
        if (amount > getBalance())
            throw new IllegalArgumentException("Nominal melebihi saldo deposito.");

        boolean isMature = !LocalDate.now().isBefore(maturityDate);
        if (!isMature) {
            double accruedInterest = calculateInterest();
            double penalty = accruedInterest * PENALTY_RATE;
            // Penalti dipotong dari saldo, bukan dari amount
            setBalance(getBalance() - amount - penalty);
        } else {
            setBalance(getBalance() - amount);
        }
    }

    /**
     * Polymorphism: bunga deposito = pokok × rate × (tenor/12).
     */
    @Override
    public double calculateInterest() {
        return getBalance() * (interestRate / 100.0) * (tenorMonths / 12.0);
    }

    public boolean isMature() {
        return !LocalDate.now().isBefore(maturityDate);
    }

    // Getters & Setters
    public double getInterestRate()    { return interestRate; }
    public int getTenorMonths()        { return tenorMonths; }
    public LocalDate getMaturityDate() { return maturityDate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
}

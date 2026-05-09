package banking.model;

import banking.model.enums.AccountType;
import java.time.LocalDate;

/**
 * CurrentAccount (Rekening Giro).
 * Aturan: boleh overdraft sampai batas overdraftLimit.
 * Bunga: tidak ada bunga (atau negatif jika overdraft — disederhanakan jadi 0).
 */
public class CurrentAccount extends Account {

    private double overdraftLimit; // batas maksimal saldo negatif, misal 5.000.000

    public CurrentAccount(String accountId, String customerId, double initialBalance,
                          double overdraftLimit, LocalDate createdAt) {
        super(accountId, customerId, initialBalance, AccountType.CURRENT, createdAt);
        this.overdraftLimit = overdraftLimit;
    }

    /**
     * Polymorphism: withdraw Giro boleh masuk negatif sampai overdraftLimit.
     */
    @Override
    public void withdraw(double amount) throws IllegalArgumentException {
        if (amount <= 0)
            throw new IllegalArgumentException("Nominal penarikan harus lebih dari 0.");
        if (getBalance() - amount < -overdraftLimit)
            throw new IllegalArgumentException(
                String.format("Melebihi batas overdraft. Limit: Rp%,.0f", overdraftLimit));
        setBalance(getBalance() - amount);
    }

    /**
     * Polymorphism: Giro tidak menghasilkan bunga.
     */
    @Override
    public double calculateInterest() {
        return 0.0;
    }

    // Getter & Setter
    public double getOverdraftLimit() { return overdraftLimit; }
    public void setOverdraftLimit(double overdraftLimit) { this.overdraftLimit = overdraftLimit; }
}

package banking.model;

/**
 * Interface Transactable — Abstraction pilar OOP.
 * Semua entitas yang bisa bertransaksi wajib mengimplementasikan kontrak ini.
 */
public interface Transactable {
    void deposit(double amount);
    void withdraw(double amount) throws IllegalArgumentException;
    void transfer(double amount, Account target) throws IllegalArgumentException;
}

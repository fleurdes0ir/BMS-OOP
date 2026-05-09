package banking.model;

import java.time.LocalDate;

/**
 * Customer — Encapsulation pilar OOP.
 * Menyimpan data pribadi nasabah. Field sensitif hanya bisa diakses via getter.
 */
public class Customer {

    private String customerId;
    private String name;
    private String email;
    private String phone;
    private LocalDate createdAt;

    public Customer(String customerId, String name, String email,
                    String phone, LocalDate createdAt) {
        this.customerId = customerId;
        this.name       = name;
        this.email      = email;
        this.phone      = phone;
        this.createdAt  = createdAt;
    }

    // Getters
    public String getCustomerId() { return customerId; }
    public String getName()       { return name; }
    public String getEmail()      { return email; }
    public String getPhone()      { return phone; }
    public LocalDate getCreatedAt() { return createdAt; }

    // Setters (hanya field yang boleh diubah)
    public void setName(String name)   { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return String.format("%s | %s | %s", customerId, name, email);
    }
}

package banking.model;

import banking.model.enums.UserRole;

/**
 * User — Encapsulation + Role-based access.
 * Menyimpan kredensial login. Password disimpan sebagai hash, tidak pernah plain text.
 * customerId null jika role == ADMIN.
 */
public class User {

    private String userId;
    private String username;
    private String passwordHash; // simpan hasil hash, bukan plain text
    private UserRole role;
    private String customerId;   // null jika ADMIN

    public User(String userId, String username, String passwordHash,
                UserRole role, String customerId) {
        this.userId       = userId;
        this.username     = username;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.customerId   = customerId;
    }

    /**
     * Verifikasi password: bandingkan hash input dengan hash tersimpan.
     * Gunakan CsvUtil.hashPassword() untuk hash sebelum memanggil ini.
     */
    public boolean verifyPassword(String inputHash) {
        return this.passwordHash.equals(inputHash);
    }

    public boolean isAdmin()    { return role == UserRole.ADMIN; }
    public boolean isCustomer() { return role == UserRole.CUSTOMER; }

    // Getters
    public String   getUserId()      { return userId; }
    public String   getUsername()    { return username; }
    public String   getPasswordHash(){ return passwordHash; }
    public UserRole getRole()        { return role; }
    public String   getCustomerId()  { return customerId; }

    // Setter password (digunakan saat ganti password)
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}

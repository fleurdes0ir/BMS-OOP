package banking.service;

import banking.model.User;
import banking.repository.UserRepository;
import banking.util.CsvUtil;

import java.io.IOException;
import java.util.Optional;

/**
 * AuthService — Singleton pattern.
 *
 * Hanya ada satu sesi aktif dalam aplikasi.
 * Singleton memastikan tidak ada dua instance AuthService yang memiliki
 * state berbeda mengenai siapa yang sedang login.
 *
 * POIN OOP: Singleton adalah Design Pattern yang memanfaatkan
 * private constructor + static instance.
 */
public class AuthService {

    // --- Singleton ---
    private static AuthService instance;

    private final UserRepository userRepository;
    private User currentUser; // null jika tidak ada yang login

    private AuthService() {
        this.userRepository = new UserRepository();
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    // -----------------------------------------------------------------------
    // Auth operations
    // -----------------------------------------------------------------------

    /**
     * Login: cari user by username, verifikasi hash password.
     *
     * @param username username yang diinput
     * @param plainPassword password plain text (akan di-hash sebelum dibandingkan)
     * @return User jika berhasil
     * @throws IllegalArgumentException jika username/password salah
     * @throws IOException jika gagal baca file
     */
    public User login(String username, String plainPassword) throws IOException {
        Optional<User> found = userRepository.findByUsername(username);

        if (found.isEmpty()) {
            throw new IllegalArgumentException("Username tidak ditemukan.");
        }

        User user = found.get();
        String inputHash = CsvUtil.hashPassword(plainPassword);

        if (!user.verifyPassword(inputHash)) {
            throw new IllegalArgumentException("Password salah.");
        }

        this.currentUser = user;
        return user;
    }

    public void logout() {
        this.currentUser = null;
    }

    // -----------------------------------------------------------------------
    // Session queries
    // -----------------------------------------------------------------------

    public boolean isLoggedIn()  { return currentUser != null; }
    public boolean isAdmin()     { return isLoggedIn() && currentUser.isAdmin(); }
    public boolean isCustomer()  { return isLoggedIn() && currentUser.isCustomer(); }
    public User    getCurrentUser() { return currentUser; }

    /**
     * Guard method — lempar exception jika belum login.
     * Gunakan di awal setiap method service yang butuh autentikasi.
     */
    public void requireLogin() {
        if (!isLoggedIn())
            throw new IllegalStateException("Anda belum login.");
    }

    public void requireAdmin() {
        requireLogin();
        if (!isAdmin())
            throw new IllegalStateException("Akses ditolak. Hanya Admin yang diizinkan.");
    }

    // -----------------------------------------------------------------------
    // User management (Admin only)
    // -----------------------------------------------------------------------

    public void registerUser(User user) throws IOException {
        requireAdmin();
        userRepository.save(user);
    }

    public String generateNextUserId() throws IOException {
        return userRepository.generateNextId();
    }
}

package banking.repository;

import banking.model.User;
import banking.model.enums.UserRole;
import banking.util.AppConfig;
import banking.util.CsvUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UserRepository — mengelola baca/tulis users.csv.
 *
 * Format baris: userId,username,passwordHash,role,customerId
 * Index kolom :    0       1          2        3       4
 */
public class UserRepository {

    public List<User> findAll() throws IOException {
        List<String[]> rows = CsvUtil.readAll(AppConfig.USERS_CSV);
        List<User> users = new ArrayList<>();
        for (String[] row : rows) {
            users.add(mapToUser(row));
        }
        return users;
    }

    public Optional<User> findByUsername(String username) throws IOException {
        return findAll().stream()
            .filter(u -> u.getUsername().equalsIgnoreCase(username))
            .findFirst();
    }

    public void save(User user) throws IOException {
        CsvUtil.appendRow(
            AppConfig.USERS_CSV,
            AppConfig.HEADER_USERS,
            mapToRow(user)
        );
    }

    /** Update password user. */
    public void update(User updated) throws IOException {
        List<User> all = findAll();
        List<String[]> rows = new ArrayList<>();
        for (User u : all) {
            rows.add(u.getUserId().equals(updated.getUserId())
                ? mapToRow(updated) : mapToRow(u));
        }
        CsvUtil.writeAll(AppConfig.USERS_CSV, AppConfig.HEADER_USERS, rows);
    }

    public String generateNextId() throws IOException {
        return CsvUtil.generateId("U", findAll().size());
    }

    // -----------------------------------------------------------------------
    // Mapping
    // -----------------------------------------------------------------------

    private User mapToUser(String[] row) {
        return new User(
            row[0],
            row[1],
            row[2],
            UserRole.valueOf(row[3]),
            CsvUtil.getOrDefault(row, 4, null)
        );
    }

    private String[] mapToRow(User u) {
        return new String[]{
            u.getUserId(),
            u.getUsername(),
            u.getPasswordHash(),
            u.getRole().name(),
            u.getCustomerId() != null ? u.getCustomerId() : ""
        };
    }
}

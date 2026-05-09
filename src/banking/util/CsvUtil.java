package banking.util;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * CsvUtil — utility statis untuk operasi CSV dan keamanan dasar.
 *
 * Tanggung jawab:
 *  - Membaca semua baris dari file CSV (lewati header)
 *  - Menulis ulang seluruh file CSV (untuk update/delete)
 *  - Menambah satu baris ke file CSV (append — untuk transaksi)
 *  - Hash password dengan SHA-256
 *  - Escape/unescape nilai yang mengandung koma
 */
public class CsvUtil {

    private CsvUtil() {} // Utility class — tidak perlu di-instantiate

    // -----------------------------------------------------------------------
    // File I/O
    // -----------------------------------------------------------------------

    /**
     * Baca semua baris data dari file CSV.
     * Baris pertama (header) dilewati.
     * Baris kosong diabaikan.
     *
     * @param filePath path lengkap ke file CSV
     * @return List of String[], setiap elemen adalah array kolom satu baris
     */
    public static List<String[]> readAll(String filePath) throws IOException {
        List<String[]> rows = new ArrayList<>();
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) return rows; // file belum ada = kosong

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                line = line.trim();
                if (!line.isEmpty()) {
                    rows.add(parseLine(line));
                }
            }
        }
        return rows;
    }

    /**
     * Tulis ulang seluruh file CSV.
     * Digunakan untuk operasi UPDATE dan DELETE.
     *
     * @param filePath path ke file CSV
     * @param header   baris header, misal "id,name,email"
     * @param rows     semua baris data yang akan ditulis
     */
    public static void writeAll(String filePath, String header,
                                List<String[]> rows) throws IOException {
        ensureParentDirs(filePath);
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, false))) {
            pw.println(header);
            for (String[] row : rows) {
                pw.println(joinLine(row));
            }
        }
    }

    /**
     * Tambah satu baris ke akhir file CSV (append).
     * Digunakan untuk INSERT (terutama transaksi).
     * Jika file belum ada, buat dengan header terlebih dahulu.
     *
     * @param filePath path ke file CSV
     * @param header   header (dipakai hanya jika file baru dibuat)
     * @param row      kolom-kolom satu baris data
     */
    public static void appendRow(String filePath, String header,
                                 String[] row) throws IOException {
        ensureParentDirs(filePath);
        boolean fileExists = Files.exists(Paths.get(filePath));
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, true))) {
            if (!fileExists) pw.println(header); // tulis header jika file baru
            pw.println(joinLine(row));
        }
    }

    // -----------------------------------------------------------------------
    // Parse & Format
    // -----------------------------------------------------------------------

    /**
     * Parse satu baris CSV menjadi array String.
     * Mendukung nilai yang di-wrap tanda kutip (mengandung koma).
     */
    public static String[] parseLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        tokens.add(current.toString().trim());
        return tokens.toArray(new String[0]);
    }

    /**
     * Gabungkan array kolom menjadi satu baris CSV.
     * Nilai yang mengandung koma atau spasi akan di-wrap tanda kutip.
     */
    public static String joinLine(String[] columns) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            String val = columns[i] == null ? "" : columns[i];
            if (val.contains(",") || val.contains("\"")) {
                val = "\"" + val.replace("\"", "\"\"") + "\"";
            }
            sb.append(val);
            if (i < columns.length - 1) sb.append(",");
        }
        return sb.toString();
    }

    /**
     * Ambil nilai dari kolom tertentu, atau default jika index out of bounds / kosong.
     */
    public static String getOrDefault(String[] row, int index, String defaultVal) {
        if (index >= row.length || row[index] == null || row[index].isEmpty())
            return defaultVal;
        return row[index];
    }

    // -----------------------------------------------------------------------
    // Security
    // -----------------------------------------------------------------------

    /**
     * Hash password menggunakan SHA-256.
     * Gunakan ini sebelum menyimpan atau membandingkan password.
     *
     * @param plainPassword password plain text
     * @return string hex 64 karakter hasil SHA-256
     */
    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(plainPassword.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 tidak tersedia di JVM ini.", e);
        }
    }

    // -----------------------------------------------------------------------
    // ID Generator
    // -----------------------------------------------------------------------

    /**
     * Generate ID baru berdasarkan prefix dan jumlah data yang sudah ada.
     * Contoh: generateId("C", 5) → "C006"
     */
    public static String generateId(String prefix, int existingCount) {
        return String.format("%s%03d", prefix, existingCount + 1);
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private static void ensureParentDirs(String filePath) throws IOException {
        Path parent = Paths.get(filePath).getParent();
        if (parent != null) Files.createDirectories(parent);
    }
}

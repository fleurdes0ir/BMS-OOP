package banking.repository;

import banking.model.Transaction;
import banking.model.enums.TransactionType;
import banking.util.AppConfig;
import banking.util.CsvUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TransactionRepository — APPEND ONLY.
 *
 * Transaksi tidak pernah diedit atau dihapus — hanya ditambah.
 * Ini mencerminkan prinsip audit trail perbankan.
 *
 * Format baris: txId,type,amount,sourceAccountId,targetAccountId,timestamp,note
 * Index kolom :   0    1     2          3               4             5      6
 */
public class TransactionRepository {

    // -----------------------------------------------------------------------
    // READ
    // -----------------------------------------------------------------------

    public List<Transaction> findAll() throws IOException {
        List<String[]> rows = CsvUtil.readAll(AppConfig.TRANSACTIONS_CSV);
        List<Transaction> list = new ArrayList<>();
        for (String[] row : rows) {
            list.add(mapToTransaction(row));
        }
        return list;
    }

    /** Ambil semua transaksi yang melibatkan satu rekening (sebagai sumber atau target). */
    public List<Transaction> findByAccountId(String accountId) throws IOException {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : findAll()) {
            if (accountId.equals(t.getSourceAccountId()) ||
                accountId.equals(t.getTargetAccountId())) {
                result.add(t);
            }
        }
        return result;
    }

    // -----------------------------------------------------------------------
    // WRITE (append only)
    // -----------------------------------------------------------------------

    public void save(Transaction tx) throws IOException {
        CsvUtil.appendRow(
            AppConfig.TRANSACTIONS_CSV,
            AppConfig.HEADER_TRANSACTIONS,
            mapToRow(tx)
        );
    }

    public String generateNextId() throws IOException {
        return CsvUtil.generateId("T", findAll().size());
    }

    // -----------------------------------------------------------------------
    // Mapping
    // -----------------------------------------------------------------------

    private Transaction mapToTransaction(String[] row) {
        return new Transaction(
            row[0],
            TransactionType.valueOf(row[1]),
            Double.parseDouble(row[2]),
            row[3],
            CsvUtil.getOrDefault(row, 4, null),
            LocalDateTime.parse(row[5].replace(" ", "T")),
            CsvUtil.getOrDefault(row, 6, "")
        );
    }

    private String[] mapToRow(Transaction t) {
        return new String[]{
            t.getTxId(),
            t.getType().name(),
            String.valueOf(t.getAmount()),
            t.getSourceAccountId(),
            t.getTargetAccountId() != null ? t.getTargetAccountId() : "",
            t.getFormattedTimestamp(),
            t.getNote()
        };
    }
}

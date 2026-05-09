package banking.model;

import banking.model.enums.TransactionType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Transaction — Immutable setelah dibuat (append-only, mencerminkan audit trail).
 * Tidak ada setter — semua field di-set saat konstruksi.
 */
public class Transaction {

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String          txId;
    private TransactionType type;
    private double          amount;
    private String          sourceAccountId;
    private String          targetAccountId; // null jika bukan transfer
    private LocalDateTime   timestamp;
    private String          note;

    public Transaction(String txId, TransactionType type, double amount,
                       String sourceAccountId, String targetAccountId,
                       LocalDateTime timestamp, String note) {
        this.txId            = txId;
        this.type            = type;
        this.amount          = amount;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.timestamp       = timestamp;
        this.note            = note;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(FORMATTER);
    }

    public String getFormattedAmount() {
        return String.format("Rp%,.0f", amount);
    }

    // Getters (tidak ada setter — immutable)
    public String          getTxId()            { return txId; }
    public TransactionType getType()            { return type; }
    public double          getAmount()          { return amount; }
    public String          getSourceAccountId() { return sourceAccountId; }
    public String          getTargetAccountId() { return targetAccountId; }
    public LocalDateTime   getTimestamp()       { return timestamp; }
    public String          getNote()            { return note; }

    @Override
    public String toString() {
        return String.format("[%s] %s %s dari %s → %s | %s",
            getFormattedTimestamp(), type, getFormattedAmount(),
            sourceAccountId,
            targetAccountId != null ? targetAccountId : "-",
            note);
    }
}

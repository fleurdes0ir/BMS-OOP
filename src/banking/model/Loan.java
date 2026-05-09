package banking.model;

import banking.model.enums.LoanStatus;
import java.time.LocalDate;

/**
 * Loan (Pinjaman) — implementasi logika angsuran sederhana.
 * Menggunakan metode anuitas flat untuk kalkulasi cicilan.
 */
public class Loan {

    private String     loanId;
    private String     customerId;
    private double     principal;     // pokok pinjaman
    private double     interestRate;  // persen per tahun, misal 12.0
    private int        tenorMonths;   // tenor dalam bulan
    private LocalDate  startDate;
    private LoanStatus status;

    public Loan(String loanId, String customerId, double principal,
                double interestRate, int tenorMonths,
                LocalDate startDate, LoanStatus status) {
        this.loanId       = loanId;
        this.customerId   = customerId;
        this.principal    = principal;
        this.interestRate = interestRate;
        this.tenorMonths  = tenorMonths;
        this.startDate    = startDate;
        this.status       = status;
    }

    /**
     * Hitung cicilan bulanan — metode flat:
     * cicilan = (pokok + total bunga) / tenor
     * total bunga = pokok × rate × (tenor/12)
     */
    public double getMonthlyInstallment() {
        double totalInterest = principal * (interestRate / 100.0) * (tenorMonths / 12.0);
        return (principal + totalInterest) / tenorMonths;
    }

    /**
     * Total yang harus dibayar selama masa pinjaman.
     */
    public double getTotalPayment() {
        return getMonthlyInstallment() * tenorMonths;
    }

    /**
     * Total bunga yang dibayar.
     */
    public double getTotalInterest() {
        return getTotalPayment() - principal;
    }

    public LocalDate getEndDate() {
        return startDate.plusMonths(tenorMonths);
    }

    // Getters
    public String     getLoanId()       { return loanId; }
    public String     getCustomerId()   { return customerId; }
    public double     getPrincipal()    { return principal; }
    public double     getInterestRate() { return interestRate; }
    public int        getTenorMonths()  { return tenorMonths; }
    public LocalDate  getStartDate()    { return startDate; }
    public LoanStatus getStatus()       { return status; }

    // Setter status (untuk update saat lunas)
    public void setStatus(LoanStatus status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("[%s] Rp%,.0f | %d bulan | cicilan Rp%,.0f | %s",
            loanId, principal, tenorMonths, getMonthlyInstallment(), status);
    }
}

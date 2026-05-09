package banking.repository;

import banking.model.Loan;
import banking.model.enums.LoanStatus;
import banking.util.AppConfig;
import banking.util.CsvUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * LoanRepository — mengelola baca/tulis loans.csv.
 *
 * Format baris: loanId,customerId,principal,interestRate,tenorMonths,startDate,status
 * Index kolom :    0       1          2           3            4         5        6
 */
public class LoanRepository {

    public List<Loan> findAll() throws IOException {
        List<String[]> rows = CsvUtil.readAll(AppConfig.LOANS_CSV);
        List<Loan> loans = new ArrayList<>();
        for (String[] row : rows) {
            loans.add(mapToLoan(row));
        }
        return loans;
    }

    public List<Loan> findByCustomerId(String customerId) throws IOException {
        List<Loan> result = new ArrayList<>();
        for (Loan l : findAll()) {
            if (l.getCustomerId().equals(customerId)) result.add(l);
        }
        return result;
    }

    public Optional<Loan> findById(String loanId) throws IOException {
        return findAll().stream()
            .filter(l -> l.getLoanId().equals(loanId))
            .findFirst();
    }

    public void save(Loan loan) throws IOException {
        CsvUtil.appendRow(
            AppConfig.LOANS_CSV,
            AppConfig.HEADER_LOANS,
            mapToRow(loan)
        );
    }

    public void update(Loan updated) throws IOException {
        List<Loan> all = findAll();
        List<String[]> rows = new ArrayList<>();
        for (Loan l : all) {
            rows.add(l.getLoanId().equals(updated.getLoanId())
                ? mapToRow(updated) : mapToRow(l));
        }
        CsvUtil.writeAll(AppConfig.LOANS_CSV, AppConfig.HEADER_LOANS, rows);
    }

    public String generateNextId() throws IOException {
        return CsvUtil.generateId("L", findAll().size());
    }

    // -----------------------------------------------------------------------
    // Mapping
    // -----------------------------------------------------------------------

    private Loan mapToLoan(String[] row) {
        return new Loan(
            row[0],
            row[1],
            Double.parseDouble(row[2]),
            Double.parseDouble(row[3]),
            Integer.parseInt(row[4]),
            LocalDate.parse(row[5]),
            LoanStatus.valueOf(row[6])
        );
    }

    private String[] mapToRow(Loan l) {
        return new String[]{
            l.getLoanId(),
            l.getCustomerId(),
            String.valueOf(l.getPrincipal()),
            String.valueOf(l.getInterestRate()),
            String.valueOf(l.getTenorMonths()),
            l.getStartDate().toString(),
            l.getStatus().name()
        };
    }
}

# Git Workflow — NusaBank BMS-OOP

## Struktur Branch

```
main   ←── kode final, stabil, hanya diisi via Pull Request dari dev
  ↑
dev    ←── branch utama development, semua fitur bermuara di sini
  ↑
feat/xxx   ←── branch per fitur (dibuat dari dev, merge kembali ke dev)
fix/xxx    ←── branch per bugfix
```

## Aturan Utama

| Branch | Boleh direct push? | Cara update |
|---|---|---|
| `main` | ❌ Tidak | Pull Request dari `dev` saja |
| `dev` | ✅ Boleh | Push langsung atau merge dari `feat/` |
| `feat/xxx` | ✅ Boleh | Push bebas, lalu merge ke `dev` |

---

## Alur Kerja Mingguan

### Minggu 1 — Project Setup & Fondasi OOP
**Fokus:** Inisialisasi project, struktur package, class skeleton

```bash
# Buat branch untuk minggu ini
git checkout dev
git checkout -b feat/week1-project-setup

# ... kerjakan: setup NetBeans, buat package structure,
#     buat class skeleton (Account, Customer, User, Transaction, Loan)

# Selesai, simpan ke dev
git add .
git commit -m "feat: initial project setup and OOP class skeleton"
git push origin feat/week1-project-setup

# Merge ke dev
git checkout dev
git merge feat/week1-project-setup
git push origin dev
```

**Deliverable:**
- [ ] Struktur package `banking.model`, `banking.repository`, `banking.service`, `banking.util`, `banking.ui`
- [ ] Semua class skeleton bisa compile
- [ ] Enum: `AccountType`, `TransactionType`, `UserRole`, `LoanStatus`

---

### Minggu 2 — Model Layer & Interface
**Fokus:** Implementasi penuh semua class model, pilar OOP terlihat jelas

```bash
git checkout dev
git checkout -b feat/week2-model-layer

# ... kerjakan: Account (abstract), SavingsAccount, CurrentAccount,
#     DepositAccount, Transactable (interface), Customer, User,
#     Transaction (immutable), Loan

git add .
git commit -m "feat: implement full model layer with OOP pillars"
git push origin feat/week2-model-layer

git checkout dev
git merge feat/week2-model-layer
git push origin dev
```

**Deliverable:**
- [ ] `Transactable` interface dengan 3 method
- [ ] `Account` abstract class dengan `calculateInterest()` abstract
- [ ] 3 subclass dengan `withdraw()` yang berbeda (Polymorphism)
- [ ] `Transaction` immutable (tidak ada setter)
- [ ] `Loan` dengan `getMonthlyInstallment()`

---

### Minggu 3 — Utility & CSV Foundation
**Fokus:** CsvUtil, AppConfig, DataSeeder — fondasi semua I/O data

```bash
git checkout dev
git checkout -b feat/week3-csv-utility

# ... kerjakan: CsvUtil (readAll, writeAll, appendRow, parseLine,
#     joinLine, hashPassword, generateId), AppConfig, DataSeeder

git add .
git commit -m "feat: implement CSV utility layer and data seeder"
git push origin feat/week3-csv-utility

git checkout dev
git merge feat/week3-csv-utility
git push origin dev
```

**Deliverable:**
- [ ] `CsvUtil.readAll()` bisa baca file CSV dengan header
- [ ] `CsvUtil.appendRow()` bisa tambah baris baru
- [ ] `CsvUtil.hashPassword()` SHA-256 berjalan
- [ ] `DataSeeder.seed()` membuat 5 file CSV dengan data awal
- [ ] Folder `data/` terbuat otomatis saat pertama run

---

### Minggu 4 — Repository Layer
**Fokus:** Semua Repository class — jembatan antara model dan CSV

```bash
git checkout dev
git checkout -b feat/week4-repository-layer

# ... kerjakan: CustomerRepository, AccountRepository (penting:
#     reconstruct polymorphism saat load), TransactionRepository
#     (append-only), UserRepository, LoanRepository

git add .
git commit -m "feat: implement repository layer with CSV persistence"
git push origin feat/week4-repository-layer

git checkout dev
git merge feat/week4-repository-layer
git push origin dev
```

**Deliverable:**
- [ ] `AccountRepository.mapToAccount()` reconstruct subclass yang tepat berdasarkan kolom `type`
- [ ] `TransactionRepository` hanya append, tidak pernah edit/delete
- [ ] Semua repository punya `findAll()`, `save()`, `generateNextId()`
- [ ] Test manual via `main()`: load data dari CSV dan print ke console

---

### Minggu 5 — Service Layer & Business Logic
**Fokus:** BankService dan AuthService — semua aturan bisnis di sini

```bash
git checkout dev
git checkout -b feat/week5-service-layer

# ... kerjakan: AuthService (Singleton, login, logout, requireAdmin),
#     BankService (deposit, withdraw, transfer, CRUD nasabah/rekening,
#     loan management)

git add .
git commit -m "feat: implement service layer with business logic"
git push origin feat/week5-service-layer

git checkout dev
git merge feat/week5-service-layer
git push origin dev
```

**Deliverable:**
- [ ] `AuthService` Singleton berjalan (hanya 1 instance)
- [ ] `AuthService.login()` verifikasi hash password
- [ ] `BankService.transfer()` membuat 2 record transaksi (TRANSFER_OUT + TRANSFER_IN)
- [ ] `BankService.deposit/withdraw()` update saldo dan simpan ke CSV
- [ ] `requireAdmin()` throw exception jika bukan admin

---

### Minggu 6 — UI JavaFX: Login & Dashboard
**Fokus:** Setup JavaFX, ThemeManager, LoginScreen, MainScreen dengan sidebar

```bash
git checkout dev
git checkout -b feat/week6-ui-login-dashboard

# ... kerjakan: setup JavaFX di NetBeans, theme.css (dark+light),
#     ThemeManager (Singleton), SceneManager, LoginScreen,
#     MainScreen (topbar + sidebar + content area)

git add .
git commit -m "feat: implement JavaFX UI - login screen and main dashboard"
git push origin feat/week6-ui-login-dashboard

git checkout dev
git merge feat/week6-ui-login-dashboard
git push origin dev
```

**Deliverable:**
- [ ] JavaFX berjalan di NetBeans (window muncul)
- [ ] `theme.css` dengan variable dark dan light mode
- [ ] `ThemeManager` toggle dark/light, persisten antar navigasi
- [ ] Login berhasil masuk ke Dashboard
- [ ] Sidebar navigasi berfungsi (highlight menu aktif)
- [ ] Logout kembali ke halaman Login

---

### Minggu 7 — UI JavaFX: Semua Panel Fitur
**Fokus:** DashboardPanel, CustomerPanel, AccountPanel, TransactionPanel, LoanPanel

```bash
git checkout dev
git checkout -b feat/week7-ui-feature-panels

# ... kerjakan: DashboardPanel (metric cards + recent transactions),
#     CustomerPanel (tabel + CRUD + search), AccountPanel (tabel + buka rekening),
#     TransactionPanel (deposit/tarik/transfer + riwayat + filter),
#     LoanPanel (tabel + ajukan pinjaman + preview cicilan realtime)

git add .
git commit -m "feat: implement all feature panels - customer, account, transaction, loan"
git push origin feat/week7-ui-feature-panels

git checkout dev
git merge feat/week7-ui-feature-panels
git push origin dev

# Minggu 7 selesai = feature complete
# Merge dev ke main sebagai versi v1.0
git checkout main
git merge dev
git push origin main

# Tag versi release
git tag -a v1.0.0 -m "release: NusaBank v1.0.0 - feature complete"
git push origin v1.0.0
```

**Deliverable:**
- [ ] Dashboard menampilkan metric cards dari data CSV
- [ ] CustomerPanel: tambah, edit, hapus, cari nasabah
- [ ] AccountPanel: lihat rekening, buka rekening baru (3 jenis)
- [ ] TransactionPanel: deposit, tarik, transfer, riwayat, filter
- [ ] LoanPanel: lihat pinjaman, ajukan baru, preview cicilan realtime
- [ ] Role-based access: nasabah hanya lihat data sendiri
- [ ] Data persist setelah restart (TC-24)
- [ ] Semua 25 test case lulus

---

## Perintah Git Harian (Quick Reference)

```bash
# Mulai hari kerja — ambil update terbaru
git checkout dev
git pull origin dev

# Buat branch untuk fitur baru
git checkout -b feat/nama-fitur

# Simpan progress (lakukan sesering mungkin)
git add .
git commit -m "feat: deskripsi singkat perubahan"

# Upload ke GitHub
git push origin feat/nama-fitur

# Selesai fitur — merge ke dev
git checkout dev
git merge feat/nama-fitur
git push origin dev

# Hapus branch fitur yang sudah selesai (opsional, biar rapi)
git branch -d feat/nama-fitur
git push origin --delete feat/nama-fitur
```

## Format Pesan Commit

| Prefix | Kapan dipakai | Contoh |
|---|---|---|
| `feat:` | Fitur baru | `feat: add customer search filter` |
| `fix:` | Bugfix | `fix: resolve overdraft notification missing` |
| `refactor:` | Refactor kode | `refactor: simplify CsvUtil parseLine method` |
| `docs:` | Update dokumentasi | `docs: update README screenshot section` |
| `chore:` | Setup/config | `chore: add JavaFX lib to gitignore` |
| `test:` | Tambah/update test | `test: verify TC-19 overdraft scenario` |

---

## Minggu 8-12 — Dokumentasi (Referensi Branch)

```bash
# Branch khusus dokumentasi
git checkout dev
git checkout -b docs/academic-report

# Setiap update dokumen
git add .
git commit -m "docs: complete chapter 3 system analysis"
git push origin docs/academic-report

# Merge ke dev setelah dokumen final
git checkout dev
git merge docs/academic-report
git push origin dev
```

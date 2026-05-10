# Transfer Knowledge — NusaBank Banking Management System

> Dokumen ini ditujukan untuk anggota tim yang akan melanjutkan,
> memahami, atau mendokumentasikan project ini secara akademik.

---

## Gambaran Besar Project

NusaBank adalah aplikasi desktop simulasi sistem manajemen perbankan yang dibangun menggunakan **Java + JavaFX**, dengan penyimpanan data berbasis **file CSV**. Project ini adalah implementasi nyata dari keempat pilar OOP dalam satu sistem yang kohesif.

### Analogi Sederhana

Bayangkan sebuah bank sungguhan:
- **Nasabah** datang membuka rekening → `Customer` + `Account`
- **Teller** memproses setoran/penarikan → `BankService.deposit()` / `withdraw()`
- **Buku tabungan** mencatat setiap transaksi → `transactions.csv`
- **Manajer** punya akses penuh, nasabah hanya akses data sendiri → Role system

Project ini mensimulasikan semua proses di atas dalam bentuk aplikasi desktop.

---

## Empat Pilar OOP — Penjelasan Awam

### 1. Encapsulation (Enkapsulasi)
**Analogi:** Brankas bank. Uang di dalam brankas tidak bisa diambil sembarangan — harus lewat prosedur resmi (teller, PIN, otorisasi).

**Di project ini:** Semua data dalam class seperti `Customer`, `Account`, `User` disimpan sebagai `private`. Untuk membaca atau mengubahnya, harus lewat method getter/setter yang terkontrol.

```java
// SALAH — tidak bisa akses langsung
customer.name = "Budi";  // error!

// BENAR — lewat setter yang terkontrol
customer.setName("Budi");  // harus lewat method resmi
```

**File yang mendemonstrasikan ini:**
- `src/banking/model/Customer.java`
- `src/banking/model/User.java`

---

### 2. Inheritance (Pewarisan)
**Analogi:** Jenis rekening di bank. Semua rekening punya nomor rekening dan saldo (sama), tapi rekening Tabungan, Giro, dan Deposito punya aturan berbeda.

**Di project ini:** `SavingsAccount`, `CurrentAccount`, dan `DepositAccount` semuanya mewarisi dari `Account`. Mereka berbagi atribut dasar (accountId, balance) tapi punya perilaku unik.

```
Account (abstract — tidak bisa dibuat langsung)
    ├── SavingsAccount  (Tabungan — ada saldo minimum)
    ├── CurrentAccount  (Giro — boleh overdraft)
    └── DepositAccount  (Deposito — ada tenor dan penalti)
```

**File yang mendemonstrasikan ini:**
- `src/banking/model/Account.java` — class induk
- `src/banking/model/SavingsAccount.java`
- `src/banking/model/CurrentAccount.java`
- `src/banking/model/DepositAccount.java`

---

### 3. Polymorphism (Polimorfisme)
**Analogi:** Mesin ATM. Tombol "Tarik Tunai" di ATM akan melakukan hal berbeda tergantung jenis kartu yang dimasukkan — kartu Tabungan ada batas minimum saldo, kartu Giro boleh overdraft.

**Di project ini:** Method `withdraw()` dipanggil dengan cara sama, tapi hasilnya berbeda tergantung jenis rekening.

```java
account.withdraw(500000);
// Jika account adalah SavingsAccount → cek saldo minimum Rp50.000
// Jika account adalah CurrentAccount → boleh minus sampai overdraftLimit
// Jika account adalah DepositAccount → kena penalti jika belum jatuh tempo
```

**File yang mendemonstrasikan ini:**
- Method `withdraw()` di ketiga subclass Account

---

### 4. Abstraction (Abstraksi)
**Analogi:** Formulir transaksi bank. Nasabah cukup isi formulir (deposit/tarik/transfer) tanpa perlu tahu proses internal bank di belakangnya.

**Di project ini:** Interface `Transactable` mendefinisikan "kontrak" — siapapun yang implement interface ini wajib bisa `deposit()`, `withdraw()`, dan `transfer()`. Detail implementasinya berbeda-beda.

```java
// Interface = kontrak
public interface Transactable {
    void deposit(double amount);
    void withdraw(double amount);
    void transfer(double amount, Account target);
}
```

**File yang mendemonstrasikan ini:**
- `src/banking/model/Transactable.java`

---

## Arsitektur Berlapis (Layer Architecture)

Project ini menggunakan 4 layer yang terpisah jelas:

```
┌─────────────────────────────────┐
│         UI Layer (JavaFX)       │  ← Yang dilihat pengguna
│  LoginScreen, DashboardPanel,   │
│  CustomerPanel, dll             │
├─────────────────────────────────┤
│       Service Layer             │  ← Aturan bisnis
│  BankService, AuthService       │
├─────────────────────────────────┤
│      Repository Layer           │  ← Baca/tulis CSV
│  CustomerRepository,            │
│  AccountRepository, dll         │
├─────────────────────────────────┤
│        Model Layer              │  ← Struktur data (OOP core)
│  Account, Customer, Transaction │
└─────────────────────────────────┘
         ↕ Data
┌─────────────────────────────────┐
│      Storage (CSV files)        │
│  customers.csv, accounts.csv,   │
│  transactions.csv, dll          │
└─────────────────────────────────┘
```

**Aturan penting:** UI tidak boleh langsung akses Repository. UI harus lewat Service dulu.

```
UI → Service → Repository → CSV   ✅ BENAR
UI → Repository → CSV             ❌ SALAH
```

---

## Alur Data: Dari Klik Tombol Sampai CSV

Contoh: pengguna klik tombol **"Deposit"** di UI.

```
1. TransactionPanel.showTransactionDialog("DEPOSIT")
   └── pengguna isi form: rekening A001, nominal Rp500.000

2. BankService.deposit("A001", 500000, "Setoran tunai")
   ├── AccountRepository.findById("A001") → load dari accounts.csv
   ├── account.deposit(500000) → saldo bertambah (di memori)
   ├── AccountRepository.update(account) → tulis ulang accounts.csv
   ├── Transaction tx = new Transaction(...) → buat objek transaksi
   └── TransactionRepository.save(tx) → append ke transactions.csv

3. UI refresh → tabel transaksi menampilkan data terbaru dari CSV
```

---

## Struktur File CSV

Semua data disimpan di folder `data/` (dibuat otomatis saat pertama run).

### customers.csv
```
customerId,name,email,phone,createdAt
C001,Budi Santoso,budi@email.com,08123456789,2024-01-15
```

### accounts.csv
```
accountId,customerId,type,balance,interestRate,overdraftLimit,tenorMonths,createdAt
A001,C001,SAVINGS,15000000,2.5,0,0,2024-01-15
```
Kolom `type` (SAVINGS/CURRENT/DEPOSIT) digunakan untuk menentukan subclass saat data dibaca kembali.

### transactions.csv
```
txId,type,amount,sourceAccountId,targetAccountId,timestamp,note
T001,DEPOSIT,1000000,A001,,2024-01-15 09:00:00,Setoran awal
```
File ini **append-only** — tidak pernah diedit, hanya ditambah. Ini mencerminkan prinsip audit trail.

### users.csv
```
userId,username,passwordHash,role,customerId
U001,admin,<hash SHA-256>,ADMIN,
U002,budi.santoso,<hash SHA-256>,CUSTOMER,C001
```
Password disimpan sebagai **hash SHA-256**, bukan plain text.

### loans.csv
```
loanId,customerId,principal,interestRate,tenorMonths,startDate,status
L001,C001,50000000,12.0,24,2024-03-01,ACTIVE
```

---

## Design Pattern yang Digunakan

### Singleton Pattern
Digunakan di `AuthService` dan `ThemeManager`.

**Masalah yang diselesaikan:** Kalau ada dua instance `AuthService`, bisa terjadi kondisi di mana satu instance bilang "sudah login" tapi instance lain bilang "belum login".

**Solusinya:** Singleton memastikan hanya ada **satu instance** selama aplikasi berjalan.

```java
// Cara pakai — selalu dapat instance yang SAMA
AuthService auth = AuthService.getInstance();
```

### Repository Pattern
Setiap entity punya repository sendiri yang bertanggung jawab atas operasi CSV-nya.

**Masalah yang diselesaikan:** Kalau logic baca/tulis CSV tersebar di mana-mana, sulit dimaintain.

**Solusinya:** Semua operasi CSV untuk `Customer` ada di `CustomerRepository`, untuk `Account` ada di `AccountRepository`, dll.

---

## Cara Menjalankan Project

### Prasyarat
1. Java JDK 17 ke atas (project ini pakai Java 25)
2. JavaFX SDK 21 ke atas (project ini pakai JavaFX 25.0.3)
3. NetBeans IDE

### Langkah Setup
1. Clone repo:
   ```bash
   git clone https://github.com/fleurdes0ir/BMS-OOP.git
   ```
2. Buka NetBeans → **File → Open Project** → pilih folder `NusaBank`
3. Konfigurasi JavaFX di `nbproject/project.properties`:
   ```properties
   javac.compilerargs=--module-path "PATH_JAVAFX/lib" --add-modules javafx.controls,javafx.fxml
   run.jvmargs=--module-path "PATH_JAVAFX/lib" --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics -Djava.library.path="PATH_JAVAFX/bin"
   ```
   Ganti `PATH_JAVAFX` dengan lokasi JavaFX SDK di komputermu.
4. **Clean and Build** (`Shift+F11`) → **Run** (`F6`)
5. Login dengan akun demo:
   - Admin: `admin` / `admin123`
   - Nasabah: `budi.santoso` / `nasabah123`

### Catatan Penting
- Folder `data/` **tidak ada di repo** — akan dibuat otomatis saat pertama kali Run
- File `.dll` JavaFX tidak di-include di repo — harus install SDK sendiri
- Kalau mau reset data ke awal, hapus semua file di folder `data/` lalu Run ulang

---

## Fitur per Panel

| Panel | Akses | Fitur |
|---|---|---|
| **Login** | Semua | Login, dark/light toggle |
| **Dashboard** | Semua | Ringkasan metrik, transaksi terbaru |
| **Nasabah** | Admin only | Lihat, tambah, edit, hapus, cari nasabah |
| **Rekening** | Semua | Lihat rekening; Admin bisa buka rekening baru |
| **Transaksi** | Semua | Deposit, tarik, transfer, riwayat, filter |
| **Pinjaman** | Semua | Lihat pinjaman; ajukan baru dengan preview cicilan |

---

## Akun Demo

| Username | Password | Role | Nasabah |
|---|---|---|---|
| `admin` | `admin123` | Administrator | — |
| `budi.santoso` | `nasabah123` | Nasabah | Budi Santoso (C001) |
| `sari.dewi` | `nasabah123` | Nasabah | Sari Dewi (C002) |

---

## Pertanyaan yang Mungkin Ditanya Dosen

**Q: Mengapa Account dibuat abstract?**
A: Karena bank tidak pernah membuka "rekening generik" — selalu salah satu dari Tabungan, Giro, atau Deposito. Dengan abstract, kita memaksa developer untuk memilih subclass yang spesifik.

**Q: Mengapa Transaction tidak punya setter?**
A: Karena transaksi keuangan bersifat immutable — setelah terjadi, tidak boleh diubah. Ini mencerminkan prinsip audit trail perbankan nyata.

**Q: Mengapa AuthService menggunakan Singleton?**
A: Karena session login harus konsisten di seluruh aplikasi. Kalau ada dua instance AuthService, bisa terjadi konflik state.

**Q: Mengapa penyimpanan menggunakan CSV bukan database?**
A: Untuk kesederhanaan implementasi di level akademik. Arsitektur Repository Pattern yang digunakan memungkinkan migrasi ke database (MySQL/PostgreSQL) di masa depan hanya dengan mengganti implementasi Repository — tanpa mengubah Model atau Service sama sekali.

**Q: Bagaimana Polymorphism terbukti di aplikasi ini?**
A: Coba tarik dana dari rekening Tabungan sampai mendekati saldo minimum — akan muncul error. Lakukan hal yang sama di rekening Giro — berhasil karena boleh overdraft. Method `withdraw()` dipanggil sama, tapi hasilnya berbeda karena subclass berbeda.

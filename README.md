# 🏦 NusaBank — Banking Management System

> Aplikasi manajemen perbankan berbasis desktop yang dibangun menggunakan **Java + JavaFX**, mengimplementasikan seluruh pilar *Object-Oriented Programming* (OOP) sebagai project akhir semester mata kuliah OOP.

---

## 📸 Preview

> *Tambahkan screenshot aplikasi di sini setelah push pertama.*
> Caranya: jalankan aplikasi → screenshot tiap panel → simpan di folder `docs/screenshots/` → ganti teks ini dengan:
> ```
> ![Login Screen](docs/screenshots/login.png)
> ![Dashboard](docs/screenshots/dashboard.png)
> ```

---

## 🎯 Latar Belakang

Industri perbankan mengelola ribuan data nasabah, rekening, dan transaksi secara bersamaan. Pengelolaan yang tidak terstruktur berpotensi menimbulkan kesalahan data dan inefisiensi layanan. Project ini hadir sebagai simulasi sistem manajemen perbankan yang mendemonstrasikan bagaimana paradigma OOP dapat memodelkan entitas perbankan secara natural dan terstruktur.

---

## ✨ Fitur Utama

| Fitur | Deskripsi |
|---|---|
| 🔐 **Autentikasi & Role** | Login dengan dua role: Admin dan Nasabah. Akses dikontrol berdasarkan role. |
| 👥 **Manajemen Nasabah** | CRUD data nasabah lengkap dengan fitur pencarian realtime. |
| 💳 **Manajemen Rekening** | Mendukung tiga jenis rekening: Tabungan, Giro, dan Deposito. |
| ↔️ **Pemrosesan Transaksi** | Deposit, penarikan, dan transfer antar rekening dengan validasi bisnis. |
| 📋 **Manajemen Pinjaman** | Pengajuan pinjaman dengan kalkulasi cicilan dan preview realtime. |
| 🌙 **Dark / Light Mode** | Toggle tema gelap dan terang yang konsisten di semua panel. |
| 💾 **Penyimpanan CSV** | Data disimpan secara permanen dalam file `.csv` tanpa database eksternal. |

---

## 🏗️ Arsitektur & Pilar OOP

Project ini dirancang dengan **4-layer architecture** yang memisahkan tanggung jawab secara bersih:

```
┌─────────────────────────────────────┐
│           UI Layer (JavaFX)         │  ← Swing diganti JavaFX untuk tampilan modern
├─────────────────────────────────────┤
│         Service Layer               │  ← Business logic & validasi
├─────────────────────────────────────┤
│        Repository Layer             │  ← Akses & manipulasi file CSV
├─────────────────────────────────────┤
│          Model Layer                │  ← Entitas OOP inti
└─────────────────────────────────────┘
```

### Implementasi Pilar OOP

#### 1. Encapsulation
Semua field di class model bersifat `private`. Data hanya bisa diakses melalui getter/setter yang terkontrol.
```java
// Class Customer — field sensitif terlindungi
private String customerId;
private String email;
private String phone;

public String getEmail() { return email; }
public void setEmail(String email) { this.email = email; }
```

#### 2. Inheritance
`Account` adalah abstract class yang diwarisi oleh tiga subclass sesuai jenis rekening.
```
Account (abstract)
├── SavingsAccount   → aturan saldo minimum
├── CurrentAccount   → fitur overdraft
└── DepositAccount   → tenor + penalti penarikan dini
```

#### 3. Polymorphism
Method `withdraw()` dan `calculateInterest()` diimplementasikan berbeda di tiap subclass.
```java
// SavingsAccount — tidak boleh di bawah saldo minimum
account.withdraw(500_000); // validasi berbeda per jenis rekening

// CurrentAccount — boleh negatif sampai overdraft limit
// DepositAccount — kena penalti jika belum jatuh tempo
```

#### 4. Abstraction
`Transactable` interface mendefinisikan kontrak transaksi. `Account` abstract class menyembunyikan detail implementasi dari layer service.
```java
public interface Transactable {
    void deposit(double amount);
    void withdraw(double amount) throws IllegalArgumentException;
    void transfer(double amount, Account target) throws IllegalArgumentException;
}
```

### Design Patterns

| Pattern | Implementasi |
|---|---|
| **Singleton** | `AuthService` — satu sesi login aktif di seluruh aplikasi |
| **Singleton** | `ThemeManager` — state tema konsisten di semua screen |
| **Repository** | Setiap entitas punya repository tersendiri untuk akses CSV |

---

## 🗂️ Struktur Project

```
NusaBank/
├── src/banking/
│   ├── Main.java                    ← Entry point JavaFX
│   ├── model/                       ← Entitas OOP
│   │   ├── Account.java             ← Abstract class
│   │   ├── SavingsAccount.java
│   │   ├── CurrentAccount.java
│   │   ├── DepositAccount.java
│   │   ├── Customer.java
│   │   ├── User.java
│   │   ├── Transaction.java         ← Immutable (audit trail)
│   │   ├── Loan.java
│   │   └── enums/                   ← AccountType, TransactionType, UserRole, LoanStatus
│   ├── repository/                  ← Akses file CSV
│   │   ├── AccountRepository.java
│   │   ├── CustomerRepository.java
│   │   ├── TransactionRepository.java
│   │   ├── UserRepository.java
│   │   └── LoanRepository.java
│   ├── service/                     ← Business logic
│   │   ├── BankService.java
│   │   └── AuthService.java         ← Singleton
│   ├── ui/                          ← JavaFX UI
│   │   ├── ThemeManager.java        ← Singleton
│   │   ├── SceneManager.java
│   │   ├── screens/
│   │   │   ├── LoginScreen.java
│   │   │   └── MainScreen.java
│   │   ├── panels/
│   │   │   ├── DashboardPanel.java
│   │   │   ├── CustomerPanel.java
│   │   │   ├── AccountPanel.java
│   │   │   ├── TransactionPanel.java
│   │   │   └── LoanPanel.java
│   │   └── styles/
│   │       └── theme.css            ← Dark & Light theme
│   └── util/
│       ├── CsvUtil.java             ← Parser, writer, SHA-256 hash
│       ├── AppConfig.java           ← Konfigurasi path terpusat
│       └── DataSeeder.java          ← Data awal otomatis
└── data/                            ← File CSV (auto-generated)
    ├── customers.csv
    ├── accounts.csv
    ├── transactions.csv
    ├── users.csv
    └── loans.csv
```

---

## 🚀 Cara Menjalankan

### Prasyarat
- **Java 17+** (tested on Java 25 LTS)
- **JavaFX SDK 24+** — download di [gluonhq.com/products/javafx](https://gluonhq.com/products/javafx/)
- **Apache NetBeans** dengan Ant

### Langkah Setup

**1. Clone repository**
```bash
git clone https://github.com/USERNAME/NusaBank.git
cd NusaBank
```

**2. Tambahkan JavaFX ke NetBeans**
- Tools → Libraries → New Library → beri nama `JavaFX`
- Add JAR: arahkan ke folder `lib/` dari JavaFX SDK
- Project Properties → Libraries → Add Library → pilih `JavaFX`

**3. Set VM Options**

Di Project Properties → Run → VM Options:
```
--module-path "PATH_TO_JAVAFX/lib" --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics -Djava.library.path="PATH_TO_JAVAFX/bin"
```

**4. Tambahkan ke `nbproject/project.properties`**
```properties
javac.compilerargs=--module-path "PATH_TO_JAVAFX\\lib" --add-modules javafx.controls,javafx.fxml
run.jvmargs=--module-path "PATH_TO_JAVAFX\\lib" --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics -Djava.library.path="PATH_TO_JAVAFX\\bin"
```

**5. Build & Run**
```
Shift + F11  →  Clean and Build
F6           →  Run
```

Folder `data/` akan otomatis dibuat beserta data awal saat pertama kali dijalankan.

---

## 👤 Akun Demo

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | Administrator |
| `budi.santoso` | `nasabah123` | Nasabah |
| `sari.dewi` | `nasabah123` | Nasabah |

---

## 🧪 Test Cases

Project ini telah melalui 25 test case yang mencakup:
- Autentikasi & role-based access
- CRUD nasabah dan rekening
- Validasi transaksi (deposit, tarik, transfer)
- Logika bisnis per jenis rekening (overdraft, saldo minimum, penalti deposito)
- Persistensi data setelah restart

---

## 🛠️ Teknologi

| Teknologi | Versi | Kegunaan |
|---|---|---|
| Java | 25 LTS | Bahasa utama |
| JavaFX | 25.0.3 | UI Framework |
| Apache Ant | Built-in NetBeans | Build tool |
| CSV | — | Penyimpanan data |
| SHA-256 | Java Security | Hash password |

---

## 📚 Mata Kuliah

> Project ini dibuat sebagai **Ujian Akhir Semester (UAS)** mata kuliah **Object-Oriented Programming**.

---

## 👨‍💻 Author

**Anaya Bintang Prawidya .aka fleurdes0ir**
- GitHub: [@fleurdes0ir](https://github.com/fleurdes0ir)
- Email: pr4widyaa@gmail.com

---

*Boosted by ☕ Traktir-Kopi dan semangat belajar OOP.*

# CoSpace - Aplikasi Reservasi Meja dan Ruang Kerja Co-Working Space

Tugas Besar Pemrograman Berorientasi Objek - Kelompok 6
S1 Teknologi Informasi, Fakultas Informatika, Telkom University

CoSpace adalah aplikasi web untuk memesan **hot desk**, **ruang privat**, dan **ruang meeting** di sebuah co-working space.
Sistem otomatis menolak reservasi yang jadwalnya bentrok, menghitung harga sesuai jenis ruang, mendukung kode promo,
pembayaran (transfer bank, e-wallet, tunai), refund, dan laporan pendapatan untuk admin.

## Anggota dan Pembagian Fitur

| No | Nama | NIM | Fitur (PIC) |
|----|------|-----|-------------|
| 1 | Harsya Brahmantyo Wibowo | 103032430021 | Autentikasi dan Manajemen Akun |
| 2 | Zeifira Naila Syakira | 103032400082 | Manajemen Ruang |
| 3 | Neizshia Ratu Rahmadhani Ockry | 103032400027 | Reservasi dan Promo |
| 4 | Salsabila Yurnisa Br Tarigan | 103032400061 | Pembayaran dan Laporan |

## Teknologi

- Java 17 (JSP + Servlet, `javax.servlet` 4.0)
- Apache Tomcat 9 (atau Tomcat bawaan XAMPP)
- MySQL / MariaDB + JDBC (`mysql-connector-j`)
- Maven (project dibuka dengan Apache NetBeans)
- HTML + CSS sederhana (tanpa framework)

## Struktur Folder

```
CoSpace/
├── database/cospace.sql          -> skrip pembuatan database + data awal
├── docs/                         -> laporan, screenshot, hasil pengujian
├── pom.xml
└── src/main/
    ├── java/
    │   ├── models/               -> 17 kelas + 1 interface sesuai class diagram (+ JadwalBentrokException)
    │   ├── database/             -> akses database (JDBC, PreparedStatement)
    │   ├── controllers/          -> Servlet (controller pada MVC)
    │   ├── utils/Helper.java     -> method bantu (format rupiah, escape HTML, dll)
    │   └── driver/Driver.java    -> uji logika kelas lewat method main
    └── webapp/
        ├── css/style.css
        ├── index.jsp
        └── WEB-INF/
            ├── web.xml
            └── views/            -> halaman JSP (view pada MVC)
```

## Cara Menjalankan

### 1. Siapkan database

1. Nyalakan MySQL (XAMPP / Laragon).
2. Buka phpMyAdmin / HeidiSQL, lalu jalankan isi file `database/cospace.sql`.
   File ini otomatis membuat database `cospace`, semua tabel, dan data awal.
3. Jika MySQL kamu memakai password, ubah `USER` / `PASSWORD` di
   `src/main/java/database/Koneksi.java`.

### 2. Siapkan Tomcat di NetBeans

1. Gunakan **Tomcat 9** (atau Tomcat 8.5 bawaan XAMPP). Tomcat 10 ke atas **tidak bisa** karena memakai `jakarta.servlet`.
2. NetBeans: `Tools > Servers > Add Server > Apache Tomcat or TomEE`, pilih folder Tomcat.

### 3. Jalankan project

1. NetBeans: `File > Open Project`, pilih folder `CoSpace`.
2. Klik kanan project > `Properties > Run`, pilih server Tomcat.
3. Klik `Run Project`. Aplikasi terbuka di `http://localhost:8080/CoSpace`.

### Akun Demo

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@cospace.id | admin12345 |
| Member | member@cospace.id | member12345 |

Kode promo contoh: `HEMAT10` (10%), `MAHASISWA20` (20%).

## Menguji Logika Tanpa Web

Buka `src/main/java/driver/Driver.java`, klik kanan > `Run File`.
Driver menguji semua kelas (registrasi, harga ruang, cek bentrok, promo, pembayaran, refund, laporan)
tanpa database. Contoh hasilnya ada di `docs/hasil_driver.txt`.

## Dokumentasi

- `docs/Laporan_Tubes_PBO_Kelompok6_CoSpace.docx` (dan `.pdf`) - laporan akhir
- `docs/PANDUAN_PIC.md` - penjelasan kode untuk setiap anggota (bahan responsi/presentasi)
- `docs/screenshots/` - tangkapan layar semua halaman
- `docs/hasil_pentest.txt` - hasil pengujian keamanan

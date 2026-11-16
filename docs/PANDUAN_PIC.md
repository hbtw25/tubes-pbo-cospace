# Panduan Kode per PIC - CoSpace

Dokumen ini membantu setiap anggota memahami kode fiturnya sendiri sebelum responsi / presentasi
(Modul 15). Baca bagian kamu, buka file-filenya di NetBeans, lalu coba jalankan `driver/Driver.java`.

Alur umum aplikasi (MVC, sama untuk semua fitur):

```
Browser --(request)--> Servlet (controllers/) --> kelas model (models/) untuk logika
                                    |
                                    +--> kelas database (database/) untuk simpan/ambil data MySQL
                                    |
                                    +--> forward ke JSP (WEB-INF/views/) untuk ditampilkan
```

- **Model** (`models/`): kelas sesuai class diagram, berisi atribut, validasi, dan logika.
- **View** (`WEB-INF/views/*.jsp`): hanya menampilkan data. Disimpan di `WEB-INF` supaya tidak bisa dibuka langsung tanpa lewat servlet.
- **Controller** (`controllers/*Servlet.java`): menerima input form, memanggil model & database, lalu memilih JSP.
- Semua query memakai `PreparedStatement` (tanda `?`) supaya aman dari SQL injection.

---

## 1. Harsya - Autentikasi dan Manajemen Akun

**File:** `models/User.java`, `Member.java`, `Admin.java`, `PengelolaAkun.java`,
`database/UserDB.java`, `controllers/AuthServlet.java`, `ProfilServlet.java`,
`views/login.jsp`, `register.jsp`, `profil.jsp`

**Konsep OOP yang dipakai**
- `User` adalah **abstract class** (tidak ada "user umum"), method abstrak `getRole()`.
- `Member` dan `Admin` **mewarisi** `User` dan memanggil `super(...)`.
- **Constructor overloading**: `User(nama, email, password, noHp)` untuk registrasi (password di-hash),
  `User(idUser, ..., passwordHash, ...)` untuk data dari database (tidak di-hash lagi).
- **Encapsulation**: semua atribut `private`; setter berisi validasi (email harus ada @, password min 8, HP 10-13 digit).
  Password tidak punya getter biasa, hanya bisa dicek lewat `cekPassword()`.
- **Agregasi** `PengelolaAkun` - `User` (array `daftarUser`).

**Alur login**: `AuthServlet.doPost` -> ambil semua user dari `UserDB.getSemua()` -> masukkan ke `PengelolaAkun`
-> `pengelola.login(email, password)` -> jika cocok, simpan user ke **session** (`session.setAttribute("user", user)`).
Session lama dibuang dulu (`invalidate`) supaya aman dari *session fixation*.

**Pertanyaan yang mungkin muncul**
- *Kenapa password di-hash?* Supaya kalau database bocor, password asli tidak terbaca. Hash memakai SHA-256 (`hashPassword`).
- *Kenapa constructor ada dua?* Saat registrasi password masih asli (perlu di-hash), saat dari database sudah berupa hash.
- *Bagaimana halaman admin dilindungi?* Setiap servlet admin mengecek `Helper.isAdmin(user)`; jika bukan admin diarahkan ke login / ditolak 403.

## 2. Zeifira - Manajemen Ruang

**File:** `models/Ruang.java`, `HotDesk.java`, `RuangPrivat.java`, `RuangMeeting.java`, `CoworkingSpace.java`,
`database/RuangDB.java`, `CoworkingSpaceDB.java`, `controllers/RuangServlet.java`,
`views/katalog.jsp`, `ruang_admin.jsp`, `form_ruang.jsp`

**Konsep OOP yang dipakai**
- `Ruang` **abstract class** dengan method abstrak `hitungHarga(durasiJam)` dan `getJenis()`.
- **Overriding / polimorfisme** `hitungHarga`:
  - `HotDesk`: tarif x durasi (kapasitas selalu 1).
  - `RuangPrivat`: tarif x durasi, diskon 15% jika >= 8 jam (konstanta `DISKON_HARIAN`, `MIN_JAM_HARIAN`).
  - `RuangMeeting`: tarif x durasi + Rp25.000 jika ada proyektor (`BIAYA_PROYEKTOR`).
- `tarifPerJam` bersifat **protected** supaya bisa dibaca langsung oleh subclass.
- **Agregasi** `CoworkingSpace` - `Ruang` (array `daftarRuang`), **asosiasi** `Admin` - `CoworkingSpace`
  (`admin.tambahRuang(cs, r)` meneruskan ke `cs.tambahRuang(r)`).
- `RuangDB.toRuang()` membuat objek subclass yang tepat berdasarkan kolom `jenis`.

**Pertanyaan yang mungkin muncul**
- *Kenapa ruang tidak dihapus permanen?* Agar riwayat reservasi lama tetap utuh; ruang hanya dinonaktifkan (`setAktif(false)`).
- *Bagaimana filter katalog bekerja?* `filterRuang(jenis, kapasitasMin)` menelusuri array, mengambil ruang aktif yang jenisnya cocok dan kapasitasnya cukup.

## 3. Neizshia - Reservasi dan Promo

**File:** `models/Reservasi.java`, `PengelolaReservasi.java`, `Promo.java`, `JadwalBentrokException.java`,
`database/ReservasiDB.java`, `PromoDB.java`, `controllers/ReservasiServlet.java`, `PromoServlet.java`,
`views/form_reservasi.jsp`, `riwayat.jsp`, `reservasi_admin.jsp`, `promo_admin.jsp`

**Logika penting**
- `Reservasi.bentrokDengan(lain)`: bentrok jika ruang & tanggal sama, reservasi lain belum dibatalkan,
  dan `jamMulai < lain.jamSelesai && jamSelesai > lain.jamMulai`.
- `PengelolaReservasi.buatReservasi(...)`: validasi tanggal tidak lampau, jam mulai < jam selesai, ruang aktif,
  lalu `cekKetersediaan()`. Jika bentrok melempar **custom exception** `JadwalBentrokException` (materi Exception).
- `hitungTotal()`: `ruang.hitungHarga(durasi)` dikurangi `promo.hitungPotongan()`.
- `terapkanPromo(p)`: hanya satu promo per reservasi, promo harus masih berlaku dan kuota ada (`p.gunakan()` mengurangi kuota).
- Status: `Menunggu Pembayaran` -> `Aktif` (setelah bayar) -> `Selesai` (ditandai admin, member dapat poin) atau `Dibatalkan`.
- Konstanta status memakai `public static final String` (materi Static).
- Di `ReservasiServlet` ada `synchronized (KUNCI)` supaya dua orang yang memesan slot sama di waktu bersamaan tidak sama-sama lolos.

**Pertanyaan yang mungkin muncul**
- *Bagaimana mencegah pemesanan ganda?* Lewat `bentrokDengan` + `cekKetersediaan`, dan blok `synchronized` saat menyimpan.
- *Kenapa pakai custom exception?* Supaya servlet bisa membedakan error jadwal bentrok dan menampilkan nama ruangnya (`getNamaRuang()`).

## 4. Salsabila - Pembayaran dan Laporan

**File:** `models/Pembayaran.java`, `TransferBank.java`, `EWallet.java`, `Tunai.java`, `Refundable.java`, `Laporan.java`,
`controllers/PembayaranServlet.java`, `LaporanServlet.java`, `AdminServlet.java`,
`views/bayar.jsp`, `laporan.jsp`, `admin_dashboard.jsp` (penyimpanan pembayaran ada di `ReservasiDB.simpanPembayaran`)

**Konsep OOP yang dipakai**
- `Pembayaran` **abstract class**: `prosesBayar()` dan `getMetode()` abstrak. `setStatus()` **protected** sehingga hanya subclass yang bisa mengubah status.
- **Interface** `Refundable` (konstanta `PERSEN_REFUND = 0.8`, `BATAS_JAM_REFUND = 24`) hanya diimplementasikan
  `TransferBank` dan `EWallet`. `Tunai` tidak, karena uang tunai tidak dikembalikan lewat aplikasi.
- **Komposisi** `Reservasi` - `Pembayaran`: objek pembayaran dibuat **di dalam** `Reservasi` (`bayarTransfer`, `bayarEWallet`, `bayarTunai`), tidak bisa ada tanpa reservasinya.
- Validasi `prosesBayar`: transfer (bank, no rekening 6-20 digit, bukti tidak kosong), e-wallet (no HP 10-13 digit), tunai (uang diterima >= jumlah, `getKembalian()`).
- Jumlah bayar diambil dari `totalHarga` reservasi di server, bukan dari input pengguna.
- `Laporan`: pendapatan bersih = uang masuk (Lunas + yang kemudian Direfund) - total refund;
  juga jumlah reservasi, pendapatan per jenis ruang, dan ruang terpopuler.

**Pertanyaan yang mungkin muncul**
- *Kenapa refund pakai interface, bukan di kelas induk?* Karena tidak semua pembayaran bisa di-refund; perilaku yang hanya dimiliki sebagian subclass diletakkan di interface.
- *Kapan refund terjadi?* Saat reservasi dibatalkan minimal 24 jam sebelum jam mulai dan pembayarannya `Refundable` serta sudah Lunas (`Reservasi.batalkan()`).
- *Kenapa simpan pembayaran pakai transaksi?* Insert pembayaran dan update status reservasi harus berhasil bersama (`setAutoCommit(false)` lalu `commit()`), sesuai materi JDBC.

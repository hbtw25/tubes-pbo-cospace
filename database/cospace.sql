-- =========================================================
-- Database CoSpace - Tugas Besar PBO Kelompok 6
-- Jalankan file ini di phpMyAdmin / HeidiSQL / mysql client
-- =========================================================

DROP DATABASE IF EXISTS cospace;
CREATE DATABASE cospace;
USE cospace;

-- Data user (member dan admin dalam satu tabel, dibedakan kolom role)
CREATE TABLE users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL,          -- hasil hash SHA-256
    no_hp VARCHAR(13) NOT NULL,
    role VARCHAR(10) NOT NULL,              -- MEMBER / ADMIN
    jabatan VARCHAR(50),                    -- khusus admin
    poin_member INT DEFAULT 0,              -- khusus member
    tanggal_daftar DATE
);

-- Informasi co-working space (hanya satu baris)
CREATE TABLE coworking_space (
    id INT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    alamat VARCHAR(200) NOT NULL,
    jam_buka INT NOT NULL,
    jam_tutup INT NOT NULL
);

-- Data ruang (Hot Desk, Ruang Privat, Ruang Meeting)
CREATE TABLE ruang (
    id_ruang INT AUTO_INCREMENT PRIMARY KEY,
    nama_ruang VARCHAR(100) NOT NULL,
    jenis VARCHAR(20) NOT NULL,             -- Hot Desk / Ruang Privat / Ruang Meeting
    kapasitas INT NOT NULL,
    tarif_per_jam DOUBLE NOT NULL,
    fasilitas VARCHAR(200),
    aktif BOOLEAN DEFAULT TRUE,
    nomor_meja VARCHAR(10),                 -- khusus Hot Desk
    ada_proyektor BOOLEAN DEFAULT FALSE     -- khusus Ruang Meeting
);

-- Kode promo
CREATE TABLE promo (
    kode_promo VARCHAR(20) PRIMARY KEY,
    persen_diskon DOUBLE NOT NULL,
    berlaku_sampai DATE NOT NULL,
    kuota INT NOT NULL
);

-- Reservasi
CREATE TABLE reservasi (
    id_reservasi INT AUTO_INCREMENT PRIMARY KEY,
    id_member INT NOT NULL,
    id_ruang INT NOT NULL,
    tanggal DATE NOT NULL,
    jam_mulai INT NOT NULL,
    jam_selesai INT NOT NULL,
    status VARCHAR(25) NOT NULL,
    kode_promo VARCHAR(20),
    total_harga DOUBLE NOT NULL,
    FOREIGN KEY (id_member) REFERENCES users(id_user),
    FOREIGN KEY (id_ruang) REFERENCES ruang(id_ruang),
    FOREIGN KEY (kode_promo) REFERENCES promo(kode_promo)
);

-- Pembayaran (satu reservasi maksimal satu pembayaran)
CREATE TABLE pembayaran (
    id_pembayaran INT AUTO_INCREMENT PRIMARY KEY,
    id_reservasi INT NOT NULL UNIQUE,
    metode VARCHAR(20) NOT NULL,            -- Transfer Bank / E-Wallet / Tunai
    jumlah DOUBLE NOT NULL,
    waktu_bayar DATETIME,
    status VARCHAR(15) NOT NULL,            -- Belum Dibayar / Lunas / Direfund
    nama_bank VARCHAR(50),
    no_rekening VARCHAR(30),
    bukti_transfer VARCHAR(100),
    jenis_ewallet VARCHAR(30),
    no_hp VARCHAR(13),
    uang_diterima DOUBLE,
    FOREIGN KEY (id_reservasi) REFERENCES reservasi(id_reservasi)
);

-- ================= DATA AWAL =================

INSERT INTO coworking_space VALUES
(1, 'CoSpace Bandung', 'Jl. Telekomunikasi No. 1, Bandung', 8, 22);

-- Akun admin   : admin@cospace.id  / admin12345
-- Akun member  : member@cospace.id / member12345
INSERT INTO users (nama, email, password, no_hp, role, jabatan, poin_member, tanggal_daftar) VALUES
('Admin CoSpace', 'admin@cospace.id', '41e5653fc7aeb894026d6bb7b2db7f65902b454945fa8fd65a6327047b5277fb', '081234567890', 'ADMIN', 'Manajer', 0, CURDATE()),
('Budi Member', 'member@cospace.id', '5c79f165f049b1a58e35969d4bf48e035e2de305e0e065689b224eaf4104a6a6', '081298765432', 'MEMBER', NULL, 0, CURDATE());

INSERT INTO ruang (nama_ruang, jenis, kapasitas, tarif_per_jam, fasilitas, aktif, nomor_meja, ada_proyektor) VALUES
('Hot Desk A1', 'Hot Desk', 1, 15000, 'Wi-Fi, stopkontak, kopi', TRUE, 'A1', FALSE),
('Hot Desk A2', 'Hot Desk', 1, 15000, 'Wi-Fi, stopkontak, kopi', TRUE, 'A2', FALSE),
('Ruang Privat Melati', 'Ruang Privat', 4, 50000, 'Wi-Fi, AC, whiteboard', TRUE, NULL, FALSE),
('Ruang Privat Mawar', 'Ruang Privat', 2, 35000, 'Wi-Fi, AC', TRUE, NULL, FALSE),
('Ruang Meeting Anggrek', 'Ruang Meeting', 10, 100000, 'Wi-Fi, AC, TV, whiteboard', TRUE, NULL, TRUE),
('Ruang Meeting Kenanga', 'Ruang Meeting', 6, 75000, 'Wi-Fi, AC, whiteboard', TRUE, NULL, FALSE);

INSERT INTO promo VALUES
('HEMAT10', 10, '2027-12-31', 50),
('MAHASISWA20', 20, '2027-06-30', 20);

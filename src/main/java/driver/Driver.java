package driver;

import java.util.Calendar;
import java.util.Date;
import models.*;

/**
 * Kelas Driver untuk menguji logika semua kelas tanpa tampilan web dan database
 * (Tahap 3 pada timeline). Jalankan dengan Run File di NetBeans.
 */
public class Driver {

    public static void main(String[] args) {
        System.out.println("===== 1. AKUN =====");
        PengelolaAkun akun = new PengelolaAkun(10);
        Member budi = akun.register("Budi", "budi@mail.com", "rahasia123", "081234567890");
        System.out.println("Register Budi, role: " + budi.getRole());
        try {
            akun.register("Budi 2", "budi@mail.com", "rahasia123", "081234567890");
        } catch (IllegalArgumentException e) {
            System.out.println("Register email sama -> " + e.getMessage());
        }
        try {
            akun.register("Andi", "andi@mail.com", "123", "081234567890");
        } catch (IllegalArgumentException e) {
            System.out.println("Register password pendek -> " + e.getMessage());
        }
        System.out.println("Login benar: " + (akun.login("budi@mail.com", "rahasia123") != null));
        System.out.println("Login salah: " + (akun.login("budi@mail.com", "salah") != null));
        System.out.println("Ubah password: " + akun.ubahPassword("rahasia123", "passwordbaru"));
        System.out.println("Login password baru: " + (akun.login("budi@mail.com", "passwordbaru") != null));

        System.out.println("\n===== 2. RUANG =====");
        CoworkingSpace cs = new CoworkingSpace("CoSpace", "Bandung", 8, 22, 10);
        Admin admin = new Admin(1, "Admin", "admin@mail.com", "hash", "081111111111", "Manajer");
        HotDesk desk = new HotDesk(1, "Hot Desk A1", 15000, "Wi-Fi", "A1");
        RuangPrivat privat = new RuangPrivat(2, "Ruang Privat Melati", 4, 50000, "AC");
        RuangMeeting meeting = new RuangMeeting(3, "Ruang Meeting Anggrek", 10, 100000, "TV", true);
        admin.tambahRuang(cs, desk);
        admin.tambahRuang(cs, privat);
        admin.tambahRuang(cs, meeting);
        System.out.println("Hot Desk 3 jam       : " + desk.hitungHarga(3) + " (harus 45000)");
        System.out.println("Ruang Privat 8 jam   : " + privat.hitungHarga(8) + " (harus 340000)");
        System.out.println("Ruang Meeting 2 jam  : " + meeting.hitungHarga(2) + " (harus 225000)");
        System.out.println("Filter kapasitas >= 4: " + cs.filterRuang("Semua", 4).length + " ruang (harus 2)");
        admin.hapusRuang(cs, 1);
        System.out.println("Setelah hot desk dinonaktifkan: " + cs.filterRuang("Semua", 1).length + " ruang aktif (harus 2)");
        desk.setAktif(true);

        System.out.println("\n===== 3. RESERVASI & PROMO =====");
        PengelolaReservasi pr = new PengelolaReservasi(10, 5);
        Date besok = hariLagi(2);
        Promo promo = new Promo("HEMAT10", 10, hariLagi(30), 1);
        pr.tambahPromo(promo);
        Reservasi r1 = pr.buatReservasi(budi, privat, besok, 9, 12);
        r1.setIdReservasi(1);
        System.out.println("Reservasi 1 dibuat, status: " + r1.getStatus() + ", total: " + r1.getTotalHarga());
        System.out.println("Pakai promo HEMAT10: " + r1.terapkanPromo(pr.cariPromo("hemat10"))
                + ", total jadi: " + r1.getTotalHarga() + " (harus 135000)");
        try {
            pr.buatReservasi(budi, privat, besok, 11, 13);
        } catch (JadwalBentrokException e) {
            System.out.println("Reservasi jam 11-13 -> " + e.getMessage() + " (" + e.getNamaRuang() + ")");
        }
        System.out.println("Jam 12-14 tersedia: " + pr.cekKetersediaan(privat, besok, 12, 14) + " (harus true)");
        try {
            pr.buatReservasi(budi, privat, hariLagi(-1), 9, 10);
        } catch (IllegalArgumentException e) {
            System.out.println("Reservasi tanggal kemarin -> " + e.getMessage());
        }

        System.out.println("\n===== 4. PEMBAYARAN & LAPORAN =====");
        System.out.println("Bayar transfer tanpa bukti: " + r1.bayarTransfer("BCA", "1234567890", ""));
        System.out.println("Bayar transfer lengkap    : " + r1.bayarTransfer("BCA", "1234567890", "TRX001"));
        System.out.println("Status reservasi: " + r1.getStatus() + ", metode: " + r1.getPembayaran().getMetode());

        Reservasi r2 = pr.buatReservasi(budi, meeting, besok, 13, 15);
        r2.setIdReservasi(2);
        r2.bayarTunai(300000);
        Tunai t = (Tunai) r2.getPembayaran();
        System.out.println("Bayar tunai 300000, kembalian: " + t.getKembalian() + " (harus 75000)");

        Reservasi r3 = pr.buatReservasi(budi, desk, besok, 8, 10);
        r3.setIdReservasi(3);
        r3.bayarEWallet("GoPay", "081234567890");
        pr.batalkanReservasi(3);
        System.out.println("Reservasi 3 dibatalkan, status bayar: " + r3.getPembayaran().getStatus()
                + ", refund: " + ((Refundable) r3.getPembayaran()).hitungRefund() + " (harus 24000)");

        r2.selesaikan();
        System.out.println("Reservasi 2 selesai, poin Budi: " + budi.getPoinMember() + " (harus 22)");

        Laporan lap = new Laporan(hariLagi(0), hariLagi(7), pr.getSemuaReservasi());
        System.out.println("Jumlah reservasi        : " + lap.hitungJumlahReservasi() + " (harus 2)");
        System.out.println("Total refund            : " + lap.hitungTotalRefund() + " (harus 24000)");
        System.out.println("Pendapatan bersih       : " + lap.hitungTotalPendapatan() + " (harus 366000)");
        System.out.println("Pendapatan Ruang Meeting: " + lap.hitungPendapatanPerJenis("Ruang Meeting"));
        System.out.println("Ruang terpopuler        : " + lap.getRuangTerpopuler().getNamaRuang());
    }

    private static Date hariLagi(int hari) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, hari);
        return c.getTime();
    }
}

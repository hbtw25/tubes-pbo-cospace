package models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Reservasi ruang oleh member.
 * Relasi komposisi dengan Pembayaran: objek pembayaran dibuat di dalam reservasi.
 *
 * PIC: Neizshia Ratu Rahmadhani Ockry
 */
public class Reservasi {

    public static final String MENUNGGU = "Menunggu Pembayaran";
    public static final String AKTIF = "Aktif";
    public static final String SELESAI = "Selesai";
    public static final String DIBATALKAN = "Dibatalkan";

    private int idReservasi;
    private Member member;
    private Ruang ruang;
    private Date tanggal;
    private int jamMulai;
    private int jamSelesai;
    private String status;
    private Promo promo;
    private Pembayaran pembayaran;
    private double totalHarga;

    // Constructor untuk reservasi baru
    public Reservasi(int idReservasi, Member member, Ruang ruang, Date tanggal, int jamMulai, int jamSelesai) {
        if (jamMulai >= jamSelesai) {
            throw new IllegalArgumentException("Jam mulai harus lebih kecil dari jam selesai");
        }
        this.idReservasi = idReservasi;
        this.member = member;
        this.ruang = ruang;
        this.tanggal = tanggal;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
        this.status = MENUNGGU;
        this.promo = null;
        this.pembayaran = null;
        hitungTotal();
    }

    // Constructor untuk data reservasi dari database
    public Reservasi(int idReservasi, Member member, Ruang ruang, Date tanggal, int jamMulai, int jamSelesai,
            String status, Promo promo, double totalHarga) {
        this.idReservasi = idReservasi;
        this.member = member;
        this.ruang = ruang;
        this.tanggal = tanggal;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
        this.status = status;
        this.promo = promo;
        this.totalHarga = totalHarga;
    }

    public int getIdReservasi() {
        return idReservasi;
    }

    public void setIdReservasi(int idReservasi) {
        this.idReservasi = idReservasi;
    }

    public Member getMember() {
        return member;
    }

    public Ruang getRuang() {
        return ruang;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public int getJamMulai() {
        return jamMulai;
    }

    public int getJamSelesai() {
        return jamSelesai;
    }

    public String getStatus() {
        return status;
    }

    public Promo getPromo() {
        return promo;
    }

    public Pembayaran getPembayaran() {
        return pembayaran;
    }

    // Dipakai saat data pembayaran dimuat dari database
    public void setPembayaran(Pembayaran pembayaran) {
        this.pembayaran = pembayaran;
    }

    // Atribut turunan: durasi dihitung dari jam selesai - jam mulai
    public int getDurasiJam() {
        return jamSelesai - jamMulai;
    }

    public double getTotalHarga() {
        return totalHarga;
    }

    // Harga ruang (sesuai jenisnya) dikurangi potongan promo jika ada
    public double hitungTotal() {
        double harga = ruang.hitungHarga(getDurasiJam());
        if (promo != null) {
            harga = harga - promo.hitungPotongan(harga);
        }
        totalHarga = harga;
        return totalHarga;
    }

    // Hanya boleh satu promo per reservasi
    public boolean terapkanPromo(Promo p) {
        if (promo != null || p == null) {
            return false;
        }
        if (p.isBerlaku(tanggal) && p.gunakan()) {
            promo = p;
            hitungTotal();
            return true;
        }
        return false;
    }

    // Bentrok jika ruang & tanggal sama, reservasi lain belum dibatalkan, dan jamnya beririsan
    public boolean bentrokDengan(Reservasi lain) {
        if (lain.getIdReservasi() == idReservasi && idReservasi != 0) {
            return false;
        }
        if (lain.getRuang().getIdRuang() != ruang.getIdRuang()) {
            return false;
        }
        if (!tanggalSama(tanggal, lain.getTanggal())) {
            return false;
        }
        if (lain.getStatus().equals(DIBATALKAN)) {
            return false;
        }
        return jamMulai < lain.getJamSelesai() && jamSelesai > lain.getJamMulai();
    }

    public boolean bayarTransfer(String namaBank, String noRekening, String buktiTransfer) {
        return bayar(new TransferBank(totalHarga, namaBank, noRekening, buktiTransfer));
    }

    public boolean bayarEWallet(String jenisEWallet, String noHp) {
        return bayar(new EWallet(totalHarga, jenisEWallet, noHp));
    }

    public boolean bayarTunai(double uangDiterima) {
        return bayar(new Tunai(totalHarga, uangDiterima));
    }

    // Proses bayar yang sama untuk semua metode (polimorfisme prosesBayar)
    private boolean bayar(Pembayaran p) {
        if (!status.equals(MENUNGGU)) {
            return false;
        }
        if (p.prosesBayar()) {
            pembayaran = p;
            status = AKTIF;
            return true;
        }
        return false;
    }

    // Bisa dibatalkan jika masih menunggu pembayaran atau aktif
    public boolean batalkan() {
        if (!status.equals(MENUNGGU) && !status.equals(AKTIF)) {
            return false;
        }
        status = DIBATALKAN;
        // Refund hanya untuk pembayaran yang Refundable dan minimal 24 jam sebelum mulai
        if (pembayaran instanceof Refundable && pembayaran.isLunas()) {
            if (sisaJamSebelumMulai() >= Refundable.BATAS_JAM_REFUND) {
                Refundable r = (Refundable) pembayaran;
                r.prosesRefund();
            }
        }
        return true;
    }

    // Admin menandai reservasi selesai, member mendapat 1 poin per Rp10.000
    public void selesaikan() {
        if (status.equals(AKTIF)) {
            status = SELESAI;
            member.tambahPoin((int) (totalHarga / 10000));
        }
    }

    // Menghitung berapa jam lagi sampai reservasi dimulai
    public long sisaJamSebelumMulai() {
        Calendar mulai = Calendar.getInstance();
        mulai.setTime(tanggal);
        mulai.set(Calendar.HOUR_OF_DAY, jamMulai);
        mulai.set(Calendar.MINUTE, 0);
        mulai.set(Calendar.SECOND, 0);
        long selisihMs = mulai.getTimeInMillis() - System.currentTimeMillis();
        return selisihMs / (1000 * 60 * 60);
    }

    private boolean tanggalSama(Date a, Date b) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        return f.format(a).equals(f.format(b));
    }
}

package models;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Laporan pendapatan admin untuk periode tertentu.
 * Relasi asosiasi berarah ke Reservasi (laporan merekap reservasi).
 *
 * PIC: Salsabila Yurnisa Br Tarigan
 */
public class Laporan {

    private Date tanggalAwal;
    private Date tanggalAkhir;
    private Reservasi[] daftarReservasi;

    public Laporan(Date tanggalAwal, Date tanggalAkhir, Reservasi[] daftarReservasi) {
        if (tanggalAwal.after(tanggalAkhir)) {
            throw new IllegalArgumentException("Tanggal awal tidak boleh melebihi tanggal akhir");
        }
        this.tanggalAwal = tanggalAwal;
        this.tanggalAkhir = tanggalAkhir;
        this.daftarReservasi = daftarReservasi;
    }

    public Date getTanggalAwal() {
        return tanggalAwal;
    }

    public Date getTanggalAkhir() {
        return tanggalAkhir;
    }

    // Pendapatan bersih = semua uang yang pernah dibayar - uang yang dikembalikan
    public double hitungTotalPendapatan() {
        double total = 0;
        for (Reservasi r : daftarReservasi) {
            if (dalamPeriode(r)) {
                total = total + uangMasuk(r);
            }
        }
        return total - hitungTotalRefund();
    }

    // Jumlah reservasi yang benar-benar terjadi (Aktif atau Selesai)
    public int hitungJumlahReservasi() {
        int jumlah = 0;
        for (Reservasi r : daftarReservasi) {
            if (dalamPeriode(r) && isTerjadi(r)) {
                jumlah++;
            }
        }
        return jumlah;
    }

    public double hitungPendapatanPerJenis(String jenis) {
        double total = 0;
        for (Reservasi r : daftarReservasi) {
            if (dalamPeriode(r) && r.getRuang().getJenis().equals(jenis)) {
                total = total + uangMasuk(r) - uangRefund(r);
            }
        }
        return total;
    }

    public double hitungTotalRefund() {
        double total = 0;
        for (Reservasi r : daftarReservasi) {
            if (dalamPeriode(r)) {
                total = total + uangRefund(r);
            }
        }
        return total;
    }

    // Ruang dengan jumlah reservasi Aktif/Selesai terbanyak dalam periode
    public Ruang getRuangTerpopuler() {
        Ruang terpopuler = null;
        int jumlahTerbanyak = 0;
        for (Reservasi r : daftarReservasi) {
            if (!dalamPeriode(r) || !isTerjadi(r)) {
                continue;
            }
            int jumlah = 0;
            for (Reservasi lain : daftarReservasi) {
                if (dalamPeriode(lain) && isTerjadi(lain)
                        && lain.getRuang().getIdRuang() == r.getRuang().getIdRuang()) {
                    jumlah++;
                }
            }
            if (jumlah > jumlahTerbanyak) {
                jumlahTerbanyak = jumlah;
                terpopuler = r.getRuang();
            }
        }
        return terpopuler;
    }

    private boolean dalamPeriode(Reservasi r) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String tgl = f.format(r.getTanggal());
        return tgl.compareTo(f.format(tanggalAwal)) >= 0 && tgl.compareTo(f.format(tanggalAkhir)) <= 0;
    }

    private boolean isTerjadi(Reservasi r) {
        return r.getStatus().equals(Reservasi.AKTIF) || r.getStatus().equals(Reservasi.SELESAI);
    }

    // Uang yang pernah masuk: pembayaran Lunas atau yang kemudian Direfund
    private double uangMasuk(Reservasi r) {
        Pembayaran p = r.getPembayaran();
        if (p != null && (p.isLunas() || p.getStatus().equals(Pembayaran.DIREFUND))) {
            return p.getJumlah();
        }
        return 0;
    }

    private double uangRefund(Reservasi r) {
        Pembayaran p = r.getPembayaran();
        if (p instanceof Refundable && p.getStatus().equals(Pembayaran.DIREFUND)) {
            return ((Refundable) p).hitungRefund();
        }
        return 0;
    }
}

package models;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Mengelola kumpulan reservasi dan promo: membuat reservasi, cek jadwal bentrok,
 * riwayat, pembatalan, dan pencarian promo.
 * Relasi agregasi dengan Reservasi dan Promo.
 *
 * PIC: Neizshia Ratu Rahmadhani Ockry
 */
public class PengelolaReservasi {

    private Reservasi[] daftarReservasi;
    private int jumlahReservasi;
    private Promo[] daftarPromo;
    private int jumlahPromo;

    public PengelolaReservasi(int maksReservasi, int maksPromo) {
        daftarReservasi = new Reservasi[maksReservasi];
        jumlahReservasi = 0;
        daftarPromo = new Promo[maksPromo];
        jumlahPromo = 0;
    }

    // Memasukkan reservasi yang sudah ada (misalnya dari database)
    public boolean tambahReservasi(Reservasi r) {
        if (jumlahReservasi < daftarReservasi.length) {
            daftarReservasi[jumlahReservasi] = r;
            jumlahReservasi++;
            return true;
        }
        return false;
    }

    public Reservasi buatReservasi(Member member, Ruang ruang, Date tanggal, int jamMulai, int jamSelesai) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        if (f.format(tanggal).compareTo(f.format(new Date())) < 0) {
            throw new IllegalArgumentException("Tanggal reservasi tidak boleh lampau");
        }
        if (jamMulai >= jamSelesai) {
            throw new IllegalArgumentException("Jam mulai harus lebih kecil dari jam selesai");
        }
        if (!ruang.isAktif()) {
            throw new IllegalArgumentException("Ruang sedang tidak aktif");
        }
        if (!cekKetersediaan(ruang, tanggal, jamMulai, jamSelesai)) {
            throw new JadwalBentrokException("Jadwal bentrok dengan reservasi lain", ruang.getNamaRuang());
        }
        if (jumlahReservasi >= daftarReservasi.length) {
            throw new IllegalArgumentException("Kapasitas data reservasi penuh");
        }
        Reservasi r = new Reservasi(0, member, ruang, tanggal, jamMulai, jamSelesai);
        tambahReservasi(r);
        return r;
    }

    // true jika tidak ada reservasi lain yang bentrok
    public boolean cekKetersediaan(Ruang ruang, Date tanggal, int jamMulai, int jamSelesai) {
        Reservasi calon = new Reservasi(0, null, ruang, tanggal, jamMulai, jamSelesai);
        for (int i = 0; i < jumlahReservasi; i++) {
            if (calon.bentrokDengan(daftarReservasi[i])) {
                return false;
            }
        }
        return true;
    }

    public Reservasi cariReservasi(int idReservasi) {
        for (int i = 0; i < jumlahReservasi; i++) {
            if (daftarReservasi[i].getIdReservasi() == idReservasi) {
                return daftarReservasi[i];
            }
        }
        return null;
    }

    public boolean batalkanReservasi(int idReservasi) {
        Reservasi r = cariReservasi(idReservasi);
        if (r == null) {
            return false;
        }
        return r.batalkan();
    }

    public Reservasi[] getRiwayat(Member member) {
        int jumlah = 0;
        Reservasi[] sementara = new Reservasi[jumlahReservasi];
        for (int i = 0; i < jumlahReservasi; i++) {
            if (daftarReservasi[i].getMember().getIdUser() == member.getIdUser()) {
                sementara[jumlah] = daftarReservasi[i];
                jumlah++;
            }
        }
        return potongArray(sementara, jumlah);
    }

    // Reservasi yang belum dibatalkan pada ruang dan tanggal tertentu
    public Reservasi[] getJadwalRuang(Ruang ruang, Date tanggal) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        int jumlah = 0;
        Reservasi[] sementara = new Reservasi[jumlahReservasi];
        for (int i = 0; i < jumlahReservasi; i++) {
            Reservasi r = daftarReservasi[i];
            if (r.getRuang().getIdRuang() == ruang.getIdRuang()
                    && f.format(r.getTanggal()).equals(f.format(tanggal))
                    && !r.getStatus().equals(Reservasi.DIBATALKAN)) {
                sementara[jumlah] = r;
                jumlah++;
            }
        }
        return potongArray(sementara, jumlah);
    }

    public Reservasi[] getSemuaReservasi() {
        return potongArray(daftarReservasi, jumlahReservasi);
    }

    public boolean tambahPromo(Promo p) {
        if (cariPromo(p.getKodePromo()) != null) {
            return false; // kode promo sudah ada
        }
        if (jumlahPromo < daftarPromo.length) {
            daftarPromo[jumlahPromo] = p;
            jumlahPromo++;
            return true;
        }
        return false;
    }

    public Promo cariPromo(String kodePromo) {
        for (int i = 0; i < jumlahPromo; i++) {
            if (daftarPromo[i].getKodePromo().equalsIgnoreCase(kodePromo)) {
                return daftarPromo[i];
            }
        }
        return null;
    }

    private Reservasi[] potongArray(Reservasi[] data, int jumlah) {
        Reservasi[] hasil = new Reservasi[jumlah];
        for (int i = 0; i < jumlah; i++) {
            hasil[i] = data[i];
        }
        return hasil;
    }
}

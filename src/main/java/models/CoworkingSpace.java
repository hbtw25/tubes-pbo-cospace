package models;

/**
 * Informasi lokasi co-working space beserta daftar ruangnya.
 * Relasi agregasi dengan Ruang (ruang dibuat di luar lalu dimasukkan).
 *
 * PIC: Zeifira Naila Syakira
 */
public class CoworkingSpace {

    private String nama;
    private String alamat;
    private int jamBuka;
    private int jamTutup;
    private Ruang[] daftarRuang;
    private int jumlahRuang;

    public CoworkingSpace(String nama, String alamat, int jamBuka, int jamTutup, int maksRuang) {
        if (jamBuka < 0 || jamTutup > 24 || jamBuka >= jamTutup) {
            throw new IllegalArgumentException("Jam buka harus lebih kecil dari jam tutup (0-24)");
        }
        this.nama = nama;
        this.alamat = alamat;
        this.jamBuka = jamBuka;
        this.jamTutup = jamTutup;
        this.daftarRuang = new Ruang[maksRuang];
        this.jumlahRuang = 0;
    }

    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public int getJamBuka() {
        return jamBuka;
    }

    public int getJamTutup() {
        return jamTutup;
    }

    public boolean tambahRuang(Ruang r) {
        if (jumlahRuang < daftarRuang.length) {
            daftarRuang[jumlahRuang] = r;
            jumlahRuang++;
            return true;
        }
        return false;
    }

    // Ruang tidak dihapus permanen, hanya dinonaktifkan agar riwayat reservasi tetap utuh
    public boolean hapusRuang(int idRuang) {
        Ruang r = cariRuang(idRuang);
        if (r == null) {
            return false;
        }
        r.setAktif(false);
        return true;
    }

    public Ruang cariRuang(int idRuang) {
        for (int i = 0; i < jumlahRuang; i++) {
            if (daftarRuang[i].getIdRuang() == idRuang) {
                return daftarRuang[i];
            }
        }
        return null;
    }

    // Mengembalikan array sesuai jumlah ruang yang terisi
    public Ruang[] getDaftarRuang() {
        Ruang[] hasil = new Ruang[jumlahRuang];
        for (int i = 0; i < jumlahRuang; i++) {
            hasil[i] = daftarRuang[i];
        }
        return hasil;
    }

    public int getJumlahRuang() {
        return jumlahRuang;
    }

    // jenis kosong / "Semua" berarti semua jenis ruang
    public Ruang[] filterRuang(String jenis, int kapasitasMin) {
        int jumlahCocok = 0;
        Ruang[] sementara = new Ruang[jumlahRuang];
        for (int i = 0; i < jumlahRuang; i++) {
            Ruang r = daftarRuang[i];
            boolean jenisCocok = jenis == null || jenis.isEmpty() || jenis.equals("Semua")
                    || r.getJenis().equals(jenis);
            if (r.isAktif() && jenisCocok && r.getKapasitas() >= kapasitasMin) {
                sementara[jumlahCocok] = r;
                jumlahCocok++;
            }
        }
        Ruang[] hasil = new Ruang[jumlahCocok];
        for (int i = 0; i < jumlahCocok; i++) {
            hasil[i] = sementara[i];
        }
        return hasil;
    }
}

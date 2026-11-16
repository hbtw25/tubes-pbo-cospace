package models;

/**
 * Kelas induk untuk semua jenis ruang.
 * Dibuat abstract karena tidak ada "ruang umum", harga dihitung beda per jenis.
 *
 * PIC: Zeifira Naila Syakira
 */
public abstract class Ruang {

    private int idRuang;
    private String namaRuang;
    private int kapasitas;
    protected double tarifPerJam; // protected agar bisa dibaca langsung oleh subclass
    private String fasilitas;
    private boolean aktif;

    public Ruang(int idRuang, String namaRuang, int kapasitas, double tarifPerJam, String fasilitas) {
        this.idRuang = idRuang;
        setNamaRuang(namaRuang);
        setKapasitas(kapasitas);
        setTarifPerJam(tarifPerJam);
        setFasilitas(fasilitas);
        this.aktif = true;
    }

    public int getIdRuang() {
        return idRuang;
    }

    public void setIdRuang(int idRuang) {
        this.idRuang = idRuang;
    }

    public String getNamaRuang() {
        return namaRuang;
    }

    public void setNamaRuang(String namaRuang) {
        if (namaRuang == null || namaRuang.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama ruang tidak boleh kosong");
        }
        this.namaRuang = namaRuang.trim();
    }

    public int getKapasitas() {
        return kapasitas;
    }

    public void setKapasitas(int kapasitas) {
        if (kapasitas < 1) {
            throw new IllegalArgumentException("Kapasitas minimal 1");
        }
        this.kapasitas = kapasitas;
    }

    public double getTarifPerJam() {
        return tarifPerJam;
    }

    public void setTarifPerJam(double tarifPerJam) {
        if (tarifPerJam <= 0) {
            throw new IllegalArgumentException("Tarif per jam harus lebih dari 0");
        }
        this.tarifPerJam = tarifPerJam;
    }

    public String getFasilitas() {
        return fasilitas;
    }

    public void setFasilitas(String fasilitas) {
        if (fasilitas == null) {
            fasilitas = "";
        }
        this.fasilitas = fasilitas.trim();
    }

    public boolean isAktif() {
        return aktif;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }

    // Method abstrak, di-override oleh setiap jenis ruang
    public abstract double hitungHarga(int durasiJam);

    public abstract String getJenis();
}

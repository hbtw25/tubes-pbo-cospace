package models;

/**
 * Hot Desk: meja untuk satu orang, harga = tarif x durasi.
 *
 * PIC: Zeifira Naila Syakira
 */
public class HotDesk extends Ruang {

    private String nomorMeja;

    // Kapasitas hot desk selalu 1 orang
    public HotDesk(int idRuang, String namaRuang, double tarifPerJam, String fasilitas, String nomorMeja) {
        super(idRuang, namaRuang, 1, tarifPerJam, fasilitas);
        this.nomorMeja = nomorMeja;
    }

    public String getNomorMeja() {
        return nomorMeja;
    }

    public void setNomorMeja(String nomorMeja) {
        this.nomorMeja = nomorMeja;
    }

    // Kapasitas hot desk tidak bisa diubah
    @Override
    public void setKapasitas(int kapasitas) {
        super.setKapasitas(1);
    }

    @Override
    public double hitungHarga(int durasiJam) {
        return tarifPerJam * durasiJam;
    }

    @Override
    public String getJenis() {
        return "Hot Desk";
    }
}

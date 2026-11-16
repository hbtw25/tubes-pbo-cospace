package models;

/**
 * Ruang Privat: harga = tarif x durasi, diskon 15% jika pesan minimal 8 jam.
 *
 * PIC: Zeifira Naila Syakira
 */
public class RuangPrivat extends Ruang {

    private static final double DISKON_HARIAN = 0.15;
    private static final int MIN_JAM_HARIAN = 8;

    public RuangPrivat(int idRuang, String namaRuang, int kapasitas, double tarifPerJam, String fasilitas) {
        super(idRuang, namaRuang, kapasitas, tarifPerJam, fasilitas);
    }

    @Override
    public double hitungHarga(int durasiJam) {
        double harga = tarifPerJam * durasiJam;
        if (durasiJam >= MIN_JAM_HARIAN) {
            harga = harga - (harga * DISKON_HARIAN);
        }
        return harga;
    }

    @Override
    public String getJenis() {
        return "Ruang Privat";
    }
}

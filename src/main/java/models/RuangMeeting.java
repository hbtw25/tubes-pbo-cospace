package models;

/**
 * Ruang Meeting: harga = tarif x durasi, ditambah biaya proyektor jika ada.
 *
 * PIC: Zeifira Naila Syakira
 */
public class RuangMeeting extends Ruang {

    private boolean adaProyektor;
    private static final double BIAYA_PROYEKTOR = 25000;

    public RuangMeeting(int idRuang, String namaRuang, int kapasitas, double tarifPerJam,
            String fasilitas, boolean adaProyektor) {
        super(idRuang, namaRuang, kapasitas, tarifPerJam, fasilitas);
        this.adaProyektor = adaProyektor;
    }

    public boolean isAdaProyektor() {
        return adaProyektor;
    }

    public void setAdaProyektor(boolean adaProyektor) {
        this.adaProyektor = adaProyektor;
    }

    @Override
    public double hitungHarga(int durasiJam) {
        double harga = tarifPerJam * durasiJam;
        if (adaProyektor) {
            harga = harga + BIAYA_PROYEKTOR;
        }
        return harga;
    }

    @Override
    public String getJenis() {
        return "Ruang Meeting";
    }
}

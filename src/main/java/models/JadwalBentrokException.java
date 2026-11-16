package models;

/**
 * Custom exception yang dilempar jika reservasi bentrok dengan jadwal lain.
 * Turunan RuntimeException (unchecked) sesuai best practice di materi Exception.
 *
 * PIC: Neizshia Ratu Rahmadhani Ockry
 */
public class JadwalBentrokException extends RuntimeException {

    private String namaRuang;

    public JadwalBentrokException(String message, String namaRuang) {
        super(message);
        this.namaRuang = namaRuang;
    }

    public String getNamaRuang() {
        return namaRuang;
    }
}

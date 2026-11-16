package models;

/**
 * Pembayaran tunai di resepsionis. Tidak bisa di-refund lewat aplikasi,
 * jadi kelas ini tidak mengimplementasikan Refundable.
 *
 * PIC: Salsabila Yurnisa Br Tarigan
 */
public class Tunai extends Pembayaran {

    private double uangDiterima;

    public Tunai(double jumlah, double uangDiterima) {
        super(jumlah);
        this.uangDiterima = uangDiterima;
    }

    public double getUangDiterima() {
        return uangDiterima;
    }

    // Atribut turunan: kembalian dihitung dari uang diterima - jumlah
    public double getKembalian() {
        return uangDiterima - getJumlah();
    }

    // Valid jika uang yang diterima cukup
    @Override
    public boolean prosesBayar() {
        if (uangDiterima < getJumlah()) {
            return false;
        }
        setStatus(LUNAS);
        return true;
    }

    @Override
    public String getMetode() {
        return "Tunai";
    }
}

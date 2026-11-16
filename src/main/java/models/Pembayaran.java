package models;

import java.util.Date;

/**
 * Kelas induk untuk semua metode pembayaran.
 * Dibuat abstract karena tidak ada "pembayaran umum".
 *
 * PIC: Salsabila Yurnisa Br Tarigan
 */
public abstract class Pembayaran {

    public static final String BELUM_DIBAYAR = "Belum Dibayar";
    public static final String LUNAS = "Lunas";
    public static final String DIREFUND = "Direfund";

    private int idPembayaran;
    private double jumlah;
    private Date waktuBayar;
    private String status;

    public Pembayaran(double jumlah) {
        this.jumlah = jumlah;
        this.status = BELUM_DIBAYAR;
    }

    public int getIdPembayaran() {
        return idPembayaran;
    }

    public void setIdPembayaran(int idPembayaran) {
        this.idPembayaran = idPembayaran;
    }

    public double getJumlah() {
        return jumlah;
    }

    public Date getWaktuBayar() {
        return waktuBayar;
    }

    public String getStatus() {
        return status;
    }

    // protected: hanya subclass yang boleh mengubah status pembayaran
    protected void setStatus(String status) {
        this.status = status;
        if (status.equals(LUNAS)) {
            waktuBayar = new Date();
        }
    }

    // Dipakai saat data pembayaran dimuat ulang dari database
    public void setDataDariDatabase(int idPembayaran, Date waktuBayar, String status) {
        this.idPembayaran = idPembayaran;
        this.waktuBayar = waktuBayar;
        this.status = status;
    }

    public boolean isLunas() {
        return status.equals(LUNAS);
    }

    public abstract boolean prosesBayar();

    public abstract String getMetode();
}

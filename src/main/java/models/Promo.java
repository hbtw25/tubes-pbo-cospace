package models;

import java.util.Date;

/**
 * Kode promo berisi persentase diskon, masa berlaku, dan kuota.
 *
 * PIC: Neizshia Ratu Rahmadhani Ockry
 */
public class Promo {

    private String kodePromo;
    private double persenDiskon;
    private Date berlakuSampai;
    private int kuota;

    public Promo(String kodePromo, double persenDiskon, Date berlakuSampai, int kuota) {
        if (kodePromo == null || kodePromo.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode promo tidak boleh kosong");
        }
        if (berlakuSampai == null) {
            throw new IllegalArgumentException("Tanggal berlaku harus diisi");
        }
        if (kuota < 0) {
            throw new IllegalArgumentException("Kuota tidak boleh negatif");
        }
        this.kodePromo = kodePromo.trim().toUpperCase();
        setPersenDiskon(persenDiskon);
        this.berlakuSampai = berlakuSampai;
        this.kuota = kuota;
    }

    public String getKodePromo() {
        return kodePromo;
    }

    public double getPersenDiskon() {
        return persenDiskon;
    }

    public void setPersenDiskon(double persenDiskon) {
        if (persenDiskon < 1 || persenDiskon > 100) {
            throw new IllegalArgumentException("Persentase diskon harus 1-100");
        }
        this.persenDiskon = persenDiskon;
    }

    public Date getBerlakuSampai() {
        return berlakuSampai;
    }

    public int getKuota() {
        return kuota;
    }

    // Promo berlaku jika tanggal belum lewat batas dan kuota masih ada
    public boolean isBerlaku(Date tanggal) {
        return !tanggal.after(berlakuSampai) && kuota > 0;
    }

    public double hitungPotongan(double total) {
        return total * persenDiskon / 100;
    }

    // Mengurangi kuota setiap kali promo dipakai
    public boolean gunakan() {
        if (kuota > 0) {
            kuota--;
            return true;
        }
        return false;
    }
}

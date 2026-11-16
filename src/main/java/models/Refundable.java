package models;

/**
 * Interface untuk pembayaran yang bisa dikembalikan (refund).
 * Hanya TransferBank dan EWallet yang mengimplementasikan, Tunai tidak.
 *
 * PIC: Salsabila Yurnisa Br Tarigan
 */
public interface Refundable {

    double PERSEN_REFUND = 0.8;   // dana dikembalikan 80%
    int BATAS_JAM_REFUND = 24;    // pembatalan minimal 24 jam sebelum jam mulai

    double hitungRefund();

    boolean prosesRefund();
}

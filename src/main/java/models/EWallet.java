package models;

/**
 * Pembayaran melalui e-wallet (GoPay, OVO, DANA, dll). Bisa di-refund.
 *
 * PIC: Salsabila Yurnisa Br Tarigan
 */
public class EWallet extends Pembayaran implements Refundable {

    private String jenisEWallet;
    private String noHp;

    public EWallet(double jumlah, String jenisEWallet, String noHp) {
        super(jumlah);
        this.jenisEWallet = jenisEWallet;
        this.noHp = noHp;
    }

    public String getJenisEWallet() {
        return jenisEWallet;
    }

    public String getNoHp() {
        return noHp;
    }

    // Valid jika jenis e-wallet diisi dan nomor HP berupa angka 10-13 digit
    @Override
    public boolean prosesBayar() {
        if (jenisEWallet == null || jenisEWallet.trim().isEmpty()) {
            return false;
        }
        if (noHp == null || !noHp.matches("[0-9]{10,13}")) {
            return false;
        }
        setStatus(LUNAS);
        return true;
    }

    @Override
    public String getMetode() {
        return "E-Wallet";
    }

    @Override
    public double hitungRefund() {
        return getJumlah() * PERSEN_REFUND;
    }

    @Override
    public boolean prosesRefund() {
        if (!isLunas()) {
            return false;
        }
        setStatus(DIREFUND);
        return true;
    }
}

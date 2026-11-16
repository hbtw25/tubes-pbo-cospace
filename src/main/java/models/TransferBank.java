package models;

/**
 * Pembayaran melalui transfer bank. Bisa di-refund.
 *
 * PIC: Salsabila Yurnisa Br Tarigan
 */
public class TransferBank extends Pembayaran implements Refundable {

    private String namaBank;
    private String noRekening;
    private String buktiTransfer;

    public TransferBank(double jumlah, String namaBank, String noRekening, String buktiTransfer) {
        super(jumlah);
        this.namaBank = namaBank;
        this.noRekening = noRekening;
        this.buktiTransfer = buktiTransfer;
    }

    public String getNamaBank() {
        return namaBank;
    }

    public String getNoRekening() {
        return noRekening;
    }

    public String getBuktiTransfer() {
        return buktiTransfer;
    }

    // Valid jika nama bank, nomor rekening, dan bukti transfer tidak kosong
    @Override
    public boolean prosesBayar() {
        if (namaBank == null || namaBank.trim().isEmpty()) {
            return false;
        }
        if (noRekening == null || !noRekening.matches("[0-9]{6,20}")) {
            return false;
        }
        if (buktiTransfer == null || buktiTransfer.trim().isEmpty()) {
            return false;
        }
        setStatus(LUNAS);
        return true;
    }

    @Override
    public String getMetode() {
        return "Transfer Bank";
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

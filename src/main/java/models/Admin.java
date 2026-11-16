package models;

/**
 * Admin adalah pengelola co-working space.
 * Akun admin tidak bisa dibuat lewat registrasi, langsung dari database.
 *
 * PIC: Harsya Brahmantyo Wibowo
 */
public class Admin extends User {

    private String jabatan;

    public Admin(int idUser, String nama, String email, String passwordHash, String noHp, String jabatan) {
        super(idUser, nama, email, passwordHash, noHp);
        this.jabatan = jabatan;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    // Asosiasi Admin - CoworkingSpace: admin meneruskan permintaan ke CoworkingSpace
    public boolean tambahRuang(CoworkingSpace cs, Ruang r) {
        return cs.tambahRuang(r);
    }

    public boolean hapusRuang(CoworkingSpace cs, int idRuang) {
        return cs.hapusRuang(idRuang);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}

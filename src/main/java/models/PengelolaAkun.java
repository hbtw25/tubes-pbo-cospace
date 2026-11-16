package models;

/**
 * Mengelola kumpulan akun: registrasi, login, logout, dan ubah profil.
 * Relasi agregasi dengan User (objek User dibuat di luar lalu dimasukkan).
 *
 * PIC: Harsya Brahmantyo Wibowo
 */
public class PengelolaAkun {

    private User[] daftarUser;
    private int jumlahUser;
    private User userAktif;

    public PengelolaAkun(int maksUser) {
        daftarUser = new User[maksUser];
        jumlahUser = 0;
        userAktif = null;
    }

    public boolean tambahUser(User u) {
        if (jumlahUser < daftarUser.length) {
            daftarUser[jumlahUser] = u;
            jumlahUser++;
            return true;
        }
        return false;
    }

    // Registrasi selalu menghasilkan akun Member
    public Member register(String nama, String email, String password, String noHp) {
        if (isEmailTerdaftar(email)) {
            throw new IllegalArgumentException("Email sudah terdaftar");
        }
        Member m = new Member(nama, email, password, noHp);
        if (!tambahUser(m)) {
            throw new IllegalArgumentException("Kapasitas akun penuh");
        }
        return m;
    }

    // Mengembalikan user jika email dan password cocok, null jika gagal
    public User login(String email, String password) {
        for (int i = 0; i < jumlahUser; i++) {
            User u = daftarUser[i];
            if (u.getEmail().equalsIgnoreCase(email) && u.cekPassword(password)) {
                userAktif = u;
                return u;
            }
        }
        return null;
    }

    public void logout() {
        userAktif = null;
    }

    public User getUserAktif() {
        return userAktif;
    }

    // Dipakai controller untuk menandai user yang sedang login (dari session)
    public void setUserAktif(User u) {
        userAktif = u;
    }

    public boolean isEmailTerdaftar(String email) {
        for (int i = 0; i < jumlahUser; i++) {
            if (daftarUser[i].getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    public boolean ubahProfil(String nama, String noHp) {
        if (userAktif == null) {
            return false;
        }
        userAktif.setNama(nama);
        userAktif.setNoHp(noHp);
        return true;
    }

    public boolean ubahPassword(String passwordLama, String passwordBaru) {
        if (userAktif == null || !userAktif.cekPassword(passwordLama)) {
            return false;
        }
        userAktif.setPassword(passwordBaru);
        return true;
    }
}

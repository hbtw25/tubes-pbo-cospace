package models;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Kelas induk untuk semua pengguna (Member dan Admin).
 * Dibuat abstract karena tidak ada "user umum".
 *
 * PIC: Harsya Brahmantyo Wibowo
 */
public abstract class User {

    private int idUser;
    private String nama;
    private String email;
    private String password; // yang disimpan selalu hasil hash, bukan password asli
    private String noHp;

    // Constructor untuk registrasi akun baru (password masih asli, akan di-hash)
    public User(String nama, String email, String password, String noHp) {
        setNama(nama);
        setEmail(email);
        setPassword(password);
        setNoHp(noHp);
    }

    // Constructor untuk data dari database (password sudah berupa hash)
    public User(int idUser, String nama, String email, String passwordHash, String noHp) {
        this.idUser = idUser;
        this.nama = nama;
        this.email = email;
        this.password = passwordHash;
        this.noHp = noHp;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama tidak boleh kosong");
        }
        this.nama = nama.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@") || email.indexOf(".", email.indexOf("@")) == -1) {
            throw new IllegalArgumentException("Format email tidak valid");
        }
        this.email = email.trim().toLowerCase();
    }

    // Password asli divalidasi lalu disimpan dalam bentuk hash
    public void setPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password minimal 8 karakter");
        }
        this.password = hashPassword(password);
    }

    // Hanya mengembalikan hash, dipakai saat menyimpan ke database
    public String getPasswordHash() {
        return password;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        if (noHp == null || !noHp.matches("[0-9]{10,13}")) {
            throw new IllegalArgumentException("Nomor HP harus angka 10-13 digit");
        }
        this.noHp = noHp;
    }

    // Membandingkan hash dari input dengan hash yang tersimpan
    public boolean cekPassword(String inputPassword) {
        if (inputPassword == null) {
            return false;
        }
        return hashPassword(inputPassword).equals(password);
    }

    // Mengubah password menjadi hash SHA-256 (64 karakter hex)
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hasil = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hasil) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Gagal melakukan hash password");
        }
    }

    public abstract String getRole();
}

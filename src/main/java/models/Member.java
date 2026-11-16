package models;

import java.util.Date;

/**
 * Member adalah pengguna umum yang memesan ruang.
 *
 * PIC: Harsya Brahmantyo Wibowo
 */
public class Member extends User {

    private int poinMember;
    private Date tanggalDaftar;

    // Constructor untuk registrasi member baru
    public Member(String nama, String email, String password, String noHp) {
        super(nama, email, password, noHp);
        this.poinMember = 0;
        this.tanggalDaftar = new Date();
    }

    // Constructor untuk data member dari database
    public Member(int idUser, String nama, String email, String passwordHash, String noHp,
            int poinMember, Date tanggalDaftar) {
        super(idUser, nama, email, passwordHash, noHp);
        this.poinMember = poinMember;
        this.tanggalDaftar = tanggalDaftar;
    }

    public int getPoinMember() {
        return poinMember;
    }

    public Date getTanggalDaftar() {
        return tanggalDaftar;
    }

    public void tambahPoin(int jumlah) {
        if (jumlah > 0) {
            poinMember = poinMember + jumlah;
        }
    }

    @Override
    public String getRole() {
        return "MEMBER";
    }
}

package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import models.Admin;
import models.Member;
import models.User;

/**
 * Akses tabel users. Semua query memakai PreparedStatement agar aman dari SQL injection.
 *
 * PIC: Harsya Brahmantyo Wibowo
 */
public class UserDB {

    // Mengubah satu baris ResultSet menjadi objek Admin atau Member
    public static User toUser(ResultSet rs) throws SQLException {
        if (rs.getString("role").equals("ADMIN")) {
            return new Admin(rs.getInt("id_user"), rs.getString("nama"), rs.getString("email"),
                    rs.getString("password"), rs.getString("no_hp"), rs.getString("jabatan"));
        }
        return new Member(rs.getInt("id_user"), rs.getString("nama"), rs.getString("email"),
                rs.getString("password"), rs.getString("no_hp"), rs.getInt("poin_member"),
                rs.getDate("tanggal_daftar"));
    }

    public static ArrayList<User> getSemua() throws SQLException {
        ArrayList<User> hasil = new ArrayList<>();
        // try-with-resources: koneksi otomatis ditutup setelah selesai
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT * FROM users");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(toUser(rs));
            }
        }
        return hasil;
    }

    public static User cari(int idUser) throws SQLException {
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE id_user = ?")) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return toUser(rs);
                }
            }
        }
        return null;
    }

    public static void simpanMember(Member m) throws SQLException {
        String sql = "INSERT INTO users (nama, email, password, no_hp, role, poin_member, tanggal_daftar) "
                + "VALUES (?, ?, ?, ?, 'MEMBER', 0, CURDATE())";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getNama());
            ps.setString(2, m.getEmail());
            ps.setString(3, m.getPasswordHash());
            ps.setString(4, m.getNoHp());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    m.setIdUser(rs.getInt(1));
                }
            }
        }
    }

    public static void updateProfil(User u) throws SQLException {
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement("UPDATE users SET nama = ?, no_hp = ? WHERE id_user = ?")) {
            ps.setString(1, u.getNama());
            ps.setString(2, u.getNoHp());
            ps.setInt(3, u.getIdUser());
            ps.executeUpdate();
        }
    }

    public static void updatePassword(User u) throws SQLException {
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement("UPDATE users SET password = ? WHERE id_user = ?")) {
            ps.setString(1, u.getPasswordHash());
            ps.setInt(2, u.getIdUser());
            ps.executeUpdate();
        }
    }

    public static void updatePoin(Member m) throws SQLException {
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement("UPDATE users SET poin_member = ? WHERE id_user = ?")) {
            ps.setInt(1, m.getPoinMember());
            ps.setInt(2, m.getIdUser());
            ps.executeUpdate();
        }
    }
}

package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import models.Promo;

/**
 * Akses tabel promo.
 *
 * PIC: Neizshia Ratu Rahmadhani Ockry
 */
public class PromoDB {

    public static Promo toPromo(ResultSet rs) throws SQLException {
        return new Promo(rs.getString("kode_promo"), rs.getDouble("persen_diskon"),
                rs.getDate("berlaku_sampai"), rs.getInt("kuota"));
    }

    public static ArrayList<Promo> getSemua() throws SQLException {
        ArrayList<Promo> hasil = new ArrayList<>();
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT * FROM promo ORDER BY berlaku_sampai DESC");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(toPromo(rs));
            }
        }
        return hasil;
    }

    public static void simpan(Promo p) throws SQLException {
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement("INSERT INTO promo VALUES (?, ?, ?, ?)")) {
            ps.setString(1, p.getKodePromo());
            ps.setDouble(2, p.getPersenDiskon());
            ps.setDate(3, new java.sql.Date(p.getBerlakuSampai().getTime()));
            ps.setInt(4, p.getKuota());
            ps.executeUpdate();
        }
    }

    public static void updateKuota(Promo p) throws SQLException {
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement("UPDATE promo SET kuota = ? WHERE kode_promo = ?")) {
            ps.setInt(1, p.getKuota());
            ps.setString(2, p.getKodePromo());
            ps.executeUpdate();
        }
    }
}

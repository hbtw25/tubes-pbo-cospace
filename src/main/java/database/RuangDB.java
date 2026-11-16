package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import models.HotDesk;
import models.Ruang;
import models.RuangMeeting;
import models.RuangPrivat;

/**
 * Akses tabel ruang dan coworking_space.
 *
 * PIC: Zeifira Naila Syakira
 */
public class RuangDB {

    // Membuat objek subclass Ruang yang sesuai dengan kolom jenis
    public static Ruang toRuang(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_ruang");
        String nama = rs.getString("nama_ruang");
        String jenis = rs.getString("jenis");
        int kapasitas = rs.getInt("kapasitas");
        double tarif = rs.getDouble("tarif_per_jam");
        String fasilitas = rs.getString("fasilitas");

        Ruang r;
        if (jenis.equals("Hot Desk")) {
            r = new HotDesk(id, nama, tarif, fasilitas, rs.getString("nomor_meja"));
        } else if (jenis.equals("Ruang Privat")) {
            r = new RuangPrivat(id, nama, kapasitas, tarif, fasilitas);
        } else {
            r = new RuangMeeting(id, nama, kapasitas, tarif, fasilitas, rs.getBoolean("ada_proyektor"));
        }
        r.setAktif(rs.getBoolean("aktif"));
        return r;
    }

    public static ArrayList<Ruang> getSemua() throws SQLException {
        ArrayList<Ruang> hasil = new ArrayList<>();
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT * FROM ruang ORDER BY jenis, nama_ruang");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(toRuang(rs));
            }
        }
        return hasil;
    }

    public static Ruang cari(int idRuang) throws SQLException {
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT * FROM ruang WHERE id_ruang = ?")) {
            ps.setInt(1, idRuang);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return toRuang(rs);
                }
            }
        }
        return null;
    }

    public static void simpan(Ruang r) throws SQLException {
        String sql = "INSERT INTO ruang (nama_ruang, jenis, kapasitas, tarif_per_jam, fasilitas, aktif, "
                + "nomor_meja, ada_proyektor) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            isiParameter(ps, r);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    r.setIdRuang(rs.getInt(1));
                }
            }
        }
    }

    public static void update(Ruang r) throws SQLException {
        String sql = "UPDATE ruang SET nama_ruang = ?, jenis = ?, kapasitas = ?, tarif_per_jam = ?, "
                + "fasilitas = ?, aktif = ?, nomor_meja = ?, ada_proyektor = ? WHERE id_ruang = ?";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            isiParameter(ps, r);
            ps.setInt(9, r.getIdRuang());
            ps.executeUpdate();
        }
    }

    // Mengisi parameter 1-8 yang sama untuk INSERT dan UPDATE
    private static void isiParameter(PreparedStatement ps, Ruang r) throws SQLException {
        ps.setString(1, r.getNamaRuang());
        ps.setString(2, r.getJenis());
        ps.setInt(3, r.getKapasitas());
        ps.setDouble(4, r.getTarifPerJam());
        ps.setString(5, r.getFasilitas());
        ps.setBoolean(6, r.isAktif());
        if (r instanceof HotDesk) {
            ps.setString(7, ((HotDesk) r).getNomorMeja());
        } else {
            ps.setString(7, null);
        }
        if (r instanceof RuangMeeting) {
            ps.setBoolean(8, ((RuangMeeting) r).isAdaProyektor());
        } else {
            ps.setBoolean(8, false);
        }
    }
}

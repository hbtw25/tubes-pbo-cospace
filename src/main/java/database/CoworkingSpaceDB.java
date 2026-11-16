package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import models.CoworkingSpace;
import models.Ruang;

/**
 * Mengambil data co-working space beserta semua ruangnya.
 *
 * PIC: Zeifira Naila Syakira
 */
public class CoworkingSpaceDB {

    public static CoworkingSpace ambil() throws SQLException {
        ArrayList<Ruang> semuaRuang = RuangDB.getSemua();
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT * FROM coworking_space WHERE id = 1");
                ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                throw new SQLException("Data co-working space belum diisi");
            }
            CoworkingSpace cs = new CoworkingSpace(rs.getString("nama"), rs.getString("alamat"),
                    rs.getInt("jam_buka"), rs.getInt("jam_tutup"), semuaRuang.size() + 10);
            for (Ruang r : semuaRuang) {
                cs.tambahRuang(r);
            }
            return cs;
        }
    }
}

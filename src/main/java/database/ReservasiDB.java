package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import models.EWallet;
import models.Member;
import models.Pembayaran;
import models.Promo;
import models.Reservasi;
import models.Ruang;
import models.TransferBank;
import models.Tunai;

/**
 * Akses tabel reservasi beserta data member, ruang, promo, dan pembayarannya.
 *
 * PIC: Neizshia Ratu Rahmadhani Ockry (reservasi) dan Salsabila Yurnisa Br Tarigan (pembayaran)
 */
public class ReservasiDB {

    // Query dasar: gabungan reservasi + member + ruang + promo + pembayaran
    private static final String SELECT_DASAR = "SELECT r.id_reservasi, r.tanggal, r.jam_mulai, r.jam_selesai, "
            + "r.status, r.total_harga, "
            + "u.id_user, u.nama, u.email, u.password, u.no_hp, u.poin_member, u.tanggal_daftar, "
            + "g.id_ruang, g.nama_ruang, g.jenis, g.kapasitas, g.tarif_per_jam, g.fasilitas, g.aktif, "
            + "g.nomor_meja, g.ada_proyektor, "
            + "p.kode_promo, p.persen_diskon, p.berlaku_sampai, p.kuota, "
            + "b.id_pembayaran, b.metode, b.jumlah, b.waktu_bayar, b.status AS status_bayar, b.nama_bank, "
            + "b.no_rekening, b.bukti_transfer, b.jenis_ewallet, b.no_hp AS bayar_no_hp, b.uang_diterima "
            + "FROM reservasi r "
            + "JOIN users u ON r.id_member = u.id_user "
            + "JOIN ruang g ON r.id_ruang = g.id_ruang "
            + "LEFT JOIN promo p ON r.kode_promo = p.kode_promo "
            + "LEFT JOIN pembayaran b ON b.id_reservasi = r.id_reservasi ";

    private static final String URUTAN = " ORDER BY r.tanggal DESC, r.jam_mulai DESC";

    public static Reservasi toReservasi(ResultSet rs) throws SQLException {
        Member member = new Member(rs.getInt("id_user"), rs.getString("nama"), rs.getString("email"),
                rs.getString("password"), rs.getString("no_hp"), rs.getInt("poin_member"),
                rs.getDate("tanggal_daftar"));
        Ruang ruang = RuangDB.toRuang(rs);
        Promo promo = null;
        if (rs.getString("kode_promo") != null) {
            promo = PromoDB.toPromo(rs);
        }
        Reservasi r = new Reservasi(rs.getInt("id_reservasi"), member, ruang, rs.getDate("tanggal"),
                rs.getInt("jam_mulai"), rs.getInt("jam_selesai"), rs.getString("status"), promo,
                rs.getDouble("total_harga"));

        // Data pembayaran (jika sudah ada)
        String metode = rs.getString("metode");
        if (metode != null) {
            double jumlah = rs.getDouble("jumlah");
            Pembayaran bayar;
            if (metode.equals("Transfer Bank")) {
                bayar = new TransferBank(jumlah, rs.getString("nama_bank"), rs.getString("no_rekening"),
                        rs.getString("bukti_transfer"));
            } else if (metode.equals("E-Wallet")) {
                bayar = new EWallet(jumlah, rs.getString("jenis_ewallet"), rs.getString("bayar_no_hp"));
            } else {
                bayar = new Tunai(jumlah, rs.getDouble("uang_diterima"));
            }
            bayar.setDataDariDatabase(rs.getInt("id_pembayaran"), rs.getTimestamp("waktu_bayar"),
                    rs.getString("status_bayar"));
            r.setPembayaran(bayar);
        }
        return r;
    }

    // Menjalankan query SELECT_DASAR + kondisi, parameter diisi berurutan
    private static ArrayList<Reservasi> ambil(String kondisi, Object... parameter) throws SQLException {
        ArrayList<Reservasi> hasil = new ArrayList<>();
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(SELECT_DASAR + kondisi + URUTAN)) {
            for (int i = 0; i < parameter.length; i++) {
                ps.setObject(i + 1, parameter[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.add(toReservasi(rs));
                }
            }
        }
        return hasil;
    }

    public static ArrayList<Reservasi> getSemua() throws SQLException {
        return ambil("");
    }

    public static ArrayList<Reservasi> getByMember(int idMember) throws SQLException {
        return ambil("WHERE r.id_member = ?", idMember);
    }

    public static ArrayList<Reservasi> getByRuangTanggal(int idRuang, Date tanggal) throws SQLException {
        return ambil("WHERE r.id_ruang = ? AND r.tanggal = ?", idRuang, new java.sql.Date(tanggal.getTime()));
    }

    public static ArrayList<Reservasi> getByPeriode(Date awal, Date akhir) throws SQLException {
        return ambil("WHERE r.tanggal BETWEEN ? AND ?", new java.sql.Date(awal.getTime()),
                new java.sql.Date(akhir.getTime()));
    }

    public static Reservasi cari(int idReservasi) throws SQLException {
        ArrayList<Reservasi> hasil = ambil("WHERE r.id_reservasi = ?", idReservasi);
        if (hasil.isEmpty()) {
            return null;
        }
        return hasil.get(0);
    }

    public static void simpan(Reservasi r) throws SQLException {
        String sql = "INSERT INTO reservasi (id_member, id_ruang, tanggal, jam_mulai, jam_selesai, status, "
                + "kode_promo, total_harga) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Koneksi.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getMember().getIdUser());
            ps.setInt(2, r.getRuang().getIdRuang());
            ps.setDate(3, new java.sql.Date(r.getTanggal().getTime()));
            ps.setInt(4, r.getJamMulai());
            ps.setInt(5, r.getJamSelesai());
            ps.setString(6, r.getStatus());
            if (r.getPromo() != null) {
                ps.setString(7, r.getPromo().getKodePromo());
            } else {
                ps.setString(7, null);
            }
            ps.setDouble(8, r.getTotalHarga());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    r.setIdReservasi(rs.getInt(1));
                }
            }
        }
    }

    // Menyimpan status reservasi dan status pembayarannya (jika ada) dalam satu transaksi
    public static void updateStatus(Reservasi r) throws SQLException {
        try (Connection con = Koneksi.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE reservasi SET status = ? WHERE id_reservasi = ?")) {
                ps.setString(1, r.getStatus());
                ps.setInt(2, r.getIdReservasi());
                ps.executeUpdate();
            }
            if (r.getPembayaran() != null) {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE pembayaran SET status = ? WHERE id_reservasi = ?")) {
                    ps.setString(1, r.getPembayaran().getStatus());
                    ps.setInt(2, r.getIdReservasi());
                    ps.executeUpdate();
                }
            }
            con.commit();
        }
    }

    // Menyimpan pembayaran baru sekaligus mengubah status reservasi menjadi Aktif
    public static void simpanPembayaran(Reservasi r) throws SQLException {
        Pembayaran p = r.getPembayaran();
        String sql = "INSERT INTO pembayaran (id_reservasi, metode, jumlah, waktu_bayar, status, nama_bank, "
                + "no_rekening, bukti_transfer, jenis_ewallet, no_hp, uang_diterima) "
                + "VALUES (?, ?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Koneksi.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, r.getIdReservasi());
                ps.setString(2, p.getMetode());
                ps.setDouble(3, p.getJumlah());
                ps.setString(4, p.getStatus());
                ps.setString(5, null);
                ps.setString(6, null);
                ps.setString(7, null);
                ps.setString(8, null);
                ps.setString(9, null);
                ps.setNull(10, java.sql.Types.DOUBLE);
                if (p instanceof TransferBank) {
                    TransferBank t = (TransferBank) p;
                    ps.setString(5, t.getNamaBank());
                    ps.setString(6, t.getNoRekening());
                    ps.setString(7, t.getBuktiTransfer());
                } else if (p instanceof EWallet) {
                    EWallet e = (EWallet) p;
                    ps.setString(8, e.getJenisEWallet());
                    ps.setString(9, e.getNoHp());
                } else if (p instanceof Tunai) {
                    ps.setDouble(10, ((Tunai) p).getUangDiterima());
                }
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE reservasi SET status = ? WHERE id_reservasi = ?")) {
                ps.setString(1, r.getStatus());
                ps.setInt(2, r.getIdReservasi());
                ps.executeUpdate();
            }
            con.commit();
        }
    }
}

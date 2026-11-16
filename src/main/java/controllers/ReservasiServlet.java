package controllers;

import database.CoworkingSpaceDB;
import database.PromoDB;
import database.ReservasiDB;
import database.UserDB;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import models.CoworkingSpace;
import models.JadwalBentrokException;
import models.Member;
import models.PengelolaReservasi;
import models.Promo;
import models.Reservasi;
import models.Ruang;
import models.User;
import utils.Helper;

/**
 * Controller reservasi: form pemesanan + jadwal ruang, riwayat member,
 * daftar semua reservasi (admin), pembatalan, dan penandaan selesai.
 *
 * PIC: Neizshia Ratu Rahmadhani Ockry
 */
@WebServlet(name = "ReservasiServlet", urlPatterns = {"/reservasi"})
public class ReservasiServlet extends HttpServlet {

    // Kunci agar dua member tidak bisa memesan slot yang sama pada waktu bersamaan
    private static final Object KUNCI = new Object();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = Helper.getUser(request);
        if (user == null) {
            response.sendRedirect("auth");
            return;
        }
        try {
            if ("form".equals(request.getParameter("aksi")) && !Helper.isAdmin(user)) {
                tampilkanForm(request, response);
                return;
            }

            PengelolaReservasi pengelola;
            if (Helper.isAdmin(user)) {
                pengelola = muatKePengelola(ReservasiDB.getSemua());
                Reservasi[] semua = pengelola.getSemuaReservasi();
                // Filter status (opsional)
                String status = request.getParameter("status");
                ArrayList<Reservasi> hasil = new ArrayList<>();
                for (Reservasi r : semua) {
                    if (status == null || status.isEmpty() || r.getStatus().equals(status)) {
                        hasil.add(r);
                    }
                }
                request.setAttribute("daftarReservasi", hasil);
                request.getRequestDispatcher("/WEB-INF/views/reservasi_admin.jsp").forward(request, response);
            } else {
                pengelola = muatKePengelola(ReservasiDB.getByMember(user.getIdUser()));
                request.setAttribute("daftarReservasi", pengelola.getRiwayat((Member) user));
                request.getRequestDispatcher("/WEB-INF/views/riwayat.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Gagal memuat reservasi: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    // Menampilkan form pemesanan beserta jadwal ruang pada tanggal yang dipilih
    private void tampilkanForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        CoworkingSpace cs = CoworkingSpaceDB.ambil();
        Ruang ruang = cs.cariRuang(Helper.parseInt(request.getParameter("idRuang"), 0));
        if (ruang == null || !ruang.isAktif()) {
            Helper.setError(request, "Ruang tidak tersedia");
            response.sendRedirect("ruang");
            return;
        }
        Date tanggal = Helper.parseTanggal(request.getParameter("tanggal"));
        if (tanggal == null) {
            tanggal = new Date();
        }
        PengelolaReservasi pengelola = muatKePengelola(ReservasiDB.getByRuangTanggal(ruang.getIdRuang(), tanggal));

        request.setAttribute("cs", cs);
        request.setAttribute("ruang", ruang);
        request.setAttribute("tanggal", tanggal);
        request.setAttribute("jadwal", pengelola.getJadwalRuang(ruang, tanggal));
        request.getRequestDispatcher("/WEB-INF/views/form_reservasi.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User user = Helper.getUser(request);
        if (user == null) {
            response.sendRedirect("auth");
            return;
        }
        String aksi = request.getParameter("aksi");
        try {
            if ("buat".equals(aksi) && !Helper.isAdmin(user)) {
                buatReservasi(request, response, (Member) user);
                return;
            }

            Reservasi r = ReservasiDB.cari(Helper.parseInt(request.getParameter("id"), 0));
            // Member hanya boleh mengubah reservasi miliknya sendiri
            if (r == null || (!Helper.isAdmin(user) && r.getMember().getIdUser() != user.getIdUser())) {
                Helper.setError(request, "Reservasi tidak ditemukan");
                response.sendRedirect("reservasi");
                return;
            }

            if ("batal".equals(aksi)) {
                PengelolaReservasi pengelola = new PengelolaReservasi(1, 0);
                pengelola.tambahReservasi(r);
                if (pengelola.batalkanReservasi(r.getIdReservasi())) {
                    ReservasiDB.updateStatus(r);
                    String info = "Reservasi #" + r.getIdReservasi() + " dibatalkan";
                    if (r.getPembayaran() != null && r.getPembayaran().getStatus().equals("Direfund")) {
                        info = info + ", dana dikembalikan 80%";
                    }
                    Helper.setPesan(request, info);
                } else {
                    Helper.setError(request, "Reservasi ini tidak bisa dibatalkan");
                }
            } else if ("selesai".equals(aksi) && Helper.isAdmin(user)) {
                if (r.getStatus().equals(Reservasi.AKTIF)) {
                    r.selesaikan();
                    ReservasiDB.updateStatus(r);
                    UserDB.updatePoin(r.getMember());
                    Helper.setPesan(request, "Reservasi #" + r.getIdReservasi() + " selesai, member mendapat poin");
                } else {
                    Helper.setError(request, "Hanya reservasi Aktif yang bisa diselesaikan");
                }
            }
        } catch (SQLException e) {
            Helper.setError(request, "Gagal memproses reservasi: " + e.getMessage());
        }
        response.sendRedirect("reservasi");
    }

    private void buatReservasi(HttpServletRequest request, HttpServletResponse response, Member member)
            throws IOException, SQLException {
        int idRuang = Helper.parseInt(request.getParameter("idRuang"), 0);
        String tanggalTeks = request.getParameter("tanggal");
        String kembali = "reservasi?aksi=form&idRuang=" + idRuang + "&tanggal="
                + java.net.URLEncoder.encode(tanggalTeks == null ? "" : tanggalTeks, "UTF-8");
        try {
            Date tanggal = Helper.parseTanggal(tanggalTeks);
            if (tanggal == null) {
                throw new IllegalArgumentException("Tanggal tidak valid");
            }
            int jamMulai = Helper.parseInt(request.getParameter("jamMulai"), -1);
            int jamSelesai = Helper.parseInt(request.getParameter("jamSelesai"), -1);
            String kodePromo = request.getParameter("kodePromo");

            Reservasi r;
            synchronized (KUNCI) {
                CoworkingSpace cs = CoworkingSpaceDB.ambil();
                Ruang ruang = cs.cariRuang(idRuang);
                if (ruang == null) {
                    throw new IllegalArgumentException("Ruang tidak ditemukan");
                }
                if (jamMulai < cs.getJamBuka() || jamSelesai > cs.getJamTutup()) {
                    throw new IllegalArgumentException("Jam harus di dalam jam operasional "
                            + cs.getJamBuka() + ".00 - " + cs.getJamTutup() + ".00");
                }
                if (isHariIni(tanggal) && jamMulai <= Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                    throw new IllegalArgumentException("Jam mulai sudah lewat");
                }

                PengelolaReservasi pengelola = muatKePengelola(ReservasiDB.getByRuangTanggal(idRuang, tanggal));
                r = pengelola.buatReservasi(member, ruang, tanggal, jamMulai, jamSelesai);

                Promo promo = null;
                if (kodePromo != null && !kodePromo.trim().isEmpty()) {
                    for (Promo p : PromoDB.getSemua()) {
                        pengelola.tambahPromo(p);
                    }
                    promo = pengelola.cariPromo(kodePromo.trim());
                    if (promo == null) {
                        throw new IllegalArgumentException("Kode promo tidak ditemukan");
                    }
                    if (!r.terapkanPromo(promo)) {
                        throw new IllegalArgumentException("Kode promo sudah tidak berlaku atau kuota habis");
                    }
                }
                ReservasiDB.simpan(r);
                if (promo != null) {
                    PromoDB.updateKuota(promo);
                }
            }
            Helper.setPesan(request, "Reservasi berhasil dibuat, silakan lakukan pembayaran");
            response.sendRedirect("bayar?id=" + r.getIdReservasi());

        } catch (JadwalBentrokException e) {
            Helper.setError(request, e.getMessage() + " di " + e.getNamaRuang() + ", pilih jam lain");
            response.sendRedirect(kembali);
        } catch (IllegalArgumentException e) {
            Helper.setError(request, e.getMessage());
            response.sendRedirect(kembali);
        }
    }

    private PengelolaReservasi muatKePengelola(ArrayList<Reservasi> data) {
        PengelolaReservasi pengelola = new PengelolaReservasi(data.size() + 1, 50);
        for (Reservasi r : data) {
            pengelola.tambahReservasi(r);
        }
        return pengelola;
    }

    private boolean isHariIni(Date tanggal) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        return f.format(tanggal).equals(f.format(new Date()));
    }
}

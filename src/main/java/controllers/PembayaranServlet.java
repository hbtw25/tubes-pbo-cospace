package controllers;

import database.ReservasiDB;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import models.Reservasi;
import models.User;
import utils.Helper;

/**
 * Controller pembayaran reservasi.
 * Member membayar lewat transfer bank / e-wallet, admin mencatat pembayaran tunai di resepsionis.
 *
 * PIC: Salsabila Yurnisa Br Tarigan
 */
@WebServlet(name = "PembayaranServlet", urlPatterns = {"/bayar"})
public class PembayaranServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = Helper.getUser(request);
        if (user == null) {
            response.sendRedirect("auth");
            return;
        }
        try {
            Reservasi r = ambilReservasi(request, user);
            if (r == null) {
                Helper.setError(request, "Reservasi tidak ditemukan");
                response.sendRedirect("reservasi");
                return;
            }
            request.setAttribute("reservasi", r);
            request.getRequestDispatcher("/WEB-INF/views/bayar.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "Gagal memuat data: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
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
        String metode = request.getParameter("metode");
        try {
            Reservasi r = ambilReservasi(request, user);
            if (r == null) {
                Helper.setError(request, "Reservasi tidak ditemukan");
                response.sendRedirect("reservasi");
                return;
            }
            if (!r.getStatus().equals(Reservasi.MENUNGGU)) {
                Helper.setError(request, "Reservasi ini tidak menunggu pembayaran");
                response.sendRedirect("reservasi");
                return;
            }

            boolean berhasil = false;
            if ("transfer".equals(metode) && !Helper.isAdmin(user)) {
                berhasil = r.bayarTransfer(request.getParameter("namaBank"), request.getParameter("noRekening"),
                        request.getParameter("buktiTransfer"));
            } else if ("ewallet".equals(metode) && !Helper.isAdmin(user)) {
                berhasil = r.bayarEWallet(request.getParameter("jenisEWallet"), request.getParameter("noHp"));
            } else if ("tunai".equals(metode) && Helper.isAdmin(user)) {
                berhasil = r.bayarTunai(Helper.parseDouble(request.getParameter("uangDiterima"), 0));
            }

            if (!berhasil) {
                Helper.setError(request, "Data pembayaran tidak valid, periksa kembali isian Anda");
                response.sendRedirect("bayar?id=" + r.getIdReservasi());
                return;
            }
            ReservasiDB.simpanPembayaran(r);
            String info = "Pembayaran " + r.getPembayaran().getMetode() + " berhasil, reservasi #"
                    + r.getIdReservasi() + " sekarang Aktif";
            if (r.getPembayaran() instanceof models.Tunai) {
                info = info + ". Kembalian: " + Helper.rupiah(((models.Tunai) r.getPembayaran()).getKembalian());
            }
            Helper.setPesan(request, info);
            response.sendRedirect("reservasi");
        } catch (SQLException e) {
            Helper.setError(request, "Gagal menyimpan pembayaran: " + e.getMessage());
            response.sendRedirect("reservasi");
        }
    }

    // Admin boleh melihat semua reservasi, member hanya miliknya sendiri
    private Reservasi ambilReservasi(HttpServletRequest request, User user) throws SQLException {
        Reservasi r = ReservasiDB.cari(Helper.parseInt(request.getParameter("id"), 0));
        if (r == null) {
            return null;
        }
        if (!Helper.isAdmin(user) && r.getMember().getIdUser() != user.getIdUser()) {
            return null;
        }
        return r;
    }
}

package controllers;

import database.PromoDB;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import models.PengelolaReservasi;
import models.Promo;
import utils.Helper;

/**
 * Controller kode promo (khusus admin): daftar dan tambah promo.
 *
 * PIC: Neizshia Ratu Rahmadhani Ockry
 */
@WebServlet(name = "PromoServlet", urlPatterns = {"/promo"})
public class PromoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!Helper.isAdmin(Helper.getUser(request))) {
            response.sendRedirect("auth");
            return;
        }
        try {
            request.setAttribute("daftarPromo", PromoDB.getSemua());
        } catch (SQLException e) {
            request.setAttribute("error", "Gagal memuat promo: " + e.getMessage());
            request.setAttribute("daftarPromo", new ArrayList<Promo>());
        }
        request.getRequestDispatcher("/WEB-INF/views/promo_admin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!Helper.isAdmin(Helper.getUser(request))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            Date berlaku = Helper.parseTanggal(request.getParameter("berlakuSampai"));
            Promo baru = new Promo(request.getParameter("kodePromo"),
                    Helper.parseDouble(request.getParameter("persenDiskon"), 0), berlaku,
                    Helper.parseInt(request.getParameter("kuota"), -1));

            // Cek kode promo kembar memakai PengelolaReservasi
            ArrayList<Promo> semua = PromoDB.getSemua();
            PengelolaReservasi pengelola = new PengelolaReservasi(0, semua.size() + 1);
            for (Promo p : semua) {
                pengelola.tambahPromo(p);
            }
            if (!pengelola.tambahPromo(baru)) {
                throw new IllegalArgumentException("Kode promo " + baru.getKodePromo() + " sudah ada");
            }
            PromoDB.simpan(baru);
            Helper.setPesan(request, "Promo " + baru.getKodePromo() + " berhasil ditambahkan");
        } catch (IllegalArgumentException e) {
            Helper.setError(request, e.getMessage());
        } catch (SQLException e) {
            Helper.setError(request, "Gagal menyimpan promo: " + e.getMessage());
        }
        response.sendRedirect("promo");
    }
}

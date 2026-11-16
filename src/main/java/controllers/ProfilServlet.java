package controllers;

import database.UserDB;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import models.PengelolaAkun;
import models.User;
import utils.Helper;

/**
 * Controller halaman profil: lihat profil & poin, ubah profil, ubah password.
 *
 * PIC: Harsya Brahmantyo Wibowo
 */
@WebServlet(name = "ProfilServlet", urlPatterns = {"/profil"})
public class ProfilServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = Helper.getUser(request);
        if (user == null) {
            response.sendRedirect("auth");
            return;
        }
        try {
            // Ambil ulang dari database agar poin member selalu terbaru
            User terbaru = UserDB.cari(user.getIdUser());
            request.getSession().setAttribute("user", terbaru);
            request.setAttribute("profil", terbaru);
        } catch (SQLException e) {
            request.setAttribute("error", "Gagal memuat profil: " + e.getMessage());
            request.setAttribute("profil", user);
        }
        request.getRequestDispatcher("/WEB-INF/views/profil.jsp").forward(request, response);
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
            User dariDb = UserDB.cari(user.getIdUser());
            PengelolaAkun pengelola = new PengelolaAkun(1);
            pengelola.tambahUser(dariDb);
            pengelola.setUserAktif(dariDb);

            if ("password".equals(aksi)) {
                String baru = request.getParameter("passwordBaru");
                if (!baru.equals(request.getParameter("konfirmasi"))) {
                    Helper.setError(request, "Konfirmasi password baru tidak sama");
                } else if (pengelola.ubahPassword(request.getParameter("passwordLama"), baru)) {
                    UserDB.updatePassword(dariDb);
                    Helper.setPesan(request, "Password berhasil diubah");
                } else {
                    Helper.setError(request, "Password lama salah");
                }
            } else {
                pengelola.ubahProfil(request.getParameter("nama"), request.getParameter("noHp"));
                UserDB.updateProfil(dariDb);
                Helper.setPesan(request, "Profil berhasil diubah");
            }
            request.getSession().setAttribute("user", dariDb);
        } catch (IllegalArgumentException e) {
            Helper.setError(request, e.getMessage());
        } catch (SQLException e) {
            Helper.setError(request, "Gagal menyimpan: " + e.getMessage());
        }
        response.sendRedirect("profil");
    }
}

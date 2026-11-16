package controllers;

import database.UserDB;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.Member;
import models.PengelolaAkun;
import models.User;
import utils.Helper;

/**
 * Controller untuk login, registrasi, dan logout.
 *
 * PIC: Harsya Brahmantyo Wibowo
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/auth"})
public class AuthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = Helper.getUser(request);
        if (user != null) {
            // Sudah login, langsung ke halaman sesuai role
            response.sendRedirect(Helper.isAdmin(user) ? "admin" : "ruang");
            return;
        }
        String aksi = request.getParameter("aksi");
        if ("register".equals(aksi)) {
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String aksi = request.getParameter("aksi");

        if ("logout".equals(aksi)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect("auth");
            return;
        }

        try {
            // Semua akun dimuat ke PengelolaAkun, lalu login/register diproses oleh kelas tersebut
            ArrayList<User> semuaUser = UserDB.getSemua();
            PengelolaAkun pengelola = new PengelolaAkun(semuaUser.size() + 1);
            for (User u : semuaUser) {
                pengelola.tambahUser(u);
            }

            if ("register".equals(aksi)) {
                Member baru = pengelola.register(request.getParameter("nama"), request.getParameter("email"),
                        request.getParameter("password"), request.getParameter("noHp"));
                UserDB.simpanMember(baru);
                Helper.setPesan(request, "Registrasi berhasil, silakan login");
                response.sendRedirect("auth");
                return;
            }

            // aksi login
            User user = pengelola.login(request.getParameter("email"), request.getParameter("password"));
            if (user == null) {
                request.setAttribute("error", "Email atau password salah");
                request.setAttribute("email", request.getParameter("email"));
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                return;
            }
            // Session lama dibuang lalu dibuat session baru (mencegah session fixation)
            HttpSession lama = request.getSession(false);
            if (lama != null) {
                lama.invalidate();
            }
            request.getSession(true).setAttribute("user", user);
            response.sendRedirect(Helper.isAdmin(user) ? "admin" : "ruang");

        } catch (IllegalArgumentException e) {
            // Error validasi dari kelas model (email tidak valid, password pendek, dll)
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "Gagal terhubung ke database: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}

package controllers;

import database.CoworkingSpaceDB;
import database.ReservasiDB;
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
import models.Laporan;
import models.Reservasi;
import utils.Helper;

/**
 * Dashboard admin: ringkasan ruang, reservasi, dan pendapatan bulan ini.
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/admin"})
public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!Helper.isAdmin(Helper.getUser(request))) {
            response.sendRedirect("auth");
            return;
        }
        try {
            CoworkingSpace cs = CoworkingSpaceDB.ambil();
            ArrayList<Reservasi> semua = ReservasiDB.getSemua();

            int ruangAktif = cs.filterRuang("Semua", 1).length;
            int menunggu = 0;
            int hariIni = 0;
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            String tglHariIni = f.format(new Date());
            for (Reservasi r : semua) {
                if (r.getStatus().equals(Reservasi.MENUNGGU)) {
                    menunggu++;
                }
                if (r.getStatus().equals(Reservasi.AKTIF) && f.format(r.getTanggal()).equals(tglHariIni)) {
                    hariIni++;
                }
            }

            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_MONTH, 1);
            Date awal = c.getTime();
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
            Laporan bulanIni = new Laporan(awal, c.getTime(), semua.toArray(new Reservasi[0]));

            // 5 reservasi terbaru
            ArrayList<Reservasi> terbaru = new ArrayList<>();
            for (int i = 0; i < semua.size() && i < 5; i++) {
                terbaru.add(semua.get(i));
            }

            request.setAttribute("cs", cs);
            request.setAttribute("ruangAktif", ruangAktif);
            request.setAttribute("menunggu", menunggu);
            request.setAttribute("hariIni", hariIni);
            request.setAttribute("pendapatanBulanIni", bulanIni.hitungTotalPendapatan());
            request.setAttribute("terbaru", terbaru);
            request.getRequestDispatcher("/WEB-INF/views/admin_dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "Gagal memuat dashboard: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}

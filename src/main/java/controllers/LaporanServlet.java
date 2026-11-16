package controllers;

import database.ReservasiDB;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import models.Laporan;
import models.Reservasi;
import utils.Helper;

/**
 * Controller laporan pendapatan per periode (khusus admin).
 *
 * PIC: Salsabila Yurnisa Br Tarigan
 */
@WebServlet(name = "LaporanServlet", urlPatterns = {"/laporan"})
public class LaporanServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!Helper.isAdmin(Helper.getUser(request))) {
            response.sendRedirect("auth");
            return;
        }
        // Default periode: awal bulan sampai akhir bulan ini
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        Date awalBulan = c.getTime();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date akhirBulan = c.getTime();

        Date awal = Helper.parseTanggal(request.getParameter("awal"));
        Date akhir = Helper.parseTanggal(request.getParameter("akhir"));
        if (awal == null) {
            awal = awalBulan;
        }
        if (akhir == null) {
            akhir = akhirBulan;
        }
        request.setAttribute("awal", awal);
        request.setAttribute("akhir", akhir);

        try {
            ArrayList<Reservasi> data = ReservasiDB.getByPeriode(awal, akhir);
            Laporan laporan = new Laporan(awal, akhir, data.toArray(new Reservasi[0]));
            request.setAttribute("laporan", laporan);
            request.setAttribute("daftarReservasi", data);
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
        } catch (SQLException e) {
            request.setAttribute("error", "Gagal memuat laporan: " + e.getMessage());
        }
        request.getRequestDispatcher("/WEB-INF/views/laporan.jsp").forward(request, response);
    }
}

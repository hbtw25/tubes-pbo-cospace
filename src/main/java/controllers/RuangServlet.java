package controllers;

import database.CoworkingSpaceDB;
import database.RuangDB;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import models.Admin;
import models.CoworkingSpace;
import models.HotDesk;
import models.Ruang;
import models.RuangMeeting;
import models.RuangPrivat;
import models.User;
import utils.Helper;

/**
 * Controller ruang: katalog + filter untuk member, kelola ruang untuk admin.
 *
 * PIC: Zeifira Naila Syakira
 */
@WebServlet(name = "RuangServlet", urlPatterns = {"/ruang"})
public class RuangServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = Helper.getUser(request);
        if (user == null) {
            response.sendRedirect("auth");
            return;
        }
        try {
            CoworkingSpace cs = CoworkingSpaceDB.ambil();
            request.setAttribute("cs", cs);

            if (Helper.isAdmin(user)) {
                String aksi = request.getParameter("aksi");
                if ("tambah".equals(aksi)) {
                    request.getRequestDispatcher("/WEB-INF/views/form_ruang.jsp").forward(request, response);
                    return;
                }
                if ("edit".equals(aksi)) {
                    Ruang r = cs.cariRuang(Helper.parseInt(request.getParameter("id"), 0));
                    if (r == null) {
                        Helper.setError(request, "Ruang tidak ditemukan");
                        response.sendRedirect("ruang");
                        return;
                    }
                    request.setAttribute("ruang", r);
                    request.getRequestDispatcher("/WEB-INF/views/form_ruang.jsp").forward(request, response);
                    return;
                }
                request.setAttribute("daftarRuang", cs.getDaftarRuang());
                request.getRequestDispatcher("/WEB-INF/views/ruang_admin.jsp").forward(request, response);
                return;
            }

            // Member: katalog ruang aktif dengan filter jenis & kapasitas minimum
            String jenis = request.getParameter("jenis");
            int kapasitasMin = Helper.parseInt(request.getParameter("kapasitas"), 1);
            request.setAttribute("daftarRuang", cs.filterRuang(jenis, kapasitasMin));
            request.getRequestDispatcher("/WEB-INF/views/katalog.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("error", "Gagal memuat data ruang: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        User user = Helper.getUser(request);
        if (!Helper.isAdmin(user)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Hanya admin yang boleh mengelola ruang");
            return;
        }
        Admin admin = (Admin) user;
        String aksi = request.getParameter("aksi");
        try {
            CoworkingSpace cs = CoworkingSpaceDB.ambil();
            int id = Helper.parseInt(request.getParameter("id"), 0);

            if ("nonaktif".equals(aksi)) {
                if (admin.hapusRuang(cs, id)) {
                    RuangDB.update(cs.cariRuang(id));
                    Helper.setPesan(request, "Ruang dinonaktifkan");
                }
            } else if ("aktifkan".equals(aksi)) {
                Ruang r = cs.cariRuang(id);
                if (r != null) {
                    r.setAktif(true);
                    RuangDB.update(r);
                    Helper.setPesan(request, "Ruang diaktifkan kembali");
                }
            } else {
                simpanRuang(request, admin, cs, id);
            }
        } catch (IllegalArgumentException e) {
            Helper.setError(request, e.getMessage());
        } catch (SQLException e) {
            Helper.setError(request, "Gagal menyimpan ruang: " + e.getMessage());
        }
        response.sendRedirect("ruang");
    }

    // Tambah ruang baru (id = 0) atau ubah ruang yang sudah ada
    private void simpanRuang(HttpServletRequest request, Admin admin, CoworkingSpace cs, int id)
            throws SQLException {
        String nama = request.getParameter("namaRuang");
        int kapasitas = Helper.parseInt(request.getParameter("kapasitas"), 0);
        double tarif = Helper.parseDouble(request.getParameter("tarif"), 0);
        String fasilitas = request.getParameter("fasilitas");

        if (id == 0) {
            String jenis = request.getParameter("jenis");
            Ruang baru;
            if ("Hot Desk".equals(jenis)) {
                baru = new HotDesk(0, nama, tarif, fasilitas, request.getParameter("nomorMeja"));
            } else if ("Ruang Privat".equals(jenis)) {
                baru = new RuangPrivat(0, nama, kapasitas, tarif, fasilitas);
            } else if ("Ruang Meeting".equals(jenis)) {
                baru = new RuangMeeting(0, nama, kapasitas, tarif, fasilitas,
                        request.getParameter("adaProyektor") != null);
            } else {
                throw new IllegalArgumentException("Jenis ruang tidak valid");
            }
            admin.tambahRuang(cs, baru);
            RuangDB.simpan(baru);
            Helper.setPesan(request, "Ruang " + baru.getNamaRuang() + " berhasil ditambahkan");
            return;
        }

        Ruang r = cs.cariRuang(id);
        if (r == null) {
            throw new IllegalArgumentException("Ruang tidak ditemukan");
        }
        r.setNamaRuang(nama);
        r.setKapasitas(kapasitas);
        r.setTarifPerJam(tarif);
        r.setFasilitas(fasilitas);
        if (r instanceof HotDesk) {
            ((HotDesk) r).setNomorMeja(request.getParameter("nomorMeja"));
        }
        if (r instanceof RuangMeeting) {
            ((RuangMeeting) r).setAdaProyektor(request.getParameter("adaProyektor") != null);
        }
        RuangDB.update(r);
        Helper.setPesan(request, "Ruang " + r.getNamaRuang() + " berhasil diubah");
    }
}

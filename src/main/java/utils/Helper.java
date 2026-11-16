package utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import models.Admin;
import models.User;

/**
 * Kumpulan method static kecil yang dipakai di banyak servlet dan JSP.
 */
public class Helper {

    // Mencegah XSS: karakter HTML diubah agar tidak dijalankan browser
    public static String esc(Object teks) {
        if (teks == null) {
            return "";
        }
        return teks.toString()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public static String rupiah(double angka) {
        NumberFormat f = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        return "Rp" + f.format(Math.round(angka));
    }

    public static String tanggal(Date tgl) {
        if (tgl == null) {
            return "-";
        }
        return new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID")).format(tgl);
    }

    public static String tanggalJam(Date tgl) {
        if (tgl == null) {
            return "-";
        }
        return new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("id", "ID")).format(tgl);
    }

    // Format yyyy-MM-dd untuk value input type="date"
    public static String tanggalInput(Date tgl) {
        if (tgl == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(tgl);
    }

    // Mengubah teks yyyy-MM-dd menjadi Date, null jika format salah
    public static Date parseTanggal(String teks) {
        try {
            return java.sql.Date.valueOf(teks);
        } catch (Exception e) {
            return null;
        }
    }

    public static int parseInt(String teks, int nilaiDefault) {
        try {
            return Integer.parseInt(teks.trim());
        } catch (Exception e) {
            return nilaiDefault;
        }
    }

    public static double parseDouble(String teks, double nilaiDefault) {
        try {
            return Double.parseDouble(teks.trim());
        } catch (Exception e) {
            return nilaiDefault;
        }
    }

    // Kelas CSS badge sesuai status reservasi
    public static String badge(String status) {
        if (status.equals("Menunggu Pembayaran")) {
            return "badge badge-menunggu";
        } else if (status.equals("Aktif")) {
            return "badge badge-aktif";
        } else if (status.equals("Selesai")) {
            return "badge badge-selesai";
        }
        return "badge badge-batal";
    }

    // Jam dalam format 08.00
    public static String jam(int jam) {
        return String.format("%02d.00", jam);
    }

    // Mengambil user yang sedang login dari session (null jika belum login)
    public static User getUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute("user");
    }

    public static boolean isAdmin(User u) {
        return u instanceof Admin;
    }

    // Menyimpan pesan sukses/gagal ke session agar tampil setelah redirect
    public static void setPesan(HttpServletRequest request, String pesan) {
        request.getSession().setAttribute("pesan", pesan);
    }

    public static void setError(HttpServletRequest request, String error) {
        request.getSession().setAttribute("error", error);
    }
}

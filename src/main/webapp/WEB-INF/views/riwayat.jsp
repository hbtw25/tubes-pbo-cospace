<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% String judul = "Riwayat Reservasi"; %>
<%@include file="header.jsp"%>
<%
    Reservasi[] daftar = (Reservasi[]) request.getAttribute("daftarReservasi");
%>
<h1>Riwayat Reservasi</h1>
<p class="subjudul">Pembatalan minimal 24 jam sebelum jam mulai mendapat refund 80% (transfer bank / e-wallet).</p>

<div class="card">
    <% if (daftar.length == 0) { %>
        <p>Belum ada reservasi. <a href="ruang">Pesan ruang sekarang</a></p>
    <% } else { %>
    <div class="tabel-scroll">
    <table>
        <tr>
            <th>No</th>
            <th>Ruang</th>
            <th>Tanggal &amp; Jam</th>
            <th>Total</th>
            <th>Status</th>
            <th>Pembayaran</th>
            <th>Aksi</th>
        </tr>
        <% for (Reservasi r : daftar) { %>
        <tr>
            <td>#<%= r.getIdReservasi() %></td>
            <td><%= Helper.esc(r.getRuang().getNamaRuang()) %><br><small><%= r.getRuang().getJenis() %></small></td>
            <td><%= Helper.tanggal(r.getTanggal()) %><br>
                <small><%= Helper.jam(r.getJamMulai()) %> - <%= Helper.jam(r.getJamSelesai()) %>
                (<%= r.getDurasiJam() %> jam)</small></td>
            <td><%= Helper.rupiah(r.getTotalHarga()) %>
                <% if (r.getPromo() != null) { %><br><small>Promo <%= Helper.esc(r.getPromo().getKodePromo()) %></small><% } %>
            </td>
            <td><span class="<%= Helper.badge(r.getStatus()) %>"><%= r.getStatus() %></span></td>
            <td>
                <% if (r.getPembayaran() != null) { %>
                    <%= r.getPembayaran().getMetode() %><br><small><%= r.getPembayaran().getStatus() %></small>
                    <% if (r.getPembayaran() instanceof Refundable && r.getPembayaran().getStatus().equals(Pembayaran.DIREFUND)) { %>
                        <br><small>Refund <%= Helper.rupiah(((Refundable) r.getPembayaran()).hitungRefund()) %></small>
                    <% } %>
                <% } else { %>
                    -
                <% } %>
            </td>
            <td class="aksi">
                <% if (r.getStatus().equals(Reservasi.MENUNGGU)) { %>
                    <a class="btn btn-kecil" href="bayar?id=<%= r.getIdReservasi() %>">Bayar</a>
                <% } %>
                <% if (r.getStatus().equals(Reservasi.MENUNGGU) || r.getStatus().equals(Reservasi.AKTIF)) { %>
                    <form method="post" action="reservasi">
                        <input type="hidden" name="aksi" value="batal">
                        <input type="hidden" name="id" value="<%= r.getIdReservasi() %>">
                        <button type="submit" class="btn btn-kecil btn-merah"
                                onclick="return confirm('Batalkan reservasi ini?')">Batalkan</button>
                    </form>
                <% } %>
            </td>
        </tr>
        <% } %>
    </table>
    </div>
    <% } %>
</div>
<%@include file="footer.jsp"%>

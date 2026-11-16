<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% String judul = "Semua Reservasi"; %>
<%@include file="header.jsp"%>
<%
    ArrayList<Reservasi> daftar = (ArrayList<Reservasi>) request.getAttribute("daftarReservasi");
    String statusDipilih = request.getParameter("status") == null ? "" : request.getParameter("status");
    String[] semuaStatus = {Reservasi.MENUNGGU, Reservasi.AKTIF, Reservasi.SELESAI, Reservasi.DIBATALKAN};
%>
<h1>Semua Reservasi</h1>
<p class="subjudul">Pantau reservasi, catat pembayaran tunai, dan tandai reservasi yang sudah selesai.</p>

<div class="card">
    <form method="get" action="reservasi" class="form-baris">
        <div>
            <label for="status">Filter Status</label>
            <select id="status" name="status">
                <option value="">Semua status</option>
                <% for (String s : semuaStatus) { %>
                    <option value="<%= s %>" <%= s.equals(statusDipilih) ? "selected" : "" %>><%= s %></option>
                <% } %>
            </select>
        </div>
        <div><button type="submit" class="btn">Tampilkan</button></div>
    </form>
</div>

<div class="card">
    <% if (daftar.isEmpty()) { %>
        <p>Tidak ada reservasi.</p>
    <% } else { %>
    <div class="tabel-scroll">
    <table>
        <tr>
            <th>No</th>
            <th>Member</th>
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
            <td><%= Helper.esc(r.getMember().getNama()) %><br><small><%= Helper.esc(r.getMember().getNoHp()) %></small></td>
            <td><%= Helper.esc(r.getRuang().getNamaRuang()) %></td>
            <td><%= Helper.tanggal(r.getTanggal()) %><br>
                <small><%= Helper.jam(r.getJamMulai()) %> - <%= Helper.jam(r.getJamSelesai()) %></small></td>
            <td><%= Helper.rupiah(r.getTotalHarga()) %></td>
            <td><span class="<%= Helper.badge(r.getStatus()) %>"><%= r.getStatus() %></span></td>
            <td>
                <% if (r.getPembayaran() != null) { %>
                    <%= r.getPembayaran().getMetode() %><br><small><%= r.getPembayaran().getStatus() %></small>
                <% } else { %>
                    -
                <% } %>
            </td>
            <td class="aksi">
                <% if (r.getStatus().equals(Reservasi.MENUNGGU)) { %>
                    <a class="btn btn-kecil" href="bayar?id=<%= r.getIdReservasi() %>">Bayar Tunai</a>
                <% } %>
                <% if (r.getStatus().equals(Reservasi.AKTIF)) { %>
                    <form method="post" action="reservasi">
                        <input type="hidden" name="aksi" value="selesai">
                        <input type="hidden" name="id" value="<%= r.getIdReservasi() %>">
                        <button type="submit" class="btn btn-kecil btn-hijau">Tandai Selesai</button>
                    </form>
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

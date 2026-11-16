<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% String judul = "Dashboard Admin"; %>
<%@include file="header.jsp"%>
<%
    CoworkingSpace cs = (CoworkingSpace) request.getAttribute("cs");
    ArrayList<Reservasi> terbaru = (ArrayList<Reservasi>) request.getAttribute("terbaru");
%>
<h1>Dashboard Admin</h1>
<p class="subjudul"><%= Helper.esc(cs.getNama()) %> &middot; Selamat datang, <%= Helper.esc(userLogin.getNama()) %></p>

<div class="stat">
    <div class="card">
        <div class="angka"><%= request.getAttribute("ruangAktif") %></div>
        <div class="label">Ruang aktif</div>
    </div>
    <div class="card">
        <div class="angka"><%= request.getAttribute("menunggu") %></div>
        <div class="label">Menunggu pembayaran</div>
    </div>
    <div class="card">
        <div class="angka"><%= request.getAttribute("hariIni") %></div>
        <div class="label">Reservasi aktif hari ini</div>
    </div>
    <div class="card">
        <div class="angka"><%= Helper.rupiah((Double) request.getAttribute("pendapatanBulanIni")) %></div>
        <div class="label">Pendapatan bersih bulan ini</div>
    </div>
</div>

<div class="card">
    <h2>Reservasi Terbaru</h2>
    <% if (terbaru.isEmpty()) { %>
        <p>Belum ada reservasi.</p>
    <% } else { %>
    <div class="tabel-scroll">
    <table>
        <tr><th>No</th><th>Member</th><th>Ruang</th><th>Tanggal</th><th>Total</th><th>Status</th></tr>
        <% for (Reservasi r : terbaru) { %>
        <tr>
            <td>#<%= r.getIdReservasi() %></td>
            <td><%= Helper.esc(r.getMember().getNama()) %></td>
            <td><%= Helper.esc(r.getRuang().getNamaRuang()) %></td>
            <td><%= Helper.tanggal(r.getTanggal()) %>, <%= Helper.jam(r.getJamMulai()) %>-<%= Helper.jam(r.getJamSelesai()) %></td>
            <td><%= Helper.rupiah(r.getTotalHarga()) %></td>
            <td><span class="<%= Helper.badge(r.getStatus()) %>"><%= r.getStatus() %></span></td>
        </tr>
        <% } %>
    </table>
    </div>
    <% } %>
    <br>
    <a class="btn" href="reservasi">Lihat semua reservasi</a>
</div>
<%@include file="footer.jsp"%>

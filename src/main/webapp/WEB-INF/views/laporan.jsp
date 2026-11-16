<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% String judul = "Laporan Pendapatan"; %>
<%@include file="header.jsp"%>
<%
    Laporan laporan = (Laporan) request.getAttribute("laporan");
    ArrayList<Reservasi> daftar = (ArrayList<Reservasi>) request.getAttribute("daftarReservasi");
    Date awal = (Date) request.getAttribute("awal");
    Date akhir = (Date) request.getAttribute("akhir");
    String[] semuaJenis = {"Hot Desk", "Ruang Privat", "Ruang Meeting"};
%>
<h1>Laporan Pendapatan</h1>
<p class="subjudul">Pendapatan bersih = pembayaran yang masuk dikurangi dana refund.</p>

<div class="card">
    <form method="get" action="laporan" class="form-baris">
        <div>
            <label for="awal">Tanggal Awal</label>
            <input type="date" id="awal" name="awal" value="<%= Helper.tanggalInput(awal) %>">
        </div>
        <div>
            <label for="akhir">Tanggal Akhir</label>
            <input type="date" id="akhir" name="akhir" value="<%= Helper.tanggalInput(akhir) %>">
        </div>
        <div><button type="submit" class="btn">Tampilkan</button></div>
    </form>
</div>

<% if (laporan != null) {
       Ruang populer = laporan.getRuangTerpopuler();
%>
<div class="stat">
    <div class="card">
        <div class="angka"><%= Helper.rupiah(laporan.hitungTotalPendapatan()) %></div>
        <div class="label">Pendapatan bersih</div>
    </div>
    <div class="card">
        <div class="angka"><%= laporan.hitungJumlahReservasi() %></div>
        <div class="label">Reservasi aktif / selesai</div>
    </div>
    <div class="card">
        <div class="angka"><%= Helper.rupiah(laporan.hitungTotalRefund()) %></div>
        <div class="label">Total refund</div>
    </div>
    <div class="card">
        <div class="angka" style="font-size:18px"><%= populer == null ? "-" : Helper.esc(populer.getNamaRuang()) %></div>
        <div class="label">Ruang terpopuler</div>
    </div>
</div>

<div class="card">
    <h2>Pendapatan per Jenis Ruang</h2>
    <table>
        <tr><th>Jenis Ruang</th><th>Pendapatan Bersih</th></tr>
        <% for (String j : semuaJenis) { %>
        <tr><td><%= j %></td><td><%= Helper.rupiah(laporan.hitungPendapatanPerJenis(j)) %></td></tr>
        <% } %>
    </table>
</div>

<div class="card">
    <h2>Rincian Reservasi Periode <%= Helper.tanggal(awal) %> - <%= Helper.tanggal(akhir) %></h2>
    <div class="tabel-scroll">
    <table>
        <tr>
            <th>No</th><th>Tanggal</th><th>Member</th><th>Ruang</th><th>Status</th><th>Metode</th><th>Dibayar</th>
        </tr>
        <% for (Reservasi r : daftar) { %>
        <tr>
            <td>#<%= r.getIdReservasi() %></td>
            <td><%= Helper.tanggal(r.getTanggal()) %></td>
            <td><%= Helper.esc(r.getMember().getNama()) %></td>
            <td><%= Helper.esc(r.getRuang().getNamaRuang()) %></td>
            <td><span class="<%= Helper.badge(r.getStatus()) %>"><%= r.getStatus() %></span></td>
            <td><%= r.getPembayaran() == null ? "-" : r.getPembayaran().getMetode() + " (" + r.getPembayaran().getStatus() + ")" %></td>
            <td><%= r.getPembayaran() == null ? "-" : Helper.rupiah(r.getPembayaran().getJumlah()) %></td>
        </tr>
        <% } %>
    </table>
    </div>
</div>
<% } %>
<%@include file="footer.jsp"%>

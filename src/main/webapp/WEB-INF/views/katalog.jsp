<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% String judul = "Katalog Ruang"; %>
<%@include file="header.jsp"%>
<%
    CoworkingSpace cs = (CoworkingSpace) request.getAttribute("cs");
    Ruang[] daftarRuang = (Ruang[]) request.getAttribute("daftarRuang");
    String jenisDipilih = request.getParameter("jenis") == null ? "Semua" : request.getParameter("jenis");
    String[] semuaJenis = {"Semua", "Hot Desk", "Ruang Privat", "Ruang Meeting"};
%>
<h1>Katalog Ruang</h1>
<p class="subjudul"><%= Helper.esc(cs.getNama()) %> &middot; <%= Helper.esc(cs.getAlamat()) %>
    &middot; Buka <%= Helper.jam(cs.getJamBuka()) %> - <%= Helper.jam(cs.getJamTutup()) %></p>

<div class="card">
    <form method="get" action="ruang" class="form-baris">
        <div>
            <label for="jenis">Jenis Ruang</label>
            <select id="jenis" name="jenis">
                <% for (String j : semuaJenis) { %>
                    <option value="<%= j %>" <%= j.equals(jenisDipilih) ? "selected" : "" %>><%= j %></option>
                <% } %>
            </select>
        </div>
        <div>
            <label for="kapasitas">Kapasitas Minimum</label>
            <input type="number" id="kapasitas" name="kapasitas" min="1"
                   value="<%= Helper.parseInt(request.getParameter("kapasitas"), 1) %>">
        </div>
        <div>
            <button type="submit" class="btn">Filter</button>
        </div>
    </form>
</div>

<% if (daftarRuang.length == 0) { %>
    <div class="card">Tidak ada ruang yang sesuai filter.</div>
<% } %>

<div class="grid">
    <% for (Ruang r : daftarRuang) { %>
    <div class="card ruang-card">
        <span class="jenis"><%= r.getJenis() %></span>
        <h3><%= Helper.esc(r.getNamaRuang()) %></h3>
        <p>Kapasitas: <%= r.getKapasitas() %> orang</p>
        <p>Fasilitas: <%= Helper.esc(r.getFasilitas()) %></p>
        <% if (r instanceof HotDesk) { %>
            <p>Nomor meja: <%= Helper.esc(((HotDesk) r).getNomorMeja()) %></p>
        <% } else if (r instanceof RuangPrivat) { %>
            <p>Diskon 15% untuk pemesanan minimal 8 jam</p>
        <% } else if (r instanceof RuangMeeting && ((RuangMeeting) r).isAdaProyektor()) { %>
            <p>Termasuk proyektor (+Rp25.000 per reservasi)</p>
        <% } %>
        <div class="harga"><%= Helper.rupiah(r.getTarifPerJam()) %> / jam</div>
        <a class="btn" href="reservasi?aksi=form&idRuang=<%= r.getIdRuang() %>">Pesan Ruang</a>
    </div>
    <% } %>
</div>
<%@include file="footer.jsp"%>

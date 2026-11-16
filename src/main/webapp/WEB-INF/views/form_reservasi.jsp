<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% String judul = "Buat Reservasi"; %>
<%@include file="header.jsp"%>
<%
    CoworkingSpace cs = (CoworkingSpace) request.getAttribute("cs");
    Ruang ruang = (Ruang) request.getAttribute("ruang");
    Date tanggal = (Date) request.getAttribute("tanggal");
    Reservasi[] jadwal = (Reservasi[]) request.getAttribute("jadwal");
    String hariIni = Helper.tanggalInput(new Date());
%>
<h1>Buat Reservasi</h1>
<p class="subjudul"><a href="ruang">&laquo; Kembali ke katalog</a></p>

<div class="grid">
    <div class="card ruang-card">
        <span class="jenis"><%= ruang.getJenis() %></span>
        <h3><%= Helper.esc(ruang.getNamaRuang()) %></h3>
        <p>Kapasitas: <%= ruang.getKapasitas() %> orang</p>
        <p>Fasilitas: <%= Helper.esc(ruang.getFasilitas()) %></p>
        <div class="harga"><%= Helper.rupiah(ruang.getTarifPerJam()) %> / jam</div>
        <% if (ruang instanceof RuangPrivat) { %>
            <p>Diskon 15% jika memesan minimal 8 jam.</p>
        <% } else if (ruang instanceof RuangMeeting && ((RuangMeeting) ruang).isAdaProyektor()) { %>
            <p>Biaya proyektor Rp25.000 per reservasi.</p>
        <% } %>
    </div>

    <div class="card">
        <h2>1. Pilih Tanggal</h2>
        <form method="get" action="reservasi" class="form-baris">
            <input type="hidden" name="aksi" value="form">
            <input type="hidden" name="idRuang" value="<%= ruang.getIdRuang() %>">
            <div>
                <input type="date" name="tanggal" min="<%= hariIni %>" value="<%= Helper.tanggalInput(tanggal) %>">
            </div>
            <div>
                <button type="submit" class="btn btn-abu">Lihat Jadwal</button>
            </div>
        </form>
        <p>Jadwal <%= Helper.tanggal(tanggal) %> (merah = sudah dipesan):</p>
        <div class="jadwal">
            <% for (int jam = cs.getJamBuka(); jam < cs.getJamTutup(); jam++) {
                   boolean terisi = false;
                   for (Reservasi r : jadwal) {
                       if (jam >= r.getJamMulai() && jam < r.getJamSelesai()) {
                           terisi = true;
                       }
                   }
            %>
                <span class="slot <%= terisi ? "slot-terisi" : "" %>"><%= Helper.jam(jam) %></span>
            <% } %>
        </div>
    </div>
</div>

<div class="card">
    <h2>2. Pilih Jam &amp; Kode Promo</h2>
    <form method="post" action="reservasi">
        <input type="hidden" name="aksi" value="buat">
        <input type="hidden" name="idRuang" value="<%= ruang.getIdRuang() %>">
        <input type="hidden" name="tanggal" value="<%= Helper.tanggalInput(tanggal) %>">
        <div class="form-baris">
            <div>
                <label for="jamMulai">Jam Mulai</label>
                <select id="jamMulai" name="jamMulai">
                    <% for (int jam = cs.getJamBuka(); jam < cs.getJamTutup(); jam++) { %>
                        <option value="<%= jam %>"><%= Helper.jam(jam) %></option>
                    <% } %>
                </select>
            </div>
            <div>
                <label for="jamSelesai">Jam Selesai</label>
                <select id="jamSelesai" name="jamSelesai">
                    <% for (int jam = cs.getJamBuka() + 1; jam <= cs.getJamTutup(); jam++) { %>
                        <option value="<%= jam %>"><%= Helper.jam(jam) %></option>
                    <% } %>
                </select>
            </div>
            <div>
                <label for="kodePromo">Kode Promo (opsional)</label>
                <input type="text" id="kodePromo" name="kodePromo" placeholder="Contoh: HEMAT10">
            </div>
        </div>
        <br>
        <button type="submit" class="btn">Pesan Sekarang</button>
    </form>
</div>
<%@include file="footer.jsp"%>

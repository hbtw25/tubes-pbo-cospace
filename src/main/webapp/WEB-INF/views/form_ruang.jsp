<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    models.Ruang ruang = (models.Ruang) request.getAttribute("ruang");
    boolean edit = ruang != null;
    String judul = edit ? "Edit Ruang" : "Tambah Ruang";
%>
<%@include file="header.jsp"%>
<h1><%= judul %></h1>
<p class="subjudul"><a href="ruang">&laquo; Kembali ke daftar ruang</a></p>

<div class="card" style="max-width:600px">
    <form method="post" action="ruang">
        <input type="hidden" name="aksi" value="simpan">
        <input type="hidden" name="id" value="<%= edit ? ruang.getIdRuang() : 0 %>">

        <label for="jenis">Jenis Ruang</label>
        <% if (edit) { %>
            <input type="text" id="jenis" value="<%= ruang.getJenis() %>" disabled>
        <% } else { %>
            <select id="jenis" name="jenis" onchange="aturForm()">
                <option value="Hot Desk">Hot Desk</option>
                <option value="Ruang Privat">Ruang Privat</option>
                <option value="Ruang Meeting">Ruang Meeting</option>
            </select>
        <% } %>

        <label for="namaRuang">Nama Ruang</label>
        <input type="text" id="namaRuang" name="namaRuang" required
               value="<%= edit ? Helper.esc(ruang.getNamaRuang()) : "" %>">

        <div id="bagianKapasitas">
            <label for="kapasitas">Kapasitas (orang)</label>
            <input type="number" id="kapasitas" name="kapasitas" min="1"
                   value="<%= edit ? ruang.getKapasitas() : 1 %>">
        </div>

        <label for="tarif">Tarif per Jam (Rp)</label>
        <input type="number" id="tarif" name="tarif" min="1" required
               value="<%= edit ? String.valueOf((long) ruang.getTarifPerJam()) : "" %>">

        <label for="fasilitas">Fasilitas</label>
        <input type="text" id="fasilitas" name="fasilitas" placeholder="Contoh: Wi-Fi, AC, whiteboard"
               value="<%= edit ? Helper.esc(ruang.getFasilitas()) : "" %>">

        <div id="bagianMeja">
            <label for="nomorMeja">Nomor Meja (khusus Hot Desk)</label>
            <input type="text" id="nomorMeja" name="nomorMeja"
                   value="<%= edit && ruang instanceof HotDesk ? Helper.esc(((HotDesk) ruang).getNomorMeja()) : "" %>">
        </div>

        <div id="bagianProyektor" class="checkbox">
            <br>
            <input type="checkbox" id="adaProyektor" name="adaProyektor"
                   <%= edit && ruang instanceof RuangMeeting && ((RuangMeeting) ruang).isAdaProyektor() ? "checked" : "" %>>
            <label for="adaProyektor">Ada proyektor (khusus Ruang Meeting, +Rp25.000)</label>
        </div>

        <br>
        <button type="submit" class="btn">Simpan</button>
    </form>
</div>

<script>
    // Menampilkan field sesuai jenis ruang yang dipilih
    function aturForm() {
        var pilihan = document.getElementById("jenis");
        var jenis = pilihan.value;
        document.getElementById("bagianMeja").style.display = jenis === "Hot Desk" ? "block" : "none";
        document.getElementById("bagianKapasitas").style.display = jenis === "Hot Desk" ? "none" : "block";
        document.getElementById("bagianProyektor").style.display = jenis === "Ruang Meeting" ? "block" : "none";
    }
    aturForm();
</script>
<%@include file="footer.jsp"%>

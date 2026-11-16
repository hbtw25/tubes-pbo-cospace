<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% String judul = "Kelola Ruang"; %>
<%@include file="header.jsp"%>
<%
    Ruang[] daftarRuang = (Ruang[]) request.getAttribute("daftarRuang");
%>
<h1>Kelola Ruang</h1>
<p class="subjudul">Tambah, ubah, atau nonaktifkan ruang. Ruang tidak dihapus permanen agar riwayat tetap utuh.</p>

<div class="card">
    <a class="btn" href="ruang?aksi=tambah">+ Tambah Ruang</a>
    <br><br>
    <div class="tabel-scroll">
    <table>
        <tr>
            <th>Nama Ruang</th>
            <th>Jenis</th>
            <th>Kapasitas</th>
            <th>Tarif / Jam</th>
            <th>Fasilitas</th>
            <th>Status</th>
            <th>Aksi</th>
        </tr>
        <% for (Ruang r : daftarRuang) { %>
        <tr>
            <td><%= Helper.esc(r.getNamaRuang()) %></td>
            <td><%= r.getJenis() %></td>
            <td><%= r.getKapasitas() %></td>
            <td><%= Helper.rupiah(r.getTarifPerJam()) %></td>
            <td><%= Helper.esc(r.getFasilitas()) %></td>
            <td>
                <% if (r.isAktif()) { %>
                    <span class="badge badge-aktif">Aktif</span>
                <% } else { %>
                    <span class="badge badge-batal">Nonaktif</span>
                <% } %>
            </td>
            <td class="aksi">
                <a class="btn btn-kecil" href="ruang?aksi=edit&id=<%= r.getIdRuang() %>">Edit</a>
                <form method="post" action="ruang">
                    <input type="hidden" name="id" value="<%= r.getIdRuang() %>">
                    <% if (r.isAktif()) { %>
                        <input type="hidden" name="aksi" value="nonaktif">
                        <button type="submit" class="btn btn-kecil btn-merah"
                                onclick="return confirm('Nonaktifkan ruang ini?')">Nonaktifkan</button>
                    <% } else { %>
                        <input type="hidden" name="aksi" value="aktifkan">
                        <button type="submit" class="btn btn-kecil btn-hijau">Aktifkan</button>
                    <% } %>
                </form>
            </td>
        </tr>
        <% } %>
    </table>
    </div>
</div>
<%@include file="footer.jsp"%>

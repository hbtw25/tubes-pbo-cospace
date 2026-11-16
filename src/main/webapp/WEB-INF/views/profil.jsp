<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% String judul = "Profil"; %>
<%@include file="header.jsp"%>
<%
    User profil = (User) request.getAttribute("profil");
%>
<h1>Profil Saya</h1>
<p class="subjudul"><%= Helper.esc(profil.getEmail()) %> &middot; <%= profil.getRole() %></p>

<% if (profil instanceof Member) { Member m = (Member) profil; %>
<div class="stat">
    <div class="card">
        <div class="angka"><%= m.getPoinMember() %></div>
        <div class="label">Poin member (1 poin setiap Rp10.000 reservasi selesai)</div>
    </div>
    <div class="card">
        <div class="angka"><%= Helper.tanggal(m.getTanggalDaftar()) %></div>
        <div class="label">Tanggal daftar</div>
    </div>
</div>
<% } else { %>
<div class="card">Jabatan: <b><%= Helper.esc(((Admin) profil).getJabatan()) %></b></div>
<% } %>

<div class="grid">
    <div class="card">
        <h2>Ubah Profil</h2>
        <form method="post" action="profil">
            <input type="hidden" name="aksi" value="profil">
            <label for="nama">Nama</label>
            <input type="text" id="nama" name="nama" required value="<%= Helper.esc(profil.getNama()) %>">
            <label for="noHp">Nomor HP</label>
            <input type="text" id="noHp" name="noHp" required value="<%= Helper.esc(profil.getNoHp()) %>">
            <br><br>
            <button type="submit" class="btn">Simpan Profil</button>
        </form>
    </div>
    <div class="card">
        <h2>Ubah Password</h2>
        <form method="post" action="profil">
            <input type="hidden" name="aksi" value="password">
            <label for="passwordLama">Password Lama</label>
            <input type="password" id="passwordLama" name="passwordLama" required>
            <label for="passwordBaru">Password Baru</label>
            <input type="password" id="passwordBaru" name="passwordBaru" required placeholder="Minimal 8 karakter">
            <label for="konfirmasi">Ulangi Password Baru</label>
            <input type="password" id="konfirmasi" name="konfirmasi" required>
            <br><br>
            <button type="submit" class="btn">Ubah Password</button>
        </form>
    </div>
</div>
<%@include file="footer.jsp"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% String judul = "Daftar Member"; %>
<%@include file="header.jsp"%>
<div class="auth-box">
    <div class="card">
        <div class="logo-besar">Daftar Member</div>
        <div class="tagline">Buat akun untuk mulai memesan ruang</div>
        <form method="post" action="auth">
            <input type="hidden" name="aksi" value="register">
            <label for="nama">Nama Lengkap</label>
            <input type="text" id="nama" name="nama" required
                   value="<%= Helper.esc(request.getParameter("nama")) %>">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" required
                   value="<%= Helper.esc(request.getParameter("email")) %>">
            <label for="noHp">Nomor HP</label>
            <input type="text" id="noHp" name="noHp" required placeholder="10-13 digit angka"
                   value="<%= Helper.esc(request.getParameter("noHp")) %>">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required placeholder="Minimal 8 karakter">
            <button type="submit" class="btn">Daftar</button>
        </form>
        <p style="text-align:center">Sudah punya akun? <a href="auth">Login</a></p>
    </div>
</div>
<%@include file="footer.jsp"%>

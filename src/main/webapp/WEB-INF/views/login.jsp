<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% String judul = "Login"; %>
<%@include file="header.jsp"%>
<div class="auth-box">
    <div class="card">
        <div class="logo-besar">CoSpace</div>
        <div class="tagline">Reservasi meja &amp; ruang kerja co-working space</div>
        <form method="post" action="auth">
            <input type="hidden" name="aksi" value="login">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" required
                   value="<%= Helper.esc(request.getAttribute("email")) %>">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>
            <button type="submit" class="btn">Login</button>
        </form>
        <p style="text-align:center">Belum punya akun? <a href="auth?aksi=register">Daftar di sini</a></p>
    </div>
</div>
<%@include file="footer.jsp"%>

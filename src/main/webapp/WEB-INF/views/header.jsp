<%-- Bagian atas yang dipakai semua halaman setelah login (di-include) --%>
<%@page import="models.*, utils.Helper, java.util.*"%>
<%
    User userLogin = (User) session.getAttribute("user");
    // Pesan dari halaman sebelumnya (setelah redirect), dihapus setelah ditampilkan
    String pesan = (String) session.getAttribute("pesan");
    String errorSesi = (String) session.getAttribute("error");
    session.removeAttribute("pesan");
    session.removeAttribute("error");
    String errorRequest = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= Helper.esc(judul) %> - CoSpace</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<div class="navbar">
    <a class="logo" href="auth">CoSpace</a>
    <div class="menu">
        <% if (userLogin != null && Helper.isAdmin(userLogin)) { %>
            <a href="admin">Dashboard</a>
            <a href="ruang">Kelola Ruang</a>
            <a href="reservasi">Reservasi</a>
            <a href="promo">Promo</a>
            <a href="laporan">Laporan</a>
        <% } else if (userLogin != null) { %>
            <a href="ruang">Katalog Ruang</a>
            <a href="reservasi">Riwayat Reservasi</a>
        <% } %>
        <% if (userLogin != null) { %>
            <a href="profil">Profil (<%= Helper.esc(userLogin.getNama()) %>)</a>
            <form method="post" action="auth">
                <input type="hidden" name="aksi" value="logout">
                <button type="submit">Logout</button>
            </form>
        <% } %>
    </div>
</div>
<div class="container">
    <% if (pesan != null) { %>
        <div class="alert alert-sukses"><%= Helper.esc(pesan) %></div>
    <% } %>
    <% if (errorSesi != null) { %>
        <div class="alert alert-error"><%= Helper.esc(errorSesi) %></div>
    <% } %>
    <% if (errorRequest != null) { %>
        <div class="alert alert-error"><%= Helper.esc(errorRequest) %></div>
    <% } %>

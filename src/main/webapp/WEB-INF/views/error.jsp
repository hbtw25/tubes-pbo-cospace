<%@page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true"%>
<% String judul = "Terjadi Kesalahan"; %>
<%@include file="header.jsp"%>
<%
    Object kode = request.getAttribute("javax.servlet.error.status_code");
%>
<div class="card">
    <h1>Halaman tidak dapat ditampilkan</h1>
    <% if (kode != null && kode.toString().equals("404")) { %>
        <p>Halaman yang Anda cari tidak ditemukan.</p>
    <% } else if (kode != null && kode.toString().equals("403")) { %>
        <p>Anda tidak memiliki akses ke halaman ini.</p>
    <% } else if (errorRequest == null) { %>
        <p>Terjadi kesalahan pada sistem. Silakan coba lagi.</p>
    <% } %>
    <a class="btn" href="<%= request.getContextPath() %>/auth">Kembali ke beranda</a>
</div>
<%@include file="footer.jsp"%>

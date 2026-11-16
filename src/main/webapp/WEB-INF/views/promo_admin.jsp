<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% String judul = "Kode Promo"; %>
<%@include file="header.jsp"%>
<%
    ArrayList<Promo> daftar = (ArrayList<Promo>) request.getAttribute("daftarPromo");
    Date sekarang = new Date();
%>
<h1>Kode Promo</h1>
<p class="subjudul">Member dapat memakai satu kode promo per reservasi.</p>

<div class="card">
    <h2>Tambah Promo</h2>
    <form method="post" action="promo" class="form-baris">
        <div>
            <label for="kodePromo">Kode Promo</label>
            <input type="text" id="kodePromo" name="kodePromo" required placeholder="Contoh: HEMAT10">
        </div>
        <div>
            <label for="persenDiskon">Diskon (%)</label>
            <input type="number" id="persenDiskon" name="persenDiskon" min="1" max="100" required>
        </div>
        <div>
            <label for="berlakuSampai">Berlaku Sampai</label>
            <input type="date" id="berlakuSampai" name="berlakuSampai" required>
        </div>
        <div>
            <label for="kuota">Kuota</label>
            <input type="number" id="kuota" name="kuota" min="1" required>
        </div>
        <div>
            <button type="submit" class="btn">Tambah</button>
        </div>
    </form>
</div>

<div class="card">
    <table>
        <tr>
            <th>Kode</th>
            <th>Diskon</th>
            <th>Berlaku Sampai</th>
            <th>Sisa Kuota</th>
            <th>Status</th>
        </tr>
        <% for (Promo p : daftar) { %>
        <tr>
            <td><b><%= Helper.esc(p.getKodePromo()) %></b></td>
            <td><%= (int) p.getPersenDiskon() %>%</td>
            <td><%= Helper.tanggal(p.getBerlakuSampai()) %></td>
            <td><%= p.getKuota() %></td>
            <td>
                <% if (p.isBerlaku(sekarang)) { %>
                    <span class="badge badge-aktif">Berlaku</span>
                <% } else { %>
                    <span class="badge badge-batal">Tidak berlaku</span>
                <% } %>
            </td>
        </tr>
        <% } %>
    </table>
</div>
<%@include file="footer.jsp"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% String judul = "Pembayaran"; %>
<%@include file="header.jsp"%>
<%
    Reservasi r = (Reservasi) request.getAttribute("reservasi");
    boolean admin = Helper.isAdmin(userLogin);
%>
<h1>Pembayaran Reservasi #<%= r.getIdReservasi() %></h1>
<p class="subjudul"><a href="reservasi">&laquo; Kembali</a></p>

<div class="card">
    <h2>Detail Reservasi</h2>
    <table>
        <tr><td>Member</td><td><%= Helper.esc(r.getMember().getNama()) %></td></tr>
        <tr><td>Ruang</td><td><%= Helper.esc(r.getRuang().getNamaRuang()) %> (<%= r.getRuang().getJenis() %>)</td></tr>
        <tr><td>Tanggal</td><td><%= Helper.tanggal(r.getTanggal()) %>, <%= Helper.jam(r.getJamMulai()) %> - <%= Helper.jam(r.getJamSelesai()) %> (<%= r.getDurasiJam() %> jam)</td></tr>
        <tr><td>Promo</td><td><%= r.getPromo() == null ? "-" : Helper.esc(r.getPromo().getKodePromo()) + " (" + (int) r.getPromo().getPersenDiskon() + "%)" %></td></tr>
        <tr><td>Status</td><td><span class="<%= Helper.badge(r.getStatus()) %>"><%= r.getStatus() %></span></td></tr>
        <tr><td><b>Total Bayar</b></td><td><b><%= Helper.rupiah(r.getTotalHarga()) %></b></td></tr>
    </table>
</div>

<% if (!r.getStatus().equals(Reservasi.MENUNGGU)) { %>
    <div class="card">
        Reservasi ini tidak sedang menunggu pembayaran.
        <% if (r.getPembayaran() != null) { %>
            Sudah dibayar melalui <b><%= r.getPembayaran().getMetode() %></b>
            pada <%= Helper.tanggalJam(r.getPembayaran().getWaktuBayar()) %>.
        <% } %>
    </div>
<% } else if (admin) { %>
    <div class="card" style="max-width:500px">
        <h2>Pembayaran Tunai (Resepsionis)</h2>
        <form method="post" action="bayar">
            <input type="hidden" name="id" value="<%= r.getIdReservasi() %>">
            <input type="hidden" name="metode" value="tunai">
            <label for="uangDiterima">Uang Diterima (Rp)</label>
            <input type="number" id="uangDiterima" name="uangDiterima" min="0" required>
            <br><br>
            <button type="submit" class="btn">Catat Pembayaran</button>
        </form>
    </div>
<% } else { %>
    <div class="grid">
        <div class="card">
            <h2>Transfer Bank</h2>
            <p>Transfer ke rekening BCA 1234567890 a.n. CoSpace, lalu isi data di bawah.</p>
            <form method="post" action="bayar">
                <input type="hidden" name="id" value="<%= r.getIdReservasi() %>">
                <input type="hidden" name="metode" value="transfer">
                <label for="namaBank">Bank Pengirim</label>
                <select id="namaBank" name="namaBank">
                    <option>BCA</option>
                    <option>BNI</option>
                    <option>BRI</option>
                    <option>Mandiri</option>
                    <option>BSI</option>
                </select>
                <label for="noRekening">Nomor Rekening Pengirim</label>
                <input type="text" id="noRekening" name="noRekening" required placeholder="6-20 digit angka">
                <label for="buktiTransfer">Nomor Referensi / Bukti Transfer</label>
                <input type="text" id="buktiTransfer" name="buktiTransfer" required>
                <br><br>
                <button type="submit" class="btn">Bayar dengan Transfer</button>
            </form>
        </div>
        <div class="card">
            <h2>E-Wallet</h2>
            <p>Bayar menggunakan e-wallet yang terhubung dengan nomor HP Anda.</p>
            <form method="post" action="bayar">
                <input type="hidden" name="id" value="<%= r.getIdReservasi() %>">
                <input type="hidden" name="metode" value="ewallet">
                <label for="jenisEWallet">Jenis E-Wallet</label>
                <select id="jenisEWallet" name="jenisEWallet">
                    <option>GoPay</option>
                    <option>OVO</option>
                    <option>DANA</option>
                    <option>ShopeePay</option>
                </select>
                <label for="noHp">Nomor HP E-Wallet</label>
                <input type="text" id="noHp" name="noHp" required value="<%= Helper.esc(r.getMember().getNoHp()) %>">
                <br><br>
                <button type="submit" class="btn">Bayar dengan E-Wallet</button>
            </form>
        </div>
        <div class="card">
            <h2>Tunai</h2>
            <p>Bayar langsung di resepsionis CoSpace. Petugas akan mencatat pembayaran Anda.</p>
            <p><small>Catatan: pembayaran tunai tidak bisa di-refund melalui aplikasi.</small></p>
        </div>
    </div>
<% } %>
<%@include file="footer.jsp"%>

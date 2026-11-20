<%@ page pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/common.jspf" %>
<%
ImportInvoice9 inv;
try(var dao=new ImportInvoiceDAO()) {inv=dao.findById(Web.p(request,"id"));}
if(inv==null) {response.sendError(404);return;}
%>
<%@ include file="WEB-INF/header.jspf" %>
<main><section class="card"><h1>Hóa đơn nhập hàng</h1>
<% if(inv.importInvoiceId.equals(session.getAttribute("importSuccess"))) {session.removeAttribute("importSuccess"); %><p class="success" role="status">Nhập hàng thành công!</p><% } %>
<%@ include file="WEB-INF/invoiceMeta.jspf" %>
<div class="meta"><p><span>Số điện thoại</span><%=Web.h(inv.supplier.phone)%></p><p><span>Địa chỉ</span><%=Web.h(inv.supplier.address)%></p></div>
<div class="table-wrap"><table><thead><tr><th>STT</th><th>Mã MH</th><th>Tên mặt hàng</th><th>ĐVT</th><th class="number">Số lượng</th><th class="number">Đơn giá nhập</th><th class="number">Thành tiền</th></tr></thead><tbody>
<% int stt=0;for(var d:inv.details) { %><tr><td><%=++stt%></td><td><%=Web.h(d.product.productId)%></td><td><%=Web.h(d.product.productName)%></td><td><%=Web.h(d.product.unit)%></td><td class="number"><%=d.quantity%></td><td class="number"><%=Web.money(d.unitPrice)%></td><td class="number"><%=Web.money(d.subTotal())%></td></tr><% } %>
</tbody></table></div><p class="total">Tổng tiền hóa đơn nhập: <strong><%=Web.money(inv.totalAmount)%></strong></p>
<div class="actions"><button onclick="window.print()">In hóa đơn</button><a class="button secondary" href="gdWarehouseHome9.jsp">Về trang chủ</a></div>
</section></main></body></html>

<%@ page pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/common.jspf" %>
<%
String error=null;ImportInvoice9 inv=Web.draft(session);
if(Web.post(request)) {
    String action=Web.p(request,"action");
    if("start".equals(action)) {
        try(var suppliers=new SupplierDAO();var invoices=new ImportInvoiceDAO()) {
            var supplier=suppliers.findById(Web.p(request,"supplierId"));
            if(supplier==null||!"Đang hợp tác".equals(supplier.status)) {response.sendRedirect("gdSearchSupplier9.jsp");return;}
            inv=new ImportInvoice9();inv.importInvoiceId=invoices.generateImportInvoiceId();inv.importDate=Web.today();
            inv.supplier=supplier;inv.warehouseStaff=(WarehouseStaff9)session.getAttribute("member");session.setAttribute("invoice",inv);
            response.sendRedirect("gdImportInvoice9.jsp");return;
        }
    }
    if(inv!=null && inv.importInvoiceId.equals(Web.p(request,"invoiceId"))) {
        if("cancel".equals(action)) {session.removeAttribute("invoice");response.sendRedirect("gdWarehouseHome9.jsp");return;}
        if("remove".equals(action)) {
            String productId=Web.p(request,"productId");inv.details.removeIf(d->d.product.productId.equals(productId));inv.calculateTotal();
            response.sendRedirect("gdImportInvoice9.jsp");return;
        }
        if("submit".equals(action)) {
            if(inv.details.isEmpty())error="Hóa đơn chưa có mặt hàng, vui lòng thêm mặt hàng trước khi lưu";
            else try(var dao=new ImportInvoiceDAO()) {
                dao.saveInvoice(inv);session.removeAttribute("invoice");session.setAttribute("importSuccess",inv.importInvoiceId);
                response.sendRedirect("gdPrintInvoice9.jsp?id="+inv.importInvoiceId);return;
            } catch(SQLException e) {application.log("Lưu hóa đơn không thành công",e);error="Lỗi hệ thống trong quá trình lưu hóa đơn. Vui lòng thử lại";}
        }
    }
}
if(inv==null) {response.sendRedirect("gdSearchSupplier9.jsp");return;}
%>
<%@ include file="WEB-INF/header.jspf" %>
<main><section class="card"><h1>Lập hóa đơn nhập hàng</h1>
<% if(error!=null) { %><p class="error" role="alert"><%=Web.h(error)%></p><% } %>
<%@ include file="WEB-INF/invoiceMeta.jspf" %>
<a class="button" href="gdSearchProduct9.jsp">Thêm mặt hàng</a>
<div class="table-wrap"><table><thead><tr><th>STT</th><th>Mã MH</th><th>Tên mặt hàng</th><th>ĐVT</th><th class="number">Số lượng</th><th class="number">Đơn giá nhập</th><th class="number">Thành tiền</th><th></th></tr></thead><tbody>
<% int stt=0;for(var d:inv.details) { %><tr><td><%=++stt%></td><td><%=Web.h(d.product.productId)%></td><td><%=Web.h(d.product.productName)%></td><td><%=Web.h(d.product.unit)%></td><td class="number"><%=d.quantity%></td><td class="number"><%=Web.money(d.unitPrice)%></td><td class="number"><%=Web.money(d.subTotal())%></td><td><form method="post"><%=Web.csrf(session)%><input type="hidden" name="invoiceId" value="<%=Web.h(inv.importInvoiceId)%>"><input type="hidden" name="productId" value="<%=Web.h(d.product.productId)%>"><button class="secondary" name="action" value="remove">Xóa</button></form></td></tr><% } %>
</tbody></table></div>
<p class="total">Tổng tiền hóa đơn nhập: <strong><%=Web.money(inv.totalAmount)%></strong></p>
<div class="actions"><form method="post"><%=Web.csrf(session)%><input type="hidden" name="invoiceId" value="<%=Web.h(inv.importInvoiceId)%>"><button name="action" value="submit">Submit</button></form><button type="button" class="secondary" onclick="document.getElementById('cancel').showModal()">Hủy bỏ</button></div>
<dialog id="cancel"><p>Bạn có chắc chắn muốn hủy bỏ hóa đơn nhập này không?</p><div class="actions"><form method="post"><%=Web.csrf(session)%><input type="hidden" name="invoiceId" value="<%=Web.h(inv.importInvoiceId)%>"><button name="action" value="cancel">Có</button></form><form method="dialog"><button class="secondary">Không</button></form></div></dialog>
</section></main></body></html>

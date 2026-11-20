<%@ page pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/common.jspf" %>
<%
var inv=Web.draft(session);if(inv==null) {response.sendRedirect("gdSearchSupplier9.jsp");return;}
String name=Web.p(request,"name"),error=null;List<Product9> products=new ArrayList<>();Product9 selected=null;
try(var dao=new ProductDAO()) {
    if(!Web.p(request,"productId").isEmpty()) {selected=dao.findById(Web.p(request,"productId"));if(selected!=null&&!"Đang kinh doanh".equals(selected.status))selected=null;}
    if(Web.post(request)&&inv.importInvoiceId.equals(Web.p(request,"invoiceId"))&&selected!=null) {
        try {
            int qty=Web.quantity(Web.p(request,"quantity"));var price=Web.positiveInteger(Web.p(request,"unitPrice"));
            Web.appendProductItem(inv,selected,qty,price);response.sendRedirect("gdImportInvoice9.jsp");return;
        } catch(IllegalArgumentException|ArithmeticException e) {error="Số lượng nhập và đơn giá phải là số nguyên dương lớn hơn 0";}
    }
    if(request.getParameter("name")!=null) {
        if(name.isEmpty())error="Vui lòng nhập từ khóa";else products=dao.findProductByName(name);
    }
}
%>
<%@ include file="WEB-INF/header.jspf" %>
<main><section class="card"><h1>Tìm kiếm mặt hàng</h1>
<form method="get" class="search" novalidate><div class="field"><label for="name">Tên mặt hàng *</label><input required id="name" name="name" value="<%=Web.h(name)%>"></div><button>Tìm kiếm</button><a class="button secondary" href="gdAddProduct9.jsp">Thêm mới mặt hàng</a></form>
<% if(error!=null) { %><p class="error" role="alert"><%=Web.h(error)%></p><% } %>
<div class="table-wrap"><table><thead><tr><th>STT</th><th>Mã MH</th><th>Tên</th><th class="number">Tồn kho</th><th class="number">Đơn giá bán</th><th></th></tr></thead><tbody>
<% int stt=0;for(var p:products) { %><tr><td><%=++stt%></td><td><%=Web.h(p.productId)%></td><td><%=Web.h(p.productName)%></td><td class="number"><%=p.stockQuantity%> <%=Web.h(p.unit)%></td><td class="number"><%=Web.money(p.listPrice)%></td><td><a class="button" href="gdSearchProduct9.jsp?productId=<%=Web.h(p.productId)%>">Chọn</a></td></tr><% } %>
</tbody></table></div><a href="gdImportInvoice9.jsp">Quay lại</a>
</section>
<% if(selected!=null) { %>
<section class="card medium"><h2>Nhập chi tiết hàng nhập</h2><form method="post" novalidate><%=Web.csrf(session)%><input type="hidden" name="invoiceId" value="<%=Web.h(inv.importInvoiceId)%>"><input type="hidden" name="productId" value="<%=Web.h(selected.productId)%>">
<div class="field"><label>Mã MH</label><input readonly value="<%=Web.h(selected.productId)%>"></div>
<div class="field"><label>Tên mặt hàng</label><input readonly value="<%=Web.h(selected.productName)%>"></div>
<div class="field"><label for="quantity">Số lượng nhập *</label><input required id="quantity" name="quantity" inputmode="numeric" value="<%=Web.h(Web.p(request,"quantity"))%>"></div>
<div class="field"><label for="unitPrice">Đơn giá nhập *</label><input required id="unitPrice" name="unitPrice" inputmode="numeric" value="<%=Web.h(Web.p(request,"unitPrice"))%>"></div>
<div class="actions"><button>Xác nhận thêm</button><a class="button secondary" href="gdImportInvoice9.jsp">Hủy</a></div></form></section>
<% } %></main></body></html>

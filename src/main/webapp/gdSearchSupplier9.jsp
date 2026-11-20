<%@ page pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/common.jspf" %>
<%
String name=Web.p(request,"name"),error=null;List<Supplier9> suppliers=new ArrayList<>();
if(request.getParameter("name")!=null) {
    if(name.isEmpty())error="Vui lòng nhập từ khóa";
    else try(var dao=new SupplierDAO()) {suppliers=dao.findSupplierByName(name);if(suppliers.isEmpty())error="Không tìm thấy nhà cung cấp";}
}
%>
<%@ include file="WEB-INF/header.jspf" %>
<main><section class="card"><h1>Tìm kiếm nhà cung cấp</h1>
<form class="search" method="get" novalidate><div class="field"><label for="name">Tên NCC *</label><input required id="name" name="name" value="<%=Web.h(name)%>"></div><button>Tìm kiếm</button><a class="button secondary" href="gdAddSupplier9.jsp">Thêm mới nhà cung cấp</a></form>
<% if(error!=null) { %><p class="error" role="alert"><%=Web.h(error)%></p><% } %>
<div class="table-wrap"><table><thead><tr><th>STT</th><th>Mã NCC</th><th>Tên NCC</th><th>Số điện thoại</th><th>Địa chỉ</th><th></th></tr></thead><tbody>
<% int stt=0;for(var s:suppliers) { %><tr><td><%=++stt%></td><td><%=Web.h(s.supplierId)%></td><td><%=Web.h(s.name)%></td><td><%=Web.h(s.phone)%></td><td><%=Web.h(s.address)%></td><td><form method="post" action="gdImportInvoice9.jsp"><%=Web.csrf(session)%><input type="hidden" name="supplierId" value="<%=Web.h(s.supplierId)%>"><button name="action" value="start">Nhập</button></form></td></tr><% } %>
</tbody></table></div></section></main></body></html>

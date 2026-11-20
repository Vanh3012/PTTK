<%@ page pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/common.jspf" %>
<% if(session.getAttribute("member") instanceof WarehouseStaff9) { response.sendRedirect("gdWarehouseHome9.jsp"); return; } %>
<%@ include file="WEB-INF/header.jspf" %>
<main><div class="catalog">
<% try(var dao=new ProductDAO()) { for(var p:dao.findProductByName("")) { %>
<article class="product"><h2><%=Web.h(p.productName)%></h2><strong><%=Web.money(p.listPrice)%></strong></article>
<% } } %>
</div></main></body></html>

<%@ page pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/common.jspf" %>
<%@ include file="WEB-INF/header.jspf" %>
<main><section class="card medium"><h1>Trang chủ nhân viên kho</h1>
<% var staff=(WarehouseStaff9)session.getAttribute("member"); %>
<p>Xin chào: <%=Web.h(staff.fullName)%> (<%=Web.h(staff.staffId)%>)</p>
<a class="button space" href="gdSearchSupplier9.jsp">Nhập hàng</a></section></main></body></html>

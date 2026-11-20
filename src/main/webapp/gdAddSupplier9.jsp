<%@ page pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/common.jspf" %>
<%@ include file="WEB-INF/header.jspf" %>
<main><section class="card medium"><h1>Thêm mới nhà cung cấp</h1>
<% if(request.getAttribute("error")!=null) { %><p class="error" role="alert"><%=Web.h(request.getAttribute("error"))%></p><% } %>
<form action="doSaveSupplier9.jsp" method="post" novalidate><%=Web.csrf(session)%>
<div class="field"><label for="name">Tên NCC *</label><input required id="name" name="name" maxlength="255" value="<%=Web.h(Web.p(request,"name"))%>"></div>
<div class="field"><label for="phone">Số điện thoại *</label><input required id="phone" name="phone" type="tel" maxlength="20" value="<%=Web.h(Web.p(request,"phone"))%>"></div>
<div class="field"><label for="address">Địa chỉ *</label><input required id="address" name="address" maxlength="255" value="<%=Web.h(Web.p(request,"address"))%>"></div>
<div class="actions"><button>Lưu</button><a class="button secondary" href="gdSearchSupplier9.jsp">Quay lại</a></div></form></section></main></body></html>

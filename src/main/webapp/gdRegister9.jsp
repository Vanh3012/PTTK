<%@ page pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/common.jspf" %>
<%@ include file="WEB-INF/header.jspf" %>
<main><section class="card auth"><h1>Đăng ký thành viên</h1>
<% if(request.getAttribute("error")!=null) { %><p class="error" role="alert"><%=Web.h(request.getAttribute("error"))%></p><% } %>
<form action="doRegister9.jsp" method="post" novalidate>
<%=Web.csrf(session)%>
<div class="field"><label for="fullName">Họ và tên *</label><input required id="fullName" name="fullName" autocomplete="name" maxlength="100" value="<%=Web.h(Web.p(request,"fullName"))%>"></div>
<div class="field"><label for="email">Email *</label><input required id="email" name="email" type="email" placeholder="ten@example.com" autocomplete="email" maxlength="120" value="<%=Web.h(Web.p(request,"email"))%>"></div>
<div class="field"><label for="phone">Số điện thoại *</label><input required id="phone" name="phone" type="tel" placeholder="0901234567" autocomplete="tel" value="<%=Web.h(Web.p(request,"phone"))%>"></div>
<div class="field"><label for="password">Mật khẩu *</label><input required id="password" name="password" type="password" autocomplete="new-password" maxlength="255" value="<%=Web.h(Web.password(request,"password"))%>"></div>
<div class="field"><label for="confirmPassword">Nhập lại mật khẩu *</label><input required id="confirmPassword" name="confirmPassword" type="password" autocomplete="new-password" maxlength="255" value="<%=Web.h(Web.password(request,"confirmPassword"))%>"></div>
<button class="full">Đăng ký</button></form></section></main></body></html>

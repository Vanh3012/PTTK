<%@ page pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/common.jspf" %>
<%
String error=null;
if(Web.post(request)) {
    if("logout".equals(Web.p(request,"action"))) {session.invalidate();response.sendRedirect("gdHome9.jsp");return;}
    try(var dao=new MemberDAO()) {
        var member=dao.login(Web.p(request,"phone"),Web.password(request,"password"));
        if(member==null)error="Đăng nhập không thành công";
        else {request.changeSessionId();session.removeAttribute("invoice");session.setAttribute("member",member);response.sendRedirect(member instanceof WarehouseStaff9?"gdWarehouseHome9.jsp":"gdHome9.jsp");return;}
    } catch(SQLException e) {application.log("Đăng nhập không thành công",e);error="Đăng nhập không thành công";}
}
%>
<%@ include file="WEB-INF/header.jspf" %>
<main><section class="card auth"><h1>Đăng nhập</h1>
<% if(session.getAttribute("registeredId")!=null) { %>
<dialog id="registered"><p>Đăng ký thành viên thành công!</p><p><%=Web.h(session.getAttribute("registeredId"))%></p><form method="dialog"><button>Đăng nhập</button></form></dialog><script>document.getElementById('registered').showModal();</script>
<% session.removeAttribute("registeredId"); } %>
<% if(error!=null) { %><p class="error" role="alert"><%=Web.h(error)%></p><% } %>
<form method="post"><%=Web.csrf(session)%>
<div class="field"><label for="phone">Số điện thoại</label><input id="phone" name="phone" type="tel" autocomplete="username" value="<%=Web.h(Web.p(request,"phone"))%>"></div>
<div class="field"><label for="password">Mật khẩu</label><input id="password" name="password" type="password" autocomplete="current-password"></div>
<button class="full">Đăng nhập</button></form></section></main></body></html>

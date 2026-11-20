<%@ page pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/common.jspf" %>
<%
String error=Web.registrationError(request);
if(error==null) {
    try(var memberDAO=new MemberDAO();var customerDAO=new CustomerDAO()) {
        if(memberDAO.checkDuplicate(Web.p(request,"email"),Web.p(request,"phone")))error="Thông tin người dùng đã tồn tại";
        else {
            var customer=new Customer9();customer.fullName=Web.p(request,"fullName");customer.email=Web.p(request,"email");
            customer.phone=Web.p(request,"phone");customer.password=Web.password(request,"password");
            customer.customerId=customerDAO.generateCustomerId();customer.registrationDate=Web.today();
            customerDAO.addCustomer(customer);session.setAttribute("registeredId",customer.customerId);
            response.sendRedirect("gdLogin9.jsp");return;
        }
    } catch(SQLException e) {
        if("23505".equals(e.getSQLState()))error="Thông tin người dùng đã tồn tại";
        else {application.log("Đăng ký không thành công",e);error="Lỗi hệ thống. Vui lòng thử lại";}
    }
}
request.setAttribute("error",error);request.getRequestDispatcher("gdRegister9.jsp").forward(request,response);
%>

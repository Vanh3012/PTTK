<%@ page pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/common.jspf" %>
<%
String error=null;
String name=Web.p(request,"name"),phone=Web.p(request,"phone"),address=Web.p(request,"address");
if(name.isEmpty()||name.length()>255||address.isEmpty()||address.length()>255||!phone.matches("[0-9]{10,11}"))error="Vui lòng điền đầy đủ thông tin";
if(error==null) {
    try(var dao=new SupplierDAO();var invoices=new ImportInvoiceDAO()) {
        var supplier=new Supplier9();supplier.name=name;supplier.phone=phone;supplier.address=address;dao.saveSupplier(supplier);
        var inv=new ImportInvoice9();inv.importInvoiceId=invoices.generateImportInvoiceId();inv.importDate=Web.today();
        inv.supplier=supplier;inv.warehouseStaff=(WarehouseStaff9)session.getAttribute("member");session.setAttribute("invoice",inv);
        response.sendRedirect("gdImportInvoice9.jsp");return;
    } catch(SQLException e) {application.log("Lưu nhà cung cấp không thành công",e);error="Lỗi hệ thống. Vui lòng thử lại";}
}
request.setAttribute("error",error);request.getRequestDispatcher("gdAddSupplier9.jsp").forward(request,response);
%>

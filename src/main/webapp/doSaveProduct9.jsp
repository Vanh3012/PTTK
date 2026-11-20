<%@ page pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/common.jspf" %>
<%
var inv=Web.draft(session);
if(inv==null||!inv.importInvoiceId.equals(Web.p(request,"invoiceId"))) {response.sendRedirect("gdImportInvoice9.jsp");return;}
String error=null;
try {
    var p=new Product9();p.productName=Web.p(request,"productName");p.brand=Web.p(request,"brand");p.model=Web.p(request,"model");
    p.origin=Web.p(request,"origin");p.unit=Web.p(request,"unit");p.description=Web.p(request,"description");
    if(p.productName.isEmpty()||p.productName.length()>255||p.brand.isEmpty()||p.brand.length()>100||p.model.isEmpty()||p.model.length()>100||p.origin.isEmpty()||p.origin.length()>100||p.unit.isEmpty()||p.unit.length()>30||p.description.length()>10000)throw new IllegalArgumentException();
    p.listPrice=Web.positiveInteger(Web.p(request,"listPrice"));
    if(!Web.p(request,"warrantyPeriod").matches("[0-9]{1,9}"))throw new IllegalArgumentException();
    p.warrantyPeriod=Integer.parseInt(Web.p(request,"warrantyPeriod"));p.warrantyRequired="true".equals(Web.p(request,"warrantyRequired"));
    p.category=new Category9();p.category.categoryId=Web.p(request,"categoryId");
    String image=Web.p(request,"imageData");
    if(!image.isEmpty()) {
        if(image.length()>4200000||!image.matches("data:image/(png|jpeg|gif);base64,[A-Za-z0-9+/=]+"))throw new IllegalArgumentException();
        byte[] bytes=Base64.getDecoder().decode(image.substring(image.indexOf(',')+1));
        try(var input=new java.io.ByteArrayInputStream(bytes)) {
            if(javax.imageio.ImageIO.read(input)==null)throw new IllegalArgumentException();
        }
        var pi=new ProductImage9();pi.imageUrl=image;p.images.add(pi);
    }
    try(var dao=new ProductDAO()) {
        if(dao.getCategories().stream().noneMatch(c->c.categoryId.equals(p.category.categoryId)))throw new IllegalArgumentException();
        dao.saveProduct(p);response.sendRedirect("gdSearchProduct9.jsp?productId="+p.productId);return;
    }
} catch(IllegalArgumentException|java.io.IOException e) {error="Vui lòng điền đầy đủ thông tin";}
catch(SQLException e) {application.log("Lưu mặt hàng không thành công",e);error="Lỗi hệ thống. Vui lòng thử lại";}
request.setAttribute("error",error);request.getRequestDispatcher("gdAddProduct9.jsp").forward(request,response);
%>

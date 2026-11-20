<%@ page pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/common.jspf" %>
<% if(Web.draft(session)==null) {response.sendRedirect("gdSearchSupplier9.jsp");return;} %>
<%@ include file="WEB-INF/header.jspf" %>
<main><section class="card medium"><h1>Thêm mới mặt hàng</h1>
<% if(request.getAttribute("error")!=null) { %><p class="error" role="alert"><%=Web.h(request.getAttribute("error"))%></p><% } %>
<form action="doSaveProduct9.jsp" method="post" id="productForm" novalidate><%=Web.csrf(session)%><input type="hidden" name="invoiceId" value="<%=Web.h(Web.draft(session).importInvoiceId)%>">
<div class="grid">
<div class="field span2"><label for="productName">Tên sản phẩm *</label><input required id="productName" name="productName" maxlength="255" value="<%=Web.h(Web.p(request,"productName"))%>"></div>
<div class="field"><label for="brand">Thương hiệu *</label><input required id="brand" name="brand" maxlength="100" value="<%=Web.h(Web.p(request,"brand"))%>"></div>
<div class="field"><label for="model">Model *</label><input required id="model" name="model" maxlength="100" value="<%=Web.h(Web.p(request,"model"))%>"></div>
<div class="field"><label for="origin">Xuất xứ *</label><input required id="origin" name="origin" maxlength="100" value="<%=Web.h(Web.p(request,"origin"))%>"></div>
<div class="field"><label for="unit">Đơn vị tính *</label><input required id="unit" name="unit" maxlength="30" value="<%=Web.h(Web.p(request,"unit"))%>"></div>
<div class="field"><label for="listPrice">Giá bán niêm yết *</label><input required id="listPrice" name="listPrice" inputmode="numeric" value="<%=Web.h(Web.p(request,"listPrice"))%>"></div>
<div class="field"><label for="warrantyPeriod">Thời hạn bảo hành *</label><input required id="warrantyPeriod" name="warrantyPeriod" inputmode="numeric" value="<%=Web.h(Web.p(request,"warrantyPeriod"))%>"></div>
<div class="field"><label for="warrantyRequired">Yêu cầu phiếu bảo hành</label><input id="warrantyRequired" name="warrantyRequired" type="checkbox" value="true" <%= "true".equals(Web.p(request,"warrantyRequired"))?"checked":"" %>> <label for="warrantyRequired" style="display:inline;font-weight:normal">Có</label></div>
<div class="field"><label for="categoryId">Nhóm hàng *</label><select required id="categoryId" name="categoryId">
<% try(var dao=new ProductDAO()) {for(var c:dao.getCategories()) { %><option value="<%=Web.h(c.categoryId)%>" <%=c.categoryId.equals(Web.p(request,"categoryId"))?"selected":""%>><%=Web.h(c.categoryName)%></option><% }} %>
</select></div>
<div class="field span2"><label for="description">Mô tả</label><textarea id="description" name="description" rows="3" maxlength="10000"><%=Web.h(Web.p(request,"description"))%></textarea></div>
<div class="field span2"><label for="fileImage">Hình ảnh</label><input type="file" id="fileImage" accept="image/png,image/jpeg,image/gif"><input type="hidden" name="imageData" id="imageData" value="<%=Web.h(Web.p(request,"imageData"))%>"></div>
</div><div class="actions"><button id="saveProduct">Lưu</button><a class="button secondary" href="gdSearchProduct9.jsp">Quay lại</a></div></form>
<script>
const fileImage=document.getElementById('fileImage'),imageData=document.getElementById('imageData'),saveProduct=document.getElementById('saveProduct');
fileImage.addEventListener('change',()=>{
 imageData.value='';const file=fileImage.files[0];if(!file)return;
 if(file.size>3*1024*1024||!['image/png','image/jpeg','image/gif'].includes(file.type)){fileImage.value='';return;}
 saveProduct.disabled=true;const reader=new FileReader();reader.onload=()=>{imageData.value=reader.result;saveProduct.disabled=false};reader.onerror=()=>{saveProduct.disabled=false};reader.readAsDataURL(file);
});
</script></section></main></body></html>

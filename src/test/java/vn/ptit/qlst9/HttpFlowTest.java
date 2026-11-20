package vn.ptit.qlst9;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.*;
import vn.ptit.qlst9.dao.*;

/** Chạy toàn bộ JSP trên Tomcat thật, bằng CSDL bộ nhớ riêng biệt. */
class HttpFlowTest {
    private static Tomcat server;
    private static String base;
    private final HttpClient client=HttpClient.newBuilder().cookieHandler(new CookieManager(null,CookiePolicy.ACCEPT_ALL)).followRedirects(HttpClient.Redirect.NORMAL).build();
    @BeforeAll static void start() throws Exception {
        System.setProperty("qlst.db","jdbc:h2:mem:httpFlow;DB_CLOSE_DELAY=-1");
        server=new Tomcat();server.setPort(0);server.setBaseDir(Files.createTempDirectory("qlst9-http-").toString());server.getConnector();
        var context=server.addWebapp("",new File("src/main/webapp").getAbsolutePath());context.setParentClassLoader(HttpFlowTest.class.getClassLoader());
        var resources=new StandardRoot(context);resources.addPreResources(new DirResourceSet(resources,"/WEB-INF/classes",new File("target/classes").getAbsolutePath(),"/"));context.setResources(resources);
        server.start();base="http://localhost:"+server.getConnector().getLocalPort()+"/";
    }
    @AfterAll static void stop() throws Exception {if(server!=null){server.stop();server.destroy();}}
    private HttpResponse<String> get(String page) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create(base+page)).GET().build(),HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }
    private HttpResponse<String> post(String page,String html,String... fields) throws Exception {
        var values=new LinkedHashMap<String,String>();
        if(html!=null)values.put("csrf",value(html,"csrf"));
        for(int i=0;i<fields.length;i+=2)values.put(fields[i],fields[i+1]);
        String body=values.entrySet().stream().map(e->encode(e.getKey())+"="+encode(e.getValue())).collect(java.util.stream.Collectors.joining("&"));
        return client.send(HttpRequest.newBuilder(URI.create(base+page)).header("Content-Type","application/x-www-form-urlencoded").POST(HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }
    private static String encode(String s) {return URLEncoder.encode(s,StandardCharsets.UTF_8);}
    private String value(String html,String field) {
        var matcher=Pattern.compile("name=\""+field+"\" value=\"([^\"]*)\"").matcher(html);
        assertTrue(matcher.find(),"Không có trường "+field);return matcher.group(1);
    }
    private String ok(HttpResponse<String> r,String expected) {
        assertEquals(200,r.statusCode(),r.body());assertTrue(r.body().contains(expected),r.body());return r.body();
    }
    private int stock(String id) throws Exception {try(var d=new ProductDAO()){return d.findById(id).stockQuantity;}}
    @Test void completeRegistrationAndImportFlows() throws Exception {
        ok(get("gdHome9.jsp"),"Siêu thị điện máy");
        var denied=get("gdImportInvoice9.jsp");assertTrue(denied.uri().getPath().endsWith("gdLogin9.jsp"));
        assertEquals(405,get("doRegister9.jsp").statusCode());assertEquals(403,post("doRegister9.jsp",null).statusCode());
        String form=ok(get("gdRegister9.jsp"),"Đăng ký thành viên");
        ok(post("doRegister9.jsp",form),"Vui lòng điền đầy đủ thông tin");
        var invalid=post("doRegister9.jsp",form,"fullName","Nguyễn Hoài Nam","email","sai","phone","123","password","abc","confirmPassword","abc");
        String body=ok(invalid,"Địa chỉ email không đúng định dạng");assertTrue(body.contains("value=\"Nguyễn Hoài Nam\""));
        ok(post("doRegister9.jsp",form,"fullName","Nguyễn Hoài Nam","email","nam@example.com","phone","123","password","abc","confirmPassword","abc"),"Số điện thoại phải bao gồm 10 chữ số");
        ok(post("doRegister9.jsp",form,"fullName","Nguyễn Hoài Nam","email","nam@example.com","phone","0987654321","password","abc","confirmPassword","abcd"),"Mật khẩu xác nhận không đúng");
        String registered=ok(post("doRegister9.jsp",form,"fullName","Nguyễn Hoài Nam","email","nam@example.com","phone","0987654321","password","abc","confirmPassword","abc"),"Đăng ký thành viên thành công!");
        assertTrue(registered.contains("KH00011"));
        ok(post("doRegister9.jsp",form,"fullName","Trùng email","email","NAM@example.com","phone","0987654322","password","abc","confirmPassword","abc"),"Thông tin người dùng đã tồn tại");
        String home=ok(post("gdLogin9.jsp",registered,"phone","0987654321","password","abc"),"Smart Tivi");
        assertTrue(get("gdWarehouseHome9.jsp").uri().getPath().endsWith("gdLogin9.jsp"));
        ok(post("gdLogin9.jsp",home,"action","logout"),"Đăng nhập");
        String login=ok(get("gdLogin9.jsp"),"Đăng nhập");
        ok(post("gdLogin9.jsp",login,"phone","0912000001","password","sai"),"Đăng nhập không thành công");
        String warehouse=ok(post("gdLogin9.jsp",login,"phone","0912000001","password","123456"),"Trang chủ nhân viên kho");
        assertFalse(warehouse.contains("Quản lý mặt hàng"));
        ok(get("gdSearchSupplier9.jsp?name="),"Vui lòng nhập từ khóa");
        ok(get("gdSearchSupplier9.jsp?name=XYZ"),"Không tìm thấy nhà cung cấp");
        String suppliers=ok(get("gdSearchSupplier9.jsp?name=Samsung"),"NCC0001");
        String invoice=ok(post("gdImportInvoice9.jsp",suppliers,"action","start","supplierId","NCC0001"),"0 VNĐ");
        String invoiceId=value(invoice,"invoiceId");
        ok(post("gdImportInvoice9.jsp",invoice,"action","submit","invoiceId",invoiceId),"Hóa đơn chưa có mặt hàng");
        String productForm=ok(get("gdSearchProduct9.jsp?productId=MH00001"),"Nhập chi tiết hàng nhập");
        ok(post("gdSearchProduct9.jsp",productForm,"invoiceId",invoiceId,"productId","MH00001","quantity","-1","unitPrice","0"),"Số lượng nhập và đơn giá phải là số nguyên dương lớn hơn 0");
        int before=stock("MH00001");
        String populated=ok(post("gdSearchProduct9.jsp",productForm,"invoiceId",invoiceId,"productId","MH00001","quantity","10","unitPrice","9500000"),"95.000.000 VNĐ");
        assertEquals(before,stock("MH00001"));
        ok(post("gdImportInvoice9.jsp",populated,"action","remove","invoiceId",invoiceId,"productId","MH00001"),"0 VNĐ");
        var canceled=post("gdImportInvoice9.jsp",populated,"action","cancel","invoiceId",invoiceId);ok(canceled,"Trang chủ nhân viên kho");assertEquals(before,stock("MH00001"));
        String addSupplier=ok(get("gdAddSupplier9.jsp"),"Thêm mới nhà cung cấp");
        ok(post("doSaveSupplier9.jsp",addSupplier,"name","","phone","123","address",""),"Vui lòng điền đầy đủ thông tin");
        invoice=ok(post("doSaveSupplier9.jsp",addSupplier,"name","Công ty Điện máy Hà Nội","phone","02435556666","address","Cầu Giấy, Hà Nội"),"Lập hóa đơn nhập hàng");
        invoiceId=value(invoice,"invoiceId");
        String addProduct=ok(get("gdAddProduct9.jsp"),"Nhóm hàng");
        ok(post("doSaveProduct9.jsp",addProduct,"invoiceId",invoiceId,"productName",""),"Vui lòng điền đầy đủ thông tin");
        String newProduct=ok(post("doSaveProduct9.jsp",addProduct,"invoiceId",invoiceId,"productName","Máy giặt LG AI 12kg","brand","LG","model","AI12","origin","Việt Nam","unit","chiếc","listPrice","12500000","warrantyPeriod","24","warrantyRequired","true","categoryId","NH003","description","Máy giặt cửa trước"),"Nhập chi tiết hàng nhập");
        String productId=value(newProduct,"productId");assertEquals(0,stock(productId));
        invoice=ok(post("gdSearchProduct9.jsp",newProduct,"invoiceId",invoiceId,"productId",productId,"quantity","2","unitPrice","10000000"),"20.000.000 VNĐ");
        String print=ok(post("gdImportInvoice9.jsp",invoice,"action","submit","invoiceId",invoiceId),"Nhập hàng thành công!");
        assertTrue(print.contains("Công ty Điện máy Hà Nội"));assertTrue(print.contains("Máy giặt LG AI 12kg"));assertTrue(print.contains("window.print()"));assertEquals(2,stock(productId));
        post("gdImportInvoice9.jsp",invoice,"action","submit","invoiceId",invoiceId);assertEquals(2,stock(productId));
        ok(get("gdPrintInvoice9.jsp?id="+invoiceId),"20.000.000 VNĐ");
        assertEquals(404,get("gdPrintInvoice9.jsp?id=unknown").statusCode());
    }
}

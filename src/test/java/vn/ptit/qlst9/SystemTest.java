package vn.ptit.qlst9;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import vn.ptit.qlst9.dao.*;
import vn.ptit.qlst9.model.*;
import vn.ptit.qlst9.web.Web;

class SystemTest {
    @BeforeEach void database() throws Exception {
        System.setProperty("qlst.db","jdbc:h2:mem:test"+UUID.randomUUID()+";DB_CLOSE_DELAY=-1");DAO.initialize();
    }
    private long count(String table) throws SQLException {
        try(var c=DAO.connect();var s=c.createStatement();var r=s.executeQuery("SELECT COUNT(*) FROM "+table)) {r.next();return r.getLong(1);}
    }
    private int stock(String id) throws SQLException {try(var d=new ProductDAO()) {return d.findById(id).stockQuantity;}}
    private Customer9 customer() throws SQLException {
        var c=new Customer9();c.fullName="Khách mới";c.phone="0987654321";c.email="NEW@example.com";c.password="mật khẩu";c.registrationDate=Web.today();
        try(var d=new CustomerDAO()) {c.customerId=d.generateCustomerId();}return c;
    }
    private ImportInvoice9 invoice(String productId,int qty) throws SQLException {
        var i=new ImportInvoice9();i.importDate=Web.today();
        try(var d=new ImportInvoiceDAO();var s=new SupplierDAO();var m=new MemberDAO();var p=new ProductDAO()) {
            i.importInvoiceId=d.generateImportInvoiceId();i.supplier=s.findById("NCC0001");i.warehouseStaff=(WarehouseStaff9)m.login("0912000001","123456");
            Web.appendProductItem(i,p.findById(productId),qty,new BigDecimal("9500000"));
        }return i;
    }
    @Test void seededDataAndRestart() throws Exception {
        for(String table:List.of("tblCustomer9","tblStaff9","tblSupplier9","tblCategory9","tblImportInvoice9","tblImportInvoiceDetail9"))assertEquals(10,count(table));
        assertEquals(20,count("tblProduct9"));
        try(var c=DAO.connect();var s=c.createStatement()) {s.executeUpdate("UPDATE tblProduct9 SET stockQuantity=37 WHERE model='50UT8050'");}
        assertEquals(20,count("tblMember9"));DAO.initialize();assertEquals(20,count("tblMember9"));assertEquals(20,count("tblProduct9"));
        try(var d=new ProductDAO()) {assertEquals(37,d.findProductByName("Smart Tivi LG UHD").get(0).stockQuantity);}
    }
    @Test void registrationHashesAndChecksBothUniqueFields() throws Exception {
        var c=customer();try(var d=new CustomerDAO()) {assertTrue(d.addCustomer(c));}
        try(var d=new MemberDAO()) {
            assertTrue(d.checkDuplicate("new@EXAMPLE.com","0999999999"));assertTrue(d.checkDuplicate("another@example.com",c.phone));
            assertNotNull(d.login(c.phone,"mật khẩu"));assertNull(d.login(c.phone,"wrong"));
        }
        try(var conn=DAO.connect();var s=conn.prepareStatement("SELECT password FROM tblMember9 WHERE phone=?")) {
            s.setString(1,c.phone);try(var r=s.executeQuery()) {r.next();assertNotEquals(c.password,r.getString(1));assertTrue(DAO.verifyPassword(c.password,r.getString(1)));}
        }
        var duplicate=customer();duplicate.phone="0987654322";
        try(var d=new CustomerDAO()) {assertThrows(SQLException.class,()->d.addCustomer(duplicate));}
        assertEquals(21,count("tblMember9"));assertEquals(11,count("tblCustomer9"));
    }
    @Test void registrationRollsBackMemberWhenCustomerInsertFails() throws Exception {
        var c=customer();c.customerId="KH00001";
        try(var d=new CustomerDAO()) {assertThrows(SQLException.class,()->d.addCustomer(c));}
        assertEquals(20,count("tblMember9"));
    }
    @Test void newProductStartsAtZeroAndPersistsImages() throws Exception {
        var p=new Product9();p.productName="Máy mới";p.brand="LG";p.model="A1";p.origin="Việt Nam";p.unit="chiếc";p.listPrice=new BigDecimal("3500000");p.warrantyPeriod=12;
        p.category=new Category9();p.category.categoryId="NH001";var image=new ProductImage9();image.imageUrl="data:image/png;base64,test";p.images.add(image);
        try(var d=new ProductDAO()) {assertTrue(d.saveProduct(p));assertEquals(0,d.findById(p.productId).stockQuantity);}
        assertEquals(1,count("tblProductImage9"));
    }
    @Test void draftDoesNotChangeStockAndSubmitCannotRepeat() throws Exception {
        int before=stock("MH00001");var inv=invoice("MH00001",10);assertEquals(before,stock("MH00001"));
        try(var d=new ImportInvoiceDAO()) {assertTrue(d.saveInvoice(inv));}
        assertEquals(before+10,stock("MH00001"));
        try(var d=new ImportInvoiceDAO()) {
            assertEquals(0,new BigDecimal("95000000").compareTo(d.findById(inv.importInvoiceId).totalAmount));
            assertThrows(SQLException.class,()->d.saveInvoice(inv));
        }
        assertEquals(before+10,stock("MH00001"));assertEquals(11,count("tblImportInvoice9"));
    }
    @Test void failedSecondStockUpdateRollsBackEntireInvoice() throws Exception {
        int before=stock("MH00001");var inv=invoice("MH00001",10);
        try(var d=new ProductDAO()) {Web.appendProductItem(inv,d.findById("MH00002"),2,new BigDecimal("1000"));}
        try(var c=DAO.connect();var s=c.createStatement()) {s.executeUpdate("UPDATE tblProduct9 SET status='Ngừng kinh doanh' WHERE productId='MH00002'");}
        try(var d=new ImportInvoiceDAO()) {assertThrows(SQLException.class,()->d.saveInvoice(inv));assertNull(d.findById(inv.importInvoiceId));}
        assertEquals(before,stock("MH00001"));assertEquals(10,count("tblImportInvoiceDetail9"));
    }
    @Test void emptyAndInvalidInvoiceAreRejected() throws Exception {
        var inv=invoice("MH00001",1);inv.details.clear();
        try(var d=new ImportInvoiceDAO()) {assertThrows(SQLException.class,()->d.saveInvoice(inv));}
        var invalid=invoice("MH00001",1);invalid.details.get(0).unitPrice=BigDecimal.ZERO;
        try(var d=new ImportInvoiceDAO()) {assertThrows(SQLException.class,()->d.saveInvoice(invalid));}
        assertEquals(10,count("tblImportInvoice9"));
    }
    @Test void concurrentImportsPreserveStockAndUniqueIds() throws Exception {
        int before=stock("MH00001");var first=invoice("MH00001",2);var second=invoice("MH00001",3);assertNotEquals(first.importInvoiceId,second.importInvoiceId);
        var pool=Executors.newFixedThreadPool(2);
        try {
            var a=pool.submit(()->{try(var d=new ImportInvoiceDAO()){return d.saveInvoice(first);}});
            var b=pool.submit(()->{try(var d=new ImportInvoiceDAO()){return d.saveInvoice(second);}});
            assertTrue(a.get(10,TimeUnit.SECONDS));assertTrue(b.get(10,TimeUnit.SECONDS));
        } finally {pool.shutdownNow();}
        assertEquals(before+5,stock("MH00001"));
    }
    @Test void inactiveRecordsAndSearchWildcardAreExcluded() throws Exception {
        try(var c=DAO.connect();var s=c.createStatement()) {
            s.executeUpdate("UPDATE tblSupplier9 SET status='Ngừng hợp tác' WHERE supplierId='NCC0001'");
            s.executeUpdate("UPDATE tblProduct9 SET status='Ngừng kinh doanh' WHERE productId='MH00001'");
        }
        try(var p=new ProductDAO();var s=new SupplierDAO()) {
            assertTrue(p.findProductByName("Samsung").stream().noneMatch(item->item.productId.equals("MH00001")));assertTrue(s.findSupplierByName("Samsung").isEmpty());
            assertTrue(p.findProductByName("%").isEmpty());assertTrue(s.findSupplierByName("' OR 1=1 --").isEmpty());
        }
    }
}

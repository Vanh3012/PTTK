package vn.ptit.qlst9.dao;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import vn.ptit.qlst9.model.*;

public class ImportInvoiceDAO extends DAO {
    public ImportInvoiceDAO() throws SQLException { super(); }
    public String generateImportInvoiceId() throws SQLException { return nextId("invoiceSeq","HDN",6); }
    public boolean saveInvoice(ImportInvoice9 inv) throws SQLException {
        if(inv.supplier==null || inv.warehouseStaff==null || inv.details.isEmpty())throw new SQLException("Invalid invoice");
        var seen=new HashSet<String>();
        for(var d:inv.details) {
            if(d.product==null || d.quantity<=0 || d.unitPrice==null || d.unitPrice.signum()<=0 || d.unitPrice.stripTrailingZeros().scale()>0 || !seen.add(d.product.productId))throw new SQLException("Invalid detail");
        }
        inv.calculateTotal();con.setAutoCommit(false);
        try {
            // Khóa NCC để trạng thái hợp tác không đổi trong lúc ghi nhận hóa đơn.
            try(var s=con.prepareStatement("SELECT status FROM tblSupplier9 WHERE supplierId=? FOR UPDATE")) {
                s.setString(1,inv.supplier.supplierId);try(var r=s.executeQuery()) {
                    if(!r.next() || !"Đang hợp tác".equals(r.getString(1)))throw new SQLException("Inactive supplier");
                }
            }
            try(var s=con.prepareStatement("SELECT position FROM tblStaff9 WHERE staffId=?")) {
                s.setString(1,inv.warehouseStaff.staffId);try(var r=s.executeQuery()) {
                    if(!r.next() || !"Nhân viên kho".equals(r.getString(1)))throw new SQLException("Invalid staff");
                }
            }
            try(var s=con.prepareStatement("INSERT INTO tblImportInvoice9 VALUES(?,?,?,?,?)")) {
                s.setString(1,inv.importInvoiceId);s.setDate(2,Date.valueOf(inv.importDate));s.setBigDecimal(3,inv.totalAmount);
                s.setString(4,inv.supplier.supplierId);s.setString(5,inv.warehouseStaff.staffId);s.executeUpdate();
            }
            new ImportInvoiceDetailDAO(con).createDetails(inv.importInvoiceId,inv.details);
            var productDAO=new ProductDAO(con);
            // Luôn cùng thứ tự khóa, tránh deadlock khi hai người nhập các mặt hàng giống nhau.
            var sorted=new ArrayList<>(inv.details);sorted.sort(Comparator.comparing(d->d.product.productId));
            for(var d:sorted)if(!productDAO.increaseStockQuantity(d.product.productId,d.quantity))throw new SQLException("Stock update failed");
            con.commit();return true;
        } catch(SQLException e) {con.rollback();throw e;}
        finally {con.setAutoCommit(true);}
    }
    public ImportInvoice9 findById(String id) throws SQLException {
        try(var s=con.prepareStatement("SELECT i.*,m.fullName FROM tblImportInvoice9 i JOIN tblStaff9 st ON st.staffId=i.warehouseStaffId JOIN tblMember9 m ON m.memberId=st.memberId WHERE importInvoiceId=?")) {
            s.setString(1,id);try(var r=s.executeQuery()) {
                if(!r.next())return null;
                var inv=new ImportInvoice9();inv.importInvoiceId=id;inv.importDate=r.getDate("importDate").toLocalDate();inv.totalAmount=r.getBigDecimal("totalAmount");
                inv.supplier=new SupplierDAO(con).findById(r.getString("supplierId"));inv.warehouseStaff=new WarehouseStaff9();
                inv.warehouseStaff.staffId=r.getString("warehouseStaffId");inv.warehouseStaff.fullName=r.getString("fullName");
                try(var ds=con.prepareStatement("SELECT * FROM tblImportInvoiceDetail9 WHERE importInvoiceId=? ORDER BY productId")) {
                    ds.setString(1,id);try(var dr=ds.executeQuery()) {
                        var products=new ProductDAO(con);
                        while(dr.next()) {var d=new ImportInvoiceDetail9();d.product=products.findById(dr.getString("productId"));d.quantity=dr.getInt("quantity");d.unitPrice=dr.getBigDecimal("unitPrice");inv.details.add(d);}
                    }
                }
                return inv;
            }
        }
    }
}

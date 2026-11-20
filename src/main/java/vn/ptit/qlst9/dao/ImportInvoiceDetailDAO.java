package vn.ptit.qlst9.dao;

import java.sql.*;
import java.util.*;
import vn.ptit.qlst9.model.*;

public class ImportInvoiceDetailDAO extends DAO {
    public ImportInvoiceDetailDAO() throws SQLException { super(); }
    ImportInvoiceDetailDAO(Connection c) { super(c); }
    public boolean createDetails(String invoiceId, List<ImportInvoiceDetail9> details) throws SQLException {
        try(var s=con.prepareStatement("INSERT INTO tblImportInvoiceDetail9 VALUES(?,?,?,?)")) {
            for(var d:details) {
                s.setString(1,invoiceId);s.setString(2,d.product.productId);s.setInt(3,d.quantity);s.setBigDecimal(4,d.unitPrice);s.addBatch();
            }
            s.executeBatch(); return true;
        }
    }
}

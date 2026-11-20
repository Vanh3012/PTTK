package vn.ptit.qlst9.dao;

import java.sql.*;
import java.util.*;
import vn.ptit.qlst9.model.Supplier9;

public class SupplierDAO extends DAO {
    public SupplierDAO() throws SQLException { super(); }
    SupplierDAO(Connection c) { super(c); }
    public List<Supplier9> findSupplierByName(String name) throws SQLException {
        var list = new ArrayList<Supplier9>();
        try (var s = con.prepareStatement("SELECT * FROM tblSupplier9 WHERE status='Đang hợp tác' AND LOWER(name) LIKE ? ESCAPE '!' ORDER BY supplierId")) {
            s.setString(1, "%" + ProductDAO.escapeLike(name) + "%");
            try (var r = s.executeQuery()) { while(r.next()) list.add(read(r)); }
        }
        return list;
    }
    public Supplier9 findById(String id) throws SQLException {
        try(var s = con.prepareStatement("SELECT * FROM tblSupplier9 WHERE supplierId=?")) {
            s.setString(1,id); try(var r=s.executeQuery()) { return r.next()?read(r):null; }
        }
    }
    private Supplier9 read(ResultSet r) throws SQLException {
        var p = new Supplier9(); p.supplierId=r.getString("supplierId"); p.name=r.getString("name");
        p.phone=r.getString("phone"); p.address=r.getString("address"); p.status=r.getString("status"); return p;
    }
    public boolean saveSupplier(Supplier9 p) throws SQLException {
        p.supplierId=nextId("supplierSeq","NCC",4); p.status="Đang hợp tác";
        try(var s=con.prepareStatement("INSERT INTO tblSupplier9 VALUES(?,?,?,?,?)")) {
            s.setString(1,p.supplierId);s.setString(2,p.name);s.setString(3,p.address);s.setString(4,p.phone);s.setString(5,p.status);
            return s.executeUpdate()==1;
        }
    }
}

package vn.ptit.qlst9.dao;

import java.sql.*;
import java.util.*;
import vn.ptit.qlst9.model.*;

public class ProductDAO extends DAO {
    public ProductDAO() throws SQLException { super(); }
    ProductDAO(Connection c) { super(c); }
    static String escapeLike(String text) { return text.toLowerCase(Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_"); }
    public List<Product9> findProductByName(String name) throws SQLException {
        var list=new ArrayList<Product9>();
        try(var s=con.prepareStatement("SELECT * FROM tblProduct9 WHERE status='Đang kinh doanh' AND LOWER(productName) LIKE ? ESCAPE '!' ORDER BY productId")) {
            s.setString(1,"%"+escapeLike(name)+"%");try(var r=s.executeQuery()) { while(r.next())list.add(read(r)); }
        }
        return list;
    }
    public Product9 findById(String id) throws SQLException {
        try(var s=con.prepareStatement("SELECT * FROM tblProduct9 WHERE productId=?")) {
            s.setString(1,id);try(var r=s.executeQuery()) { return r.next()?read(r):null; }
        }
    }
    private Product9 read(ResultSet r) throws SQLException {
        var p=new Product9();p.productId=r.getString("productId");p.productName=r.getString("productName");
        p.brand=r.getString("brand");p.model=r.getString("model");p.origin=r.getString("origin");p.unit=r.getString("unit");
        p.listPrice=r.getBigDecimal("listPrice");p.stockQuantity=r.getInt("stockQuantity");p.warrantyPeriod=r.getInt("warrantyPeriod");
        p.warrantyRequired=r.getBoolean("warrantyRequired");p.description=r.getString("description");p.status=r.getString("status");
        p.category=new Category9();p.category.categoryId=r.getString("categoryId");return p;
    }
    public List<Category9> getCategories() throws SQLException {
        var list=new ArrayList<Category9>();
        try(var s=con.createStatement();var r=s.executeQuery("SELECT * FROM tblCategory9 ORDER BY categoryId")) {
            while(r.next()) {var c=new Category9();c.categoryId=r.getString("categoryId");c.categoryName=r.getString("categoryName");c.description=r.getString("description");list.add(c);}
        }
        return list;
    }
    public boolean saveProduct(Product9 p) throws SQLException {
        p.productId=nextId("productSeq","MH",5);p.stockQuantity=0;p.status="Đang kinh doanh";
        con.setAutoCommit(false);
        try {
            try(var s=con.prepareStatement("INSERT INTO tblProduct9 VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                s.setString(1,p.productId);s.setString(2,p.productName);s.setString(3,p.brand);s.setString(4,p.model);s.setString(5,p.origin);s.setString(6,p.unit);
                s.setBigDecimal(7,p.listPrice);s.setInt(8,0);s.setInt(9,p.warrantyPeriod);s.setBoolean(10,p.warrantyRequired);s.setString(11,p.description);s.setString(12,p.status);s.setString(13,p.category.categoryId);
                s.executeUpdate();
            }
            int i=0;
            for(var image:p.images)try(var s=con.prepareStatement("INSERT INTO tblProductImage9 VALUES(?,?,?)")) {
                s.setString(1,p.productId+"-"+(++i));s.setString(2,image.imageUrl);s.setString(3,p.productId);s.executeUpdate();
            }
            con.commit();return true;
        } catch(SQLException e) {con.rollback();throw e;}
        finally {con.setAutoCommit(true);}
    }
    public boolean increaseStockQuantity(String id,int qty) throws SQLException {
        if(qty<=0)throw new SQLException("Invalid quantity");
        try(var s=con.prepareStatement("UPDATE tblProduct9 SET stockQuantity=stockQuantity+? WHERE productId=? AND status='Đang kinh doanh'")) {
            s.setInt(1,qty);s.setString(2,id);return s.executeUpdate()==1;
        }
    }
}

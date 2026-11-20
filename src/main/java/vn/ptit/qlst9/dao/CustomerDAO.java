package vn.ptit.qlst9.dao;

import java.sql.*;
import java.util.Locale;
import vn.ptit.qlst9.model.Customer9;

public class CustomerDAO extends DAO {
    public CustomerDAO() throws SQLException { super(); }
    public String generateCustomerId() throws SQLException { return nextId("customerSeq", "KH", 5); }
    public boolean addCustomer(Customer9 c) throws SQLException {
        con.setAutoCommit(false);
        try {
            try (var s = con.prepareStatement("INSERT INTO tblMember9(fullName,phone,email,password) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                s.setString(1,c.fullName); s.setString(2,c.phone); s.setString(3,c.email.toLowerCase(Locale.ROOT)); s.setString(4,hashPassword(c.password)); s.executeUpdate();
                try (var r = s.getGeneratedKeys()) { r.next(); c.memberId = r.getInt(1); }
            }
            try (var s = con.prepareStatement("INSERT INTO tblCustomer9 VALUES(?,?,?)")) {
                s.setString(1,c.customerId); s.setInt(2,c.memberId); s.setDate(3,Date.valueOf(c.registrationDate)); s.executeUpdate();
            }
            con.commit(); return true;
        } catch (SQLException e) { con.rollback(); throw e; }
        finally { con.setAutoCommit(true); }
    }
}

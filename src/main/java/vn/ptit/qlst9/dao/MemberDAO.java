package vn.ptit.qlst9.dao;

import java.sql.*;
import vn.ptit.qlst9.model.*;

public class MemberDAO extends DAO {
    public MemberDAO() throws SQLException { super(); }
    public boolean checkDuplicate(String email, String phone) throws SQLException {
        try (var s = con.prepareStatement("SELECT 1 FROM tblMember9 WHERE LOWER(email)=LOWER(?) OR phone=?")) {
            s.setString(1, email); s.setString(2, phone);
            try (var r = s.executeQuery()) { return r.next(); }
        }
    }
    public Member9 login(String phone, String password) throws SQLException {
        String sql = "SELECT m.*,s.staffId,s.position,c.customerId,c.registrationDate FROM tblMember9 m "
                + "LEFT JOIN tblStaff9 s ON s.memberId=m.memberId LEFT JOIN tblCustomer9 c ON c.memberId=m.memberId WHERE m.phone=?";
        try (var s = con.prepareStatement(sql)) {
            s.setString(1, phone);
            try (var r = s.executeQuery()) {
                if (!r.next() || !verifyPassword(password, r.getString("password"))) return null;
                Member9 member;
                if (r.getString("staffId") != null) {
                    if (!"Nhân viên kho".equals(r.getString("position"))) return null;
                    var staff = new WarehouseStaff9(); staff.staffId = r.getString("staffId"); staff.position = r.getString("position"); member = staff;
                } else {
                    var customer = new Customer9(); customer.customerId = r.getString("customerId");
                    customer.registrationDate = r.getDate("registrationDate").toLocalDate(); member = customer;
                }
                member.memberId = r.getInt("memberId"); member.fullName = r.getString("fullName");
                member.phone = r.getString("phone"); member.email = r.getString("email"); member.address = r.getString("address");
                return member;
            }
        }
    }
}

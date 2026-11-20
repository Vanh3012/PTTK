package vn.ptit.qlst9.dao;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.security.*;
import java.util.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class DAO implements AutoCloseable {
    protected final Connection con;
    private final boolean owner;
    public DAO() throws SQLException { this(connect(), true); }
    protected DAO(Connection con) { this(con, false); }
    private DAO(Connection con, boolean owner) { this.con = con; this.owner = owner; }
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(System.getProperty("qlst.db", "jdbc:h2:file:./data/qlst9;DB_CLOSE_ON_EXIT=FALSE"), "sa", "");
    }
    @Override public void close() throws SQLException { if (owner) con.close(); }
    protected String nextId(String sequence, String prefix, int width) throws SQLException {
        try (var s = con.createStatement(); var r = s.executeQuery("SELECT NEXT VALUE FOR " + sequence)) {
            r.next(); return prefix + String.format(Locale.ROOT, "%0" + width + "d", r.getLong(1));
        }
    }
    public static String hashPassword(String password) {
        byte[] salt = new byte[16]; new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(derive(password, salt));
    }
    private static byte[] derive(String password, byte[] salt) {
        var spec = new PBEKeySpec(password.toCharArray(), salt, 120000, 256);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        catch (GeneralSecurityException e) { throw new IllegalStateException(e); }
        finally { spec.clearPassword(); }
    }
    public static boolean verifyPassword(String password, String stored) {
        try { var parts = stored.split(":"); return MessageDigest.isEqual(derive(password, Base64.getDecoder().decode(parts[0])), Base64.getDecoder().decode(parts[1])); }
        catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) { return false; }
    }
    public static synchronized void initialize() throws Exception {
        Class.forName("org.h2.Driver");
        try (var c = connect()) {
            for (String sql : resource("/schema.sql").split(";")) {
                if (!sql.isBlank()) try (var s = c.createStatement()) { s.execute(sql); }
            }
            // Dữ liệu gốc chỉ nạp một lần; phần mặt hàng bổ sung kiểm tra tồn tại trước khi thêm.
            boolean empty;
            try (var s = c.createStatement(); var r = s.executeQuery("SELECT COUNT(*) FROM tblMember9")) {
                r.next(); empty = r.getInt(1) == 0;
            }
            c.setAutoCommit(false);
            try {
                if (empty) for (String sql : resource("/seed.sql").replace("{PASSWORD_HASH}", hashPassword("123456")).split(";")) {
                    if (!sql.isBlank()) try (var s = c.createStatement()) { s.execute(sql); }
                }
                for (String sql : resource("/seed-products.sql").split(";")) {
                    if (!sql.isBlank()) try (var s = c.createStatement()) { s.execute(sql); }
                }
                c.commit();
            } catch (Exception e) { c.rollback(); throw e; }
        }
    }
    private static String resource(String name) throws IOException {
        try (var in = DAO.class.getResourceAsStream(name)) {
            if (in == null) throw new FileNotFoundException(name);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}

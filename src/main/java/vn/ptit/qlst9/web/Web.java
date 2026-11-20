package vn.ptit.qlst9.web;

import jakarta.servlet.http.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.*;
import java.util.*;
import vn.ptit.qlst9.model.*;

/** Các hàm dùng chung cho JSP; không thêm màn hình hay nghiệp vụ. */
public final class Web {
    private Web() { }
    public static String h(Object value) {
        if(value==null)return "";
        return value.toString().replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;");
    }
    public static String p(HttpServletRequest r,String name) { String v=r.getParameter(name);return v==null?"":v.trim(); }
    public static String password(HttpServletRequest r,String name) {String v=r.getParameter(name);return v==null?"":v;}
    public static LocalDate today() { return LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")); }
    public static String money(BigDecimal amount) {return NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).format(amount)+" VNĐ";}
    public static String date(LocalDate d) {return d.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));}
    public static String csrf(HttpSession s) {
        if(s.getAttribute("csrf")==null)s.setAttribute("csrf",UUID.randomUUID().toString());
        return "<input type=\"hidden\" name=\"csrf\" value=\""+s.getAttribute("csrf")+"\">";
    }
    public static boolean post(HttpServletRequest r) {return "POST".equals(r.getMethod());}
    public static ImportInvoice9 draft(HttpSession s) {return (ImportInvoice9)s.getAttribute("invoice");}
    public static String registrationError(HttpServletRequest r) {
        if(p(r,"fullName").isEmpty()||p(r,"email").isEmpty()||p(r,"phone").isEmpty()||password(r,"password").isEmpty()||password(r,"confirmPassword").isEmpty())return "Vui lòng điền đầy đủ thông tin";
        if(!p(r,"email").matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")||p(r,"email").length()>120)return "Địa chỉ email không đúng định dạng";
        if(!p(r,"phone").matches("[0-9]{10}"))return "Số điện thoại phải bao gồm 10 chữ số";
        if(!password(r,"password").equals(password(r,"confirmPassword")))return "Mật khẩu xác nhận không đúng";
        if(p(r,"fullName").length()>100||password(r,"password").length()>255)return "Vui lòng điền đầy đủ thông tin";
        return null;
    }
    public static BigDecimal positiveInteger(String text) {
        if(!text.matches("[0-9]{1,16}"))throw new IllegalArgumentException();
        var n=new BigDecimal(text);if(n.signum()<=0)throw new IllegalArgumentException();return n;
    }
    public static int quantity(String text) {return positiveInteger(text).intValueExact();}
    public static void appendProductItem(ImportInvoice9 inv,Product9 p,int qty,BigDecimal price) {
        // Khóa ghép hóa đơn + mặt hàng: gộp nếu cùng đơn giá, khác đơn giá thì nhập lại dòng đó.
        for(var d:inv.details)if(d.product.productId.equals(p.productId)) {
            if(d.unitPrice.compareTo(price)==0)d.quantity=Math.addExact(d.quantity,qty);
            else {d.quantity=qty;d.unitPrice=price;}
            inv.calculateTotal();return;
        }
        var d=new ImportInvoiceDetail9();d.product=p;d.quantity=qty;d.unitPrice=price;inv.details.add(d);inv.calculateTotal();
    }
}

package vn.ptit.qlst9.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class ImportInvoiceDetail9 {
    public Product9 product;
    public int quantity;
    public BigDecimal unitPrice;
    public BigDecimal subTotal() { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }
}

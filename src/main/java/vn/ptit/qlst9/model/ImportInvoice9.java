package vn.ptit.qlst9.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class ImportInvoice9 {
    public String importInvoiceId;
    public LocalDate importDate;
    public BigDecimal totalAmount = BigDecimal.ZERO;
    public Supplier9 supplier;
    public WarehouseStaff9 warehouseStaff;
    public List<ImportInvoiceDetail9> details = new ArrayList<>();
    public void calculateTotal() { totalAmount = details.stream().map(ImportInvoiceDetail9::subTotal).reduce(BigDecimal.ZERO, BigDecimal::add); }
}

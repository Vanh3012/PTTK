package vn.ptit.qlst9.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class Product9 {
    public String productId, productName, brand, model, origin, unit, description, status;
    public BigDecimal listPrice;
    public int stockQuantity, warrantyPeriod;
    public boolean warrantyRequired;
    public Category9 category;
    public List<ProductImage9> images = new ArrayList<>();
}

package com.inventory.util;

import com.inventory.model.Category;
import com.inventory.model.Product;
import com.inventory.model.Supplier;
import com.inventory.service.CategoryService;
import com.inventory.service.ProductService;
import com.inventory.service.SupplierService;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class CsvExporter {

    
    private static final String EXPORT_FOLDER = "exports";

    
    public static String exportProducts(ProductService productService,
                                        CategoryService categoryService,
                                        SupplierService supplierService)
            throws IOException {

        
        java.io.File folder = new java.io.File(EXPORT_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filePath = EXPORT_FOLDER + "/inventory_" + timestamp + ".csv";

        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {

            
            writer.println(
                "Product ID,Name,Description,Category,Supplier," +
                "Selling Price,Cost Price,Quantity,Low Stock Threshold,Status,Date Added"
            );

            List<Product> products = productService.getAllProducts();

            for (Product p : products) {

                String categoryName = categoryService.getCategoryById(p.getCategoryId())
                        .map(Category::getName).orElse("Unknown");
                String supplierName = supplierService.getSupplierById(p.getSupplierId())
                        .map(Supplier::getName).orElse("Unknown");

                String status = p.isLowStock() ? "LOW STOCK" : "OK";

                
                writer.printf("%s,%s,%s,%s,%s,%.2f,%.2f,%d,%d,%s,%s%n",
                    p.getId(),
                    escape(p.getName()),
                    escape(p.getDescription()),
                    escape(categoryName),
                    escape(supplierName),
                    p.getPrice(),
                    p.getCostPrice(),
                    p.getQuantity(),
                    p.getLowStockThreshold(),
                    status,
                    p.getDateAdded() != null ? p.getDateAdded().toString() : "N/A"
                );
            }


            writer.println();
            writer.println();
            writer.println("--- SUMMARY ---");
            writer.printf("Total Products,%d%n", products.size());
            writer.printf("Total Inventory Value,%.2f%n",
                productService.getTotalInventoryValue());
            writer.printf("Low Stock Items,%d%n",
                productService.getLowStockProducts().size());
            writer.printf("Export Date,%s%n",
                LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        }

        return filePath; 
    }

    
    private static String escape(String value) {
        if (value == null) return "";


        String escaped = value.replace("\"", "\"\"");


        if (escaped.contains(",") || escaped.contains("\"")
                || escaped.contains("\n") || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
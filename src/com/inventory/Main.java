package com.inventory;

import com.inventory.exception.ValidationException;
import com.inventory.model.Category;
import com.inventory.model.Product;
import com.inventory.model.Supplier;
import com.inventory.service.CategoryService;
import com.inventory.service.ProductService;
import com.inventory.service.SupplierService;

import java.util.List;


public class Main {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   Inventory Management System — Test     ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println();

       
        CategoryService categoryService = new CategoryService();
        SupplierService supplierService = new SupplierService();
        ProductService  productService  = new ProductService();

        
        System.out.println("─ TEST 1: Adding Categories ─");

        
        try {
            categoryService.addCategory("Electronics", "Electronic gadgets and devices");
            System.out.println("Added category: Electronics");

            categoryService.addCategory("Groceries", "Food and daily essentials");
            System.out.println("Added category: Groceries");

            categoryService.addCategory("Clothing", "Apparel and accessories");
            System.out.println("Added category: Clothing");
        } catch (ValidationException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }

        
        System.out.println("\n─ TEST 2: Duplicate Category (should fail) ─");
        try {
            categoryService.addCategory("Electronics", "Duplicate attempt");
            System.out.println("Should NOT reach here!");
        } catch (ValidationException e) {
            
            System.out.println("Correctly rejected: " + e.getMessage());
        }

        
        System.out.println("\n─ TEST 3: Empty Category Name (should fail) ─");
        try {
            categoryService.addCategory("   ", ""); // just spaces — should fail
            System.out.println("Should NOT reach here!");
        } catch (ValidationException e) {
            System.out.println("Correctly rejected: " + e.getMessage());
        }

        
        System.out.println("\n─ TEST 4: Adding Suppliers ─");
        try {
            supplierService.addSupplier(
                "Samsung India", "Rajesh Kumar",
                "9876543210", "rajesh@samsung.in", "Mumbai, Maharashtra"
            );
            System.out.println("Added supplier: Samsung India");

            supplierService.addSupplier(
                "Fresh Farms Co.", "Priya Sharma",
                "9123456780", "priya@freshfarms.com", "Pune, Maharashtra"
            );
            System.out.println("Added supplier: Fresh Farms Co.");
        } catch (ValidationException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }

        
        System.out.println("\n─ TEST 5: Supplier with no phone (should fail) ─");
        try {
            supplierService.addSupplier("NoPhone Corp", "Nobody", "", "", "");
            System.out.println("Should NOT reach here!");
        } catch (ValidationException e) {
            System.out.println("Correctly rejected: " + e.getMessage());
        }

        
        System.out.println("\n─ TEST 6: Adding Products ─");
        try {
            productService.addProduct(
                "Samsung Galaxy S24",          // name
                "Latest flagship smartphone",  // description
                79999.00,                      // selling price
                60000.00,                      // cost price
                50,                            // quantity in stock
                10,                            // alert if below 10
                "CAT001",                      // Electronics category
                "SUP001"                       // Samsung India supplier
            );
            System.out.println("Added product: Samsung Galaxy S24");

            productService.addProduct(
                "Basmati Rice 5kg",
                "Premium long-grain basmati rice",
                450.00,
                300.00,
                200,
                30,
                "CAT002",   // Groceries
                "SUP002"    // Fresh Farms
            );
            System.out.println("Added product: Basmati Rice 5kg");

            productService.addProduct(
                "USB-C Charging Cable",
                "2 meter fast charging cable",
                599.00,
                200.00,
                5,    // only 5 in stock
                10,   // threshold = 10, so 5 < 10 → LOW STOCK!
                "CAT001",
                "SUP001"
            );
            System.out.println("Added product: USB-C Charging Cable (low stock!)");

        } catch (ValidationException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }

        
        System.out.println("\n── TEST 7: Negative Price (should fail) ──");
        try {
            productService.addProduct(
                "Bad Product", "", -100.0, 0, 10, 5, "CAT001", "SUP001"
            );
            System.out.println("Should NOT reach here!");
        } catch (ValidationException e) {
            System.out.println("Correctly rejected: " + e.getMessage());
        }

      
        System.out.println("\n── TEST 8: Invalid Category ID (should fail) ──");
        try {
            productService.addProduct(
                "Ghost Product", "", 100.0, 50.0, 10, 5, "CAT999", "SUP001"
            );
            System.out.println("Should NOT reach here!");
        } catch (ValidationException e) {
            System.out.println("Correctly rejected: " + e.getMessage());
        }

        
        System.out.println("\n── TEST 9: Reading All Data from Files ──");

        List<Category> categories = categoryService.getAllCategories();
        System.out.println("\nCategories saved in file (" + categories.size() + "):");
        for (Category c : categories) {
            
            System.out.printf("   [%s] %-15s → %s%n",
                c.getId(), c.getName(), c.getDescription());
        }

        List<Supplier> suppliers = supplierService.getAllSuppliers();
        System.out.println("\nSuppliers saved in file (" + suppliers.size() + "):");
        for (Supplier s : suppliers) {
            System.out.printf("   [%s] %-20s → %s%n",
                s.getId(), s.getName(), s.getPhone());
        }

        List<Product> products = productService.getAllProducts();
        System.out.println("\nProducts saved in file (" + products.size() + "):");
        for (Product p : products) {
            System.out.printf("   [%s] %-25s qty=%-5d price=₹%.2f %s%n",
                p.getId(), p.getName(), p.getQuantity(), p.getPrice(),
                p.isLowStock() ? "⚠ LOW STOCK" : ""); // ternary operator
        }

        // ════════════════════════════════════════════════════════════════════
        // TEST 10 — Low stock report
        // ════════════════════════════════════════════════════════════════════
        System.out.println("\n─ TEST 10: Low Stock Alert ─");
        List<Product> lowStock = productService.getLowStockProducts();
        if (lowStock.isEmpty()) {
            System.out.println("   All products are sufficiently stocked.");
        } else {
            System.out.println( lowStock.size() + " product(s) need restocking:");
            for (Product p : lowStock) {
                System.out.printf("   → %s (qty=%d, threshold=%d)%n",
                    p.getName(), p.getQuantity(), p.getLowStockThreshold());
            }
        }

       
        System.out.println("\n── TEST 11: Search Products by name 'sam' ──");
        List<Product> searchResults = productService.searchByName("sam");
        for (Product p : searchResults) {
            System.out.println("   Found: " + p.getName());
        }

        
        System.out.println("\n─TEST 12: Total Inventory Value ─");
        double totalValue = productService.getTotalInventoryValue();
        System.out.printf("  Total stock value: ₹%.2f%n", totalValue);

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║   All tests complete! Check /data folder ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }
}
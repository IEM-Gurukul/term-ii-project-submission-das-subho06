package com.inventory.model;

import java.io.Serializable;
import java.time.LocalDate;


public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

   

    private String id;               // e.g. "PRD001" — unique identifier
    private String name;             // e.g. "Samsung Galaxy S24"
    private String description;      // longer text about the product

    
    private double price;            // selling price (what customer pays)
    private double costPrice;        // cost price (what we paid supplier)

    private int quantity;            // how many units currently in stock
    private int lowStockThreshold;   // alert if quantity drops below this number
    private String categoryId;       // which category this product belongs to
    private String supplierId;       // which supplier provides this product

   
    private LocalDate dateAdded;

   
    public Product() {
        // Required for deserialization
    }

    
    public Product(String id, String name, String description,
                   double price, double costPrice,
                   int quantity, int lowStockThreshold,
                   String categoryId, String supplierId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.costPrice = costPrice;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.dateAdded = LocalDate.now(); // auto-set to today
    }

   
    public boolean isLowStock() {
        return quantity <= lowStockThreshold;
    }

    
    public double getProfitMargin() {
        if (costPrice == 0) return 0.0;
        return ((price - costPrice) / price) * 100.0;
    }

    
    public double getTotalStockValue() {
        return price * quantity;
    }

    

    public String getId()                { return id; }
    public String getName()              { return name; }
    public String getDescription()       { return description; }
    public double getPrice()             { return price; }
    public double getCostPrice()         { return costPrice; }
    public int getQuantity()             { return quantity; }
    public int getLowStockThreshold()    { return lowStockThreshold; }
    public String getCategoryId()        { return categoryId; }
    public String getSupplierId()        { return supplierId; }
    public LocalDate getDateAdded()      { return dateAdded; }

    

    public void setId(String id)                         { this.id = id; }
    public void setName(String name)                     { this.name = name; }
    public void setDescription(String description)       { this.description = description; }
    public void setPrice(double price)                   { this.price = price; }
    public void setCostPrice(double costPrice)           { this.costPrice = costPrice; }
    public void setQuantity(int quantity)                { this.quantity = quantity; }
    public void setLowStockThreshold(int t)              { this.lowStockThreshold = t; }
    public void setCategoryId(String categoryId)         { this.categoryId = categoryId; }
    public void setSupplierId(String supplierId)         { this.supplierId = supplierId; }
    public void setDateAdded(LocalDate dateAdded)        { this.dateAdded = dateAdded; }

    

    @Override
    public String toString() {
        return String.format("Product[%s | %s | qty=%d | ₹%.2f]",
                id, name, quantity, price);
    }
}
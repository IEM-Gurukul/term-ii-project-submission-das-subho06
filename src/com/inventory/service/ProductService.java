package com.inventory.service;

import com.inventory.dao.interfaces.ProductDAO;
import com.inventory.dao.impl.ProductDAOImpl;
import com.inventory.dao.interfaces.CategoryDAO;
import com.inventory.dao.impl.CategoryDAOImpl;
import com.inventory.dao.interfaces.SupplierDAO;
import com.inventory.dao.impl.SupplierDAOImpl;
import com.inventory.exception.ValidationException;
import com.inventory.model.Product;

import java.util.List;
import java.util.Optional;


public class ProductService {

    
    private ProductDAO  productDAO  = new ProductDAOImpl();
    private CategoryDAO categoryDAO = new CategoryDAOImpl();
    private SupplierDAO supplierDAO = new SupplierDAOImpl();

    
    public void addProduct(String name, String description,
                           double price, double costPrice,
                           int quantity, int lowStockThreshold,
                           String categoryId, String supplierId) {

        
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Product name cannot be empty.");
        }

        
        if (price <= 0) {
            throw new ValidationException(
                "Selling price must be greater than zero. You entered: " + price);
        }

        
        if (costPrice < 0) {
            throw new ValidationException(
                "Cost price cannot be negative. You entered: " + costPrice);
        }

        
        if (quantity < 0) {
            throw new ValidationException(
                "Quantity cannot be negative. You entered: " + quantity);
        }

        
        if (lowStockThreshold < 0) {
            throw new ValidationException(
                "Low stock threshold cannot be negative. You entered: "
                + lowStockThreshold);
        }

        
        if (!categoryDAO.findById(categoryId).isPresent()) {
            throw new ValidationException(
                "Category with ID '" + categoryId + "' does not exist. "
                + "Please add the category first.");
        }

        
        if (!supplierDAO.findById(supplierId).isPresent()) {
            throw new ValidationException(
                "Supplier with ID '" + supplierId + "' does not exist. "
                + "Please add the supplier first.");
        }

        
        String id = productDAO.generateNextId();
        Product product = new Product(
            id, name.trim(), description,
            price, costPrice,
            quantity, lowStockThreshold,
            categoryId, supplierId
        );
        productDAO.save(product);
    }

    
    public void updateProduct(String id, String name, String description,
                              double price, double costPrice,
                              int quantity, int lowStockThreshold,
                              String categoryId, String supplierId) {

        
        Optional<Product> existing = productDAO.findById(id);
        if (!existing.isPresent()) {
            throw new ValidationException("Product with ID '" + id + "' not found.");
        }

        
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Product name cannot be empty.");
        }
        if (price <= 0) {
            throw new ValidationException("Selling price must be greater than zero.");
        }
        if (costPrice < 0) {
            throw new ValidationException("Cost price cannot be negative.");
        }
        if (quantity < 0) {
            throw new ValidationException("Quantity cannot be negative.");
        }
        if (lowStockThreshold < 0) {
            throw new ValidationException("Low stock threshold cannot be negative.");
        }
        if (!categoryDAO.findById(categoryId).isPresent()) {
            throw new ValidationException(
                "Category with ID '" + categoryId + "' does not exist.");
        }
        if (!supplierDAO.findById(supplierId).isPresent()) {
            throw new ValidationException(
                "Supplier with ID '" + supplierId + "' does not exist.");
        }

        
        Product updated = existing.get(); 
        updated.setName(name.trim());
        updated.setDescription(description);
        updated.setPrice(price);
        updated.setCostPrice(costPrice);
        updated.setQuantity(quantity);
        updated.setLowStockThreshold(lowStockThreshold);
        updated.setCategoryId(categoryId);
        updated.setSupplierId(supplierId);
        

        productDAO.update(updated);
    }

    
    public void updateQuantity(String id, int newQuantity) {

        Optional<Product> existing = productDAO.findById(id);
        if (!existing.isPresent()) {
            throw new ValidationException("Product with ID '" + id + "' not found.");
        }
        if (newQuantity < 0) {
            throw new ValidationException("Quantity cannot be negative.");
        }

        Product product = existing.get();
        product.setQuantity(newQuantity);
        productDAO.update(product);
    }

    
    public void deleteProduct(String id) {
        if (!productDAO.findById(id).isPresent()) {
            throw new ValidationException("Product with ID '" + id + "' not found.");
        }
        productDAO.delete(id);
    }

    
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    
    public Optional<Product> getProductById(String id) {
        return productDAO.findById(id);
    }

    
    public List<Product> searchByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productDAO.findAll(); // empty search = show everything
        }
        return productDAO.searchByName(keyword.trim());
    }

    
    public List<Product> getProductsByCategory(String categoryId) {
        return productDAO.findByCategoryId(categoryId);
    }

    
    public List<Product> getLowStockProducts() {
        return productDAO.findLowStock();
    }

    
    public double getTotalInventoryValue() {
        return productDAO.findAll()
                .stream()
                .mapToDouble(Product::getTotalStockValue)
                .sum();
    }
}
package com.inventory.dao.interfaces;


import com.inventory.model.Product;


import java.util.List;


import java.util.Optional;


public interface ProductDAO {

   
    void save(Product product);

    
    void update(Product product);

    
    void delete(String id);

    
    Optional<Product> findById(String id);

    
    List<Product> findAll();

    
    List<Product> findByCategoryId(String categoryId);

    
    List<Product> findLowStock();

    
    List<Product> searchByName(String keyword);

    
    String generateNextId();
}
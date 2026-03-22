package com.inventory.dao.interfaces;

import com.inventory.model.Category;
import java.util.List;
import java.util.Optional;


public interface CategoryDAO {

    
    void save(Category category);

    
    void update(Category category);

    
    void delete(String id);

    
    Optional<Category> findById(String id);

    
    List<Category> findAll();

    
    boolean isUsedByProduct(String categoryId);

    
    String generateNextId();
}
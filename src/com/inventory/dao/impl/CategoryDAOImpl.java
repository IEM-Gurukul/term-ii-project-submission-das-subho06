package com.inventory.dao.impl;

import com.inventory.dao.interfaces.CategoryDAO;
import com.inventory.model.Category;
import com.inventory.util.FileManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class CategoryDAOImpl implements CategoryDAO {

    
    private final String filePath = FileManager.CATEGORIES_FILE;

    

    private List<Category> loadAll() {
        return FileManager.loadData(filePath);
    }

    
    @Override
    public void save(Category category) {
        List<Category> list = loadAll();   // Step 1: load existing data
        list.add(category);                 // Step 2: add the new item
        FileManager.saveData(list, filePath); // Step 3: write everything back
    }

    
    @Override
    public void update(Category category) {
        List<Category> list = loadAll();

        
        for (int i = 0; i < list.size(); i++) {
            
            if (list.get(i).getId().equals(category.getId())) {
                
                list.set(i, category);
                break; 
            }
        }

        FileManager.saveData(list, filePath);
    }

    
    @Override
    public void delete(String id) {
        List<Category> list = loadAll();
        list.removeIf(c -> c.getId().equals(id)); // remove where ID matches
        FileManager.saveData(list, filePath);
    }

   
    @Override
    public Optional<Category> findById(String id) {
        return loadAll().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    
    @Override
    public List<Category> findAll() {
        return loadAll();
    }

    
    @Override
    public boolean isUsedByProduct(String categoryId) {
       
        List<com.inventory.model.Product> products =
                FileManager.loadData(FileManager.PRODUCTS_FILE);

        return products.stream()
                .anyMatch(p -> p.getCategoryId().equals(categoryId));
    }

    
    @Override
    public String generateNextId() {
        List<Category> list = loadAll();

        if (list.isEmpty()) {
            return "CAT001"; 
        }

        
        int maxId = list.stream()
                .mapToInt(c -> Integer.parseInt(c.getId().substring(3)))
                .max()     
                .orElse(0); 

        
        return String.format("CAT%03d", maxId + 1);
    }
}
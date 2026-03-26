package com.inventory.service;

import com.inventory.dao.interfaces.CategoryDAO;
import com.inventory.dao.impl.CategoryDAOImpl;
import com.inventory.exception.ValidationException;
import com.inventory.model.Category;

import java.util.List;
import java.util.Optional;


public class CategoryService {

   
    private CategoryDAO categoryDAO = new CategoryDAOImpl();

    
    public void addCategory(String name, String description) {

        
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Category name cannot be empty.");
        }

       
        List<Category> existing = categoryDAO.findAll();
        for (Category c : existing) {
            if (c.getName().equalsIgnoreCase(name.trim())) {
                throw new ValidationException(
                    "A category with the name '" + name + "' already exists.");
            }
        }

       
        String id = categoryDAO.generateNextId(); 

        
        Category category = new Category(id, name.trim(), description);
        categoryDAO.save(category);
    }

   
    public void updateCategory(String id, String newName, String newDescription) {

        
        Optional<Category> existing = categoryDAO.findById(id);
        if (!existing.isPresent()) {
            throw new ValidationException("Category with ID '" + id + "' not found.");
        }

        
        if (newName == null || newName.trim().isEmpty()) {
            throw new ValidationException("Category name cannot be empty.");
        }

        
        List<Category> all = categoryDAO.findAll();
        for (Category c : all) {
            
            if (c.getId().equals(id)) continue;

            if (c.getName().equalsIgnoreCase(newName.trim())) {
                throw new ValidationException(
                    "Another category with the name '" + newName + "' already exists.");
            }
        }

        
        Category updated = new Category(id, newName.trim(), newDescription);
        categoryDAO.update(updated);
    }

   
    public void deleteCategory(String id) {

        
        if (!categoryDAO.findById(id).isPresent()) {
            throw new ValidationException("Category with ID '" + id + "' not found.");
        }

        
        if (categoryDAO.isUsedByProduct(id)) {
            throw new ValidationException(
                "Cannot delete this category because products are assigned to it. " +
                "Please reassign or delete those products first.");
        }

        
        categoryDAO.delete(id);
    }

    
    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

   
    public Optional<Category> getCategoryById(String id) {
        return categoryDAO.findById(id);
    }
}
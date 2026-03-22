package com.inventory.dao.impl;

import com.inventory.dao.interfaces.ProductDAO;
import com.inventory.model.Product;
import com.inventory.util.FileManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class ProductDAOImpl implements ProductDAO {

    private final String filePath = FileManager.PRODUCTS_FILE;

    private List<Product> loadAll() {
        return FileManager.loadData(filePath);
    }

    @Override
    public void save(Product product) {
        List<Product> list = loadAll();
        list.add(product);
        FileManager.saveData(list, filePath);
    }

    @Override
    public void update(Product product) {
        List<Product> list = loadAll();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(product.getId())) {
                list.set(i, product);
                break;
            }
        }
        FileManager.saveData(list, filePath);
    }

    @Override
    public void delete(String id) {
        List<Product> list = loadAll();
        list.removeIf(p -> p.getId().equals(id));
        FileManager.saveData(list, filePath);
    }

    @Override
    public Optional<Product> findById(String id) {
        return loadAll().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Product> findAll() {
        return loadAll();
    }

    
    @Override
    public List<Product> findByCategoryId(String categoryId) {
        return loadAll().stream()
                .filter(p -> p.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    
    @Override
    public List<Product> findLowStock() {
        return loadAll().stream()
                .filter(Product::isLowStock)
                .collect(Collectors.toList());
    }

    
    @Override
    public List<Product> searchByName(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return loadAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    
    @Override
    public String generateNextId() {
        List<Product> list = loadAll();
        if (list.isEmpty()) return "PRD001";

        int maxId = list.stream()
                .mapToInt(p -> Integer.parseInt(p.getId().substring(3)))
                .max()
                .orElse(0);

        return String.format("PRD%03d", maxId + 1);
    }
}
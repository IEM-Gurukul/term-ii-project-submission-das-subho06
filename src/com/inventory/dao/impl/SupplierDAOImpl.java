package com.inventory.dao.impl;

import com.inventory.dao.interfaces.SupplierDAO;
import com.inventory.model.Supplier;
import com.inventory.util.FileManager;

import java.util.List;
import java.util.Optional;


public class SupplierDAOImpl implements SupplierDAO {

    private final String filePath = FileManager.SUPPLIERS_FILE;

    private List<Supplier> loadAll() {
        return FileManager.loadData(filePath);
    }

    @Override
    public void save(Supplier supplier) {
        List<Supplier> list = loadAll();
        list.add(supplier);
        FileManager.saveData(list, filePath);
    }

    @Override
    public void update(Supplier supplier) {
        List<Supplier> list = loadAll();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(supplier.getId())) {
                list.set(i, supplier);
                break;
            }
        }
        FileManager.saveData(list, filePath);
    }

    @Override
    public void delete(String id) {
        List<Supplier> list = loadAll();
        list.removeIf(s -> s.getId().equals(id));
        FileManager.saveData(list, filePath);
    }

    @Override
    public Optional<Supplier> findById(String id) {
        return loadAll().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Supplier> findAll() {
        return loadAll();
    }

    @Override
    public boolean isUsedByProduct(String supplierId) {
        List<com.inventory.model.Product> products =
                FileManager.loadData(FileManager.PRODUCTS_FILE);
        return products.stream()
                .anyMatch(p -> p.getSupplierId().equals(supplierId));
    }

    @Override
    public String generateNextId() {
        List<Supplier> list = loadAll();
        if (list.isEmpty()) return "SUP001";

        int maxId = list.stream()
                .mapToInt(s -> Integer.parseInt(s.getId().substring(3)))
                .max()
                .orElse(0);

        return String.format("SUP%03d", maxId + 1);
    }
}
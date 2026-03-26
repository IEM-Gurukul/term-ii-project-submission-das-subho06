package com.inventory.service;

import com.inventory.dao.interfaces.SupplierDAO;
import com.inventory.dao.impl.SupplierDAOImpl;
import com.inventory.exception.ValidationException;
import com.inventory.model.Supplier;

import java.util.List;
import java.util.Optional;


public class SupplierService {

    private SupplierDAO supplierDAO = new SupplierDAOImpl();

    
    public void addSupplier(String name, String contactName,
                            String phone, String email, String address) {

        
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Supplier name cannot be empty.");
        }

        
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("Supplier phone number cannot be empty.");
        }

        
        List<Supplier> existing = supplierDAO.findAll();
        for (Supplier s : existing) {
            if (s.getName().equalsIgnoreCase(name.trim())) {
                throw new ValidationException(
                    "A supplier with the name '" + name + "' already exists.");
            }
        }

        
        String id = supplierDAO.generateNextId();
        Supplier supplier = new Supplier(
            id, name.trim(), contactName, phone.trim(), email, address
        );
        supplierDAO.save(supplier);
    }

    
    public void updateSupplier(String id, String name, String contactName,
                               String phone, String email, String address) {

        if (!supplierDAO.findById(id).isPresent()) {
            throw new ValidationException("Supplier with ID '" + id + "' not found.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Supplier name cannot be empty.");
        }

        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("Supplier phone number cannot be empty.");
        }

        
        List<Supplier> all = supplierDAO.findAll();
        for (Supplier s : all) {
            if (s.getId().equals(id)) continue; // skip self
            if (s.getName().equalsIgnoreCase(name.trim())) {
                throw new ValidationException(
                    "Another supplier with the name '" + name + "' already exists.");
            }
        }

        Supplier updated = new Supplier(
            id, name.trim(), contactName, phone.trim(), email, address
        );
        supplierDAO.update(updated);
    }

    

    public void deleteSupplier(String id) {

        if (!supplierDAO.findById(id).isPresent()) {
            throw new ValidationException("Supplier with ID '" + id + "' not found.");
        }

        // Cannot delete if products still reference this supplier
        if (supplierDAO.isUsedByProduct(id)) {
            throw new ValidationException(
                "Cannot delete this supplier because products are linked to it. " +
                "Please reassign or delete those products first.");
        }

        supplierDAO.delete(id);
    }

    

    public List<Supplier> getAllSuppliers() {
        return supplierDAO.findAll();
    }

    public Optional<Supplier> getSupplierById(String id) {
        return supplierDAO.findById(id);
    }
}
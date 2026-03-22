package com.inventory.dao.interfaces;

import com.inventory.model.Supplier;
import java.util.List;
import java.util.Optional;


public interface SupplierDAO {

    void save(Supplier supplier);

    void update(Supplier supplier);

    void delete(String id);

    Optional<Supplier> findById(String id);

    List<Supplier> findAll();

    
    boolean isUsedByProduct(String supplierId);

    
    String generateNextId();
}
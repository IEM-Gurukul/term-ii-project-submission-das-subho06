package com.inventory.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


public class FileManager {

    

    public static final String DATA_FOLDER    = "data";
    public static final String PRODUCTS_FILE  = "data/products.dat";
    public static final String CATEGORIES_FILE = "data/categories.dat";
    public static final String SUPPLIERS_FILE  = "data/suppliers.dat";

   
    public static void ensureDataFolderExists() {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs(); // creates the "data/" folder
        }
    }

    
    public static <T> void saveData(List<T> list, String filePath) {

        
        ensureDataFolderExists();

        
        try (
            FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            
            oos.writeObject(list);

        } catch (IOException e) {
            
            throw new RuntimeException("Error saving data to file: " + filePath
                    + "\nReason: " + e.getMessage());
        }
    }

    
    @SuppressWarnings("unchecked")
    public static <T> List<T> loadData(String filePath) {

        File file = new File(filePath);

        
        if (!file.exists()) {
            return new ArrayList<>();
        }

        
        try (
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            
            return (List<T>) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
           
            throw new RuntimeException("Error loading data from file: " + filePath
                    + "\nReason: " + e.getMessage());
        }
    }
}
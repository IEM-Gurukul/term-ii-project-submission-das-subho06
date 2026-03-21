package com.inventory.model;



import java.io.Serializable;  // The Serializable "marker interface" — more on this below


public class Category implements Serializable {

    
    private static final long serialVersionUID = 1L;

    

    private String id;           // Unique identifier, e.g. "CAT001"
    private String name;         // Display name, e.g. "Electronics"
    private String description;  // Optional longer text

    
    public Category() {
        // empty — Java serialization fills the fields itself
    }

    
    public Category(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    
    public String getId() {
        return id;  // "return" sends this value back to whoever called the method
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    

    @Override
    public String toString() {
        return name;  // e.g. "Electronics"
    }
}
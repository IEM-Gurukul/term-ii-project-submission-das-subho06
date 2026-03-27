package com.inventory.model;

import java.io.Serializable;


public class Supplier implements Serializable {

    private static final long serialVersionUID = 1L;

    
    private String id;           // e.g. "SUP001"
    private String name;         // Company name, e.g. "Samsung Ltd."
    private String contactName;  // Person to contact, e.g. "John Smith"
    private String phone;        // e.g. "9876543210"
    private String email;        // e.g. "john@samsung.com"
    private String address;      // Physical address

    

    public Supplier() {
        // Required for deserialization
    }

    
    public Supplier(String id, String name, String contactName,
                    String phone, String email, String address) {
        this.id = id;
        this.name = name;
        this.contactName = contactName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    

    public String getId()          { return id; }
    public String getName()        { return name; }
    public String getContactName() { return contactName; }
    public String getPhone()       { return phone; }
    public String getEmail()       { return email; }
    public String getAddress()     { return address; }

    

    public void setId(String id)                   { this.id = id; }
    public void setName(String name)               { this.name = name; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public void setPhone(String phone)             { this.phone = phone; }
    public void setEmail(String email)             { this.email = email; }
    public void setAddress(String address)         { this.address = address; }

    

    @Override
    public String toString() {
        return name;  // e.g. "Samsung Ltd."
    }
}
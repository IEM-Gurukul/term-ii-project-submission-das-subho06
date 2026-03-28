[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/pG3gvzt-)
# PCCCS495 – Term II Project


## Project Title
Inventory Management System

---

## Problem Statement (max 150 words)
Small businesses managing inventory through paper registers or spreadsheets
face problems like data loss, no search capability, and no alerts when stock
runs low. This project provides a Java desktop application where a shopkeeper
can digitally manage Products, Categories, and Suppliers in one place. It uses
the DAO (Data Access Object) design pattern to cleanly separate data access
from business logic, and persists all data to local binary files using Java
Serialization — no external database required. The Service layer validates
every input before saving, enforcing rules like positive prices, non-empty
names, and referential integrity between entities. Features include full CRUD,
low-stock alerts, search, filter, column sorting, and CSV export — making it a
practical and maintainable solution for small-scale inventory management.

---

## Target User
Small business owners and retail shopkeepers who need a lightweight desktop
tool to track product stock, manage supplier contacts, and get alerts when
items need restocking — without any internet connection or database setup.

---

## Core Features

- Full CRUD (Create, Read, Update, Delete) for Products, Categories, and Suppliers
- Search products by name, filter by category, sort by any column header
- Dashboard with live stats — total products, inventory value, and low-stock count
- Dedicated Low Stock Alert panel showing shortage per item with one-click restock
- Input validation with clear error messages (blank name, negative price, broken references)
- Export full inventory to a timestamped CSV file

---

## OOP Concepts Used

- **Abstraction:** `ProductDAO`, `CategoryDAO`, and `SupplierDAO` are interfaces that define what operations exist without exposing how data is stored. The UI and Service layers never touch file I/O directly — they only call the interface methods.

- **Inheritance:** `ValidationException extends RuntimeException`, inheriting all exception behaviour. All five UI panels (`DashboardPanel`, `ProductPanel`, `CategoryPanel`, `SupplierPanel`, `LowStockPanel`) extend `JPanel`. The three DAO implementation classes implement their respective DAO interfaces.

- **Polymorphism:** The Service layer holds a `ProductDAO` reference but points to `ProductDAOImpl` at runtime — the correct method executes without the caller knowing which implementation is used. A custom `DefaultTableCellRenderer` overrides `getTableCellRendererComponent()` across all panels to provide different row coloring behaviour from the same method signature.

- **Exception Handling:** A custom `ValidationException` is thrown by all Service methods when a business rule is violated (e.g. negative price, blank name, deleting a category still in use by products). The UI catches these and displays user-friendly `JOptionPane` error dialogs. A global `Thread.setDefaultUncaughtExceptionHandler` prevents silent crashes for any unexpected runtime error.

- **Collections / Threads:** `List<Product>`, `List<Category>`, and `List<Supplier>` are used throughout for storing and iterating data. Java Streams with `.filter()`, `.map()`, `.collect()`, and `.anyMatch()` handle search and low-stock queries. `SwingUtilities.invokeLater()` ensures all UI creation and updates happen on the Event Dispatch Thread (EDT), preventing race conditions.

---

## Proposed Architecture Description

The application follows a strict 4-layer architecture where each layer
communicates only with the layer directly below it:
```
UI Layer       →  MainFrame, DashboardPanel, ProductPanel,
                  CategoryPanel, SupplierPanel, LowStockPanel
     ↓ calls
Service Layer  →  ProductService, CategoryService, SupplierService
                  (input validation + business rules)
     ↓ calls via interfaces
DAO Layer      →  Interfaces : ProductDAO, CategoryDAO, SupplierDAO
                  Impls      : ProductDAOImpl, CategoryDAOImpl, SupplierDAOImpl
     ↓ uses
File System    →  FileManager → products.dat, categories.dat, suppliers.dat
```

The Model classes (`Product`, `Category`, `Supplier`) implement `Serializable`
and are shared across all layers as plain data objects. The DAO interfaces act
as contracts — if the storage mechanism were changed from file serialization to
a relational database in future, only the `impl/` classes would need to be
rewritten. Everything else remains unchanged. This demonstrates the
**maintainability** and **extensibility** benefits of the DAO pattern.

---

## How to Run

1. Clone the repository and open in **IntelliJ IDEA**
2. Set **Java 17+** as the Project SDK (File → Project Structure → SDK)
3. Right-click `src/com/inventory/Main.java` → **Run 'Main.main()'**
4. The `/data` folder is created automatically on first run — no setup needed
5. Exported CSV files appear in the `/exports` folder after using the Export button

No external libraries or dependencies required — uses Java Standard Library only.

---



## Git Discipline Notes
Minimum 10 meaningful commits required.

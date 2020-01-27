
package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.Nullable;

public class Inventory {
    
    //observable list fields
    private static ObservableList<Part> allParts = FXCollections.observableArrayList();
    private static ObservableList<Part> filteredParts = FXCollections.observableArrayList();
    private static ObservableList<Product> allProducts = FXCollections.observableArrayList();
    private static ObservableList<Product> filteredProducts = FXCollections.observableArrayList();
    
    //variable to auto generate inventory numbers
    public static int inventoryNumber;

    //Class Methods
    
    //auto generates a new inventory number for every created part/product
    public static int addInventory() {
        return inventoryNumber++;
    }
    
    public static ObservableList<Part> getAllFilteredParts() {
        return filteredParts;
    }
    
    public static ObservableList<Product> getFilteredProducts() {
        return filteredProducts;
    }
    
    public static void addPart(Part part) {
        allParts.add(part);
    }
    
    public static void addProduct(Product product) {
        allProducts.add(product);
    }

    @Nullable
    public static Part lookupPart(int partId) {
        for (Part part : Inventory.getAllParts()) {
            if (part.getId() == partId)
                return part;
        }
        
        return null;
        
    }
    
    @Nullable
    public static Product lookupProduct(int productId) {
        for (Product product : Inventory.getAllProducts()) {
            if (product.getId() == productId)
                return product;
        }
        
        return null;
        
    }
    
    public static ObservableList<Part> lookupPart(String partName) {
        if (!(filteredParts.isEmpty()))
            filteredParts.clear();
        
        for (Part part : allParts) {
            if (part.getName().contains(partName))
                filteredParts.add(part);
        }
        
        return filteredParts;
        
    }
    
    public static ObservableList<Product> lookupProduct(String productName) {
        
        if (!(filteredProducts.isEmpty()))
            filteredProducts.clear();
        
        for (Product product : allProducts) {
                        
            if (product.getName().contains(productName))
                filteredProducts.add(product);
            
        }
        
        return filteredProducts;
        
    }
    
    public static void deletePart (Part selectedPart) {
        getAllParts().remove(selectedPart);
    }
    
    public static void deleteProduct (Product selectedProduct) {
        getAllProducts().remove(selectedProduct);
    }

    public static ObservableList<Part> getAllParts() {
        return allParts;
    }
    public static ObservableList<Product> getAllProducts() {
        return allProducts;
    }
}
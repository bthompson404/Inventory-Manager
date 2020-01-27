
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Inventory;
import model.Part;
import model.Product;

public class MainMenuController implements Initializable {

    //context switching variables
    Stage stage;
    Parent scene;
    
    public static Part selectedPart;
    public static Product selectedProduct;
    
    //context flags tell opened menus which version to display
    public static boolean showAddPartMenu = false;
    public static boolean showModifyPartMenu = false;
    public static boolean showAddProductMenu = false;
    public static boolean showModifyProductMenu = false;
    public static boolean searchPart = false;
    
    //variables for passing information between menus
    public static int selectedPartId;
    public static int selectedProductId;
    
    //input handlers
    @FXML
    private TextField partSearchTxt;

    @FXML
    private TableView<Part> partTableView;

    @FXML
    private TableColumn<Part, Integer> partIdCol;

    @FXML
    private TableColumn<Part, String> partNameCol;

    @FXML
    private TableColumn<Part, Integer> partInvCol;

    @FXML
    private TableColumn<Part, Double> partPriceCol;

    @FXML
    private TextField productSearchTxt;

    @FXML
    private TableView<Product> productTableView;

    @FXML
    private TableColumn<Product, Integer> productIdCol;

    @FXML
    private TableColumn<Product, String> productNameCol;

    @FXML
    private TableColumn<Product, Integer> productInvCol;

    @FXML
    private TableColumn<Product, Double> productPriceCol;
    
    //event handlers
    // opens add part menu
    @FXML
    void onActionAddPart(ActionEvent event) throws IOException {
        showAddPartMenu = true;
        
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/PartMenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    //opens add product menu
    @FXML
    void onActionAddProduct(ActionEvent event) throws IOException {
        showAddProductMenu = true;
        
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/ProductMenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    void onActionDeletePart(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this part?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            selectedPart = Inventory.lookupPart(partTableView.getSelectionModel().getSelectedItem().getId());   
            Inventory.deletePart(selectedPart);
            partTableView.setItems(Inventory.getAllParts());
        }
    }

    @FXML
    void onActionDeleteProduct(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this product?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            selectedProduct = Inventory.lookupProduct(productTableView.getSelectionModel().getSelectedItem().getId());
            Inventory.deleteProduct(selectedProduct);
            productTableView.setItems(Inventory.getAllProducts());
        }
    }

    @FXML
    void onActionExit(ActionEvent event) {
        System.exit(0);
    }

    //opens modify part menu and passes selected part information
    @FXML
    void onActionModifyPart(ActionEvent event) throws IOException {
        try {
            showModifyPartMenu = true;
            selectedPartId = partTableView.getSelectionModel().getSelectedItem().getId();

            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/PartMenu.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
        catch(NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialogue");
            alert.setContentText("Please select a part.");
            alert.showAndWait();
        }
    }
    
    //opens modify product menu and passes highlighted product information
    @FXML
    void onActionModifyProduct(ActionEvent event) throws IOException {
        try {
            showModifyProductMenu = true;
            selectedProductId = productTableView.getSelectionModel().getSelectedItem().getId();

            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/ProductMenu.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
        catch(NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialogue");
            alert.setContentText("Please select a product.");
            alert.showAndWait();
        }
    }

    //searches for a part based on name or ID number. ID number highlights found part. Name filters list down to match
    @FXML
    void onActionSearchPart(ActionEvent event) {
        partTableView.setItems(Inventory.getAllParts());
        searchPart = true;
        
        if (partSearchTxt.getText() == "")
            partTableView.setItems(Inventory.getAllParts());
        
        else
            try {
                Part search = Inventory.lookupPart(Integer.parseInt(partSearchTxt.getText()));
                partTableView.getSelectionModel().select(search);
            }
            catch (NumberFormatException e) {
                Inventory.lookupPart(partSearchTxt.getText());
                partTableView.setItems(Inventory.getAllFilteredParts());
            }
    }

    //same as former but for products
    @FXML
    void onActionSearchProduct(ActionEvent event) {
        productTableView.setItems(Inventory.getAllProducts());
        
        if (productSearchTxt.getText() == "")
            productTableView.setItems(Inventory.getAllProducts());
        else
            try {
                Product search = Inventory.lookupProduct(Integer.parseInt(productSearchTxt.getText()));
                productTableView.getSelectionModel().select(search);
            }
            catch (NumberFormatException e) {
                Inventory.lookupProduct(productSearchTxt.getText());
                productTableView.setItems(Inventory.getFilteredProducts());
            }
    }
    
    //method for resetting flags from context switching
    public static void resetFlags() {
        showAddPartMenu = false;
        showModifyPartMenu = false;
        showAddProductMenu = false;
        showModifyProductMenu = false;
        searchPart = false;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //create parts table
        partTableView.setItems(Inventory.getAllParts());
        partIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        partInvCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        //create products table
        productTableView.setItems(Inventory.getAllProducts());
        productIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        productInvCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    }
}
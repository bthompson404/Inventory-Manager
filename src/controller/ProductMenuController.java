
package controller;

import static controller.MainMenuController.selectedProduct;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Inventory;
import model.Part;
import model.Product;

public class ProductMenuController implements Initializable {
    
    //context switching variables
    Stage stage;
    Parent scene;
    
    //temporary observable list for storing associated parts for a new product before the object has been created and
    //creates a snapshot of an existing objects associated parts to restore if modify is cancelled
    private ObservableList<Part> tempAssociatedParts = FXCollections.observableArrayList();;
    
    @FXML
    private Label titleText;
    
    @FXML
    private TextField productIDTxt;

    @FXML
    private TextField productNameTxt;

    @FXML
    private TextField productUnitsAvailTxt;

    @FXML
    private TextField productUnitPriceTxt;

    @FXML
    private TextField productMaxUnitsTxt;

    @FXML
    private TextField productMinUnitsTxt;

    @FXML
    private TextField partSearchTxt;

    @FXML
    private TableView<Part> partTableView;

    @FXML
    private TableColumn<Part, Integer> partIdCol;

    @FXML
    private TableColumn<Part, String> partNameCol;

    @FXML
    private TableColumn<Part, Integer> partStockCol;

    @FXML
    private TableColumn<Part, Double> partPriceCol;

    @FXML
    private TableView<Part> associatedPartTableView;

    @FXML
    private TableColumn<Part, Integer> associatedPartIdCol;

    @FXML
    private TableColumn<Part, String> associatedPartNameCol;

    @FXML
    private TableColumn<Part, Integer> associatedPartStockCol;

    @FXML
    private TableColumn<Part, Double> associatedPartPriceCol;

    // Button Handlers
    //creates or modifies a part when the save button is pressed
    @FXML
    void onActionAddPart(ActionEvent event) {
        if (MainMenuController.showModifyProductMenu) {
            selectedProduct.addAssociatedPart(Inventory.lookupPart(partTableView.getSelectionModel().getSelectedItem().getId()));
            associatedPartTableView.setItems(selectedProduct.getAllAssociatedParts());
        }
        else {
            tempAssociatedParts.add(Inventory.lookupPart(partTableView.getSelectionModel().getSelectedItem().getId()));
            associatedPartTableView.setItems(tempAssociatedParts);
        }
    }

    //returns to the main menu when the cancel button is pressed. throws a confirmation that unsaved data will be lost
    @FXML
    void onActionDisplayMainMenu(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "All unsaved data will be lost. Continue?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            if (MainMenuController.showModifyProductMenu) {
                //replaces changes made with objects original list
                selectedProduct.setAssociatedParts(tempAssociatedParts);
            }
            
            MainMenuController.resetFlags();

            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }

    //removes an associated part from the current product
    @FXML
    void onActionRemovePart(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove this part?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            if (MainMenuController.showModifyProductMenu) {
                Part selectedPart = Inventory.lookupPart(associatedPartTableView.getSelectionModel().getSelectedItem().getId());
                selectedProduct.deleteAssociatedPart(selectedPart);
                associatedPartTableView.setItems(selectedProduct.getAllAssociatedParts());
            }
            else {
                Part selectedPart = Inventory.lookupPart(associatedPartTableView.getSelectionModel().getSelectedItem().getId());
                tempAssociatedParts.remove(selectedPart);
                associatedPartTableView.setItems(tempAssociatedParts);
            }
        }
    }

    @FXML
    void onActionSaveProduct(ActionEvent event) throws IOException {
        //branch if adding product
        if (MainMenuController.showAddProductMenu) {
            int id = Inventory.addInventory();
            String name = productNameTxt.getText();
            double price = Double.parseDouble(productUnitPriceTxt.getText());
            int stock = Integer.parseInt(productUnitsAvailTxt.getText());
            int min = Integer.parseInt(productMinUnitsTxt.getText());
            int max = Integer.parseInt(productMaxUnitsTxt.getText());

            //checks to ensure stock amount is within correct bounds
            if (!(stock <= max && stock >= min)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Stock amount out of bounds!");
                alert.setContentText("Enter stock between min and max.");
                alert.showAndWait();
            }
            else {
                Inventory.addProduct(new Product(id, name, price, stock, min, max));
                Product newProduct = Inventory.lookupProduct(id);

                for (Part part : tempAssociatedParts) {
                    newProduct.addAssociatedPart(part);
                }
            }
        }
        
        //branch if modifying product
        else if (MainMenuController.showModifyProductMenu) {
            String name = productNameTxt.getText();
            double price = Double.parseDouble(productUnitPriceTxt.getText());
            int stock = Integer.parseInt(productUnitsAvailTxt.getText());
            int min = Integer.parseInt(productMinUnitsTxt.getText());
            int max = Integer.parseInt(productMaxUnitsTxt.getText());
            
            //checks to ensure stock amount is within correct bounds
            if (!(stock <= max && stock >= min)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Stock amount out of bounds!");
                alert.setContentText("Enter stock between min and max.");
                alert.showAndWait();
            }
            else {
                selectedProduct.setName(name);
                selectedProduct.setPrice(price);
                selectedProduct.setStock(stock);
                selectedProduct.setMin(min);
                selectedProduct.setMax(max);
            }
        }
        
        //resets context switching flags from the main menu
        MainMenuController.resetFlags();
            
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    //searches for a part by name or ID. if no search parameters set, returns all parts
    @FXML
    void onActionSearchPart(ActionEvent event) {
        partTableView.setItems(Inventory.getAllParts());
        
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

    //Method to pull the selected product for modification from the main menu
    void setProduct(Product product) {
        productIDTxt.setText(Integer.toString(selectedProduct.getId()));
        productNameTxt.setText(selectedProduct.getName());
        productUnitsAvailTxt.setText(Integer.toString(selectedProduct.getStock()));
        productUnitPriceTxt.setText(Double.toString(selectedProduct.getPrice()));
        productMaxUnitsTxt.setText(Integer.toString(selectedProduct.getMax()));
        productMinUnitsTxt.setText(Integer.toString(selectedProduct.getMin()));
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //sets up modify products menu
        if (MainMenuController.showModifyProductMenu) {
            titleText.setText("Modify Product");
            
            selectedProduct= Inventory.lookupProduct(MainMenuController.selectedProductId);
            setProduct(selectedProduct);
            
            //creates a temporary associated parts list to use as a replacement in case of cancel
            this.tempAssociatedParts = FXCollections.observableArrayList(selectedProduct.getAllAssociatedParts());
            
            //associated parts table
            associatedPartTableView.setItems(selectedProduct.getAllAssociatedParts());
        }
            
        //disable the ability to manually create ID numbers
        productIDTxt.setDisable(true);
        
        //parts table
        partTableView.setItems(Inventory.getAllParts());
        
        partIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        partStockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        associatedPartIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        associatedPartNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        associatedPartStockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        associatedPartPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    }
}
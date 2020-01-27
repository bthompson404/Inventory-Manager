
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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.InHouse;
import model.Inventory;
import model.Outsourced;
import model.Part;

public class PartMenuController implements Initializable {
    
    //context switching variables
    Stage stage;
    Parent scene;
    
    //variables used for manipulating parts
    private Part selectedPart;
    private InHouse selectedInhouse;
    private Outsourced selectedOutsourced;

    //Input handlers
    @FXML
    private Label titleText;

    @FXML
    private RadioButton partInhouseRBtn;

    @FXML
    private RadioButton partOutsourcedRBtn;

    @FXML
    private TextField partIdTxt;

    @FXML
    private TextField partNameTxt;

    @FXML
    private TextField partUnitsAvailTxt;

    @FXML
    private TextField partUnitPriceTxt;

    @FXML
    private TextField partMaxUnitsTxt;

    @FXML
    private TextField partMinUnitsTxt;

    @FXML
    private Label variableLabel;

    @FXML
    private TextField variableField;

    @FXML
    private ToggleGroup partSourceToggle;
    
    //Button handlers
    @FXML
    void onActionTogglePartSource(ActionEvent event) {
        radioHandler();
    }
    
    //used when the cancel button is pressed. throws a confirmation box
    @FXML
    void onActionDisplayMainMenu(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "All unsaved data will be lost. Continue?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            MainMenuController.resetFlags();

            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }

    //save button creates a new part object or modifies an existing one based on context
    @FXML
    void onActionCreatePart(ActionEvent event) throws IOException {
        try {
            //verifies and adds an In House part
            if(MainMenuController.showAddPartMenu && partInhouseRBtn.isSelected()){
                int id = Inventory.addInventory();
                String name = partNameTxt.getText();
                int stock = Integer.parseInt(partUnitsAvailTxt.getText());
                double price = Double.parseDouble(partUnitPriceTxt.getText());
                int max = Integer.parseInt(partMaxUnitsTxt.getText());
                int min = Integer.parseInt(partMinUnitsTxt.getText());
                int machineID = Integer.parseInt(variableField.getText());
                
                //checks to ensure stock amount is within correct bounds
                if (!(stock <= max && stock >= min)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Stock amount out of bounds!");
                    alert.setContentText("Please enter a stock amount between the min and max.");
                    alert.showAndWait();
                }
                else {
                    Inventory.addPart(new InHouse(id, name, price, stock, min, max, machineID));
                }
            }
        
            //verifies and adds an Outsourced part
            else if (MainMenuController.showAddPartMenu && partOutsourcedRBtn.isSelected()) {
                int id = Inventory.addInventory();
                String name = partNameTxt.getText();
                int stock = Integer.parseInt(partUnitsAvailTxt.getText());
                double price = Double.parseDouble(partUnitPriceTxt.getText());
                int max = Integer.parseInt(partMaxUnitsTxt.getText());
                int min = Integer.parseInt(partMinUnitsTxt.getText());
                String companyName = variableField.getText();

                ////checks to ensure stock amount is within correct bounds
                if (!(stock <= max && stock >= min)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Stock amount out of bounds!");
                    alert.setContentText("Enter stock between min and max.");
                    alert.showAndWait();
                }
                else {
                    Inventory.addPart(new Outsourced(id, name, price, stock, min, max, companyName));
                }
            }
            //default if the add part flag is not thrown. checks for In House or Outsourced and modifies accordingly
            else {
                String name = partNameTxt.getText();
                int stock = Integer.parseInt(partUnitsAvailTxt.getText());
                double price = Double.parseDouble(partUnitPriceTxt.getText());
                int max = Integer.parseInt(partMaxUnitsTxt.getText());
                int min = Integer.parseInt(partMinUnitsTxt.getText());

                //checks for inhouse part and modifies
                if (partInhouseRBtn.isSelected()) {
                    //checks to ensure stock amount is within correct bounds
                    if (!(stock <= max && stock >= min)) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Stock amount out of bounds!");
                        alert.setContentText("Enter stock between min and max.");
                        alert.showAndWait();
                    }
                    else {
                        int machineID = Integer.parseInt(variableField.getText());

                        selectedInhouse.setName(name);
                        selectedInhouse.setStock(stock);
                        selectedInhouse.setPrice(price);
                        selectedInhouse.setMax(max);
                        selectedInhouse.setMin(min);
                        selectedInhouse.setMachineId(machineID);
                    }
                }
                //default to outsource part if not inhouse
                else {
                    //checks to ensure stock amount is within correct bounds
                    if (!(stock <= max && stock >= min)) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Stock amount out of bounds!");
                        alert.setContentText("Enter stock between min and max.");
                        alert.showAndWait();
                    }
                    else {
                        String companyName = variableField.getText();
                        selectedOutsourced.setName(name);
                        selectedOutsourced.setStock(stock);
                        selectedOutsourced.setPrice(price);
                        selectedOutsourced.setMax(max);
                        selectedOutsourced.setMin(min);
                        selectedOutsourced.setCompanyName(companyName);
                    }
                }
            }
        }
        //catch for invalid input format
        catch(NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialogue");
            alert.setContentText("Please enter valid inputs.");
            alert.showAndWait();
        }
        
        //resets context switching flags from the main menu
        MainMenuController.resetFlags();
            
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }
    
    //Method to pull the selected part for modification from the main menu
    void setPart(Part part) {
        partIdTxt.setText(Integer.toString(selectedPart.getId()));
        partNameTxt.setText(selectedPart.getName());
        partUnitsAvailTxt.setText(Integer.toString(selectedPart.getStock()));
        partUnitPriceTxt.setText(Double.toString(selectedPart.getPrice()));
        partMaxUnitsTxt.setText(Integer.toString(selectedPart.getMax()));
        partMinUnitsTxt.setText(Integer.toString(selectedPart.getMin()));
    }
    
    //changes text fields and labels depending on whether inhouse or outsourced radio button is selected
    public void radioHandler() {
         if (this.partSourceToggle.getSelectedToggle().equals(this.partInhouseRBtn)){
            variableLabel.setText("Machine ID");
            variableField.setPromptText("Machine ID");
         }
         
         if (this.partSourceToggle.getSelectedToggle().equals(this.partOutsourcedRBtn)){
            variableLabel.setText("Company Name");
            variableField.setPromptText("Company Name");
         }
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Set variable display parameters
        partInhouseRBtn.setSelected(true);
        
        /*checks flags to determine which variables to display. If modify part was
        * selected, pulls over and populates data of the selected part from the
        * main menu.
        */
        if (MainMenuController.showAddPartMenu) {
            titleText.setText("Add Part");
            variableLabel.setText("Machine ID");
        }
        else if (MainMenuController.showModifyPartMenu) {
            titleText.setText("Modify Part");
            variableLabel.setText("Machine ID");
            
            selectedPart= Inventory.lookupPart(MainMenuController.selectedPartId);
            setPart(selectedPart);
            
            if(selectedPart instanceof InHouse) {
                selectedInhouse = (InHouse) selectedPart;
                variableField.setText(Integer.toString(selectedInhouse.getMachineId()));
                partOutsourcedRBtn.setDisable(true);
            }
            else {
                partOutsourcedRBtn.setSelected(true);
                selectedOutsourced = (Outsourced) selectedPart;
                variableField.setText(selectedOutsourced.getCompanyName());
                partInhouseRBtn.setDisable(true);
            }
        }
        //disable the ability to manually create ID numbers
        partIdTxt.setDisable(true);
    }
}
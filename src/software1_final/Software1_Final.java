package software1_final;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Software1_Final extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        /* Testing Objects. uncomment to use
        
        //test parts objects
        InHouse part1 = new InHouse(1, "wheel", 49.99, 10, 5, 20, 5);
        InHouse part2 = new InHouse(2, "windshield", 149.99, 10, 5, 20, 5);
        
        //test products objects
        Product product1 = new Product(1, "car", 3999.99, 1, 1, 5);
        Product product2 = new Product(2, "bike", 199.99, 5, 5, 20);
        
        //add products to allProducts Observable List
        Inventory.addProduct(product1);
        Inventory.addProduct(product2);
        
        //add parts to allParts Observable List
        Inventory.addPart(part1);
        Inventory.addPart(part2);
        
        */

        launch(args);
    }
    
}

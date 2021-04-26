/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamemypart;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
        
/**
 *
 * @author Buton
 */
public class GameMyPart extends Application {
    
    @Override
    public void init() throws Exception {
        super.init();
        System.out.println("init");
    }
    
   private FXMLDocumentController controller;

    @Override
    public void start (Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Pane rootPane = loader.load();
        GridPane rootGridPane = (GridPane) rootPane.getChildren().get(0);

        controller = loader.getController();
        controller.createPlayground();

        Scene scene = new Scene(rootPane);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect4");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }
    
}





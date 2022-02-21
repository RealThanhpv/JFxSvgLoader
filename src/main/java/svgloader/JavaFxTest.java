/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package svgloader;

/**
 * @author Thanh
 */


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class JavaFxTest extends Application {


    public static void main(String[] args) {

        launch(args);

    }

    public void start(Stage primaryStage) {
        try {

            Application.Parameters params = getParameters();
            java.util.List<String> pl = params.getRaw();


            StackPane root = new StackPane();

            Scene scene = new Scene(root);
            scene.setFill(Color.GRAY);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Test SVGLoader Window");
//            SVGLoader loader = new SVGLoader("./tiger1.svg");


            Node svgPane = SVGLoader.loadSVG("./tiger1.svg");
            svgPane.setScaleX(1);
            svgPane.setScaleY(1);


            root.getChildren().addAll(svgPane);
            svgPane.setCache(true);
            primaryStage.show();



        } catch (Exception e) {
        }

    }
}
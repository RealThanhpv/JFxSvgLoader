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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
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


            StackPane pane = new StackPane();

            Parent root = new Pane(pane);
            Scene scene = new Scene(root);
            scene.setFill(Color.GRAY);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Test SVGLoader Window");
			SVGLoader loader = null;

            if (pl.isEmpty()) {
				loader = new SVGLoader("./tiger3.svg");
			}
            else {
				loader = new SVGLoader(pl.get(0));
			}
            long start = System.currentTimeMillis();
            Pane svgPane = loader.loadSVG();
            svgPane.setScaleX(1);
            svgPane.setScaleY(1);


            pane.getChildren().addAll(svgPane);
            svgPane.setCache(true);
            long end = System.currentTimeMillis();
            primaryStage.show();

            System.out.println("Total time: " + (end - start) + " mili secs");


        } catch (Exception e) {
        }

    }
}
package oop.lisp.gui;

import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.Application;
import oop.lisp.map.BoundedRectangularMap;
import oop.lisp.map.IWorldMap;
import oop.lisp.map.UnboundedRectangularMap;

public class App extends Application {

    private MapBuilder boundedBuilder, unboundedBuilder;
    private Scene initScene, mapScene;
    private Stage primaryStage;

    public void refreshMap(int mapID) {
        if (mapID == 0) boundedBuilder.refreshMap();
        else unboundedBuilder.refreshMap();
    }

    public void startSimulation(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, int startAnimalsNumber, double jungleRatio) {
        IWorldMap boundedMap = new BoundedRectangularMap(width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio, startAnimalsNumber);
        IWorldMap unboundedMap = new UnboundedRectangularMap(width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio, startAnimalsNumber);

        boundedBuilder = new MapBuilder(this, boundedMap);
        unboundedBuilder = new MapBuilder(this, unboundedMap);

        HBox maps = new HBox(500);
        maps.getChildren().addAll(boundedBuilder.getRoot(), unboundedBuilder.getRoot());

        mapScene = new Scene(maps);
        primaryStage.setScene(mapScene);
        primaryStage.setMaximized(true);

        boundedBuilder.startSimulation();
        unboundedBuilder.startSimulation();

    }


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Land of Lisp");

        InitWindowBuilder initBuilder = new InitWindowBuilder(this);
        initScene = new Scene(initBuilder.getRoot(), 500,600);
        primaryStage.setScene(initScene);

        primaryStage.show();

    }
}

package oop.lisp.gui;

import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.Application;
import oop.lisp.map.*;

public class App extends Application {

    private MapBuilder boundedBuilder, unboundedBuilder;
    private Stage primaryStage;

    public void refreshMap(int mapID) {
        if (mapID == 0) boundedBuilder.refreshMap();
        else unboundedBuilder.refreshMap();
    }

    public void startSimulation(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, int startAnimalsNumber,
                                double jungleRatio, int moveDelay, boolean mgcBnd, boolean mgcUnb ) {
        IWorldMap boundedMap;
        if (!mgcBnd) {
            boundedMap = new BoundedMap(width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio, startAnimalsNumber);
        } else boundedMap = new BoundedMagicMap(width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio, startAnimalsNumber);

        IWorldMap unboundedMap;
        if (!mgcUnb) {
            unboundedMap = new UnboundedMap(width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio, startAnimalsNumber);
        } else unboundedMap = new UnboundedMagicMap(width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio, startAnimalsNumber);

        boundedBuilder = new MapBuilder(this, boundedMap, moveDelay, "Bounded Map");
        unboundedBuilder = new MapBuilder(this, unboundedMap, moveDelay, "Unbounded Map");

        HBox maps = new HBox(20);
        maps.getChildren().addAll(boundedBuilder.getRoot(), unboundedBuilder.getRoot());

        Scene mapScene = new Scene(maps);
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
        Scene initScene = new Scene(initBuilder.getRoot(), 500, 700);
        primaryStage.setScene(initScene);

        primaryStage.show();

    }
}

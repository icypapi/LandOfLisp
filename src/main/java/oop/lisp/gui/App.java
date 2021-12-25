package oop.lisp.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.Application;
import oop.lisp.engine.SimulationEngine;
import oop.lisp.map.BoundedRectangularMap;
import oop.lisp.map.IWorldMap;
import oop.lisp.map.UnboundedRectangularMap;

public class App extends Application {

    private MapBuilder boundedBuilder, unboundedBuilder;
    Scene initScene, mapScene;
    Stage primaryStage;
    Thread boundedEngineThread, unboundedEngineThread;

    public void refreshMap(int mapID) {
        if (mapID == 0) boundedBuilder.refreshMap();
        else unboundedBuilder.refreshMap();
    }

    public void startSimulation(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, int startAnimalsNumber, double jungleRatio) {
        IWorldMap boundedMap = new BoundedRectangularMap(width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio, startAnimalsNumber);
        IWorldMap unboundedMap = new UnboundedRectangularMap(width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio, startAnimalsNumber);

        SimulationEngine boundedEngine = new SimulationEngine(boundedMap, this);
        SimulationEngine unboundedEngine = new SimulationEngine(unboundedMap, this);
        boundedEngineThread = new Thread(boundedEngine);
        unboundedEngineThread = new Thread(unboundedEngine);

        boundedBuilder = new MapBuilder(this, boundedMap, boundedEngine);
        unboundedBuilder = new MapBuilder(this, unboundedMap, unboundedEngine);

        HBox maps = new HBox(500);
        maps.getChildren().addAll(boundedBuilder.getRoot(), unboundedBuilder.getRoot());
        maps.setAlignment(Pos.CENTER);

        mapScene = new Scene(maps);
        primaryStage.setScene(mapScene);
        primaryStage.setMaximized(true);

        boundedEngineThread.start();
        unboundedEngineThread.start();

    }


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Land of Lisp");

        initWindowBuilder initBuilder = new initWindowBuilder(this);
        initScene = new Scene(initBuilder.getRoot(), 500,600);
        primaryStage.setScene(initScene);

        primaryStage.show();

    }
}

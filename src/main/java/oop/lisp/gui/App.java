package oop.lisp.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.Application;
import oop.lisp.additional.Vector2d;
import oop.lisp.engine.SimulationEngine;
import oop.lisp.map.RectangularJungle;
import oop.lisp.mapelement.IMapElement;

public class App extends Application {
    public RectangularJungle map;
    private final GridPane grid = new GridPane();
    private SimulationEngine engine;
    private Thread engineThread;
    Button[][] buttons;

    private int width;
    private int height;
    private int startEnergy;
    private int moveEnergy;
    private int plantEnergy;
    private int startAnimalsNumber;
    private double jungleRatio;

    Scene initScene, sceneMap;
    Stage primaryStage;

    public void refreshMap() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                IMapElement elementAt = (IMapElement) map.objectAt(new Vector2d(i, j));
                if (elementAt != null)
                    buttons[i][j].setStyle("-fx-background-color: " + elementAt.toColor());
                else buttons[i][j].setStyle("-fx-background-color: #ebd834;");
            }
        }
    }


    public void setupGrid(){
        int width = map.getUpperRight().x + 1;
        int height = map.getUpperRight().y + 1;
        int bW = 10; //button Width

        for (int i = 0; i < height; i++) grid.getRowConstraints().add(new RowConstraints(bW));
        for (int i = 0; i < width; i++) grid.getColumnConstraints().add(new ColumnConstraints(bW));

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Button btn = new Button();
                btn.setPadding(new Insets(bW/2-8,bW/2,bW/2-8,bW/2)); // right, bottom - 8
                grid.add(btn, j, i);
                buttons[j][height-i-1] = btn;
            }
        }

        grid.setAlignment(Pos.CENTER);

    }

    public void setMapProps(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, int startAnimalsNumber, double jungleRatio) {
        this.width = width;
        this.height = height;
        this.jungleRatio = jungleRatio;
        this.startAnimalsNumber = startAnimalsNumber;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
    }

    public void startSimulation() {
        sceneMap = buildMapScene();
        primaryStage.setScene(sceneMap);
        engineThread.start();
    }

    public Scene buildMapScene() {
        map = new RectangularJungle(width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio, startAnimalsNumber);
        engine = new SimulationEngine(map, this);
        engineThread = new Thread(engine);
        buttons = new Button[width][height];

        setupGrid();
        return new Scene(grid, 1600, 1000);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Land of Lisp");

        initWindowBuilder initBuilder = new initWindowBuilder(this);
        initScene = new Scene(initBuilder.getRoot(), 500,500);
        primaryStage.setScene(initScene);

        primaryStage.show();

    }
}

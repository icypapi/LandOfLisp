package oop.lisp.gui;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import oop.lisp.additional.IPositionChangeObserver;
import javafx.application.Application;
import oop.lisp.additional.Vector2d;
import oop.lisp.engine.SimulationEngine;
import oop.lisp.map.RectangularJungle;
import oop.lisp.mapelement.Animal;
import oop.lisp.mapelement.IMapElement;

public class App extends Application {
    public RectangularJungle map;
    private final GridPane grid = new GridPane();
    private SimulationEngine engine;
    private Thread engineThread;
    Button[][] buttons;

    private int width = 40;
    private int height = 40;
    private int startEnergy = 150;
    private int moveEnergy = 1;
    private int plantEnergy = 20;
    private int startAnimalsNumber = 100;
    private double jungleRatio = 0.1;


    @Override
    public void init(){
        this.map = new RectangularJungle(width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio, startAnimalsNumber);
        this.engine = new SimulationEngine(map, this);
        this.engineThread = new Thread(engine);
        this.buttons = new Button[width][height];
    }

    public void refreshMap() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                IMapElement elementAt = map.objectAt(new Vector2d(i, j));
                if (elementAt != null)
                    buttons[i][j].setStyle("-fx-background-color: " + elementAt.toColor());
                else buttons[i][j].setStyle("-fx-background-color: #ebd834;");
            }
        }
    }


    public void setupGrid(){
        //grid.setGridLinesVisible(true);
        int width = map.getUpperRight().x + 1;
        int height = map.getUpperRight().y + 1;
        int bW = 20;
        for (int i = 0; i < height; i++){
            grid.getRowConstraints().add(new RowConstraints(bW));
        }
        for (int i = 0; i < width; i++){
            grid.getColumnConstraints().add(new ColumnConstraints(bW));
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Button btn = new Button();
                btn.setPadding(new Insets(bW/2-8,bW/2,bW/2-8,bW/2)); // right, bottom - 8
                grid.add(btn, j, i);
                buttons[j][height-i-1] = btn;
            }
        }

        //btn.setStyle("-fx-background-color: #ff0000;");

        grid.setAlignment(Pos.CENTER);

    }

    @Override
    public void start(Stage primaryStage) {

        setupGrid();

        Scene scene = new Scene(grid, 1600, 1000);

        primaryStage.setTitle("Zwierzaczki");
        primaryStage.setScene(scene);
        primaryStage.show();
        engineThread.start();
    }
}

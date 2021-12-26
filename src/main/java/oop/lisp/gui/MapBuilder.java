package oop.lisp.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import oop.lisp.additional.Vector2d;
import oop.lisp.engine.SimulationEngine;
import oop.lisp.map.BoundedRectangularMap;
import oop.lisp.map.IWorldMap;
import oop.lisp.mapelement.Animal;
import oop.lisp.mapelement.IMapElement;

public class MapBuilder {

    private final IWorldMap map;

    private final SimulationEngine engine;
    private final Thread engineThread;

    private boolean paused = false;
    private boolean animalPicked = false;

    Plot chart;
    private final GridPane grid = new GridPane();
    private Button[][] buttons;
    private final VBox root = new VBox(40);

    public MapBuilder(App app, IWorldMap map) {
        this.map = map;
        engine = new SimulationEngine(map, app);
        engineThread = new Thread(engine);

        setupGrid();

        Label mapLabel;
        if (map instanceof BoundedRectangularMap) {
            mapLabel = new Label("Bounded Map");
        } else mapLabel = new Label("Unbounded Map");

        Button pauseBtn = new Button("Pause");
        pauseBtn.setOnAction(e -> {
            if (!paused) {
                pauseBtn.setText("Resume");
                paused = true;
                engine.switchState();
                refreshMap();
            } else {
                pauseBtn.setText("Pause");
                paused = false;
                engine.switchState();
                synchronized (engine) {
                    engine.notify();
                }
            }
        });
        HBox labelBtn = new HBox(10);
        labelBtn.getChildren().addAll(mapLabel, pauseBtn);
        labelBtn.setAlignment(Pos.CENTER);

        chart = new Plot(map);

        root.getChildren().addAll(labelBtn, grid, chart.getChart());
        root.setAlignment(Pos.CENTER);
    }

    public void setupGrid(){
        int width = map.getWidth();
        int height = map.getHeight();
        buttons = new Button[width][height];
        int bW = 10; //button Width

        for (int i = 0; i < height; i++) grid.getRowConstraints().add(new RowConstraints(bW));
        for (int i = 0; i < width; i++) grid.getColumnConstraints().add(new ColumnConstraints(bW));

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Button btn = new Button();
                btn.setPadding(new Insets(bW/2-8,bW/2,bW/2-8,bW/2)); // right, bottom - 8
                grid.add(btn, j, i);
                buttons[j][height-i-1] = btn;
                // Adding button listener
                int finalI = j, finalJ = height-i-1;
                btn.setOnAction(e -> {
                    if (paused && !animalPicked) {
                        if (map.objectAt(new Vector2d(finalI, finalJ)) instanceof Animal) {
                            map.animalToWatch(new Vector2d(finalI, finalJ));
                            animalPicked = true;
                            refreshMap();
                        }
                    }
                });
            }
        }

        grid.setAlignment(Pos.CENTER);
    }

    public void refreshMap() {
        if (map.getPickedAnimal() == null || map.getPickedAnimal().isDead()) {
            animalPicked = false;
        }

        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                IMapElement elementAt = (IMapElement) map.objectAt(new Vector2d(i, j));
                if (elementAt != null)
                    buttons[i][j].setStyle("-fx-background-color: " + elementAt.toColor());
                else buttons[i][j].setStyle("-fx-background-color: #ebd834;");
            }
        }
        if (!paused) {
            chart.updatePlot();
            synchronized (engine) {
                engine.notify();
            }
        }
    }

    public void startSimulation() {
        engineThread.start();
    }

    public VBox getRoot() {
        return root;
    }
}

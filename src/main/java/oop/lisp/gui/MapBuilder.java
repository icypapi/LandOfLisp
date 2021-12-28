package oop.lisp.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import oop.lisp.additional.Vector2d;
import oop.lisp.engine.SimulationEngine;
import oop.lisp.map.IWorldMap;
import oop.lisp.mapelement.Animal;
import oop.lisp.mapelement.IMapElement;

public class MapBuilder {

    private final IWorldMap map;
    private final SimulationEngine engine;
    private final Thread engineThread;

    private boolean paused = false;
    private boolean animalPicked = false;

    private final Plot chart;
    private final Button pauseBtn, showDominants;
    private final VBox pickedInfo;
    private Text pickedChildren, diedIn;
    private final  Text dominant;
    private final GridPane grid = new GridPane();
    private Button[][] buttons;
    private final VBox root = new VBox(40);

    public MapBuilder(App app, IWorldMap map, int moveDelay, String label) {
        this.map = map;
        engine = new SimulationEngine(map, app, moveDelay);
        engineThread = new Thread(engine);

        setupGrid();

        Label mapLabel = new Label(label);
        mapLabel.setFont(Font.font(20));

        pauseBtn = new Button("Pause");
        pauseBtn.setOnAction(e -> pause());

        showDominants = new Button("Show dominants");
        showDominants.setVisible(false);
        showDominants.setOnAction(e -> {
            showDominants();
        });

        HBox labelBtn = new HBox(10);
        labelBtn.getChildren().addAll(mapLabel, pauseBtn, showDominants);
        labelBtn.setAlignment(Pos.CENTER);

        HBox chartDominant = new HBox(10);
        chart = new Plot(map);
        dominant = new Text("Dominant genotype:");
        dominant.setFont(Font.font(12));
        dominant.setWrappingWidth(200);
        chartDominant.getChildren().addAll(chart.getChart(), dominant);

        HBox mapAndPicked = new HBox(10);
        pickedInfo = new VBox(10);
        mapAndPicked.getChildren().addAll(grid, pickedInfo);

        root.getChildren().addAll(labelBtn, mapAndPicked, chartDominant);
        root.setAlignment(Pos.CENTER);
    }

    private void showDominants() {
        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                IMapElement elementAt = (IMapElement) map.objectAt(new Vector2d(i, j));
                if (elementAt != null) {
                    if (elementAt instanceof Animal && ((Animal) elementAt).getGenotype().equals(map.getDominant()))
                        buttons[i][j].setStyle("-fx-background-color: #FFFFFF");
                    else buttons[i][j].setStyle("-fx-background-color: " + elementAt.toColor());
                } else buttons[i][j].setStyle("-fx-background-color: #ebd834;");
            }
        }
    }

    private void pause() {
        if (!paused) {
            pauseBtn.setText("Resume");
            paused = true;
            engine.switchState();
            refreshMap();
            showDominants.setVisible(true);
        } else {
            showDominants.setVisible(false);
            pauseBtn.setText("Pause");
            paused = false;
            engine.switchState();
            synchronized (engine) {
                engine.notify();
            }
        }
    }

    public void setupGrid(){
        int width = map.getWidth();
        int height = map.getHeight();
        buttons = new Button[width][height];
        int bW = width > height ? 500 / width : 500 / height; //button Width

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
                    if (paused) {
                        if (map.objectAt(new Vector2d(finalI, finalJ)) instanceof Animal) {
                            map.animalToWatch(new Vector2d(finalI, finalJ));
                            setPickedInfo();
                            animalPicked = true;
                            refreshMap();
                        }
                    }
                });
            }
        }

        grid.setAlignment(Pos.CENTER);
    }

    private void setPickedInfo() {
        pickedInfo.getChildren().clear();
        Text pickedGenotype = new Text("Picked genes: \n" + map.getPickedAnimal().getGenotype().toString());
        pickedChildren = new Text("Children from now: 0");
        diedIn = new Text("Died in epoch: -");
        pickedGenotype.setFont(Font.font(15));
        pickedChildren.setFont(Font.font(15));
        diedIn.setFont(Font.font(15));
        pickedGenotype.setWrappingWidth(150);
        pickedInfo.getChildren().addAll(pickedGenotype, pickedChildren, diedIn);
    }

    public void refreshMap() {
        if (animalPicked && (map.getPickedAnimal() == null || map.getPickedAnimal().isDead())) {
            if (map.getPickedAnimal() != null && map.getPickedAnimal().isDead()) {
                diedIn.setText("Died in epoch: " + map.getEpoch());
            }
            animalPicked = false;
        } else if (animalPicked){
            if (map.getPickedAnimal() != null && map.getPickedAnimal().isDead()) {
                diedIn.setText("Died in epoch: " + map.getEpoch());
            }
            pickedChildren.setText("Children: " + map.getPickedAnimalChildren());
        }

        dominant.setText("Dominant genotype: " + map.getDominant());

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

package oop.lisp.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class initWindowBuilder {

    private int width;
    private int height;
    private int startEnergy;
    private int moveEnergy;
    private int plantEnergy;
    private int startAnimalsNumber;
    private double jungleRatio;

    VBox root = new VBox(40);
    App app;

    public initWindowBuilder(App app) {
        this.app = app;
        // Width Field
        HBox widthBox = new HBox(20);
        widthBox.setAlignment(Pos.CENTER);
        Label widthLabel = new Label("Map Width: ");
        TextField widthText = new TextField("40");
        widthBox.getChildren().addAll(widthLabel, widthText);

        // Height Field
        HBox heightBox = new HBox(20);
        heightBox.setAlignment(Pos.CENTER);
        Label heightLabel = new Label("Map Height: ");
        TextField heightText = new TextField("40");
        heightBox.getChildren().addAll(heightLabel, heightText);

        // Start Energy Field
        HBox startEnergyBox = new HBox(20);
        startEnergyBox.setAlignment(Pos.CENTER);
        Label startEnergyLabel = new Label("Start Energy: ");
        TextField startEnergyText = new TextField("150");
        startEnergyBox.getChildren().addAll(startEnergyLabel, startEnergyText);

        // Move Energy Field
        HBox moveEnergyBox = new HBox(20);
        moveEnergyBox.setAlignment(Pos.CENTER);
        Label moveEnergyLabel = new Label("Move Energy: ");
        TextField moveEnergyText = new TextField("1");
        moveEnergyBox.getChildren().addAll(moveEnergyLabel, moveEnergyText);

        // Plant Energy
        HBox plantEnergyBox = new HBox(20);
        plantEnergyBox.setAlignment(Pos.CENTER);
        Label plantEnergyLabel = new Label("Plant Energy: ");
        TextField plantEnergyText = new TextField("20");
        plantEnergyBox.getChildren().addAll(plantEnergyLabel, plantEnergyText);

        // Start Animals
        HBox startAnimalsBox = new HBox(20);
        startAnimalsBox.setAlignment(Pos.CENTER);
        Label startAnimalsLabel = new Label("Start Animals: ");
        TextField startAnimalsText = new TextField("100");
        startAnimalsBox.getChildren().addAll(startAnimalsLabel, startAnimalsText);

        // Jungle Ratio
        HBox jungleRatioBox = new HBox(20);
        jungleRatioBox.setAlignment(Pos.CENTER);
        Label jungleRatioLabel = new Label("Jungle Ratio(double (0, 1)): ");
        TextField jungleRatioText = new TextField("0.1");
        jungleRatioBox.getChildren().addAll(jungleRatioLabel, jungleRatioText);

        Button startBtn = new Button("Start");
        startBtn.setOnAction(e -> {
            width = Integer.parseInt(widthText.getText());
            height = Integer.parseInt(heightText.getText());
            startEnergy = Integer.parseInt(startEnergyText.getText());
            moveEnergy = Integer.parseInt(moveEnergyText.getText());
            plantEnergy = Integer.parseInt(plantEnergyText.getText());
            startAnimalsNumber = Integer.parseInt(startAnimalsText.getText());
            jungleRatio = Double.parseDouble(jungleRatioText.getText());
            app.setMapProps(width, height, startEnergy, moveEnergy, plantEnergy, startAnimalsNumber, jungleRatio);
            app.startSimulation();
        });

        root.getChildren().addAll(widthBox, heightBox, startEnergyBox, moveEnergyBox, plantEnergyBox, startAnimalsBox, jungleRatioBox, startBtn);
        root.setAlignment(Pos.CENTER);
    }

    public VBox getRoot() {
        return root;
    }
}

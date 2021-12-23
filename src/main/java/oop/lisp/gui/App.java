package oop.lisp.gui;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    @Override
    public void init(){
        this.map = new RectangularJungle(15, 15, 1000, 1, 5, 0.3, 10);
        this.engine = new SimulationEngine(map, this);
        this.engineThread = new Thread(engine);
    }

    public void positionChanged() {
        grid.setGridLinesVisible(false);
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();
        grid.getChildren().clear();
        grid.setGridLinesVisible(true);
        setupGrid();
    }


    public void setupGrid(){
        grid.setGridLinesVisible(true);
        Vector2d loverLeft = map.getLowerLeft();
        Vector2d upperRight= map.getUpperRight();

        int startColIdx = loverLeft.x;
        int startRowIdx=loverLeft.y;
        int endColIdx=upperRight.x;
        int endRowIdx=upperRight.y;

        Label label = new Label("y/x");
        grid.add(label,0,0, 1, 1);
        grid.getRowConstraints().add(new RowConstraints(40));
        grid.getColumnConstraints().add(new ColumnConstraints(40));
        GridPane.setHalignment(label, HPos.CENTER);

        for(int i = startColIdx; i <= endColIdx; i++) {
            label = new Label(i+"");
            GridPane.setHalignment(label, HPos.CENTER);
            grid.addColumn(i-startColIdx+1,label);
            grid.getColumnConstraints().add(new ColumnConstraints(40));
        }

        for(int i = endRowIdx; i >= startRowIdx; i--) {
            label = new Label(i+"");
            GridPane.setHalignment(label, HPos.CENTER);
            grid.addRow(endRowIdx - i + 1, label);
            grid.getRowConstraints().add(new RowConstraints(40));
        }

        for(int i = endRowIdx; i >= startRowIdx; i--) {
            for(int j = startColIdx; j <= endColIdx; j++) {
                Vector2d pos = new Vector2d(j, i);
                if (!this.map.isUnoccupied(pos) ) {
                    Button btn = new Button(map.objectAt(pos).toString());
                    grid.add(btn,j-startColIdx+1,endRowIdx-i+1,1,1);
                    GridPane.setHalignment(btn, HPos.CENTER);
                }
            }
        }
        grid.setAlignment(Pos.CENTER);
    }

    @Override
    public void start(Stage primaryStage) {

        setupGrid();

        Scene scene = new Scene(grid, 800, 800);

        primaryStage.setTitle("Zwierzaczki");
        primaryStage.setScene(scene);
        primaryStage.show();
        engineThread.start();
    }
}

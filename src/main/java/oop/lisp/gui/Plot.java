package oop.lisp.gui;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import oop.lisp.map.IWorldMap;

public class Plot {
    private final IWorldMap map;
    private final LineChart<Number, Number> lineChart;

    private final NumberAxis xAxis;
    private final NumberAxis yAxis;
    private final XYChart.Series<Number, Number> animalsAlive;
    private final XYChart.Series<Number, Number> grassOnMap;
    private final XYChart.Series<Number, Number> avgEnergy;
    private final XYChart.Series<Number, Number> avgLifeExpectancy;
    private final XYChart.Series<Number, Number> avgChildNum;

    public Plot(IWorldMap map) {
        this.map = map;
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);

        xAxis.setLabel("Epoch");
        yAxis.setLabel("Value");
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setCreateSymbols(false);

        xAxis.setAutoRanging(true);

        animalsAlive = new XYChart.Series<>();
        grassOnMap = new XYChart.Series<>();
        avgEnergy = new XYChart.Series<>();
        avgLifeExpectancy = new XYChart.Series<>();
        avgChildNum = new XYChart.Series<>();


        animalsAlive.setName("Animals Alive");
        grassOnMap.setName("Grass on map");
        avgEnergy.setName("Avg Energy");
        avgLifeExpectancy.setName("Avg Life Exp");
        avgChildNum.setName("Avg Num of Children");

        lineChart.getData().addAll(animalsAlive, grassOnMap, avgEnergy, avgLifeExpectancy, avgChildNum);
    }

    public LineChart<Number, Number> getChart() {
        return lineChart;
    }

    public void updatePlot() {
        int epoch = map.getEpoch();

        if (epoch < 50 || epoch % 5 == 0) {
            animalsAlive.getData().add(new XYChart.Data<>(epoch, map.getAnimalsAlive()));
            grassOnMap.getData().add(new XYChart.Data<>(epoch, map.getGrassOnMap()));
            avgEnergy.getData().add(new XYChart.Data<>(epoch, map.getAvgEnergy()));
            avgLifeExpectancy.getData().add(new XYChart.Data<>(epoch, map.getAvgLifeExpectancy()));
            avgChildNum.getData().add(new XYChart.Data<>(epoch, map.getAvgChildrenBorn()));
        }

    }
}

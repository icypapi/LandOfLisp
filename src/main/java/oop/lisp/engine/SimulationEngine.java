package oop.lisp.engine;

import oop.lisp.map.RectangularJungle;

public class SimulationEngine {
    private final RectangularJungle map;

    public SimulationEngine(int mapWidth, int mapHeight, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio, int startAnimals) {
        map = new RectangularJungle(mapWidth, mapHeight, startEnergy, moveEnergy, plantEnergy, jungleRatio, startAnimals);
    }

    public void run() {
        for (int i = 0; i < 100; i++) {
            map.day();
            System.out.println("--------------------");
        }
    }

}

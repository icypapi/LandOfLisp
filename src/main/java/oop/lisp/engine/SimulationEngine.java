package oop.lisp.engine;

import javafx.application.Platform;
import oop.lisp.gui.App;
import oop.lisp.map.BoundedRectangularMap;
import oop.lisp.map.IWorldMap;

public class SimulationEngine implements Runnable {
    private final IWorldMap map;
    private final App application;
    private final int mapID;

    public SimulationEngine(IWorldMap map, App application) {
        this.map = map;
        this.application = application;
        mapID = (map instanceof BoundedRectangularMap) ? 0 : 1;
    }

    public void run() {
        while (map.getAnimalsAlive() > 0) {
            map.day();

            Platform.runLater(() -> {
                application.refreshMap(mapID);
            });

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                System.out.println("Thread.sleep error: " + e);
            }

        }
    }
}

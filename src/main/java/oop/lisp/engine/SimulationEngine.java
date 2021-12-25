package oop.lisp.engine;

import javafx.application.Platform;
import oop.lisp.gui.App;
import oop.lisp.map.BoundedRectangularMap;
import oop.lisp.map.IWorldMap;

public class SimulationEngine implements Runnable {
    private final IWorldMap map;
    private final App application;
    private final int mapID;
    private boolean paused = false;

    public SimulationEngine(IWorldMap map, App application) {
        this.map = map;
        this.application = application;
        mapID = (map instanceof BoundedRectangularMap) ? 0 : 1;
    }

    public void run() {
        while (map.getAnimalsAlive() > 0) {

            if (paused) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            map.day();
            Platform.runLater(() -> {
                application.refreshMap(mapID);
            });

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                System.out.println("Thread.sleep error: " + e);
            }
        }
    }

    public void switchState() {
        if (paused) paused = false;
        else paused = true;
    }

}

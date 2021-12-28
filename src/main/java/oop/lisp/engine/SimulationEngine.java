package oop.lisp.engine;

import javafx.application.Platform;
import oop.lisp.gui.App;
import oop.lisp.map.BoundedMagicMap;
import oop.lisp.map.BoundedMap;
import oop.lisp.map.IWorldMap;

public class SimulationEngine implements Runnable {
    private final IWorldMap map;
    private final App application;
    private final int mapID, moveDelay;
    private boolean paused = false;


    public SimulationEngine(IWorldMap map, App application, int moveDelay) {
        this.map = map;
        this.application = application;
        this.moveDelay = moveDelay;
        mapID = (map instanceof BoundedMap || map instanceof BoundedMagicMap) ? 0 : 1;
    }

    public void run() {
        while (map.getAnimalsAlive() > 0) {
            // If paused stop going
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

            // Refresh map after full day passes
            Platform.runLater(() -> {
                application.refreshMap(mapID);
            });

            // Paused while GUI is drawing the map and plots, woken up by GUI when it's finished
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //Move Delay
            try {
                Thread.sleep(moveDelay);
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

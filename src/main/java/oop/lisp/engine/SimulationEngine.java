package oop.lisp.engine;

import javafx.application.Platform;
import oop.lisp.gui.App;
import oop.lisp.map.RectangularJungle;

public class SimulationEngine implements Runnable{
    private final RectangularJungle map;
    private final App application;

    public SimulationEngine(RectangularJungle map, App application) {
        this.map = map;
        this.application = application;
    }

    public void run() {
        while (map.animalsAlive > 0) {
            map.day();

            Platform.runLater(() -> {
                application.refreshMap();
            });

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                System.out.println("Thread.sleep error: " + e);
            }

        }
    }
}

package oop.lisp.engine;

import javafx.application.Platform;
import oop.lisp.gui.App;
import oop.lisp.map.RectangularJungle;
import oop.lisp.mapelement.Animal;

public class SimulationEngine implements Runnable{
    private final RectangularJungle map;
    private App application = null;

    public SimulationEngine(RectangularJungle map, App application) {
        this.map = map;
        this.application = application;
    }

    public void run() {
        while (map.animalsAlive > 0) {
            map.day();
            Platform.runLater(() -> {
                application.positionChanged();
            });
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.out.println("Thread.sleep error: " + e);
            }
        }

    }

}

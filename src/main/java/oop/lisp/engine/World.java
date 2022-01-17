package oop.lisp.engine;

import javafx.application.Application;
import oop.lisp.gui.App;

public class World {    // lepiej to wyciągnąć do pakietu piętro wyżej

    public static void main(String [] args) {
        try {
            Application.launch(App.class, args);
        } catch(IllegalArgumentException ex) {
            System.out.println("App launch : " + ex);
        } finally {
            System.out.println("Exit");
            System.exit(0);
        }
    }

}

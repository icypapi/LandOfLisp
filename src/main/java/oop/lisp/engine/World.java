package oop.lisp.engine;

import oop.lisp.additional.Vector2d;
import oop.lisp.map.RectangularJungle;
import oop.lisp.mapelement.Animal;

import java.util.Arrays;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class World {

    public static void main(String [] args) {
        SimulationEngine engine = new SimulationEngine(7, 7, 30, 5, 5, 1.0, 10);
        engine.run();
    }

}

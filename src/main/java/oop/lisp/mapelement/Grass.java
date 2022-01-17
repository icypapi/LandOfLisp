package oop.lisp.mapelement;

import oop.lisp.additional.Vector2d;

public class Grass implements IMapElement {
    private final Vector2d position;
    private final int plantEnergy;

    public Grass(Vector2d position, int plantEnergy) {
        this.position = position;
        this.plantEnergy = plantEnergy;
    }

    public String toColor() {   // lepiej by to przenieść gdzieś do GUI
        return "#00FF00;";
    }

    public int getPlantEnergy() {
        return plantEnergy;
    }

    public Vector2d getPosition() {
        return this.position;
    }

    @Override
    public String toString() {
        return "*";
    }

}

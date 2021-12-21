package oop.lisp.mapelement;

import oop.lisp.additional.Vector2d;

public class Grass {
    private final Vector2d position;

    public Grass(Vector2d position) {
        this.position = position;
    }

    public Vector2d getPosition() {
        return this.position;
    }

    @Override
    public String toString() {
        return "*";
    }

}

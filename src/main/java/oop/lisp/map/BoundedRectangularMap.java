package oop.lisp.map;

import oop.lisp.additional.Vector2d;

public class BoundedRectangularMap extends AbstractRectangularMap {

    public BoundedRectangularMap(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio, int startAnimalsNumber) {
        super(width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio, startAnimalsNumber);
    }

    @Override
    public Vector2d moveTo(Vector2d oldPosition, Vector2d position) {
        if (position.follows(mapLowerLeft) && position.precedes(mapUpperRight))
            return position;
        else return oldPosition;
    }
}

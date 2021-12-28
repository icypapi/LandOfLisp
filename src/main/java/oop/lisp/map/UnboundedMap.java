package oop.lisp.map;

import oop.lisp.additional.Vector2d;

public class UnboundedMap extends AbstractMap {

    public UnboundedMap(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio, int startAnimalsNumber) {
        super(width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio, startAnimalsNumber);
    }

    @Override
    public Vector2d moveTo(Vector2d oldPosition, Vector2d position) {
        if (position.follows(mapLowerLeft) && position.precedes(mapUpperRight))
            return position;
        else return new Vector2d((position.x + mapUpperRight.x) % mapUpperRight.x, (position.y + mapUpperRight.y) % mapUpperRight.y);
    }

}

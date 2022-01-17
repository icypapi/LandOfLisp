package oop.lisp.map;

import oop.lisp.additional.Vector2d;

public class BoundedMagicMap extends AbstractMap {  // każda nowa funkcjonalność nam mnoży liczbę klas, które musimy napisać

    public BoundedMagicMap(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio, int startAnimalsNumber) {
        super(width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio, startAnimalsNumber);
    }

    @Override
    public Vector2d moveTo(Vector2d oldPosition, Vector2d position) {
        if (position.follows(mapLowerLeft) && position.precedes(mapUpperRight))
            return position;
        else return oldPosition;
    }

    @Override
    public void day() {
        if (getAnimalsAlive() == 5 && getMagicNumber() < 3) {
            super.doMagic();
        }
        super.day();
    }


}

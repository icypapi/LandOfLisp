package oop.lisp.map;

import oop.lisp.additional.Vector2d;
import oop.lisp.mapelement.Animal;
import oop.lisp.mapelement.Grass;

public interface IWorldMap {

    void day();

    Object objectAt(Vector2d position);

    int getAnimalsAlive();

    Vector2d getUpperRight();

    Vector2d moveTo(Vector2d oldPosition, Vector2d newPosition);

}

package oop.lisp.map;

import oop.lisp.additional.Vector2d;
import oop.lisp.mapelement.Animal;
import oop.lisp.mapelement.Grass;

public interface IWorldMap {


    void placeAnimal(Vector2d position, Animal an);

    void placeGrass(Vector2d position, Grass gr);

    void day();

    void deleteDead();

    boolean isOccupied(Vector2d position);

    Object objectAt(Vector2d position);

    Vector2d getUpperRight();
}

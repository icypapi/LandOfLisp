package oop.lisp.map;

import oop.lisp.additional.Genotype;
import oop.lisp.additional.Vector2d;
import oop.lisp.mapelement.Animal;

public interface IWorldMap {

    void day();

    Object objectAt(Vector2d position);

    Vector2d moveTo(Vector2d oldPosition, Vector2d newPosition);

    int getAnimalsAlive();

    int getGrassOnMap();

    int getAvgEnergy();

    int getAvgLifeExpectancy();

    double getAvgChildrenBorn();

    int getWidth();

    int getHeight();

    int getEpoch();

    void animalToWatch(Vector2d position);

    Animal getPickedAnimal();

    int getPickedAnimalChildren();

    Genotype getDominant();

}

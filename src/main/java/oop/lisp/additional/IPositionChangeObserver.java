package oop.lisp.additional;

import oop.lisp.mapelement.Animal;

public interface IPositionChangeObserver {
    void positionChanged(Animal an, Vector2d oldPosition, Vector2d newPosition);    // jest sens przekazywać nową pozycję, jeśli przekazujemy całe zwierzę?
}

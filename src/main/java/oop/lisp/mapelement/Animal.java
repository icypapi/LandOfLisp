package oop.lisp.mapelement;

import oop.lisp.additional.*;
import oop.lisp.map.IWorldMap;

import java.util.ArrayList;

public class Animal implements IMapElement {
    private MapDirection direction;
    private Vector2d position;
    private final IWorldMap map;
    private int energy;
    public final int moveEnergy, startEnergy;
    private final Genotype genotype;
    private final ArrayList<IPositionChangeObserver> observers = new ArrayList<IPositionChangeObserver>();
    private int age = 0;
    private int childrenBorn = 0;
    private boolean watchingMe = false;

    // This constructor is used when we add 'random' initial Animal to the map
    public Animal(IWorldMap map, Vector2d initialPosition, int startEnergy, int moveEnergy) {
        this.map = map;
        this.position = initialPosition;
        this.direction = MapDirection.randomDirection();
        this.genotype = new Genotype();
        this.energy = startEnergy;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
    }

    // This one is used when the new Animal is born to its parents
    public Animal(IWorldMap map, Vector2d initialPosition, int startEnergy, int moveEnergy, Genotype genotype) {
        this.map = map;
        this.position = initialPosition;
        this.direction = MapDirection.randomDirection();
        this.energy = startEnergy;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.genotype = genotype;
    }

    public void move() {
        int moveID = genotype.getGene();

        if (moveID == 0 || moveID == 4) {
            Vector2d oldPosition = position;
            Vector2d newPosition = (moveID == 0) ? map.moveTo(oldPosition, position.add(direction.toUnitVector())) : map.moveTo(oldPosition, position.subtract(direction.toUnitVector()));
            position = newPosition;
            energy -= moveEnergy;
            if (!newPosition.equals(oldPosition)) positionChanged(oldPosition, newPosition);
        } else {
            for (int i = 0; i < moveID; i++) direction = direction.next();
            energy -= moveEnergy;
        }
        age++;
    }

    // Reproduces two animals, returns the child object
    public Animal reproduce(Animal mom) {
        Genotype childGenotype = genotype.getChildGenotype(mom.getGenotype(), (double) (energy) / (double)(energy + mom.getEnergy()));
        int childEnergy = giveOutEnergy() + mom.giveOutEnergy();
        childrenBorn();
        mom.childrenBorn();
        return new Animal(map, position, childEnergy, moveEnergy, childGenotype);
    }

    public void childrenBorn() {
        childrenBorn++;
    }

    // Makes animal eat a part of grass, sharing it with <divider> other animals
    public void eatGrass(Grass gr, int divider) {
        int energyToGain = gr.getPlantEnergy() / divider;
        energy += energyToGain;
    }

    // Used when the child is born to give it out 25% of the energy
    private int giveOutEnergy() {
        int energyToGive = (int) (energy * 0.25);
        energy -= energyToGive;
        return energyToGive;
    }

    public void setWatching() {
        watchingMe = true;
    }

    // Return true if animal is healthy enough to produce a child
    public boolean isHealthy() {
        return energy > startEnergy * 0.5;
    }

    // Animal is dead when its energy is smaller than moveEnergy because we move before we eat
    public boolean isDead() {
        return energy < moveEnergy;
    }

    @Override
    public String toString() {
        return direction.toString();
    }

    public String toColor() {
        if (watchingMe) return "#800080;";
        return "#ff0000;";
    }

    /* --- Getters Section --- */
    public Vector2d getPosition(){
        return this.position;
    }

    public MapDirection getDirection(){
        return this.direction;
    }

    public Genotype getGenotype() {
        return genotype;
    }

    public int getEnergy() {
        return energy;
    }

    public int getAge() {
        return age;
    }

    public int getChildrenBorn() {
        return childrenBorn;
    }

    /* --- Observers Section --- */
    public void addObserver(IPositionChangeObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer) {
        observers.remove(observer);
    }

    private void positionChanged(Vector2d oldPos, Vector2d newPos) {
        for(IPositionChangeObserver observer: observers) {
            observer.positionChanged(this, oldPos, newPos);
        }
    }

}

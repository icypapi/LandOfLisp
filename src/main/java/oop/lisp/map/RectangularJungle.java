package oop.lisp.map;

import oop.lisp.additional.IPositionChangeObserver;
import oop.lisp.additional.Vector2d;
import oop.lisp.mapelement.Animal;
import oop.lisp.mapelement.Grass;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeSet;

public class RectangularJungle implements IPositionChangeObserver {
    private final Vector2d loverLeft, upperRight;
    private final int width, height;
    private final int startEnergy, moveEnergy, plantEnergy;

    private final double jungleRatio;

    private final LinkedHashMap<Vector2d, Grass> grass = new LinkedHashMap<>();
    private final LinkedHashMap<Vector2d, TreeSet<Animal>> animals = new LinkedHashMap<>();
    private final LinkedList<Animal> animalsList = new LinkedList<>();
    private final LinkedList<Grass> grassList = new LinkedList<>();

    /* --- Stats --- */
    int animalsAlive = 0;
    int animalsDied = 0;
    int grassOnMap = 0;
    int grassEaten = 0;

    /* --- Comparator for TreeSet --- */
    private final Comparator<Animal> compare = (an1, an2) -> {
        if (an1.getEnergy() == an2.getEnergy()) return 1;
        if (an1.equals(an2)) return 0;
        return an1.getEnergy() - an2.getEnergy();
    };

    public RectangularJungle(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio, int startAnimalsNumber) {
        this.width = width;
        this.height = height;
        this.loverLeft = new Vector2d(0, 0);
        this.upperRight = new Vector2d(width - 1, height - 1);
        this.jungleRatio = jungleRatio;

        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        initAnimals(startAnimalsNumber);
    }

    // Puts the first animals on the map randomly
    private void initAnimals(int startAnimalsNumber) {
        int tooMuch;

        for(int i = 0; i < startAnimalsNumber; i++) {
            tooMuch = 0;
            while (tooMuch < 2 * width * height) {
                Vector2d position = new Vector2d( (int) (Math.random()*width), (int) (Math.random()*height) );
                if (getAnimalsAt(position) == null) {
                    place(new Animal(this, position, startEnergy, moveEnergy), position);
                    break;
                }
                tooMuch++;
            }
        }
    }

    // Places animal considering the position is correct
    private void place(Animal an, Vector2d position) {
        TreeSet<Animal> animalsAt = animals.get(position);
        an.addObserver(this);

        if (animalsAt == null) {
            animalsAt = new TreeSet<>(compare);
            animalsAt.add(an);
            animals.put(position, animalsAt);
            animalsList.add(an);
            return;
        }

        animalsAt.add(an);
        animalsList.add(an);

        animalsAlive += 1;
    }

    // After this method is completed one full day passes
    public void day() {
        deleteDead();
        moveAnimals();
        //eatGrass();
        //reproduce();
        //addGrass();
    }

    // Deletes dead animals from the map
    private void deleteDead() {
        LinkedList<Animal> animalsToRemove = new LinkedList<>();

        for (Animal an: animalsList) {
            if (an.isDead()) animalsToRemove.add(an);
        }

        for (Animal an: animalsToRemove) {
            animalsList.remove(an);
            animals.get(an.getPosition()).remove(an);
            animalsAlive--;
            animalsDied++;
            System.out.println("DIED");
        }
    }

    // Tells all alive animals to make a move
    private void moveAnimals() {
        for (Animal an: animalsList) an.move();
    }

    // Implement process of eating grass by animals
    private void eatGrass() {
        return;
    }

    // Implements reproduction of the two strongest animals on the same position
    private void reproduce() {
        //1 position - 1 child
        //method in Animal class for reproduction, returns child's object
        //this.place(child)
    }

    // Adds one grass to each zone of the map
    private void addGrass() {
        //if addGrassJungle() grassNum++
        //if addGrassSteppe() grassNum++
    }

    // Animal asks map about the position it should move to
    public Vector2d moveTo(Vector2d position) {
        if (position.follows(loverLeft) && position.precedes(upperRight))
            return position;
        else return new Vector2d((position.x + upperRight.x) % upperRight.x, (position.y + upperRight.y) % upperRight.y);
    }

    @Override
    public void positionChanged(Animal an, Vector2d oldPosition, Vector2d newPosition) {
        getAnimalsAt(oldPosition).remove(an);
        TreeSet<Animal> animalsAt = getAnimalsAt(newPosition);
        System.out.println("Position changed");
        if ( animalsAt == null ) {
            animalsAt = new TreeSet<>(compare);
            animalsAt.add(an);
            animals.put(newPosition, animalsAt);
            return;
        }

        animalsAt.add(an);

    }

    /* --- Getters Section --- */
    public TreeSet<Animal> getAnimalsAt(Vector2d position) {
        return animals.get(position);
    }

    public Grass getGrassAt(Vector2d position) {
        return grass.get(position);
    }

    public Vector2d getLoverLeft() {
        return loverLeft;
    }

    public Vector2d getUpperRight() {
        return upperRight;
    }

}

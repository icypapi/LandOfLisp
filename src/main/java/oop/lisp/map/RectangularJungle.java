package oop.lisp.map;

import oop.lisp.additional.IPositionChangeObserver;
import oop.lisp.additional.Vector2d;
import oop.lisp.mapelement.Animal;
import oop.lisp.mapelement.Grass;

import java.util.*;

public class RectangularJungle implements IPositionChangeObserver {
    private final Vector2d mapLowerLeft, mapUpperRight;
    private Vector2d jungleLowerLeft, jungleUpperRight;
    private final int width, height;
    private int jungleWidth, jungleHeight;
    private final int startEnergy, moveEnergy, plantEnergy, startAnimalsNumber;

    private final double jungleRatio;

    private final LinkedHashMap<Vector2d, Grass> grass = new LinkedHashMap<>();
    private final LinkedHashMap<Vector2d, ArrayList<Animal>> animals = new LinkedHashMap<>();
    private final ArrayList<Animal> animalsList = new ArrayList<>();
    private final ArrayList<Grass> grassList = new ArrayList<>();

    /* --- Stats --- */
    public int epoch = 0;
    public int animalsAlive = 0;
    public int animalsDied = 0;
    public int animalsBorn = 0;
    public int grassOnMap = 0;
    public int grassEaten = 0;

    /* --- Comparator for sorting the animals ArrayList --- */
    private final Comparator<Animal> compare = (an1, an2) -> {
        if (an1.getEnergy() == an2.getEnergy()) return 0;
        return an1.getEnergy() - an2.getEnergy();
    };

    public RectangularJungle(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio, int startAnimalsNumber) {
        this.width = width;
        this.height = height;
        this.mapLowerLeft = new Vector2d(0, 0);
        this.mapUpperRight = new Vector2d(width - 1, height - 1);
        this.jungleRatio = jungleRatio;

        this.startAnimalsNumber = startAnimalsNumber;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;

        initAnimals();
        initJungle();
    }

    // Puts the first animals on the map randomly
    private void initAnimals() {
        int tooMuch;
        // if there's > 0 positions to place
        for(int i = 0; i < startAnimalsNumber; i++) {
            tooMuch = 0;
            while (tooMuch < 2 * width * height) {
                Vector2d position = new Vector2d( (int) (Math.random()*width), (int) (Math.random()*height) );
                if (!isOccupied(position)) {
                    placeAnimal(position, new Animal(this, position, startEnergy, moveEnergy));
                    break;
                }
                tooMuch++;
            }
        }
    }

    // Evaluates jungleProps and it's position on the map
    private void initJungle() {
        jungleHeight = (int) ((double)height * Math.sqrt( jungleRatio/(1.0+jungleRatio) ));
        jungleWidth = (int) ((double)(jungleHeight * width) / (double) height);

        int lljx = 0, lljy = 0;
        int urjx = width - 1, urjy = height - 1;

        for (int i = 0; i < (width - jungleWidth); i++) {
            if (i % 2 == 0) lljx++;
            else urjx--;
        }

        for (int i = 0; i < (height - jungleHeight); i++) {
            if (i % 2 == 0) lljy++;
            else urjy--;
        }

        jungleLowerLeft = new Vector2d(lljx, lljy);
        jungleUpperRight = new Vector2d(urjx, urjy);

    }

    // Places animal considering the position is correct
    private void placeAnimal(Vector2d position, Animal an) {
        ArrayList<Animal> animalsAt = getAnimalsAt(position);
        an.addObserver(this);
        animalsAlive++;

        if (animalsAt == null) {
            animalsAt = new ArrayList<>();
            animalsAt.add(an);
            animals.put(position, animalsAt);
            animalsList.add(an);
            return;
        }

        animalsAt.add(an);
        if (animalsAt.size() > 1) animalsAt.sort(compare);
        animalsList.add(an);

    }

    // Places grass considering the position is correct
    private void placeGrass(Vector2d position, Grass gr) {
        grassList.add(gr);
        grass.put(position, gr);
    }

    // Used for displaying elements on the map, returns the strongest animal on position, or grass, or null
    public synchronized Object objectAt(Vector2d position) {
        ArrayList<Animal> animalsAt = getAnimalsAt(position);

        if (animalsAt == null || animalsAt.size() == 0) {
            return getGrassAt(position);
        } else return animalsAt.get(0);

    }

    public synchronized boolean isOccupied(Vector2d position) {
        return objectAt(position) != null;
    }

    // After this method is completed one full day passes
    public void day() {
        deleteDead();
        moveAnimals();
        eatGrass();
        reproduce();
        addGrass();
        epoch++;
    }

    // Deletes dead animals from the map
    private void deleteDead() {
        ArrayList<Animal> animalsToRemove = new ArrayList<>();

        for (Animal an: animalsList) {
            if (an.isDead()) animalsToRemove.add(an);
        }

        for (Animal an: animalsToRemove) {
            animalsList.remove(an);
            animals.get(an.getPosition()).remove(an);
            an.removeObserver(this);
            animalsAlive--;
            animalsDied++;
        }
    }

    // Tells all alive animals to make a move
    private void moveAnimals() {
        for (Animal an: animalsList) an.move();
    }

    // Implements process of eating grass by animals
    private synchronized void eatGrass() {
        ArrayList<Grass> grassToRemoveFromList = new ArrayList<>();

        for(Grass gr : grassList) {
            ArrayList<Animal> animalsAt = getAnimalsAt(gr.getPosition());

            if (animalsAt != null && animalsAt.size() > 0) {
                if (animalsAt.size() > 1) {
                    int maxEnergyAt = animalsAt.get(0).getEnergy();
                    int i = 1;
                    while (animalsAt.size() > i && animalsAt.get(i).getEnergy() == maxEnergyAt) i++;
                    for (int j = 0; j < i; j++) animalsAt.get(j).eatGrass(gr, i);
                } else animalsAt.get(0).eatGrass(gr, 1);

                grassToRemoveFromList.add(gr);
                grass.remove(gr.getPosition());
                grassOnMap--;
                grassEaten++;
            }
        }

        if (grassToRemoveFromList.size() > 0) for(Grass gr : grassToRemoveFromList) grassList.remove(gr);
    }

    // Implements reproduction of the two strongest animals on the same position
    private synchronized void reproduce() {
        for(ArrayList<Animal> animalsOnPos : animals.values()) {
            if (animalsOnPos.size() >= 2) {
                Animal dad =  animalsOnPos.get(0);
                Animal mom = animalsOnPos.get(1);

                if (dad.isHealthy() && mom.isHealthy()) {
                    Animal son = dad.reproduce(mom);
                    animalsBorn++;
                    placeAnimal(dad.getPosition(), son);
                }
            }
        }
    }

    // Adds grass to random position in jungle
    private void addGrassJungle() {
        int tooMuch = 0;
        while (tooMuch < 2 * jungleWidth * jungleHeight) {
            int randX = (int) ((Math.random() * (jungleUpperRight.x + 1 - jungleLowerLeft.x)) + jungleLowerLeft.x);
            int randY = (int) ((Math.random() * (jungleUpperRight.y + 1 - jungleLowerLeft.y)) + jungleLowerLeft.y);
            Vector2d position = new Vector2d(randX, randY);
            if (!isOccupied(position)) {
                placeGrass(position, new Grass(position, plantEnergy));
                grassOnMap++;
                break;
            }
            tooMuch++;
        }
    }

    // Adds grass to random position in savanna
    private void addGrassSavanna() {
        int tooMuch = 0;
        while (tooMuch < 2 * width * height) {
            Vector2d position = new Vector2d( (int) (Math.random()*width), (int) (Math.random()*height) );
            if (!(position.follows(jungleLowerLeft) && position.precedes(jungleUpperRight)) && !isOccupied(position)) {
                placeGrass(position, new Grass(position, plantEnergy));
                grassOnMap++;
                break;
            }
            tooMuch++;
        }

    }

    private void addGrass() {
        addGrassJungle();
        addGrassSavanna();
    }

    // Animal asks map about the position it should move to, return correct map position
    public Vector2d moveTo(Vector2d position) {
        if (position.follows(mapLowerLeft) && position.precedes(mapUpperRight))
            return position;
        else return new Vector2d((position.x + mapUpperRight.x) % mapUpperRight.x, (position.y + mapUpperRight.y) % mapUpperRight.y);
    }

    @Override
    public void positionChanged(Animal an, Vector2d oldPosition, Vector2d newPosition) {
        getAnimalsAt(oldPosition).remove(an);
        ArrayList<Animal> animalsAt = getAnimalsAt(newPosition);

        if ( animalsAt == null ) {
            animalsAt = new ArrayList<>();
            animalsAt.add(an);
            animals.put(newPosition, animalsAt);
            return;
        }

        animalsAt.add(an);
        if (animalsAt.size() > 1) animalsAt.sort(compare);
    }

    /* --- Getters Section --- */
    public synchronized ArrayList<Animal> getAnimalsAt(Vector2d position) {
        return animals.get(position);
    }

    public synchronized Grass getGrassAt(Vector2d position) {
        return grass.get(position);
    }

    public Vector2d getUpperRight() {
        return mapUpperRight;
    }

}

package oop.lisp.map;

import oop.lisp.additional.IPositionChangeObserver;
import oop.lisp.additional.Vector2d;
import oop.lisp.mapelement.Animal;
import oop.lisp.mapelement.Grass;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;

public abstract class AbstractRectangularMap implements IWorldMap, IPositionChangeObserver {
    protected final Vector2d mapLowerLeft, mapUpperRight;
    private Vector2d jungleLowerLeft, jungleUpperRight;
    private final int width, height;
    private int jungleWidth, jungleHeight;
    private final int startEnergy, moveEnergy, plantEnergy, startAnimalsNumber;

    private final double jungleRatio;

    private final LinkedHashMap<Vector2d, Grass> grass = new LinkedHashMap<>();
    private final LinkedHashMap<Vector2d, ArrayList<Animal>> animals = new LinkedHashMap<>();
    private final ArrayList<Animal> animalsList = new ArrayList<>();
    private final ArrayList<Grass> grassList = new ArrayList<>();

    private Animal animalPicked;

    /* --- Stats --- */
    private int epoch = 0;
    private int animalsAlive = 0;
    private int animalsDead = 0;
    private int deadAnimalsAgeSum = 0;
    private int grassOnMap = 0;
    private int avgEnergy = 0;
    private int avgChildrenBorn = 0;
    private int avgLifeExp = 0;

    /* --- Comparator for sorting the animals ArrayList --- */
    private final Comparator<Animal> compare = (an1, an2) -> {
        if (an1.getEnergy() == an2.getEnergy()) return 0;
        return an1.getEnergy() - an2.getEnergy();
    };

    public AbstractRectangularMap(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio, int startAnimalsNumber) {
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
        ArrayList<Animal> animalsAt = animals.get(position);
        an.addObserver(this);
        animalsAlive++;

        if (animalsAt == null) {
            animalsAt = new ArrayList<>();
            animalsAt.add(an);
            synchronized (animals) {
                animals.put(position, animalsAt);
            }
            synchronized (animalsList) {
                animalsList.add(an);
            }
            return;
        }

        synchronized (animals) {
            animalsAt.add(an);
            if (animalsAt.size() > 1) animalsAt.sort(compare);
        }

        synchronized (animalsList) {
            animalsList.add(an);
        }

    }

    // Places grass considering the position is correct
    private void placeGrass(Vector2d position, Grass gr) {
        grassList.add(gr);
        grass.put(position, gr);
        grassOnMap++;
    }

    // Used for displaying elements on the map, returns the strongest animal on position, or grass, or null
    public Object objectAt(Vector2d position) {

        ArrayList<Animal> animalsAt = animals.get(position);
        if (animalsAt == null || animalsAt.size() == 0) {
            return grass.get(position);
        } else return animalsAt.get(0);

    }

    public boolean isOccupied(Vector2d position) {
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
        calculateStats();
    }

    private void calculateStats() {
        // 1. Average energy for all alive animals
        int energySum = 0;
        for (Animal an: animalsList) {
            if (!an.isDead()) energySum += an.getEnergy();
        }
        avgEnergy = energySum / animalsAlive;

        // 2. Average children number for all alive animals
        int childrenSum = 0;
        for (Animal an: animalsList) {
            if (!an.isDead() && an.getChildrenBorn() > 0) childrenSum += an.getChildrenBorn();
        }
        avgChildrenBorn =  childrenSum / animalsAlive;

        // 3. Average Life Expectancy
        if (animalsDead != 0) avgLifeExp = deadAnimalsAgeSum / animalsDead;

    }

    // Deletes dead animals from the map
    private void deleteDead() {
        ArrayList<Animal> animalsToRemove = new ArrayList<>();

        for (Animal an: animalsList) {
            if (an.isDead()) animalsToRemove.add(an);
        }

        for (Animal an: animalsToRemove) {
            synchronized (animalsList) {
                animalsList.remove(an);
            }
            synchronized (animals) {
                animals.get(an.getPosition()).remove(an);
            }
            an.removeObserver(this);
            animalsAlive--;
            animalsDead++;
            deadAnimalsAgeSum += an.getAge();
        }
    }

    // Tells all alive animals to make a move
    private void moveAnimals() {
        for (Animal an: animalsList) an.move();
    }

    // Implements process of eating grass by animals
    private void eatGrass() {
        ArrayList<Grass> grassToRemoveFromList = new ArrayList<>();

        for(Grass gr : grassList) {
            ArrayList<Animal> animalsAt = animals.get(gr.getPosition());

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
            }
        }
        if (grassToRemoveFromList.size() > 0) for (Grass gr : grassToRemoveFromList) grassList.remove(gr);
    }

    // Implements reproduction of the two strongest animals on the same position
    private void reproduce() {
        for(ArrayList<Animal> animalsOnPos : animals.values()) {
            if (animalsOnPos.size() >= 2) {
                Animal dad =  animalsOnPos.get(0);
                Animal mom = animalsOnPos.get(1);

                if (dad.isHealthy() && mom.isHealthy()) {
                    Animal son = dad.reproduce(mom);
                    placeAnimal(dad.getPosition(), son);
                }
            }
        }
    }

    public void animalToWatch(Vector2d position) {
        animalPicked = (Animal) objectAt(position);
        animalPicked.setWatching();
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
    abstract public Vector2d moveTo(Vector2d oldPosition, Vector2d position);

    @Override
    public void positionChanged(Animal an, Vector2d oldPosition, Vector2d newPosition) {
        synchronized (animals) {
            animals.get(oldPosition).remove(an);
        }
        ArrayList<Animal> animalsAt = animals.get(newPosition);

        if ( animalsAt == null ) {
            animalsAt = new ArrayList<>();
            animalsAt.add(an);
            synchronized (animals) {
                animals.put(newPosition, animalsAt);
            }
            return;
        }

        synchronized (animalsAt) {
            animalsAt.add(an);
            if (animalsAt.size() > 1) animalsAt.sort(compare);
        }

    }

    /* --- Getters Section --- */
    public Vector2d getUpperRight() {
        return mapUpperRight;
    }

    public Vector2d getLowerLeft() {
        return mapLowerLeft;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getAnimalsAlive(){
        return animalsAlive;
    }

    public int getGrassOnMap() {
        return grassOnMap;
    }

    public int getAvgLifeExpectancy() {
        return avgLifeExp;
    }

    public int getEpoch() {
        return epoch;
    }

    public int getAvgEnergy() {
        return avgEnergy;
    }

    public int getAvgChildrenBorn() {
        return avgChildrenBorn;
    }

    public Animal getPickedAnimal() {
        return animalPicked;
    }

}

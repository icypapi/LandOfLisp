package oop.lisp.map;

import oop.lisp.additional.Genotype;
import oop.lisp.additional.IPositionChangeObserver;
import oop.lisp.additional.Vector2d;
import oop.lisp.mapelement.Animal;
import oop.lisp.mapelement.Grass;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;

public abstract class AbstractMap implements IWorldMap, IPositionChangeObserver {
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
    private final LinkedHashMap<Genotype, Integer> genotypes = new LinkedHashMap<>();

    private Animal animalPicked;
    private Genotype dominant;

    /* --- Stats --- */
    private int epoch = 0;
    private int animalsAlive = 0;
    private int animalsDead = 0;
    private int deadAnimalsAgeSum = 0;
    private int grassOnMap = 0;
    private int avgEnergy = 0;
    private double avgChildrenBorn = 0;
    private int avgLifeExp = 0;
    private int pickedAnimalChildren = 0;
    private int magicNum = 0;

    /* --- Comparator for sorting the animals ArrayList --- */
    private final Comparator<Animal> compare = (an1, an2) -> {
        if (an1.getEnergy() == an2.getEnergy()) return 0;
        return an1.getEnergy() - an2.getEnergy();
    };

    public AbstractMap(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio, int startAnimalsNumber) {
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
        // This is calculated from formulas: jungleRatio = jungleArea / (mapArea - jungleArea)
        // And assumption that jungleWidth / jungleHeight = mapWidth / mapHeight (always in center)
        jungleHeight = (int) ((double)height * Math.sqrt( jungleRatio/(1.0+jungleRatio) ));
        jungleWidth = (int) ((double)(jungleHeight * width) / (double) height);

        jungleLowerLeft = new Vector2d((width / 2) - (jungleWidth / 2), (height / 2) - (jungleHeight / 2));
        jungleUpperRight = jungleLowerLeft.add(new Vector2d(jungleWidth-1, jungleHeight-1));

    }

    protected void doMagic() {
        int tooMuch;
        for (int i = 0; i < 5; i++) {
            tooMuch = 0;
            while (tooMuch < 2 * width * height) {
                Vector2d position = new Vector2d( (int) (Math.random()*width), (int) (Math.random()*height) );
                if (!isOccupied(position)) {
                    placeAnimal(position, new Animal(this, position, startEnergy, moveEnergy, animalsList.get(i).getGenotype()));
                    break;
                }
                tooMuch++;
            }
        }
        magicNum++;
        System.out.println("Just did some magic!");
    }

    // Places animal considering the position is correct
    private void placeAnimal(Vector2d position, Animal an) {
        ArrayList<Animal> animalsAt = animals.get(position);
        an.addObserver(this);
        animalsAlive++;
        if (genotypes.get(an.getGenotype()) == null) genotypes.put(an.getGenotype(), 0);
        else {
            Integer count = genotypes.get(an.getGenotype());
            genotypes.remove(an.getGenotype());
            genotypes.put(an.getGenotype(), count + 1);
        }

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
        if (animalsAlive != 0) avgEnergy = energySum / animalsAlive;

        // 2. Average children number for all alive animals
        int childrenSum = 0;
        for (Animal an: animalsList) {
            if (!an.isDead() && an.getChildrenBorn() > 0) childrenSum += an.getChildrenBorn();
        }
        if (animalsAlive != 0) avgChildrenBorn =  (double) childrenSum / animalsAlive;

        // 3. Average Life Expectancy
        if (animalsDead != 0) avgLifeExp = deadAnimalsAgeSum / animalsDead;

        // 4. Dominant genotype
        int maxCount = 0;
        for (Genotype gn : genotypes.keySet()) {
            if (genotypes.get(gn) >= maxCount) {
                maxCount = genotypes.get(gn);
                dominant = gn;
            }
        }

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
            animalsDead++;
            deadAnimalsAgeSum += an.getAge();

            Integer count = genotypes.get(an.getGenotype());
            genotypes.remove(an.getGenotype());
            if (count != 0) {
                genotypes.put(an.getGenotype(), count-1);
            }
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
                    if (dad == animalPicked || mom == animalPicked) pickedAnimalChildren++;
                }
            }
        }
    }

    public void animalToWatch(Vector2d position) {
        if (animalPicked != null) {
            animalPicked.unsetWatching();
        }
        animalPicked = animals.get(position).get(0);
        animalPicked.setWatching();
        pickedAnimalChildren = 0;
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
            animals.put(newPosition, animalsAt);
            return;
        }

        animalsAt.add(an);
        if (animalsAt.size() > 1) animalsAt.sort(compare);

    }

    /* --- Getters Section --- */

    public Genotype getDominant() {
        return dominant;
    }

    public int getMagicNumber() {
        return magicNum;
    }

    public int getPickedAnimalChildren() {
        return pickedAnimalChildren;
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

    public double getAvgChildrenBorn() {
        return avgChildrenBorn;
    }

    public Animal getPickedAnimal() {
        return animalPicked;
    }

}

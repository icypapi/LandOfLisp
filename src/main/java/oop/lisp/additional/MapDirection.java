package oop.lisp.additional;

public enum MapDirection {
    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST;

    // Array of Unit Vectors representing our Map Directions respectively to MapDirection.values()
    private final Vector2d[] vectors = {
            new Vector2d(0, 1), new Vector2d(1, 1),   // N, NE
            new Vector2d(1, 0), new Vector2d(1, -1),  // E, SE
            new Vector2d(0, -1), new Vector2d(-1, -1),// S, SW
            new Vector2d(-1, 0), new Vector2d(-1, 1)  // W, NW
    };

    @Override
    public String toString() {
        return switch (this) {
            case NORTH -> "N";
            case NORTH_EAST -> "NE";
            case EAST -> "E";
            case SOUTH_EAST -> "SE";
            case SOUTH -> "S";
            case SOUTH_WEST -> "SW";
            case WEST -> "W";
            case NORTH_WEST -> "NW";
        };
    }

    // Returns random MapDirection
    public static MapDirection randomDirection(){
        return MapDirection.values()[(int) (Math.random() * 8)];
    }

    // Returns the 'next' MapDirection clockwise
    public MapDirection next() {
        return this.ordinal() < MapDirection.values().length - 1 ? MapDirection.values()[this.ordinal() + 1] : MapDirection.values()[0];
    }

    // Return Vector2d that is representing 'this' MapDirection
    public Vector2d toUnitVector() {
        return vectors[this.ordinal()];
    }

}

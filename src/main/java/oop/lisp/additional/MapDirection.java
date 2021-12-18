package oop.lisp.additional;

public enum MapDirection {
    NORTH,
    NORTHEAST,
    EAST,
    SOUTHEAST,
    SOUTH,
    SOUTHWEST,
    WEST,
    NORTHWEST;

    final Vector2d vectors[] = {
            new Vector2d(0, 1), new Vector2d(1, 1),
            new Vector2d(1, 0), new Vector2d(1, -1),
            new Vector2d(0, -1), new Vector2d(-1, -1),
            new Vector2d(-1, 0), new Vector2d(-1, 1)
    };

    public String toString() {
        return switch (this) {
            case NORTH -> "N";
            case NORTHEAST -> "NE";
            case EAST -> "E";
            case SOUTHEAST -> "SE";
            case SOUTH -> "S";
            case SOUTHWEST -> "SW";
            case WEST -> "W";
            case NORTHWEST -> "NW";
        };
    }

    public MapDirection next() {
        return this.ordinal() < MapDirection.values().length - 1 ? MapDirection.values()[this.ordinal() + 1] : MapDirection.values()[0];
    }


    public Vector2d toUnitVector() {
        return vectors[this.ordinal()];
    }
}

package se.artcomputer.game;

public enum Condition {
    DOCKED,
    RED,
    YELLOW,
    GREEN;

    public String toString() {
        return switch (this) {
            case DOCKED ->  "DOCKED";
            case RED ->  "*RED*";
            case YELLOW ->  "YELLOW";
            case GREEN ->  "GREEN";
        };
    }
}

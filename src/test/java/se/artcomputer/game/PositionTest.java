package se.artcomputer.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PositionTest {

    @Test
    void direction1() {
        Position origo = new Position(1, 1);
        Position target = new Position(1, 5);
        assertEquals(1.0, origo.directionTo(target), 0.01);
    }

    @Test
    void direction2() {
        Position origo = new Position(5, 2);
        Position target = new Position(4, 3);
        assertEquals(2.0, origo.directionTo(target), 0.01);
    }

    @Test
    void direction3() {
        Position origo = new Position(5, 3);
        Position target = new Position(4, 3);
        assertEquals(3.0, origo.directionTo(target), 0.01);
    }

    @Test
    void direction4() {
        Position origo = new Position(5, 3);
        Position target = new Position(4, 2);
        assertEquals(4.0, origo.directionTo(target), 0.01);
    }

    @Test
    void direction5() {
        Position origo = new Position(5, 2);
        Position target = new Position(5, 1);
        assertEquals(5.0, origo.directionTo(target), 0.01);
    }

    @Test
    void direction6() {
        Position origo = new Position(5, 2);
        Position target = new Position(6, 1);
        assertEquals(6.0, origo.directionTo(target), 0.01);
    }

    @Test
    void direction7() {
        Position origo = new Position(5, 2);
        Position target = new Position(6, 2);
        assertEquals(7.0, origo.directionTo(target), 0.01);
    }

    @Test
    void direction75() {
        Position origo = new Position(5, 2);
        Position target = new Position(8, 3);
        assertEquals(7.4, origo.directionTo(target), 0.01);
    }
}

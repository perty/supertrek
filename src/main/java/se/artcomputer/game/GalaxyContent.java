package se.artcomputer.game;

/**
 * Instead of having a numerical representation for each quadrant's content.
 */
public class GalaxyContent {
    private final Quadrant[][] content;

    public GalaxyContent() {
        content = new Quadrant[8][8];
        for (int q1 = 1; q1 <= 8; q1++) {
            for (int q2 = 1; q2 <= 8; q2++) {
                setQuadrant(q1, q2, new Quadrant(0, 0, 0));
            }
        }
    }

    public String statistics() {
        int klingons = 0, bases = 0;
        for (int q1 = 1; q1 <= 8; q1++) {
            for (int q2 = 1; q2 <= 8; q2++) {
                Quadrant quadrant = getQuadrant(q1, q2);
                klingons += quadrant.klingons;
                bases += quadrant.bases;
            }
        }
        return String.format("Klingons: %d, Bases: %d", klingons, bases);
    }

    public void initQuadrant(int q1, int q2, int klingons, int bases, int stars) {
        if (q1 < 1 || q1 > 8 || q2 < 1 || q2 > 8) {
            System.err.printf("Quadrant of bounds %d,%d%n", q1, q2);
        } else {
            content[q1 - 1][q2 - 1] = new Quadrant(klingons, bases, stars);
        }
    }

    public void setBases(int q1, int q2, int bases) {
        getQuadrant(q1, q2).bases = bases;
    }

    public int getKlingons(int q1, int q2) {
        return getQuadrant(q1, q2).klingons;
    }

    public void setKlingons(int q1, int q2, int klingons) {
        getQuadrant(q1, q2).klingons = klingons;
    }

    public int getBases(int q1, int q2) {
        return getQuadrant(q1, q2).bases;
    }

    Quadrant getQuadrant(int q1, int q2) {
        if (q1 < 1 || q1 > 8 || q2 < 1 || q2 > 8) {
            System.err.printf("Quadrant of bounds %d,%d%n", q1, q2);
            return new Quadrant(-1, -1, -1);
        }
        return content[q1 - 1][q2 - 1];
    }

    public void setQuadrant(int q1, int q2, Quadrant quadrant) {
        if (q1 < 1 || q1 > 8 || q2 < 1 || q2 > 8) {
            System.err.printf("Quadrant of bounds %d,%d%n", q1, q2);
        } else {
            content[q1 - 1][q2 - 1] = quadrant;
        }
    }

    public int getStars(int q1, int q2) {
        return getQuadrant(q1, q2).stars;
    }

    public float numeric(int q1, int q2) {
        Quadrant quadrant = getQuadrant(q1, q2);
        return quadrant.klingons * 100 + quadrant.bases * 10 + quadrant.stars;
    }

    public int getTotalKlingons() {
        int klingons = 0;
        for (int q1 = 1; q1 <= 8; q1++) {
            for (int q2 = 1; q2 <= 8; q2++) {
                Quadrant quadrant = getQuadrant(q1, q2);
                klingons += quadrant.klingons;
            }
        }
        return klingons;
    }

    private static class Quadrant {
        int klingons;
        int bases;
        int stars;

        public Quadrant(int klingons, int bases, int stars) {
            this.klingons = klingons;
            this.bases = bases;
            this.stars = stars;
        }
    }


}

package se.artcomputer.game;

/**
 * Instead of having a string representation of the quadrant contents.
 */
public class QuadrantContent {
    private final String[][] content;

    public QuadrantContent() {
        content = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                content[i][j] = "   ";
            }
        }
    }


    public String get(int s1, int s2) {
        if (s1 < 1 || s1 > 8 || s2 < 1 || s2 > 8) {
            System.err.printf("Sector of bounds %d,%d%n", s1, s2);
            return "";
        }
        return content[s1 - 1][s2 - 1];
    }

    public void set(int s1, int s2, String value) {
        if (s1 < 1 || s1 > 8 || s2 < 1 || s2 > 8) {
            System.err.printf("Sector of bounds %d,%d%n", s1, s2);
        } else {
            content[s1 - 1][s2 - 1] = value;
        }
    }

    public String getRow(int row) {
        if (row < 1 || row > 8 ) {
            System.err.printf("Row out of bounds %d%n", row);
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            result.append(content[row - 1][i]);
        }
        return result.toString();
    }
}

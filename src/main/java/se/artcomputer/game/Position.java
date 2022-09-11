package se.artcomputer.game;

public record Position(int row, int col) {
    double distanceTo(Position position1) {
        float rowDist = Math.abs(row() - position1.row());
        float colDist = Math.abs(col() - position1.col());
        return Math.sqrt(Math.pow(rowDist, 2) + Math.pow(colDist, 2));
    }

    public double directionTo(Position target) {
        int deltaCol = target.col() - this.col();
        int deltaRow = this.row() - target.row();
        double intermediate = (Math.atan2(deltaRow, deltaCol)) / (2 * Math.PI) * 8 + 1;
        if (deltaRow >= 0) {
            return intermediate;
        }
        return intermediate + 8;
    }
}

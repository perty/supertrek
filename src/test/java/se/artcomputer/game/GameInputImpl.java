package se.artcomputer.game;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GameInputImpl implements GameInput {
        private Queue<String> line = new LinkedList<>();

        @Override
        public String nextLine() {
            if (line.isEmpty()) {
                throw new RuntimeException("Empty buffer.");
            }
            return line.poll();
        }

        public void setLines(String... lines) {
            this.line = new LinkedList<>(List.of(lines));
        }
    }
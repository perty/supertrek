package se.artcomputer.game;

import java.io.StringWriter;

public class GameBufferOutput implements GameOutput {
    private final StringWriter buffer = new StringWriter();

    @Override
    public void print(String message) {
        buffer.append(message);
    }

    @Override
    public void println(String message) {
        buffer.append(message).append('\n');
    }

    String getMessages() {
        return buffer.toString();
    }
}

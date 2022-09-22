package se.artcomputer.game;

public class GameSystemOutput implements GameOutput{
    @Override
    public void print(String message) {
        System.out.print(message);
    }

    @Override
    public void println(String message) {
        System.out.println(message);
    }
}

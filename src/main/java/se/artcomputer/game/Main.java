package se.artcomputer.game;

import java.util.Scanner;

import static se.artcomputer.game.GameResult.CONTINUE;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Scanner scanner = new Scanner(System.in);
        Game game = new Game();
        GameResult gameResult = CONTINUE;
        while (gameResult == CONTINUE) {
            System.out.print(">");
            String line = scanner.nextLine();
            gameResult = game.run(line);
        }
    }
}
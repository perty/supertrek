package se.artcomputer.game;

import java.util.Random;

/**
 * Super Star Trek - May, 16 1978 - Requires 24k memory
 */
public class Game {

    private GameState gameState = GameState.INITIAL;
    private Random random = new Random();
    private String Z$ = "                      "; // 270
    private float[][] G = new float[8][8]; // 330
    private float[][] C = new float[9][2]; // 330
    private float[][] K = new float[3][3]; // 330
    private float[] N = new float[3]; // 330
    private float[][] Z = new float[8][8]; // 330
    private float[] D = new float[8]; // 330
    private int T = Math.round(random.nextFloat() * 20 + 20) * 100; // 370
    private int T0 = T; // 370
    private int T9 = 25 + Math.round(random.nextFloat() * 10); // 370
    private float D0 = 0.0F; // 370
    private int E = 3000; // 370
    private int E0 = E; // 370

    private int P = 10; // 440
    private int P0 = P; // 440
    private int S9 = 200; // 440
    private int S = 0; // 440
    private int B9 = 0; // 440
    private int K9 = 0; // 440
    private String X$ = ""; // 440
    private String X0$ = " IS "; // 440

    private float fnd() { // 470
        return Math.pow(Math.sqrt(K[I][1] - S1), 2) + Math.pow(K[I][2] - S2, 2);
    }

    private int fnr() { // 475
        return Math.round(random.nextFloat());
    }

    // 480 Initialize Enterprise's position.
    private int O1 = fnr(); // 490
    private int O2 = fnr(); // 490
    private int S1 = fnr(); // 490
    private int S2 = fnr(); // 490

    public GameResult command(String line) {
        switch (gameState) {

            case INITIAL -> {
                initial();
            }
        }
        if (line.equals("end")) {
            return GameResult.END;
        }
        return GameResult.CONTINUE;
    }

    private void initial() {
        print("                                  ,-----*-----,");
        print("                                   `---  ----´");
    }


    private void print(String s) {
        System.out.println(s);
    }

}

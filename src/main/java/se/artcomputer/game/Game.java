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
        return Math.pow(Math.sqrt(K[I][0] - S1), 2) + Math.pow(K[I][1] - S2, 2);
    }

    private int fnr() { // 475
        return Math.round(random.nextFloat());
    }

    // 480 Initialize Enterprise's position.
    private int O1 = fnr(); // 490
    private int O2 = fnr(); // 490
    private int S1 = fnr(); // 490
    private int S2 = fnr(); // 490

    // 815 REM K3 = # Klingons B3 = # Starbases S3 = # Stars
    private int K3;
    private int B3;
    private int S3;

    private void initValues() {
        for (int i = 0; i < 9; i++) { // 530 (arrays in Java are zero based, not in BASIC).
            C[i][0] = 0; //Redundant
            C[i][1] = 0; //Redundant
        }
        C[2][0] = -1; // 540
        C[1][0] = -1; // 540
        C[3][0] = -1; // 540
        C[3][1] = -1; // 540
        C[4][1] = -1; // 540 C(5,2)=-1
        C[5][1] = -1; // 540 C(6,2)=-1

        // 600 C(1,2)=1:C(2,2)=1:C(6,1)=1:C(7,1)=1:C(8,1)=1:C(8,2)=1:C(9,2)=1
        C[0][1] = 1;
        C[1][1] = 1;
        C[5][0] = 1;
        C[6][0] = 1;
        C[7][0] = 1;
        C[7][1] = 1;
        C[8][1] = 1;

        // 670 FORI=1TO8:D(I)=0:NEXTI
        for (int i = 0; i < 8; i++) {
            D[i] = 0; //Redundant
        }

    }

    // 710 A1$="NAVSRSLRSPHATORSHEDAMCOMXXX"
    private String A1$ = "NAVSRSLRSPHATORSHEDAMCOMXXX";

    // 810 REM Setup what exists in galaxy...
    private void setupGalaxy() {
        // 815 REM K3 = # Klingons B3 = # Starbases S3 = # Stars
        // 820 FOR I=1TO8: FOR J=1TO8:K3=0:Z(I,J)=0:R1=RND(1)
        // 850 IFR1>.98 THEN K3=3:K9=K9+3:GOTO 980
        // 860 IFR1>.95 THEN K3=2:K9=K9+2:GOTO 980
        // 870 IFR1>.80 THEN K3=1:K9=K9+1
        // 980 B3=0:IF RND(1) > .96 THEN B3=1:B9=B9+1
        // 1040 G(I,J)=K3*100+B3*10+FNR(1):NEXTJ:NEXTI:IFK9>T9 THEN T9=K9+1
        // 1100 IF B9 <> 0 THEN 1200
        // 1150 IF G(Q1,Q2)<200 THEN G(Q1,Q2)=G(Q1,Q2)+120:K9=K9+1
        // 1160 B9=1:G(Q1,Q2)=G(Q1,Q2)+10:Q1=FNR(1):Q2=FNR(1)
        // 1200 K7=K9:IFB9<>1 THEN X$=" S": X0$=" ARE "

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                float r1 = fnr();
                if (r1 > 0.98) {
                    K3 = 3;
                    K9 = K9 + 3;
                } else if (r1 > 0.95) {
                    K3 = 2;
                    K9 = K9 + 2;
                } else {
                    K3 = 1;
                    K9 = K9 + 1;
                }
                B3 = 0;
                if (fnr() > 0.96) {
                    B3 = 1;
                    B9 = B9 + 1;
                }
                G[i][j] = K3 * 100 + B3 * 10 + fnr();
            }
        }
        if (K9 > T9) {
            T9 = K9 + 1;
        }
        if (B9 == 0) {
            if (G[Q1][Q2]<200 ){

            }THEN G(Q1,Q2)=G(Q1,Q2)+120:K9=K9+1 // 1150 IF G(Q1,Q2)<200 THEN G(Q1,Q2)=G(Q1,Q2)+120:K9=K9+1
        }
    }

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
        initValues();
        setupGalaxy();
        print("                                  ,-----*-----,");
        print("                                   `---  ----´");
    }


    private void print(String s) {
        System.out.println(s);
    }

}

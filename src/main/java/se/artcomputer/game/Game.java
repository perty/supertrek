package se.artcomputer.game;

import java.util.Random;

import static se.artcomputer.game.GameState.INITIAL;
import static se.artcomputer.game.GameState.RUNNING;

/**
 * Super Star Trek - May, 16 1978 - Requires 24k memory
 */
public class Game {

    private GameState gameState = INITIAL;
    private Random random = new Random();
    private String Z$ = "                      "; // 270
    private double[][] G = new double[8][8]; // 330
    private double[][] C = new double[9][2]; // 330
    private double[][] K = new double[3][3]; // 330
    private double[] N = new double[3]; // 330
    private double[][] Z = new double[8][8]; // 330
    private double[] D = new double[8]; // 330
    private int T = Math.round(random.nextFloat() * 20 + 20) * 100; // 370
    private int T0 = T; // 370
    private int T9 = 25 + Math.round(random.nextFloat() * 10); // 370
    private double D0 = 0.0F; // 370
    private int E = 3000; // 370
    private int E0 = E; // 370

    private int P = 10; // 440
    private int P0 = P; // 440
    private int S9 = 200; // 440
    private int S = 0; // 440
    private int B9 = 0; // 440
    private int K9 = 0; // 440
    private int K7 = 0; // 1200
    private String X$ = ""; // 440
    private String X0$ = " IS "; // 440

    private int I; // TODO: What is is this?
    private int Z4;
    private int Z5;
    private int G5;
    private double D4;

    private String G2$;

    private double fnd() { // 470
        return Math.pow(Math.sqrt(K[I][0] - S1), 2) + Math.pow(K[I][1] - S2, 2);
    }


    // Generate a random in 0-7
    private int fnr() { // 475
        return random.nextInt(8);
    }

    // 480 Initialize Enterprise's position.
    private int O1 = fnr(); // 490
    private int O2 = fnr(); // 490
    private int S1 = fnr(); // 490
    private int S2 = fnr(); // 490

    // 815 REM K3 = # Klingons B3 = # Starbases S3 = # Stars
    private int K3;
    private int B3;
    private double S3;
    private String Q$; // 1600
    private String A$; // 1680

    private double Z1, Z2; // 1680
    private double B4, B5; // 1880

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

    private int Q1, Q2;  // TODO: figure these out

    private double R1, R2;

    // 810 REM Setup what exists in galaxy...
    private void setupGalaxy() {
        // 815 REM K3 = # Klingons B3 = # Starbases S3 = # Stars
        // 820 FOR I=1TO8: FOR J=1TO8:K3=0:Z(I,J)=0:R1=RND(1)
        // 850 IFR1>.98 THEN K3=3:K9=K9+3:GOTO 980
        // 860 IFR1>.95 THEN K3=2:K9=K9+2:GOTO 980
        // 870 IFR1>.80 THEN K3=1:K9=K9+1
        // 980 B3=0:IF RND(1) > .96 THEN B3=1:B9=B9+1
        // 1040 G(I,J)=K3*100+B3*10+FNR(1):NEXTJ:NEXTI:IFK9>T9 THEN T9=K9+1

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                R1 = random.nextDouble();
                if (R1 > 0.98) {
                    K3 = 3;
                    K9 = K9 + 3;
                } else if (R1 > 0.95) {
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
                G[i][j] = K3 * 100L + B3 * 10L + fnr();
            }
        }
        if (K9 > T9) {
            T9 = K9 + 1;
        }
        // 1100 IF B9 <> 0 THEN 1200
        if (B9 == 0) {
            // 1150 IF G(Q1,Q2)<200 THEN G(Q1,Q2)=G(Q1,Q2)+120:K9=K9+1
            if (G[Q1][Q2] < 200) {
                G[Q1][Q2] = G[Q1][Q2] + 120;
                K9 = K9 + 1;
            }
            // 1160 B9=1:G(Q1,Q2)=G(Q1,Q2)+10:Q1=FNR(1):Q2=FNR(1)
            B9 = 1;
            G[Q1][Q2] = G[Q1][Q2] + 10;
            Q1 = fnr();
            Q2 = fnr();
        }
        // 1200 K7=K9:IFB9<>1 THEN X$=" S": X0$=" ARE "
        K7 = K9;
        if (B9 != 1) {
            X$ = "s";
            X0$ = " are ";
        }
    }

    public GameResult command(String line) {
        switch (gameState) {

            case INITIAL -> {
                initial();
                gameState = RUNNING;
            }
            case RUNNING -> {
                running();
            }
        }
        if (line.equals("end")) {
            return GameResult.END;
        }
        return GameResult.CONTINUE;
    }

    private void running() {
        // 1310 REM Here any time a new quadrant entered.
        // 1320 Z4=Q1:Z5=Q2:K3=0:B3=0:S3=0:G5=0:D4=0.5*RND(1):Z(Q1,Q2)=G(Q1,Q2)
        Z4 = Q1;
        Z5 = Q2;
        K3 = 0;
        S3 = 0;
        G5 = 0;
        D4 = 0.5 * random.nextDouble();
        Z[Q1][Q2] = G[Q1][Q2];
        // 1390 IF Q1<1 OR Q1>8 OR Q2<1 OR Q2>8 THEN 1600
        if (Q1 >= 0 && Q1 < 8 && Q2 >= 0 && Q2 < 8) {
            // 1430 GOSUB 9030:Print:IF T0<>T THEN 1490
            goSub9030();
            print("");
            if (T0 == T) {
                // 1460 Print "Your mission begins with your starship located"
                print("Your mission begins with your starship located");
                // 1470 Print "in the galactic quadrant," G2$ " quadrant ...": GOTO 1500
                print("in the galactic quadrant," + G2$ + " quadrant ...");
            } else {
                // 1490 Print "Now entering " G2$ " quadrant ..."
                print("Now entering " + G2$ + " quadrant ...");
            }
            // 1500 S3=G[Q1][Q2]-100 * K3 - 10 * B3: IF K3=0 THEN 1590
            S3 = G[Q1][Q2] - 100 * K3 - 10 * B3;
            if (K3 != 0) {
                // 1560 Print "Combat area    condition red": IF S>200 THEN 1590
                print("Combat area    condition red");
                if (S <= 200) {
                    // 1580 Print "     Shields dangerously low"
                    print("     Shields dangerously low");
                }
            }
            // 1590 FOR I=1TO3: K(I,1)=0:K(I,2)=0: NEXT I
            for (int i = 0; i < 3; i++) {
                K[i][0] = 0;
                K[i][1] = 0;
            }
        }
        // 1600
        for (int i = 0; i < 3; i++) {
            K[i][2] = 0;
        }
        Q$ = Z$ + Z$ + Z$ + Z$ + Z$ + Z$ + left(Z$, 17);
        // 1660 REM Position Enterprise in quadrant, then place "K3" Klingons, &
        // 1670 REM "B3" starbases, & "S3" stars elsewhere.
        // 1680 A$="<*>":Z1=S1:Z2=S2:GOSUB 8670:IF K3<1 THEN 1820
        A$ = "<*>";
        Z1 = S1;
        Z2 = S2;
        goSub8670();
        if (K3 >= 1) {
            // 1720 FOR I=1TOK3: GOSUB 8590: A$="+K+":Z1=R1:Z2=R2
            for (int i = 0; i < K3; i++) {
                goSub8590();
                A$ = "+K+";
                Z1 = R1;
                Z2 = R2;
                // 1780 GOSUB 8670: K(I,1)=R1:K(I,2)=R2;K(I,3)=S9*0.5+RND(1):NEXTI
                goSub8670();
                K[i][0] = R1;
                K[i][1] = R2;
                K[i][2] = S9 * 0.5 + random.nextDouble();
            }
        }
        // 1820 IF B3<1 THEN 1910
        if (B3 >= 1) {
            // 1880 GOSUB 8590: A$=">!<":Z1=R1:B4=R1:Z2=R2:B5=R2:GOSUB 8670
            goSub8590();
            A$ = ">!<";
            Z1 = R1;
            B4 = R1;
            Z2 = R2;
            B5 = R2;
            goSub8670();
        }
        // 1910 FOR I=1TOS3:GOSUB 8590:A$=" * ":Z1=R1:Z2=R2:GOSUB 8670:NEXTI
        for (int i = 0; i < 3; i++) {
            goSub8590();
            Z1 = R1;
            Z2 = R2;
            goSub8670();
        }
    }

    private void goSub8590() {
    }

    private void goSub8670() {

    }

    private String left(String input, int i) {
        return input.substring(0, i);
    }

    private static final String[] quadrantName1 =
            new String[]{
                    "ANTARES",
                    "RIGEL",
                    "PROCYON",
                    "VEGA",
                    "CANOPUS",
                    "ALTAIR",
                    "SAGITTARIUS",
                    "POLLUX"
            };
    private static final String[] quadrantName2 =
            new String[]{
                    "SIRIUS",
                    "DENEB",
                    "CAPELLA",
                    "BETELGEUSE",
                    "ALDEBARAN",
                    "REGULUS",
                    "ARCTURUS",
                    "SPICA"
            };

    private void goSub9030() {
        // 9010 REM Quadrant name in G2$ from Z4,Z5 = G(Q1,Q2)
        // 9030 IF Z5<=4 THEN ON Z4 GOTO 9040,9050,9060,9070,9070,9080,9100,9110
        if (Z5 < 4) {
            G2$ = quadrantName1[Z4];
        } else {
            G2$ = quadrantName2[Z4];
        }

        // 9210 IF G5 <> 1 ON Z5 GOTO ...
        if (G5 != 1) {
            switch (Z5) {
                case 0 -> G2$ += " I";
                case 1 -> G2$ += " II";
                case 2 -> G2$ += " III";
                case 3 -> G2$ += " IV";
            }
        }
    }

    private void initial() {
        initValues();
        setupGalaxy();
        print("                                  ,-----*-----,"); // 221
        print("                                   `---  ----´");
        prompt(); // 1230
    }

    private void prompt() {
        print("Your orders are as follows:"); //1230
        print("\t Destroy the " + K9 + " Klingon warships which has invaded"); //1240
        print("\t the galaxy before they can attack federation headquarters"); //1250
        print("\t on stardate " + (T0 + T9) + ". This gives you " + T9 + " days. There" + X0$); //1260
        print("\t " + B9 + " starbase" + X$ + " in the galaxy to resupply your ship."); //1270
        print("");
        print("Hit return when you are ready.");
    }


    private void print(String s) {
        System.out.println(s);
    }

}

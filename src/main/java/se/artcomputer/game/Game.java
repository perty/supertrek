package se.artcomputer.game;

import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import static se.artcomputer.game.GameState.INITIAL;
import static se.artcomputer.game.GameState.RUNNING;

/**
 * Super Star Trek - May, 16 1978 - Requires 24k memory
 */
public class Game {

    public static final String STARSHIP_ICON = "<*>";
    public static final String KLINGON_ICON = "+K+";
    public static final String STARBASE_ICON = ">!<";
    public static final String STAR_ICON = " * ";
    public static final String EMPTY_ICON = "   ";
    private GameState gameState = INITIAL;

    Scanner scanner = new Scanner(System.in);

    private Random random = new Random();
    private String Z$ = String.join("", Collections.nCopies(25, " "));  // 270

    // Try adding one to the size of each array and use 1 based index, as in Basic.
    // 338 DIM G(8,8),C(9,2),K(3.3),N(3),Z(8,8),D(8)
    /**
     * Galaxy? In the LRS format.
     */
    private float[][] G = new float[9][9]; // 330
    private float[][] C = new float[10][3]; // 330
    /**
     * Klingon sector position and health.
     * [1][2] : position?
     * [3] : hit points
     */
    private float[][] K = new float[4][4]; // 330
    //private float[] NA = new float[4]; // 330
    private float N; // 3170
    private float[][] Z = new float[9][9]; // 330
    /**
     * Damage
     * 1 = Warp engines
     * 3 = lrs
     * 4 = phasers
     * 8 = computer
     */
    private float[] D = new float[9]; // 330
    /**
     * Current day
     */
    private int T = Math.round(random.nextFloat() * 20 + 20) * 100; // 370
    /**
     * Start day
     */
    private int T0 = T; // 370
    /**
     * Days for mission
     */
    private int T9 = 25 + Math.round(random.nextFloat() * 10); // 370
    /**
     * Docked at a starbase
     */
    private int D0 = 0; // 370
    /**
     * Energy available
     */
    private float E = 3000; // 370
    private float E0 = E; // 370

    /**
     * Photon torpedoes
     */
    private int P = 10; // 440
    private int P0 = P; // 440
    private int S9 = 200; // 440
    /**
     * Shield energy
     */
    private float S = 0; // 440
    /**
     * Number of starbases
     */
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

    /**
     * Name of quadrant
     */
    private String G2$;
    private float W1; // 2360
    private float D1, D6; // 2700
    private float X1, X, C1, Y; // 3110
    private float X2, Q4, Q5; // 3140
    private float T8; // 3370
    private int X5; // 3620
    private String O1$; // 4040
    private int H1; // 4450
    private float H; // 4480
    private int S8; // 8670
    private int Z3; // 8590
    private String C$; // 6580

    private double fnd() { // 470
        return Math.pow(Math.sqrt(K[I][1] - S1), 2) + Math.pow(K[I][2] - S2, 2);
    }


    /**
     * Generate a random in 1-8
     */
    private int fnr() { // 475
        return random.nextInt(8) + 1;
    }

    // 480 Initialize Enterprise's position.
    /**
     * Quadrant position
     */
    private int Q1 = fnr(); // 490
    /**
     * Quadrant position
     */
    private int Q2 = fnr(); // 490
    /**
     * Sector position
     */
    private int S1 = fnr(); // 490
    /**
     * Sector position
     */
    private int S2 = fnr(); // 490

    // 815 REM K3 = # Klingons B3 = # Starbases S3 = # Stars
    /**
     * No of Klingons in the galaxy
     */
    private int K3;
    /**
     * Starbases in the galaxy
     */
    private int B3;
    /**
     * Stars in the galaxy
     */
    private double S3;
    /**
     * String representation of the current quadrant.
     * A quadrant has 8x8 positions. Each position is 3 characters, therefore the string is
     * 8 X 8 x 3 = 192 characters.
     * To index into it, multiply by 3 for one dimension and then by 24 (8 * 3) for the other.
     */
    private String Q$; // 1600
    private String A$; // 1680

    private float Z1, Z2; // 1680
    private float B4, B5; // 1880

    private void initValues() {
        for (int I = 1; I <= 9; I++) { // 530 FOR I=1TO9
            C[I][1] = 0;
            C[I][2] = 0;
        }
        C[3][1] = -1; // 540
        C[2][1] = -1; // 540
        C[4][1] = -1; // 540
        C[4][2] = -1; // 540
        C[5][2] = -1; // 540 C(5,2)=-1
        C[6][2] = -1; // 540 C(6,2)=-1

        // 600 C(1,2)=1:C(2,2)=1:C(6,1)=1:C(7,1)=1:C(8,1)=1:C(8,2)=1:C(9,2)=1
        C[1][2] = 1;
        C[2][2] = 1;
        C[6][1] = 1;
        C[7][1] = 1;
        C[8][1] = 1;
        C[8][2] = 1;
        C[9][2] = 1;

        // 670 FOR I=1TO8:D(I)=0:NEXT I
        for (int I = 1; I <= 8; I++) {
            D[I] = 0; //Redundant
        }
    }

    // 710 A1$="NAVSRSLRSPHATORSHEDAMCOMXXX"
    private final String A1$ = "NAVSRSLRSPHATORSHEDAMCOMXXX";

    private float R1, R2;

    // 810 REM Setup what exists in galaxy...
    private void setupGalaxy() {
        // 815 REM K3 = # Klingons B3 = # Starbases S3 = # Stars

        for (int I = 1; I <= 8; I++) {
            // 820 FOR I=1TO8: FOR J=1TO8:K3=0:Z(I,J)=0:R1=RND(1)
            for (int J = 0; J < 8; J++) {
                R1 = random.nextFloat();
                if (R1 > 0.98) {
                    // 850 IFR1>.98 THEN K3=3:K9=K9+3:GOTO 980
                    K3 = 3;
                    K9 = K9 + 3;
                } else if (R1 > 0.95) {
                    // 860 IFR1>.95 THEN K3=2:K9=K9+2:GOTO 980
                    K3 = 2;
                    K9 = K9 + 2;
                } else {
                    // 870 IFR1>.80 THEN K3=1:K9=K9+1
                    K3 = 1;
                    K9 = K9 + 1;
                }
                // 980 B3=0:IF RND(1) > .96 THEN B3=1:B9=B9+1
                B3 = 0;
                if (fnr() > 0.96) {
                    B3 = 1;
                    B9 = B9 + 1;
                }
                // 1040 G(I,J)=K3*100+B3*10+FNR(1):NEXTJ:NEXTI:IFK9>T9 THEN T9=K9+1
                G[I][J] = K3 * 100 + B3 * 10 + fnr();
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
            X$ = "S";
            X0$ = " ARE ";
        }
    }

    public GameResult run(String line) {
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

    private void newQuadrant1320() {
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
        if (!(Q1 < 1 || Q1 > 8 || Q2 < 1 || Q2 > 8)) {
            // 1430 GOSUB 9030:Print:IF T0<>T THEN 1490
            goSub9030();
            println("");
            if (T0 == T) {
                // 1460 Print "Your mission begins with your starship located"
                println("YOUR MISSION BEGINS WITH YOUR STARSHIP LOCATED");
                // 1470 PRINT "IN THE GALACTIC QUADRANT," G2$ " QUADRANT ...": GOTO 1500
                println("IN THE GALACTIC QUADRANT," + G2$ + " QUADRANT ...");
            } else {
                // 1490 Print "Now entering " G2$ " quadrant ..."
                println("NOW ENTERING " + G2$ + " QUADRANT ...");
            }
            // 1500 S3=G[Q1][Q2]-100 * K3 - 10 * B3: IF K3=0 THEN 1590
            S3 = G[Q1][Q2] - 100 * K3 - 10 * B3;
            if (K3 != 0) {
                // 1560 Print "COMBAT AREA    CONDITION RED": IF S>200 THEN 1590
                println("COMBAT AREA CONDITION RED");
                if (S <= 200) {
                    // 1580 Print "     Shields dangerously low"
                    println("     SHIELDS DANGEROUSLY LOW");
                }
            }
            // 1590 FOR I=1TO3: K(I,1)=0:K(I,2)=0: NEXT I
            for (int I = 1; I <= 3; I++) {
                K[I][1] = 0;
                K[I][2] = 0;
            }
        }
        // 1600
        for (int I = 0; I <= 3; I++) {
            K[I][3] = 0;
        }
        //Q$ = Z$ + Z$ + Z$ + Z$ + Z$ + Z$ + Z$ + left$(Z$, 17); // 7 * 25 + 17 = 192
        Q$ = String.join("", Collections.nCopies(192, " "));
        // 1660 REM Position Enterprise in quadrant, then place "K3" Klingons, &
        // 1670 REM "B3" starbases, & "S3" stars elsewhere.
        // 1680 A$="<*>":Z1=S1:Z2=S2:GOSUB 8670:IF K3<1 THEN 1820
        A$ = STARSHIP_ICON;
        Z1 = S1;
        Z2 = S2;
        insertIconInQuadrantString8670();
        if (K3 >= 1) {
            // 1720 FOR I=1TOK3: GOSUB 8590: A$="+K+":Z1=R1:Z2=R2
            for (int I = 1; I <= K3; I++) {
                findEmptyPlaceInQuadrant8590();
                A$ = KLINGON_ICON;
                Z1 = R1;
                Z2 = R2;
                // 1780 GOSUB 8670: K(I,1)=R1:K(I,2)=R2;K(I,3)=S9*0.5+RND(1):NEXTI
                insertIconInQuadrantString8670();
                K[I][1] = R1;
                K[I][2] = R2;
                K[I][3] = S9 * 0.5F + random.nextFloat();
            }
        }
        // 1820 IF B3<1 THEN 1910
        if (B3 >= 1) {
            // 1880 GOSUB 8590: A$=">!<":Z1=R1:B4=R1:Z2=R2:B5=R2:GOSUB 8670
            findEmptyPlaceInQuadrant8590();
            A$ = STARBASE_ICON;
            Z1 = R1;
            B4 = R1;
            Z2 = R2;
            B5 = R2;
            insertIconInQuadrantString8670();
        }
        // 1910 FOR I=1TOS3:GOSUB 8590:A$=" * ":Z1=R1:Z2=R2:GOSUB 8670:NEXTI
        for (int I = 1; I <= 3; I++) {
            findEmptyPlaceInQuadrant8590();
            A$ = STAR_ICON;
            Z1 = R1;
            Z2 = R2;
            insertIconInQuadrantString8670();
        }
        // 1980 GOSUB 6430
        shortRangeSensors6430();
    }

    private void running() {
        do {
            command();
        } while (true);
    }

    private void command() {
        // 1990 IF S+E > 10 THEN IF E>10 OR D[7]=0 THEN 2060
        if (S + E <= 10) {
            if (E <= 10 && D[7] != 0) {
                // 2020 2030 2040 2050
                println("");
                println("** FATAL ERROR ** YOU'VE JUST STRANDED YOUR SHIP IN ");
                println("SPACE");
                println("YOU HAVE INSUFFICIENT MANEUVERING ENERGY,");
                println(" AND SHIELD CONTROL");
                println("IS PRESENTLY INCAPABLE OF CROSS");
                println("-CIRCUITING ENGINE ROOM");
                // GOTO 6220
                goto6220();
            }
        }
        // 2060 INPUT "COMMAND"; A$
        A$ = input$("COMMAND");

        // 2080 FOR I=1TO9:IF LEFT$(A$,3)<>MID$(A1$, 3*I-2,3) THEN 2160
        // 2140 ON I GOTO 2300,1980,4000,4260,4700,5530,5690,7290,6270
        // 2160 NEXT I: PRINT "ENTER ONE OF THE FOLLOWING"
        switch (A$.toUpperCase()) {
            case "NAV" -> gotoNAV2300();
            case "SRS" -> shortRangeSensors6430(); // GOTO 1980
            case "LRS" -> longRangeSensors();
            case "PHA" -> phaserControl();
            case "TOR" -> photonTorpedo();
            case "SHE" -> gotoSHE5530();
            case "DAM" -> gotoDAM5690();
            case "COM" -> gotoCOM7290();
            case "XXX" -> gotoXXX6270();
            case "GRS" -> galaxySensor();
            default -> help();
        }
    }


    private void gotoNAV2300() {
        // 2290 REM Course control begins here
        // 2300
        int C1 = input("Course (0-9)");
        if (C1 == 9) {
            C1 = 1;
        }
        if (C1 < 1 || C1 > 9) {
            println(" LT. SULU REPORTS 'INCORRECT COURSE DATA, SIR!'");
        } else {
            X$ = "8"; // 2350 NSeF"DCG1)<8OTH"ENX:S=L"ge2"
            if (D[1] < 0) {
                X$ = "0.2";
            }
            // 2360
            W1 = inputF("WARP FACTOR (0 -" + X$ + "):");
            if (W1 != 0) {
                if (D[1] < 0 && W1 > 0.2) {
                    // 2470
                    println("WARP ENGINES ARE DAMAGED, MAXIMUM SPEED = WARP 0.2");
                } else {
                    if (W1 > 0 && W1 <= 8) {
                        // 2490
                        long N = Math.round(W1 * 8 + 0.5);
                        if (E - N < 0) {
                            println("ENGINEERING REPORTS 'INSUFFICIENT ENERGY AVAILABLE");
                            println("  FOR MANEUVERING AT WARP " + W1 + "!");
                            // 2530
                            if (S >= N - E && D[7] > 0) {
                                println("DEFLECTOR CONTROL ROOM ACKNOWLEDGES " + S + " UNITS OF ENERGY");
                                println("  PRESENTLY DEPLOYED TO SHIELDS.");
                            }
                        }
                        klingons();
                        moveStarShip();
                    }
                }

            }
        }
    }

    /**
     * 2580 REM KLINGONS MOVE/FIRE ON MOVING STARSHIP ...
     */
    private void klingons() {
        // 2590 FOR I=1TOK3: IF K(I,3) = 0 THEN 2700
        for (int I = 1; I <= K3; I++) {
            if (K[I][3] != 0) {
                // 2610
                A$ = EMPTY_ICON;
                Z1 = K[I][1];
                Z2 = K[I][2];
                insertIconInQuadrantString8670();
                findEmptyPlaceInQuadrant8590();
                K[I][1] = Z1;
                K[I][2] = Z2;
                A$ = KLINGON_ICON;
                insertIconInQuadrantString8670();
            }
        } // 2700 NEXT I: GOSUB 6000
        klingonsShooting6000();
        D1 = 0;
        D6 = W1;
        if (W1 >= 1) {
            D6 = 1;
        }
        // 2770 FORI=1TO8:IFD(I)>=0THEN2880
        for (int I = 1; I <= 8; I++) {
            if (D[I] < 0) {
                // 2790 D(I)=D(I)+D6:IFD(I)>-.1 AND D(I)<0 THEN D(I)=.1 : GOTO 2880
                D[I] = D[I] + D6;
                if (D[I] > -0.1 && D[I] < 0) {
                    D[I] = -0.1F;
                } else {
                    if (D[I] >= 0) {
                        if (D1 != 1) {
                            D1 = 1;
                        }
                        println("DAMAGE CONTROL REPORT:  ");
                        println("\t\t\t\t\t\t\t");
                        R1 = I;
                        goSub8790();
                        println(G2$);
                        println(" REPAIR COMPLETED.");
                    }
                }
            }
        }// 2880 NEXT I: IF RND(1) > 0.2 THEN 3070
        if (random.nextFloat() <= 0.2) {
            R1 = fnr(); // 2910
            int index = Math.round(R1);
            if (random.nextFloat() < 0.6) {
                D[index] = D[index] - (random.nextFloat() * 5 + 1);
                println("DAMAGE CONTROL REPORT:  ");
                goSub8790();
                println(G2$);
                println("DAMAGED");
                println("");
            } else {
                // 3000
                D[index] = D[index] + (random.nextFloat() * 3 + 1);
                println("DAMAGE CONTROL REPORT:  ");
                goSub8790();
                println(G2$);
                println(" STATE OF REPAIR IMPROVED.");
            }
        } // 3070
    }


    // 3060

    private void moveStarShip() {
        // 3070 A$ = "   " ...
        A$ = EMPTY_ICON;
        Z1 = S1;
        Z2 = S2;
        insertIconInQuadrantString8670();
        // 3110
        int C1int = Math.round(C1);
        X1 = C[C1int][1] + (C[C1int + 1][1] - C[C1int][1]) * (C1 - C1int);
        X = S1;
        Y = S2;
        X2 = C[C1int][2] + (C[C1int + 1][2] - C[C1int][2]) * (C1 - C1int);
        Q4 = Q1;
        Q5 = Q2;
        // 3170 FORI=1TON:Si=Si+X1:S2=S2+X2:1FSI<LORS1>=9ORS2<1ORS2>=9THEN 3500
        boolean shutdown = false;
        for (int i = 0; i < N; i++) {
            S1 = S1 + Math.round(X1);
            S2 = S2 + Math.round(X2);
            if (S1 < 1 || S1 >= 9 || S2 < 1 || S2 >= 9) {
                exceededQuadrantLimits3500();
            } else {
                int S8 = Math.round(S1) * 24 + Math.round(S2) * 3 - 26;
                if (!mid$(Q$, S8, 3).equals(EMPTY_ICON)) {
                    S1 = Math.round(S1 - X1);
                    S2 = Math.round(S2 - X2);
                    println("WARP ENGINES SHUT DOWN AT");
                    println("SECTOR " + S1 + "," + S2 + " DUE TO BAD NAVIGATION.");
                    shutdown = true;
                    break;
                }
            }
        } // 3360
        if (!shutdown) {
            S1 = Math.round(S1);
            S2 = Math.round(S2);
        }
        goto3370();
    }

    private void goto3370() {
        // 3370
        A$ = STARSHIP_ICON;
        Z1 = Math.round(S1);
        Z2 = Math.round(S2);
        insertIconInQuadrantString8670();
        maneuverEnergy();
        T8 = 1;
        if (W1 < 1) {
            T8 = 0.1F * Math.round(10 * W1);
        }
        // 3470 REM See if docked, then command
        // 3480 GOTO 1980
        shortRangeSensors6430();
    }

    // 3498 REM EXCEEDED QUADRANT LIMITS

    private void exceededQuadrantLimits3500() {
        // 3500 X=8*Q1+X+N*X1:Y=8*Q2+Y+N*X2:Q1=INT(X/8):Q2=INT(Y/8):S1=INT(X-Q1*8)
        X = 8 * Q1 + X + N * X1;
        Y = 8 * Q2 + Y + N * X2;
        Q1 = Math.round(X / 8);
        Q2 = Math.round(Y / 8);
        S1 = Math.round(X - Q1 * 8);
        // 3556 S2=INT(Y-Q2*8): IF S1=0 THEN Q1=Q1-1:S1=8
        S2 = Math.round(Y - Q2 * 8);
        if (S1 == 0) {
            Q1 = Q1 - 1;
            S1 = 8;
        }
        // 3590 IF S2=0 THEN Q2=Q2-1:S2=8
        if (S2 == 0) {
            Q2 = Q2 - 1;
            S2 = 8;
        }
        // 3620 X5=0:IF Q1<1 THEN X5=1:Q1=1:S1=1
        X5 = 0;
        if (Q1 < 1) {
            X5 = 1;
            Q1 = 1;
            S1 = 1;
        }
        // 3670 IF Q1>8 THEN X5=1:Q1=8:S1=8
        if (Q1 > 8) {
            X5 = 1;
            Q1 = 8;
            S1 = 8;
        }
        // 3710 IF Q2<1 THEN X5=1:Q2=1:S2=1
        if (Q2 < 1) {
            X5 = 1;
            Q2 = 1;
            S2 = 1;
        }
        // 3750 IF Q2>8 THEN X5=1:Q2=8:S2=8
        if (Q2 > 8) {
            X5 = 1;
            Q2 = 8;
            S2 = 8;
        }
        // 3790 IF X5=0 THEN 3860
        if (X5 != 0) {
            // 3800 PRINT"LT-UHURA REPORTS MESSAGE FROM STARFLEET COMMAND:"
            // 3810 PRINT"’ PERMISSION TO ATTEMPT CROSSING OF GALACTIC PERIMETER"
            // 3820 PRINT" IS HEREBY *DENIED*. SHUT DOWN YOUR ENGINES.'"
            // 3830 PRINT"CHIEF ENGINEER SCOTT REPORTS ‘WARP ENGINES SHUT DOWN"
            println("LT-UHURA REPORTS MESSAGE FROM STARFLEET COMMAND:");
            println("’ PERMISSION TO ATTEMPT CROSSING OF GALACTIC PERIMETER");
            println(" IS HEREBY *DENIED*. SHUT DOWN YOUR ENGINES.'");
            println("CHIEF ENGINEER SCOTT REPORTS ‘WARP ENGINES SHUT DOWN'");
            // 3840 PRINT" AT SECTOR";S1;",";S2;"OF QUADRANT";Q1;",";Q2;"-'"
            println(" AT SECTOR" + S1 + "," + S2 + "OF QUADRANT" + Q1 + "," + Q2 + "-'");
            // 3850 IF T>T0+T9 THEN 6220
            if (T > T0 + T9) {
                goto6220();
            }
        }
        // 3860 IF8*Q1+Q2=8*Q4+Q5 THEN 3370
        if (8 * Q1 + Q2 == 8 * Q4 + Q5) {
            goto3370();
        } else {
            // 3870 T=T+1:GOSUB 3910: GOTO 1320
            T = T + 1;
            goSub3910();
            // goto 1320 is entering a new quadrant.
            newQuadrant1320();
        }
    }

    private void longRangeSensors() {
        //  3990 REM LONG RANGE SENSOR SCAN CODE
        //  4000 IFD(3)<0 THEN PRINT "LONG RANGE SENSORS ARE INOPERABLE'":GOT0 1990
        if (D[3] < 0) {
            println("LONG RANGE SENSORS ARE INOPERABLE'");
        } else {
            //  4030 PRINT"LONG RANGE SCAN FOR QUADRANT";Q1;",";Q2
            //  4040 O1$="-------------------":PRINT O1$
            println("LONG RANGE SCAN FOR QUADRANT " + Q1 + "," + Q2);
            O1$ = "-------------------";
            println(O1$);
            //  4060 FOR I=Q1-1 TO Q1+1:N(1)=-1:N(2)=-2:N(3)=-3:FOR J=Q2-1T0Q2+1
            float[] N = new float[4]; // Why an array with same name as a single?
            for (int I = Q1 - 1; I <= Q1 + 1; I++) {
                N[1] = -1;
                N[2] = -2;
                N[3] = -3;
                for (int J = Q2 - 1; J <= Q2 + 1; J++) {
                    //  4120 IF I>0 AND I<9 AND J>D AND J<9 THEN N(J-Q2+2)=G(I,J):Z(I,J)=G(I,J)
                    if (I > 0 && I < 9 && J > 0 && J < 9) {
                        N[J - Q2 + 1] = G[I][J];
                        Z[I][J] = G[I][J];
                    }
                }
                // 4180 NEXTJ: FOR L=1TO3:PRINT": “;:IFN(L)<0 THEN PRINT"*** ";: GOTO4230
                for (int L = 1; L <= 3; L++) {
                    print(":");
                    if (N[L] < 0) {
                        print(" *** ");
                    } else {
                        // 4210 PRINT RIGHT$(STR$(N(L)+1000),3);" “;
                        print(String.format(" %03.0f ", N[L]));
                    }
                    // 4230 NEXTL: PRINT":": PRINT O1$:NEXT1: GOTO 1990

                }
                println(":");
                println(O1$);
            }
        }
    }

    private void galaxySensor() {
        // For debugging
        for (int i = 1; i <= 8; i++) {
            print(String.format("%4d", i));
        }
        println("");
        for (int i = 1; i <= 8; i++) {
            println("----------------------------------");
            print("" + i);
            for (int j = 0; j <= 8; j++) {
                print(String.format(" %03.0f", G[i][j]));
            }
            println("");
        }
    }

    /**
     * 4260 REM PHASER CONTROL CODE BEGINS HERE
     */
    private void phaserControl() {
        // 4260 IFD(4)<@THENPRINT'PHASERS INOPERATI VE":GOTO1996
        if (D[4] < 0) {
            println("PHASERS INOPERATIVE");
            return;
        }
        // 4265 TFK3>8 THEN 4330
        if (K3 <= 8) {
            // 4270 PRINT'SCI ENCE OFFICER SPOCK REPORTS ‘SENSORS SHOW NG ENEMY SHIPS"
            // 4280 PRINT" IN THIS QUADRAN "'3G0TO1996
            println("SCIENCE OFFICER SPOCK REPORTS ‘SENSORS SHOWING ENEMY SHIPS");
            println(" IN THIS QUADRANT'");
            return;
        }

        // 4330 IFDCS)<@THEN PRINT "COMPUTER FAILURE HANPERS ACCURACY"
        if (D[8] < 0) {
            println("COMPUTER FAILURE HAMPERS ACCURACY");
        }
        // 4350 PRINT"PHASERS LOCKED ON TARGETS "3
        println("PHASERS LOCKED ON TARGETS ");
        do {
            // 4360 PRINT" ENERGY AVAILABLE =";E; "UNITS"
            println(" ENERGY AVAILABLE =" + E + "UNITS");
            // 4370 INPUT'NUMBER OF UNITS TO FIRE";X:IF X<=0 THEN 1990
            X = input("NUMBER OF UNITS TO FIRE");
            if (X <= 0) {
                return;
            }
            // 4400 IFE-X<0THEN 4360
        } while (E - X < 0);
        // 4410 E=E-X:1FDC7)<6THEN K=XRN*DC1)
        E = E - X;
        // 4450 HI=INT(X/K3) :FORI= 1T03: IFK(I,3)<=0 THEN 4670
        H1 = Math.round(X / K3);
        for (int I = 1; I <= 3; I++) {
            if (K[I][3] > 0) {
                // 4480 H=INT((H1/FND(0)))*CRN2D1FCH>.11S*)KC+Ls23)THEN 4530
                H = Math.round((H1 / fnd()) * (random.nextFloat() + 2));
                if (H <= 0.15 * K[I][3]) {
                    // 4500 PRINT"SENSORS SHOW NO DAMAGE TO ENEMY AT";K(I,1);",";K(I,2):GOTO 4670
                    println("SENSORS SHOW NO DAMAGE TO ENEMY AT" + K[I][1] + "," + K[I][2]);
                } else {
                    // 4530 K(I,3)=K(I,3)-H:PRINT H;" UNIT HIT ON KLINGON IN SECTOR";K(I,1);",";K(I,2)
                    K[I][3] = K[I][3] - H;
                    println(H + " UNIT HIT ON KLINGON IN SECTOR" + K[I][1] + "," + K[I][2]);
                    // 4550 PRINTK(C I+ 2)s1 FKCLs 3)<=@THENPRINT"*** KLINGON DESTROYED ***"':GOTO 4580
                    if (K[I][3] > 0) {
                        // 4560 PRINT" (SENSORS SHOW'SKCI303."UNITS REMAINING)":GOTO 4670
                        println("  (SENSORS SHOW " + K[I][3] + " UNITS REMAINING)");
                    } else {
                        println("*** KLINGON DESTROYED ***");
                        // 4580 K3=K3-1:K9=K9-1:Z1=K(I,1):Z2=K(I,2):A$="   ":GOSUB 8670
                        K3 = K3 - 1;
                        K9 = K9 - 1;
                        Z1 = K[I][1];
                        Z2 = K[I][2];
                        A$ = EMPTY_ICON;
                        insertIconInQuadrantString8670();
                        // 4650 K(I,3)=0:G(Q1,Q2)=G(Q1,Q2)-100:Z(Q1,Q2)=G(Q1,Q2):IF K9<=0 THEN 6370
                        K[I][3] = 0;
                        G[Q1][Q2] = G[Q1][Q2] - 100; // Count down on Klingons in this sector
                        Z[Q1][Q2] = G[Q1][Q2];
                        if (K9 < 0) {
                            goto6370();
                        }
                    }
                }
            }
        }
        // 4670 NEXT I: GOSUB 6000: GOTO 1990
        klingonsShooting6000();
    }


    private void goSub3910() {
        println("goSub3910");
    }

    private void photonTorpedo() {
        // 4690 REM PHOTON TORPEDO CODE BEGINS HERE
        println("photonTorpedo");
    }

    private void gotoSHE5530() {
        println("gotoSHE5530");
    }

    private void gotoDAM5690() {
        println("gotoDAM5690");
    }

    private void gotoXXX6270() {
        println("gotoXXX6270");
    }

    private void goto6370() {
        println("goto6370");
    }

    private void goto6220() {
        println("goto6220");
    }

    private void goto6240() {
        print("goto6240");
    }

    private void gotoCOM7290() {
        println("gotoCOM7290");
    }

    private void goSub8790() {
        println("goSub8790");
    }

    private void help() {
        // 2160
        println("ENTER ONE OF THE FOLLOWING:");
        println("  NAV (TO SET COURSE)");
        println("  SRS (FOR SHORT RANGE SENSOR SCAN)");
        println("  LRS (FOR LONG RANGE SENSOR SCAN)");
        println("  PHA (TO FIRE PHASERS)");
        println("  TOR (TO FIRE PHOTON TORPEDOES)");
        println("  SHE (TO RAISE OR LOWER SHIELDS)");
        println("  DAM (FOR DAMAGE CONTROL REPORT)");
        println("  COM (TO CALL ON LIBRARY-COMPUTER)");
        println("  XXX (TO RESIGN YOUR COMMAND)");
        println("");
        // 2260 GOTO 1990
    }

    private void maneuverEnergy() {
        //        3900 REM MANEUVER ENERGY S/R **
        //        3910 E=E-N-10:IFE>=0THEN RETURN
        //        3930 PRINT"SHIELD CONTROL SUPPLIES ENERGY TO COMPLETE THE MANEUVER."
        //        3940 S=S+E: E=0: IFS<=0THEN S=0
        //        3980 RETURN
        E = E - N - 10;
        if (E < 0) {
            println("SHIELD CONTROL SUPPLIES ENERGY TO COMPLETE THE MANEUVER.");
            S = S + E;
            E = 0;
            if (S <= 0) {
                S = 0;
            }
        }
    }

    private void klingonsShooting6000() {
        println("goSub6000");
        //5990 REM KLINGONS SHOOTING
        //6000 IFK3<=0 THEN RETURN
        if (K3 <= 0) {
            return;
        }
        // 6010 TFDG<>QTHENPRINT'STARBASE SHIELDS PROTECT THE ENTERPRISE": RETURN
        if (D0 != 0) {
            print("STARBASE SHIELDS PROTECT THE ENTERPRISE");
            return;
        }
        // 6040 FORI= 1TO3: IFK(I, 3) <= 0 THEN 6200
        for (int I = 1; I <= 3; I++) {
            if (K[I][3] > 0) {
                // 6060 H=INTCCKCEs 3) /FNDC 1) *C24+PNDC 120): SsS-HikCls 3=KCLs 3) /C3+RND(0)
                H = Math.round((K[I][3] / fnd()) * 2 + random.nextFloat());
                S = S - H;
                K[I][3] = K[I][3] / (3 + random.nextFloat()); // Here RND(0) is in the code as opposed to RND(1).
                // 6080 PRINT H;"UNIT HIT ON ENTERPRISE FROM SECTOR";K(I,1);",";K(I,2)"
                print(H + "UNIT HIT ON ENTERPRISE FROM SECTOR " + K[I][1] + "," + K[I][2]);
                // 6090 IFS<=0 THEN 6240
                if (S <= 0) {
                    goto6240(); // Ouch, we're done
                } else {
                    // 6100 PRINT" <SHIELDS DOWN TO"S Ss “UNL TS> "3 :IFH<20 THEN 6200
                    print(" <SHIELDS DOWN TO " + S + " UNITS> ");
                    if (H < 20) {
                        continue;
                    }
                    // 6120 IF RND(1)>.60 OR H/S<=.02 THEN 6200
                    if (random.nextFloat() > 0.6 || H / S <= 0.02) {
                        continue;
                    }
                    // 6140 R1=FNR(1):D(R1)= FNRC1) 2DORI = DCRL) -H/S- «S*PNDC 1) :GOSUB 8790
                    R1 = fnr();
                    int index = Math.round(R1);
                    D[index] = D[index] - H / S - 0.5F * random.nextFloat();
                    goSub8790();
                    // 6170 PRINT"DAMAGE CONTROL REPORTS ‘";G2$;" DAMAGED BY THE HIT'"
                    print("DAMAGE CONTROL REPORTS ‘" + G2$ + " DAMAGED BY THE HIT'");
                }
            }
        }
        // 6200 NEXTI:RETURN
    }

    private void shortRangeSensors6430() {
        // 6430 FORI=Si~!TOS1+1: FOR J=S2-1TOS2t1
        boolean docked = false;
        for (int I = S1 - 1; I <= S1 + 1; I++) {
            for (int J = S2 - 1; J <= S2 + 1; J++) {
                // 6450 TF INT C L ++. .5) >58 OR)IN T<C U+1- 5)G < LOORIRN TIC J+.W 5)T>3 C THEN 6540
                if (!(Math.round(I + 0.5) < 1 || Math.round(I + 0.5) > 8 || Math.round(J + 0.5) < 1 || Math.round(J + 0.5) > 8)) {
                    // 6490 ASH">!<"3Zfal:Z2eI:GIOG:S1FUz3+L1B THEN 6580
                    A$ = STARBASE_ICON;
                    Z1 = I;
                    Z2 = J;
                    checkForIcon8830();
                    if (Z3 == 1) {
                        docked = true;
                        break;
                    }
                }
            }
        }
        // 6540 NEXTUSNEC:TI D@=G:GOTO6650
        if (!docked) {
            D0 = 0;
        } else {
            // 6580 D0=1:C$="DOCKED":E=E0:P=P0
            D0 = 1;
            C$ = "DOCKED";
            E = E0;
            P = P0;
            // 6620 PRINT'SHIELDS DROPPED FOP DOCKING PURPOSES": S=0:GOTO 6720
            println("SHIELDS DROPPED FOP DOCKING PURPOSES");
            S = 0;
        }
        // 6650 IM(3>@THEN C$="*RED*'":GOTO 6720
        if (K3 > 0) {
            C$ = "*RED*";
        } else {
            // 6660 CS="GREEN"':iFE<EG*«1THENCS="YELLOW"
            C$ = "GREEN";
            if (E < E0 * 0.1) {
                C$ = "YELLOW";
            }
        }
        // 6720 ILFDC 2) >= G@THEN 6770
        if (D[2] < 0) {
            // 6730 PRINT: PRINT #*# SHORT RANGE SENSORS ARE OUT #4##": PRENT: RETURN
            println("");
            println("*** SHORT RANGE SENSORS ARE OUT ***");
            println("");
            return;
        }
        // 6770 Oss" “sPRINTOL :FOR I=1T08
        O1$ = String.join("", Collections.nCopies(24, "-"));
        println(O1$);
        for (int I = 1; I <= 8; I++) {
            // 6820 FORJ=(I-1)*24im Le 24+ 1TOCI-4) *24+ 22STEP3:PRINT" “;MID$(QS,I, J);:NEXTJ
//            for (int J = (I - 1) * 24 + 1; J <= (I - 1) * 24 + 22; J += 3) {
//                print(mid$(Q$, J, 3));
//            }
            String line = mid$(Q$, (I - 1) * 24 + 1, 24);
            print(line);
            // 6830 ON I GOTO
            switch (I) {
                case 1 -> println("     STARDATE           " + Math.round(T * 10) * 0.1);
                case 2 -> println("     CONDITION          " + C$);
                case 3 -> println("     QUADRANT           " + Q1 + "," + Q2);
                case 4 -> println("     SECTOR             " + S1 + "," + S2);
                case 5 -> println("     PHOTON TORPEDOES   " + Math.round(P));
                case 6 -> println("     TOTAL ENERGY       " + Math.round(E + S));
                case 7 -> println("     SHIELDS            " + Math.round(S));
                case 8 -> println("     KLINGONS REMAINING " + Math.round(K9));
            }
        }
        println(O1$);
    }

    private void findEmptyPlaceInQuadrant8590() {
        // 8580 REM FIND EMPTY PLACE IN QUADRANT (FOR THINGS)
        // 8590 RI= FNC 1): R2=FNRC 1) :Aas=" ":Z1=R12Z2= R2: GOSUBBE 38:1 FZ3=OTHEN B590
        do {
            R1 = fnr();
            R2 = fnr();
            A$ = EMPTY_ICON;
            Z1 = R1;
            Z2 = R2;
            checkForIcon8830();
        } while (Z3 == 0);
    }

    private void insertIconInQuadrantString8670() {
        // 8660 REM INSERT IN STRING ARRAY FOR QUADRANT
        // 8670 S8=INT(Z2-.5)*3+INT(Z1-.5)*24+1
        S8 = Math.toIntExact(Math.round(Z2 - 0.5) * 3 + Math.round(Z1 - 0.5) * 24 + 1);
        // 8675 IF LEN(AS)<>3 THEN PRINT "ERROR": STOP
        if (A$.length() != 3) {
            print("ERROR");
            stop();
        }
        // 8680 IFS8= 1 THEN QS=A$+RIGHTS( QS 189): RETURN
        if (S8 == 1) {
            Q$ = A$ + right$(Q$, 189);
        }
        // 8690 IFS8= 196THEN QS=LEFTS( @$s 189) +AS: RETURN
        else if (S8 == 190) {
            Q$ = left$(Q$, 189) + A$;
        } else
            // 8700 Q$=LEFT$(Q$,S8 - 1) +A$+ RIGHT$(Q$,190-S8): RETURN
            Q$ = left$(Q$, S8 - 1) + A$ + right$(Q$, 190 - S8);
    }

    /**
     * Check if an expected icon A$ is at Z1,Z2.
     * Z3 = 1 is used to indicate success.
     */
    private void checkForIcon8830() {
        // 8820 REM STRING COMPARISON IN QUADRANT ARRAY
        // 8830 Z=INT(Z1+.5): sZO=INTCZO+.5) 2S8=(ZO=1) e340 Z1=1)*Bat 12:Z3=0
        Z1 = Math.round(Z1 + 0.5);
        Z2 = Math.round(Z2 + 0.5);
        S8 = Math.round((Z2 - 1) * 3 + (Z1 - 1) * 24 + 1);
        // 8890 1FMI D&C QS, $8. 3)<>ASTHENRETURN
        if (mid$(Q$, S8, 3).equals(A$)) {
            Z3 = 1;
        } else {
            Z3 = 0;
        }
        // 8900 Z3=1:RETURN
    }

    private void stop() {
        print("STOP");
    }

    private String left$(String input, int i) {
        return input.substring(0, Math.min(i - 1, input.length() - 1));
    }

    private String right$(String input, int i) {
        if (input.length() - i - 1 > input.length()) {
            println("right$ out of bounds " + i + "(" + input.length() + ")");
            return "";
        }
        return input.substring(input.length() - i - 1);
    }

    private String mid$(String string, int start, int length) {
        if (string.length() < start - 1 + length || start <= 0) {
            println("out of bounds " + (start + length));
            return "";
        }
        return string.substring(start - 1, start - 1 + length);
    }

    private static final String[] quadrantName1 =
            new String[]{
                    "unused",
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
                    "unused",
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
        println("                                  ,-----*-----,"); // 221
        println("                                   `---  ----´");
        prompt(); // 1230
        newQuadrant1320();
    }

    private void prompt() {
        println("YOUR ORDERS ARE AS FOLLOWS:"); //1230
        println("  DESTROY THE " + K9 + " KLINGON WARSHIPS WHICH HAS INVADED"); //1240
        println("  THE GALAXY BEFORE THEY CAN ATTACK FEDERATION HEADQUARTERS"); //1250
        println("  ON STARDATE " + (T0 + T9) + ". THIS GIVES YOU " + T9 + " DAYS. THERE" + X0$); //1260
        println("  " + B9 + " STARBASE" + X$ + " IN THE GALAXY TO RESUPPLY YOUR SHIP."); //1270
        println("");
        println("HIT RETURN WHEN YOU ARE READY.");
    }

    private String input$(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int input(String prompt) {
        System.out.print(prompt);
        return scanner.nextInt();
    }

    private float inputF(String prompt) {
        System.out.print(prompt);
        return scanner.nextFloat();
    }

    private void println(String s) {
        System.out.println(s);
    }

    private void print(String s) {
        System.out.print(s);
    }
}

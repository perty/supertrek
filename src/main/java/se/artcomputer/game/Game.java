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

    /**
     * Galaxy? In the LRS format.
     */
    private float[][] G = new float[8][8]; // 330
    private float[][] C = new float[9][2]; // 330
    private float[][] K = new float[3][3]; // 330
    //private float[] N = new float[3]; // 330
    private float[][] Z = new float[8][8]; // 330
    private float[] D = new float[8]; // 330
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
    private double D0 = 0.0F; // 370
    /**
     * Energy available
     */
    private float E = 3000; // 370
    private float E0 = E; // 370

    private int P = 10; // 440
    private int P0 = P; // 440
    private int S9 = 200; // 440
    /**
     * Shield energy
     */
    private float S = 0; // 440
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
    private int N; // 3170
    private float T8; // 3370
    private int X5; // 3620
    private String O1$; // 4040
    private int H1; // 4450
    private float H; // 4480
    private int S8; // 8670
    private int Z3; // 8590

    private double fnd() { // 470
        return Math.pow(Math.sqrt(K[I][0] - S1), 2) + Math.pow(K[I][1] - S2, 2);
    }


    /**
     * Generate a random in 0-7
     */
    private int fnr() { // 475
        return random.nextInt(8);
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
    private float S1 = fnr(); // 490
    /**
     * Sector position
     */
    private float S2 = fnr(); // 490

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
     * String representation of the current quadrant
     */
    private String Q$; // 1600
    private String A$; // 1680

    private float Z1, Z2; // 1680
    private float B4, B5; // 1880

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
    private final String A1$ = "NAVSRSLRSPHATORSHEDAMCOMXXX";

    private float R1, R2;

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
                R1 = random.nextFloat();
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
        if (Q1 >= 0 && Q1 < 8 && Q2 >= 0 && Q2 < 8) {
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
            for (int i = 0; i < 3; i++) {
                K[i][0] = 0;
                K[i][1] = 0;
            }
        }
        // 1600
        for (int i = 0; i < 3; i++) {
            K[i][2] = 0;
        }
        Q$ = Z$ + Z$ + Z$ + Z$ + Z$ + Z$ + left$(Z$, 17);
        // 1660 REM Position Enterprise in quadrant, then place "K3" Klingons, &
        // 1670 REM "B3" starbases, & "S3" stars elsewhere.
        // 1680 A$="<*>":Z1=S1:Z2=S2:GOSUB 8670:IF K3<1 THEN 1820
        A$ = STARSHIP_ICON;
        Z1 = S1;
        Z2 = S2;
        insertIconInString();
        if (K3 >= 1) {
            // 1720 FOR I=1TOK3: GOSUB 8590: A$="+K+":Z1=R1:Z2=R2
            for (int i = 0; i < K3; i++) {
                findEmptyPlaceInQuadrant();
                A$ = KLINGON_ICON;
                Z1 = R1;
                Z2 = R2;
                // 1780 GOSUB 8670: K(I,1)=R1:K(I,2)=R2;K(I,3)=S9*0.5+RND(1):NEXTI
                insertIconInString();
                K[i][0] = R1;
                K[i][1] = R2;
                K[i][2] = S9 * 0.5F + random.nextFloat();
            }
        }
        // 1820 IF B3<1 THEN 1910
        if (B3 >= 1) {
            // 1880 GOSUB 8590: A$=">!<":Z1=R1:B4=R1:Z2=R2:B5=R2:GOSUB 8670
            findEmptyPlaceInQuadrant();
            A$ = STARBASE_ICON;
            Z1 = R1;
            B4 = R1;
            Z2 = R2;
            B5 = R2;
            insertIconInString();
        }
        // 1910 FOR I=1TOS3:GOSUB 8590:A$=" * ":Z1=R1:Z2=R2:GOSUB 8670:NEXTI
        for (int i = 0; i < 3; i++) {
            findEmptyPlaceInQuadrant();
            A$ = STAR_ICON;
            Z1 = R1;
            Z2 = R2;
            insertIconInString();
        }
        // 1980 GOSUB 6430
        goSub6430();
    }

    private void running() {
        do {
            command();
        } while (true);
    }

    private void command() {
        // 1990 IF S+E > 10 THEN IF E>10 OR D[7]=0 THEN 2060
        if (S + E <= 10) {
            if (E <= 10 && D[6] != 0) {
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
            case "SRS" -> goSub6430(); // GOTO 1980
            case "LRS" -> longRangeSensors();
            case "PHA" -> phaserControl();
            case "TOR" -> photonTorpedo();
            case "SHE" -> gotoSHE5530();
            case "DAM" -> gotoDAM5690();
            case "COM" -> gotoCOM7290();
            case "XXX" -> gotoXXX6270();
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
            println("\tLt. Sulu reports 'Incorrect course data, sir!'");
        } else {
            X$ = "8"; // 2350
            if (D[0] < 0) {
                X$ = "0.2";
            }
            // 2360
            W1 = inputF("Warp factor (0 -" + X$ + "):");
            if (W1 != 0) {
                if (D[0] < 0 && W1 > 0.2) {
                    // 2470
                    println("Warp engines are damaged, maximum speed = warp 0.2");
                } else {
                    if (W1 > 0 && W1 <= 8) {
                        // 2490
                        long N = Math.round(W1 * 8 + 0.5);
                        if (E - N < 0) {
                            println("Engineering reports 'insufficient energy available");
                            println("\t for maneuvering at warp " + W1 + "!");
                            // 2530
                            if (S >= N - E && D[6] > 0) {
                                println("Deflector control room acknowledges " + S + " units of energy");
                                println("\t presently deployed to shields.");
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
        for (int i = 0; i < K3; i++) {
            if (K[i][2] != 0) {
                // 2610
                A$ = EMPTY_ICON;
                Z1 = K[i][0];
                Z2 = K[i][1];
                insertIconInString();
                findEmptyPlaceInQuadrant();
                K[i][0] = Z1;
                K[i][1] = Z2;
                A$ = KLINGON_ICON;
                insertIconInString();
            }
        } // 2700 NEXT I: GOSUB 6000
        goSub6000();
        D1 = 0;
        D6 = W1;
        if (W1 >= 1) {
            D6 = 1;
        }
        for (int i = 0; i < 8; i++) {
            if (D[i] < 0) {
                D[i] = D[i] + D6;
                if (D[i] > -0.1 && D[i] < 0) {
                    D[i] = -0.1F;
                } else {
                    if (D[i] >= 0) {
                        if (D1 != 1) {
                            D1 = 1;
                        }
                        println("Damage control report:  ");
                        println("\t\t\t\t\t\t\t");
                        R1 = i;
                        goSub8790();
                        println(G2$);
                        println("\t repair completed.");
                    }
                }
            }
        }// 2880 NEXT I: IF RND(1) > 0.2 THEN 3070
        if (random.nextFloat() <= 0.2) {
            R1 = fnr(); // 2910
            if (random.nextFloat() < 0.6) {
                int index = Math.round(R1);
                D[index] = D[index] - (random.nextFloat() * 5 + 1);
                println("Damage control report:  ");
                goSub8790();
                println(G2$);
                println("damaged");
                println("");
            } else {
                // 3000
                int index = Math.round(R1);
                D[index] = D[index] + (random.nextFloat() * 3 + 1);
                println("Damage control report:  ");
                goSub8790();
                println(G2$);
                println("\t state of repair improved.");
            }
        } // 3070
    }


    // 3060

    private void moveStarShip() {
        // 3070 A$ = "   " ...
        A$ = "   ";
        Z1 = S1;
        Z2 = S2;
        insertIconInString();
        // 3110
        int C1int = Math.round(C1);
        X1 = C[C1int][0] + (C[C1int + 1][0] - C[C1int][0]) * (C1 - C1int);
        X = S1;
        Y = S2;
        X2 = C[C1int][1] + (C[C1int + 1][1] - C[C1int][1]) * (C1 - C1int);
        Q4 = Q1;
        Q5 = Q2;
        // 3170
        boolean shutdown = false;
        for (int i = 0; i < N; i++) {
            S1 = S1 + X1;
            S2 = S2 + X2;
            if (S1 < 1 || S1 >= 9 || S2 < 1 || S2 >= 9) {
                exceededQuadrantLimits();
            } else {
                int S8 = Math.round(S1) * 24 + Math.round(S2) * 3 - 26;
                if (!mid$(Q$, S8, 2).equals("  ")) {
                    S1 = Math.round(S1 - X1);
                    S2 = Math.round(S2 - X2);
                    println("Warp engines shut down at");
                    println("sector " + S1 + "," + S2 + " due to bad navigation.");
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
        A$ = "<*>";
        Z1 = Math.round(S1);
        Z2 = Math.round(S2);
        insertIconInString();
        maneuverEnergy();
        T8 = 1;
        if (W1 < 1) {
            T8 = 0.1F * Math.round(10 * W1);
        }
        // 3470 REM See if docked, then command
        // 3480 GOTO 1980
        goSub6430();
    }

    // 3498 REM EXCEEDED QUADRANT LIMITS

    private void exceededQuadrantLimits() {
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
            println("LONG RANGE SCAN FOR QUADRANT" + Q1 + "," + Q2);
            O1$ = "-------------------";
            println(O1$);
            //  4060 FORI=Q1-1TOQ1+1:N(1)=-1:N(2)=-2:N(3)=-3:FOR J=Q2-1T0Q2+1
            float[] N = new float[3]; // TODO Why an array with same name as a single?
            for (int i = Q1 - 2; i <= Q1; i++) {  // Q1,Q2 is 1 based.
                N[0] = -1;
                N[1] = -2;
                N[2] = -3;
                for (int j = Q2 - 2; j <= Q2; j++) {
                    //  4120 IF I>0 AND I<9 AND J>D AND J<9 THEN N(J-Q2+2)=G(I,J):Z(I,J)=G(I,J)
                    if (i > 0 && i < 9 && j > 0 && j < 9) {
                        N[j - (Q2 - 1)] = G[i][j];
                        Z[i][j] = G[i][j];
                    }
                }
                // 4180 NEXTJ: FOR L=1TO3:PRINT": “;:IFN(L)<0 THEN PRINT"*** ";: GOTO4230
                for (int l = 0; l < 3; l++) {
                    print(":");
                    if (N[l] < 0) {
                        print(" *** ");
                    } else {
                        // 4210 PRINT RIGHT$(STR$(N(L)+1000),3);" “;
                        print(String.format(" %03.0f ", N[l]));
                    }
                    // 4230 NEXTL: PRINT":": PRINT O1$:NEXT1: GOTO 1990

                }
                println(":");
                println(O1$);
            }
        }
    }

    /**
     * 4260 REM PHASER CONTROL CODE BEGINS HERE
     */
    private void phaserControl() {
        // 4260 IFDC 4)<@THENPRINT'PHASERS INOPERATI VE":GOTO1996
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
        for (int i = 0; i < 3; i++) {
            if (K[i][2] > 0) {
                // 4480 H=INT((H1/FND(0)))*CRN2D1FCH>.11S*)KC+Ls23)THEN 4530
                H = Math.round((H1 / fnd()) * (random.nextFloat() + 2));
                if (H <= 0.15 * K[i][2]) {

                    // 4500 PRINT"SENSORS SHOW NO DAMAGE TO ENEMY AT";K(I,1);",";K(I,2):GOTO 4670
                    println("SENSORS SHOW NO DAMAGE TO ENEMY AT" + K[i][0] + "," + K[i][1]);
                } else {
                    // 4530 K(I,3)=K(I,3)-H:PRINT H;" UNIT HIT ON KLINGON IN SECTOR";K(I,1);",";K(I,2)
                    K[i][2] = K[i][2] - H;
                    println(H + " UNIT HIT ON KLINGON IN SECTOR" + K[i][0] + "," + K[i][1]);
                    // 4550 PRINTK(C I+ 2)s1 FKCLs 3)<=@THENPRINT"*** KLINGON DESTROYED ***"':GOTO 4580
                    if (K[i][2] > 0) {
                        // 4560 PRINT" (SENSORS SHOW'SKCI303."UNITS REMAINING)":GOTO 4670
                        println("  (SENSORS SHOW " + K[i][2] + " UNITS REMAINING)");
                    } else {
                        println("*** KLINGON DESTROYED ***");
                        // 4580 K3=K3-1:K9=K9-1:Z1=K(I,1):Z2=K(I,2):A$="   ":GOSUB 8670
                        K3 = K3 - 1;
                        K9 = K9 - 1;
                        Z1 = K[i][0];
                        Z2 = K[i][1];
                        A$ = "   ";
                        insertIconInString();
                        // 4650 K(I,3)=0:G(Q1,Q2)=G(Q1,Q2)-100:Z(Q1,Q2)=G(Q1,Q2):IF K9<=0 THEN 6370
                        K[i][2] = 0;
                        G[Q1][Q2] = G[Q1][Q2] - 100;
                        Z[Q1][Q2] = G[Q1][Q2];
                        if (K9 < 0) {
                            goto6370();
                        }
                    }
                }
            }
        }
        // 4670 NEXT I: GOSUB 6000: GOTO 1990
        goSub6000();
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

    private void gotoCOM7290() {
        println("gotoCOM7290");
    }

    private void goSub8790() {
        println("goSub8790");
    }

    private void help() {
        // 2160
        println("Enter one of the following:");
        println("\tNAV (to set course)");
        println("\tSRS (for short range sensor scan)");
        println("\tLRS (for long range sensor scan)");
        println("\tPHA (to fire phasers)");
        println("\tTOR (to fire photon torpedoes)");
        println("\tSHE (to raise or lower shields)");
        println("\tDAM (for damage control report)");
        println("\tCOM (to call on library-computer)");
        println("\tXXX (to resign your command)");
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

    private void goSub6000() {
        println("goSub6000");
    }

    private void goSub6430() {
        println("goSub6430");
    }

    private void findEmptyPlaceInQuadrant() {
        // 8580 REM FIND EMPTY PLACE IN QUADRANT (FOR THINGS)
        println("goSub8590");
        // 8590 RI= FNC 1): R2=FNRC 1) :Aas=" ":Z1=R12Z2= R2: GOSUBBE 38:1 FZ3=OTHEN B590
        do {
            R1 = fnr();
            R2 = fnr();
            A$ = EMPTY_ICON;
            Z1 = R1;
            Z2 = R2;
            goSub8830();
        } while (Z3 == 0);
    }

    private void insertIconInString() {
        // 8660 REM INSERT IN STRING ARRAY FOR QUADRANT
        // 8670 S8=INT(Z2-.5)*3+INT(Z1-.5)*24+1
        S8 = Math.toIntExact(Math.round(Z2 - 0.5) * 3 + Math.round(Z1 - 0.5) * 24 + 1);
        println("insert " + A$ + " in string " + Q$ + " at " + S8);
        // 8675 IF LEN(AS)<>3 THEN PRINT "ERROR": STOP
        if (A$.length() != 3) {
            print("ERROR");
            stop();
        }
        // 8680 IFS8= 1 THEN QS=A$+RIGHTS( QS 189): RETURN
        if (S8 == 1) {
            Q$ = A$ + right$(Q$, 189);
        }
        // 8690 IFS8= 196TH ENQS=LEFTS( @$s 189) +AS: RETURN
        else if (S8 == 190) {
            Q$ = left$(Q$, 189) + A$;
        } else
            // 8700 QS=LEFTSC0 Ss S6- 1) +AS+RIGHTSC OSs 199-S8): RETURN
            Q$ = left$(Q$, S8 - 1) + A$ + right$(Q$, 190 - S8);
    }

    private void goSub8830() {
        // 8820 REM STRING COMPARISON IN QUADRANT ARRAY
        // Z3 is used to indicate success.
        // 8830 ZASINTCZ 14.5) sZO=INTCZO+.5) 2S8=(ZO=1) e340 Z1=1)*Bat 12:Z3=0
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
        return input.substring(0, i);
    }

    private String right$(String input, int i) {
        return input.substring(input.length() - i);
    }

    private String mid$(String string, int start, int length) {
        return string.substring(start, start + length);
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

package se.artcomputer.game;

import java.util.Random;
import java.util.Scanner;

import static se.artcomputer.game.GameState.INITIAL;
import static se.artcomputer.game.GameState.RUNNING;

/**
 * Super Star Trek - May, 16 1978 - Requires 24k memory
 */
public class Game {

    private GameState gameState = INITIAL;

    Scanner scanner = new Scanner(System.in);

    private Random random = new Random();
    private String Z$ = "                      "; // 270
    private double[][] G = new double[8][8]; // 330
    private float[][] C = new float[9][2]; // 330
    private double[][] K = new double[3][3]; // 330
    //private double[] N = new double[3]; // 330
    private double[][] Z = new double[8][8]; // 330
    private double[] D = new double[8]; // 330
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
     * Energy?
     */
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
    private int O1 = fnr(); // 490
    private int O2 = fnr(); // 490
    private float S1 = fnr(); // 490
    private float S2 = fnr(); // 490

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
        Q$ = Z$ + Z$ + Z$ + Z$ + Z$ + Z$ + left$(Z$, 17);
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
        // 1980 GOSUB 6430
        goSub6430();
        command();
    }

    private void command() {
        // 1990 IF S+E > 10 THEN IF E>10 OR D[7]=0 THEN 2060
        if (S + E <= 10) {
            if (E <= 10 && D[6] != 0) {
                // 2020 2030 2040 2050
                print("");
                print("** FATAL ERROR ** You've just stranded your ship in ");
                print("space");
                print("You have insufficient maneuvering energy,");
                print(" and shield control");
                print("Is presently incapable of cross");
                print("-circuiting engine room");
                // GOTO 6220
            }
        }
        // 2060 INPUT "COMMAND"; A$
        A$ = input$("Command");

        // 2080 FOR I=1TO9:IF LEFT$(A$,3)<>MID$(A1$, 3*I-2,3) THEN 2160
        // 2140 ON I GOTO 2300,1980,4000,4260,4700,5530,5690,7290,6270
        // 2160 NEXT I: PRINT "ENTER ONE OF THE FOLLOWING"
        switch (A$.toUpperCase()) {
            case "NAV" -> gotoNAV2300();
            case "SRS" -> goSub6430(); // GOTO 1980
            case "LRS" -> gotoLSR4000();
            case "PHA" -> gotoPHA4260();
            case "TOR" -> gotoTOR4700();
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
            print("\tLt. Sulu reports 'Incorrect course data, sir!'");
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
                    print("Warp engines are damaged, maximum speed = warp 0.2");
                } else {
                    if (W1 > 0 && W1 <= 8) {
                        // 2490
                        long N = Math.round(W1 * 8 + 0.5);
                        if (E - N < 0) {
                            print("Engineering reports 'insufficient energy available");
                            print("\t for maneuvering at warp " + W1 + "!");
                            // 2530
                            if (S >= N - E && D[6] > 0) {
                                print("Deflector control room acknowledges " + S + " units of energy");
                                print("\t presently deployed to shields.");
                            }
                        }
                        klingons();
                        moveStarShip();
                    }
                }

            }
        }
    }

    // 2580 REM KLINGONS MOVE/FIRE ON MOVING STARSHIP ...

    private void klingons() {
        // 2590 FOR I=1TOK3: IF K(I,3) = 0 THEN 2700
        for (int i = 0; i < K3; i++) {
            if (K[i][2] != 0) {
                // 2610
                A$ = "   ";
                Z1 = K[i][0];
                Z2 = K[i][1];
                goSub8670();
                goSub8590();
                K[i][0] = Z1;
                K[i][1] = Z2;
                A$ = "+K+";
                goSub8670();
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
                    D[i] = -0.1;
                } else {
                    if (D[i] >= 0) {
                        if (D1 != 1) {
                            D1 = 1;
                        }
                        print("Damage control report:  ");
                        print("\t\t\t\t\t\t\t");
                        R1 = i;
                        goSub8790();
                        print(G2$);
                        print("\t repair completed.");
                    }
                }
            }
        }// 2880 NEXT I: IF RND(1) > 0.2 THEN 3070
        if (random.nextFloat() <= 0.2) {
            R1 = fnr(); // 2910
            if (random.nextFloat() < 0.6) {
                int index = Math.round(R1);
                D[index] = D[index] - (random.nextFloat() * 5 + 1);
                print("Damage control report:  ");
                goSub8790();
                print(G2$);
                print("damaged");
                print("");
            } else {
                // 3000
                int index = Math.round(R1);
                D[index] = D[index] + (random.nextFloat() * 3 + 1);
                print("Damage control report:  ");
                goSub8790();
                print(G2$);
                print("\t state of repair improved.");
            }
        } // 3070
    }

    // 3060

    private void moveStarShip() {
        // 3070 A$ = "   " ...
        A$ = "   ";
        Z1 = S1;
        Z2 = S2;
        goSub8670();
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
                exceededQuadrantLimits3500();
            } else {
                int S8 = Math.round(S1) * 24 + Math.round(S2) * 3 - 26;
                if (!mid$(Q$, S8, 2).equals("  ")) {
                    S1 = Math.round(S1 - X1);
                    S2 = Math.round(S2 - X2);
                    print("Warp engines shut down at");
                    print("sector " + S1 + "," + S2 + " due to bad navigation.");
                    shutdown = true;
                    break;
                }
            }
        } // 3360
        if (!shutdown) {
            S1 = Math.round(S1);
            S2 = Math.round(S2);
        }
        // 3370
        A$ = "<*>";
        Z1 = Math.round(S1);
        Z2 = Math.round(S2);
        goSub8670();
        goSub3910();
        T8 = 1;
        if (W1 < 1) {
            T8 = 0.1F * Math.round(10 * W1);
        }
        // 3470 REM See if docked, then command
        // 3480 GOTO 1980
        goSub6430();
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
        }
        S1 = 8;
        // 3590 IF S2=0 THEN Q2=Q2-1:S2=8
        if (S2 == 0) {
            Q2 = Q2 - 1;
        }
        S2 = 8;
        // 3620 XSP"Ol<@LTHE2NKS1=1:@1=2Siel
        //        3678 LFQ1>8 THENX S=
        //                37168
        //        3756 LFGQ2>8 THENX S= 1:
        //        379G 1 Fi S= STHENS 66
        //        3896PRINT"LT-UHURAREPORTSMESSAGEFROMSTARFLEETCONMAND:" 6276PRINT"THEREWERKOES"'KLISNGONBATTLECRUISERSLEFTAT"
        //        3810 PRINT’ "PERMISSION TO ATTEMPT CROSSING OF GALACTIC PERIMETER" 38 26 PRINT’ IS HEREBY *DENIEDt. SHUT DOWN YOUR ENGINES.'"
        //        38368 PRINT"CHLEF ENGINEER SCOTT REPORTS ‘WARP ENGINES SHUT DOWN" 38 40 PRINT" AT SECTOR'S Si3'2"3 523 "GF QUADRANT"3 O13". "3 Qa3".'" 3856IFT>T@+19THEN6226
        //        38 6G IF6+01+02=8%*C4+tQSTHEN3379
        //        3873 T= T+ 1:GOSUB39 lo: GOTOI323

    }

    private void gotoLSR4000() {
    }

    private void gotoPHA4260() {
    }

    private void gotoTOR4700() {
    }

    private void gotoSHE5530() {
    }

    private void gotoDAM5690() {
    }

    private void gotoXXX6270() {
    }

    private void gotoCOM7290() {
    }

    private void help() {
        // 2160
        print("Enter one of the following:");
        print("\tNAV (to set course)");
        print("\tSRS (for short range sensor scan)");
        print("\tLRS (for long range sensor scan)");
        print("\tPHA (to fire phasers)");
        print("\tTOR (to fire photon torpedoes)");
        print("\tSHE (to raise or lower shields)");
        print("\tDAM (for damage control report)");
        print("\tCOM (to call on library-computer)");
        print("\tNAV (to resign your command)");
        print("");
        // 2260 GOTO 1990
    }

    private void goSub3910() {
        //        3900 REM MANEUVER ENERGY S/hk **
        //        3910E=E-N~16:1FE>=@THENRETURN
        //        3936 PRINT"SHIELD CONTROL SUPPLIES ENERGY TO COMPLETE THE MANEUVER." 39 43 S=St+E: Ee@:1 FS<=GTHENS=G
        //        6235 PRINT"THE END OF YOUR MISSION."
        //        629 PRINT: PRINT: IFE9= 0TH EN6365
        //        6316 PRINT" THE FEDERATION IS IN NEED OF A NEV STARSHIP COMMANDER" 6326 PRINT'FOR A SIMILAN MISSION -- IF THERE 1S A VOLUNTEER" 6330INPUT"LETHIMSTEPFORVARDANDENTER‘'AYE'FA"S=3"AAYES"T?HE1NIG 6368 END
        //        6370 PRINT"CONGRULATOIN» CAPTAIN! THE LAST KLINGON BATTLE CRUISER" 6388 PRINT"MENACING THE FEDERATION HAS BEEN DESTROYED.": PRINT 648EPRINT"YOUR EFFICLENCY RATINGI15"319G@*I~1(92)K127:GO/TO0(6E9G 6426 REM SHORT RANGE SENSOR SCAN & STARTUP SUBROUTINE
        //                6438 FORI=Si~!TOS1+1: FORU=S2-1TOS2t1
        //        6458 T F I N T C L ++. .5) >58 OR)IN T<C U+1- 5)G < LOORIRN TIC J+.W 5)T>3 CTHELNG54G
        //        3986 RETURN
    }

    private void goSub6000() {
    }

    private void goSub6430() {

    }

    private void goSub8590() {
    }

    private void goSub8670() {
    }

    private String left$(String input, int i) {
        return input.substring(0, i);
    }

    private String mid$(String string, int start, int end) {
        return string.substring(start end);
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

    private void print(String s) {
        System.out.println(s);
    }

}
